package io.github.salehjg.pocketzotero.smbutils;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_CREATE;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OVERWRITE_IF;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.EnumSet;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SmbSendMultipleToHost {

    public interface Listener {
        void onFinished(
                boolean wasTotalSuccess,
                Vector<Boolean> hasSucceeded,
                Vector<String> filePathsToSend,
                Vector<String> filePathsOnHost,
                Vector<Boolean> overWrite
        );
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private SmbServerInfo mServerInfo;

    private AtomicInteger mProgressTotal;
    private AtomicInteger mProgressTotalLastReported;
    private int []mProgressPerSub;

    private int mSubCount;

    private Vector<Boolean> mHasSucceeded;
    private Vector<String> mFilePathsToSend;
    private Vector<String> mFilePathsOnHost;
    private Vector<Boolean> mOverWrites;

    private Vector<Future<?>> mFutures;
    private Future<?> mFuturesOnFinished;
    private ScheduledExecutorService mExecutor;
    private int mConcurrentTransferCount;
    private Handler mHandler;
    private Vector<Runnable> mRunnables;
    private Listener mListener;

    private int mStartupDelayMs;

    public SmbSendMultipleToHost(
            SmbServerInfo serverInfo,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            Vector<Boolean> overWrites,
            int concurrentTransferCount,
            int startupDelayMs,
            Listener listener){
        assert filePathsToSend.size() == filePathsOnHost.size();
        assert filePathsOnHost.size() == overWrites.size();

        mStartupDelayMs = startupDelayMs;
        mFilePathsToSend = filePathsToSend;
        mFilePathsOnHost = filePathsOnHost;
        mOverWrites = overWrites;
        mSubCount = filePathsToSend.size();

        mProgressTotal = new AtomicInteger(0);
        mProgressTotalLastReported = new AtomicInteger(0);
        mProgressPerSub = new int[mSubCount];

        mHasSucceeded = new Vector<Boolean>();
        for(int i=0; i<mSubCount; i++){mHasSucceeded.add(false);}
        mServerInfo = serverInfo;
        mHandler = new Handler(Looper.getMainLooper());
        mListener = listener;

        mFutures = new Vector<>();
        mRunnables = new Vector<>();
        for(int i=0;i<mSubCount;i++){
            int finalI = i;
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    doFileTransferAt(finalI);
                }
            });
        }

        mConcurrentTransferCount = concurrentTransferCount;
        mExecutor = Executors.newScheduledThreadPool(mConcurrentTransferCount+1);
        mFuturesOnFinished = mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if(mFutures.size()==mSubCount){
                            if(isFinished()) break;
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                _onFinished();
            }
        });
    }

    private void doFileTransferAt(int index){
        try {
            if(index<mConcurrentTransferCount){
                Thread.sleep(mStartupDelayMs);
            }

            progressUpdateAt(index, 0);
            Log.w("SmbSendFileToHost2", "Code0");
            SMBClient client = new SMBClient();
            Log.w("SmbSendFileToHost2", "Code1");
            try (Connection connection = client.connect(mServerInfo.getIp())) {
                Log.w("SmbSendFileToHost2", "Code2");
                throwIfCanceled();
                AuthenticationContext ac = new AuthenticationContext(mServerInfo.getUsername(), mServerInfo.getPassword().toCharArray(), mServerInfo.getIp());
                Session session = connection.authenticate(ac);
                throwIfCanceled();
                Log.w("SmbSendFileToHost2", "Code3");

                // Connect to Share
                try (DiskShare share = (DiskShare) session.connectShare(getSmbDiskName(index))) {
                    Log.w("SmbSendFileToHost2", "Code4");
                    throwIfCanceled();
                    //int maxReadSize = share.getTreeConnect().getSession().getConnection().getNegotiatedProtocol().getMaxReadSize();

                    File smbFile = share.openFile(getSmbPathWithoutDiskName(index),
                            EnumSet.of(AccessMask.FILE_APPEND_DATA),
                            null,
                            SMB2ShareAccess.ALL,
                            mOverWrites.get(index)? FILE_OVERWRITE_IF: FILE_CREATE,
                            null);

                    java.io.File localFile = new java.io.File(mFilePathsToSend.get(index));
                    long localFileSize = localFile.length();
                    long bytesProcessed = 0;
                    Log.w("SmbSendFileToHost2", "Code5");

                    try(OutputStream smbStream = smbFile.getOutputStream();
                        InputStream localStream = new FileInputStream(localFile);
                        ReadableByteChannel inputChannel = Channels.newChannel(localStream);
                        WritableByteChannel outputChannel = Channels.newChannel(smbStream);){
                        Log.w("SmbSendFileToHost2", "Code6");
                        if (smbStream != null && localFile.exists()) {
                            // https://stackoverflow.com/a/13748290/8296604
                            final ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);

                            Log.w("SmbSendFileToHost2", "Code7");

                            while (inputChannel.read(buffer) != -1) {
                                Log.w("SmbSendFileToHost2", "Code8");
                                bytesProcessed += buffer.position();

                                // prepare the buffer to be drained
                                buffer.flip();
                                // write to the channel, may block
                                outputChannel.write(buffer);
                                // If partial transfer, shift remainder down
                                // If buffer is empty, same as doing clear()
                                buffer.compact();

                                progressUpdateAt(index, (int) ((bytesProcessed*100)/localFileSize));
                                throwIfCanceled();
                            }
                            // EOF will leave buffer in fill state
                            buffer.flip();

                            Log.w("SmbSendFileToHost2", "Code9");

                            // make sure the buffer is fully drained.
                            while (buffer.hasRemaining()) {
                                Log.w("SmbSendFileToHost2", "Code10");
                                outputChannel.write(buffer);
                                throwIfCanceled();
                            }
                        }
                    }
                }
            }
            Log.w("SmbSendFileToHost2", "Code11");
            mHasSucceeded.set(index, true);
        }
        catch (Exception e) {
            Log.w("SmbSendFileToHost2", "ERROOOOOOOOOOOOOOOOOOORRRRRRRRRRRR" + e.toString());
            _onError(e);
        }
        progressUpdateAt(index, 100);
        Log.w("SmbSendFileToHost2", "Code12");
    }

    public SmbSendMultipleToHost(
            SmbServerInfo serverInfo,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            boolean overWriteAll,
            int concurrentTransferCount,
            Listener listener){
        this(
                serverInfo,
                filePathsToSend,
                filePathsOnHost,
                getOverWriteVector(overWriteAll, filePathsToSend.size()),
                concurrentTransferCount,
                0,
                listener
        );
    }

    public SmbSendMultipleToHost(
            SmbServerInfo serverInfo,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            boolean overWriteAll,
            int concurrentTransferCount,
            int startDelayMs,
            Listener listener){
        this(
                serverInfo,
                filePathsToSend,
                filePathsOnHost,
                getOverWriteVector(overWriteAll, filePathsToSend.size()),
                concurrentTransferCount,
                startDelayMs,
                listener
        );
    }

    public void enqueueToRunAll(){
        for(Runnable runnable:mRunnables){
            mFutures.add(mExecutor.submit(runnable));
        }
    }

    public boolean isFinished(){
        boolean allFinished = true;
        for(Future<?> future:mFutures){
            if(!future.isDone()){
                allFinished = false;
                break;
            }
        }
        return allFinished;
    }

    public boolean isFinishedAt(int index){
        return mFutures.get(index).isDone();
    }

    public void shutdown(){
        mExecutor.shutdown();
    }

    public void shutdownNow(){
        mExecutor.shutdownNow();
    }

    private static Vector<Boolean> getOverWriteVector(boolean overWriteAll, int count){
        Vector<Boolean> overWrites = new Vector<Boolean>();
        for(int i=0; i<count; i++){ overWrites.add(overWriteAll); }
        return overWrites;
    }

    private void throwIfCanceled() throws InterruptedException {
        if(Thread.currentThread().isInterrupted()){ throw new InterruptedException(); }
    }

    private String getSmbPathWithoutDiskName(int index) throws Exception {
        int unixStyle = mFilePathsOnHost.get(index).indexOf("/");
         if ( unixStyle!=-1){
            return mFilePathsOnHost.get(index).substring(unixStyle+1);
        }else{
            throw new Exception("Non-unix style path is not supported.");
        }
    }

    private String getSmbDiskName(int index) throws Exception {
        int unixStyle = mFilePathsOnHost.get(index).indexOf("/");
        if ( unixStyle!=-1){
            return mFilePathsOnHost.get(index).substring(0, unixStyle);
        }else{
            throw new Exception("Non-unix style path is not supported.");
        }
    }

    private void _onFinished(){
        progressTotalSetForcibly(100);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean totalSuccess = true;
                for(Boolean b:mHasSucceeded){
                    totalSuccess = totalSuccess & b;
                    if(!b)break;
                }
                if(mListener!=null)
                    mListener.onFinished(
                            totalSuccess,
                            mHasSucceeded,
                            mFilePathsToSend,
                            mFilePathsOnHost,
                            mOverWrites
                    );
            }
        });
    }

    private void progressUpdateAt(int index, int percent){
        mProgressPerSub[index] = percent;
        progressTotalCalculate();
    }

    private void progressTotalCalculate(){
        int totalSum = 0;
        for(int i=0;i<mSubCount;i++){
            totalSum+=mProgressPerSub[i];
        }
        mProgressTotal.set(totalSum/mSubCount);
        _onProgressTick(mProgressTotal.get(), false);
    }

    private void progressTotalSetForcibly(int percent){
        _onProgressTick(percent, true);
    }

    private void _onProgressTick(int percent, boolean forcedTick){
        // To avoid possible burst of progress ticks with tiny deltas.
        if(Math.abs(percent - mProgressTotalLastReported.get()) > 5 || forcedTick) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) mListener.onProgressTick(percent);
                }
            });
            mProgressTotalLastReported.set(percent);
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

}
