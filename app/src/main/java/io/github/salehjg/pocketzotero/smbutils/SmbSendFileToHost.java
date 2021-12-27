package io.github.salehjg.pocketzotero.smbutils;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_CREATE;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OVERWRITE_IF;

import android.os.Handler;
import android.os.Looper;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmbSendFileToHost {


    public interface Listener {
        void onFinished();
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private final boolean mOverWrite;
    private String mFilePathToSend;
    private String mFilePathOnHost;
    private SmbServerInfo mServerInfo;

    private Listener mListener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    public SmbSendFileToHost(
            SmbServerInfo serverInfo,
            String filePathToSend,
            String filePathOnHost,
            boolean overWrite,
            Listener mListener){
        this.mServerInfo = serverInfo;
        this.mFilePathToSend = filePathToSend;
        this.mFilePathOnHost = filePathOnHost;
        this.mOverWrite = overWrite;
        this.mListener = mListener;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _onProgressTick(0);
                    SMBClient client = new SMBClient();
                    try (Connection connection = client.connect(mServerInfo.getIp())) {
                        AuthenticationContext ac = new AuthenticationContext(mServerInfo.getUsername(), mServerInfo.getPassword().toCharArray(), serverInfo.getIp());
                        Session session = connection.authenticate(ac);

                        // Connect to Share
                        try (DiskShare share = (DiskShare) session.connectShare(getSmbDiskName())) {

                            File smbFile = share.openFile(getSmbPathWithoutDiskName(),
                                    EnumSet.of(AccessMask.FILE_APPEND_DATA),
                                    null,
                                    SMB2ShareAccess.ALL,
                                    mOverWrite? FILE_OVERWRITE_IF: FILE_CREATE,
                                    null);

                            OutputStream smbStream = smbFile.getOutputStream();

                            java.io.File localFile = new java.io.File(mFilePathToSend);
                            long localFileSize = localFile.length();
                            InputStream localStream = new FileInputStream(localFile);

                            long bytesProcessed = 0;
                            try{
                                if (smbStream != null && localFile.exists()) {
                                    byte[] buffer = new byte[10 * 1024 * 1024];
                                    int length;
                                    while ((length = localStream.read(buffer)) > 0) {
                                        smbStream.write(buffer, 0, length);
                                        bytesProcessed += length;
                                        _onProgressTick((int) ((bytesProcessed*100)/localFileSize));
                                        if(Thread.currentThread().isInterrupted()) break;
                                    }
                                }
                            } finally {
                                localStream.close();
                                if(smbStream!=null) smbStream.close();
                            }
                        }
                    }
                }
                catch (Exception e) {
                    _onError(e);
                }
                _onProgressTick(100);
                _onFinished();
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener!=null) mListener.onFinished();
            }
        });
    }

    private void _onProgressTick(int percent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener!=null) mListener.onProgressTick(percent);
            }
        });
    }

    private void _onError(Exception e){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mListener!=null) mListener.onError(e);
            }
        });
    }

    private String getSmbDiskName(){
        int windowsStyle = mFilePathOnHost.indexOf(":\\");
        int unixStyle = mFilePathOnHost.indexOf("/");
        if(windowsStyle!=-1){
            return mFilePathOnHost.substring(0, windowsStyle);
        } else if ( unixStyle!=-1){
            return mFilePathOnHost.substring(0, unixStyle);
        }else{
            return "";
        }
    }

    private String getSmbPathWithoutDiskName(){
        int windowsStyle = mFilePathOnHost.indexOf(":\\");
        int unixStyle = mFilePathOnHost.indexOf("/");
        if(windowsStyle!=-1){
            return "ERROR";
        } else if ( unixStyle!=-1){
            return mFilePathOnHost.substring(unixStyle+1);
        }else{
            return "ERROR";
        }
    }

}