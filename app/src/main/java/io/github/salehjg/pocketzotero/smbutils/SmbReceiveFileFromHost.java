package io.github.salehjg.pocketzotero.smbutils;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_CREATE;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OVERWRITE_IF;

import android.os.Handler;
import android.os.Looper;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmbReceiveFileFromHost {

    public interface Listener {
        void onFinished();
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private String mFilePathOnHostToReceive;
    private String mFilePathToSave;
    private SmbServerInfo mServerInfo;
    private int mLastProgressReport;

    private Listener mListener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    public SmbReceiveFileFromHost(
            SmbServerInfo serverInfo,
            String filePathOnHostToReceive,
            String filePathToSave,
            Listener mListener){
        this.mServerInfo = serverInfo;
        this.mFilePathOnHostToReceive = filePathOnHostToReceive;
        this.mFilePathToSave = filePathToSave;
        this.mListener = mListener;
        this.mLastProgressReport = 0;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _onProgressTick(0, false);
                    SMBClient client = new SMBClient();
                    try (Connection connection = client.connect(mServerInfo.getIp())) {
                        AuthenticationContext ac = new AuthenticationContext(mServerInfo.getUsername(), mServerInfo.getPassword().toCharArray(), serverInfo.getIp());
                        Session session = connection.authenticate(ac);

                        // Connect to Share
                        try (DiskShare share = (DiskShare) session.connectShare(getSmbDiskName())) {
                            //int maxReadSize = share.getTreeConnect().getSession().getConnection().getNegotiatedProtocol().getMaxReadSize();

                            File smbFile = share.openFile(getSmbPathWithoutDiskName(),
                                    EnumSet.of(AccessMask.FILE_READ_DATA),
                                    null,
                                    SMB2ShareAccess.ALL,
                                    FILE_OPEN,
                                    null);
                            InputStream smbStream = smbFile.getInputStream();
                            long localFileSize = share.getFileInformation(getSmbPathWithoutDiskName()).getStandardInformation().getEndOfFile();

                            java.io.File localFile = new java.io.File(mFilePathToSave);
                            OutputStream localStream = new FileOutputStream(localFile);

                            final WritableByteChannel outputChannel = Channels.newChannel(localStream);
                            final ReadableByteChannel inputChannel = Channels.newChannel(smbStream);

                            long bytesProcessed = 0;
                            try{
                                if (smbStream != null && localFile.exists()) {
                                    // https://stackoverflow.com/a/13748290/8296604
                                    final ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
                                    while (inputChannel.read(buffer) != -1) {
                                        bytesProcessed += buffer.position();

                                        // prepare the buffer to be drained
                                        buffer.flip();
                                        // write to the channel, may block
                                        outputChannel.write(buffer);
                                        // If partial transfer, shift remainder down
                                        // If buffer is empty, same as doing clear()
                                        buffer.compact();

                                        _onProgressTick((int) ((bytesProcessed*100)/localFileSize), false);
                                    }
                                    // EOF will leave buffer in fill state
                                    buffer.flip();
                                    // make sure the buffer is fully drained.
                                    while (buffer.hasRemaining()) {
                                        outputChannel.write(buffer);
                                    }
                                }
                            } finally {
                                inputChannel.close();
                                outputChannel.close();

                                localStream.close();
                                if(smbStream!=null) smbStream.close();
                            }
                        }
                    }
                    _onFinished();
                }
                catch (Exception e) {
                    _onError(e);
                }

                mExecutor.shutdown();
            }
        };
    }

    public void RunInBackground(){
        mExecutor.execute(mRunnable);
    }

    public void TerminateWithForce(){
        mExecutor.shutdownNow();
    }

    public boolean isTerminated(){
        return mExecutor.isTerminated();
    }

    private void _onFinished(){
        _onProgressTick(100, true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener!=null) mListener.onFinished();
            }
        });
    }

    private void _onProgressTick(int percent, boolean forcedTick){
        // To avoid possible burst of progress ticks with tiny deltas.
        if(percent - mLastProgressReport > 5 || forcedTick) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) mListener.onProgressTick(percent);
                }
            });
            mLastProgressReport = percent;
        }
    }

    private void _onError(Exception e){
        _onProgressTick(100, true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener!=null) mListener.onError(e);
            }
        });
    }

    private String getSmbDiskName(){
        int windowsStyle = mFilePathOnHostToReceive.indexOf(":\\");
        int unixStyle = mFilePathOnHostToReceive.indexOf("/");
        if(windowsStyle!=-1){
            return mFilePathOnHostToReceive.substring(0, windowsStyle);
        } else if ( unixStyle!=-1){
            return mFilePathOnHostToReceive.substring(0, unixStyle);
        }else{
            return "";
        }
    }

    private String getSmbPathWithoutDiskName(){
        int windowsStyle = mFilePathOnHostToReceive.indexOf(":\\");
        int unixStyle = mFilePathOnHostToReceive.indexOf("/");
        if(windowsStyle!=-1){
            return "ERROR";
        } else if ( unixStyle!=-1){
            return mFilePathOnHostToReceive.substring(unixStyle+1);
        }else{
            return "ERROR";
        }
    }

}