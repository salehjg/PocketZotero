package io.github.salehjg.pocketzotero.fragments.settings;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ExtractLocalZipFile {

    public interface Listener {
        void onFinished();
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private ContentResolver mContentResolver;
    private Uri mUriZipFile;
    private File mBaseDir;
    private boolean mWipeBaseDirFirst;
    private int mLastProgressReport;

    private Listener mListener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    public ExtractLocalZipFile(
            ContentResolver contentResolver,
            Uri uriZipFile,
            File baseDir,
            boolean wipeBaseDirFirst,
            Listener mListener){
        this.mContentResolver = contentResolver;
        this.mUriZipFile = uriZipFile;
        this.mBaseDir = baseDir;
        this.mWipeBaseDirFirst = wipeBaseDirFirst;

        this.mListener = mListener;
        this.mLastProgressReport = 0;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                ZipInputStream zis = null;
                ZipEntry entry = null;
                FileOutputStream fos = null;

                try {
                    _onProgressTick(0, false);

                    if(mWipeBaseDirFirst){
                        File[] files = baseDir.listFiles();
                        if(files!=null) {
                            for (File file : files) {
                                //if (!file.isDirectory()) {
                                    if (!file.delete()) {
                                        throw new Exception("Failed to wipe the directory first before extraction (file: " + file.getPath() + ")");
                                    }
                                //}
                            }
                        }
                    }

                    inputStream = mContentResolver.openInputStream(mUriZipFile);
                    if(inputStream==null) throw new Exception("The content resolver failed to get an inputStream for the given URI.");
                    zis = new ZipInputStream(new BufferedInputStream(inputStream));

                    long entryProcessed = 0;
                    while ((entry = zis.getNextEntry()) != null) {
                        entryProcessed++;
                        File currentFile = new File(mBaseDir, entry.getName());
                        if(entry.isDirectory()){
                           if(!currentFile.exists()){
                               if(!currentFile.mkdirs()){
                                   throw new Exception("Failed to make a new directory at " + currentFile.getPath());
                               }
                           }
                        }else {
                            fos = new FileOutputStream(currentFile);





                            final WritableByteChannel outputChannel = Channels.newChannel(fos);
                            final ReadableByteChannel inputChannel = Channels.newChannel(zis);

                            // https://stackoverflow.com/a/13748290/8296604
                            final ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
                            while (inputChannel.read(buffer) != -1) {

                                // prepare the buffer to be drained
                                buffer.flip();
                                // write to the channel, may block
                                outputChannel.write(buffer);
                                // If partial transfer, shift remainder down
                                // If buffer is empty, same as doing clear()
                                buffer.compact();

                                //_onProgressTick((int) ((bytesProcessed*100)/localFileSize), false);
                            }
                            // EOF will leave buffer in fill state
                            buffer.flip();
                            // make sure the buffer is fully drained.
                            while (buffer.hasRemaining()) {
                                outputChannel.write(buffer);
                            }




                        }
                    }
                    _onFinished();
                }
                catch (Exception e) {
                    _onError(e);
                }finally {
                    try {
                        if (fos != null) fos.close();
                        if (zis != null) zis.close();
                        if (inputStream != null) inputStream.close();
                    }catch (Exception e){
                        _onError(e);
                    }
                }
                mExecutor.shutdown();
            }
        };
    }

    public void runInBackground(){
        mExecutor.execute(mRunnable);
    }

    public void shutdownNow(){
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

}