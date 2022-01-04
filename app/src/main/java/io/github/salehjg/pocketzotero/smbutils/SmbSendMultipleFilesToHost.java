package io.github.salehjg.pocketzotero.smbutils;

import java.util.Vector;

public class SmbSendMultipleFilesToHost {
    private Listener mListener;
    private SmbSendFileToHost.Listener mSubListener;
    private SmbServerInfo mServerInfo;

    private int mLastProgressReport;
    private boolean mTerminationRequested;
    private int mCurrentIndex;
    private int mCurrentIndexErrorCount;
    private int mSubCount;

    private Vector<Boolean> mHasSucceeded;
    private Vector<String> mFilePathsToSend;
    private Vector<String> mFilePathsOnHost;
    private Vector<Boolean> mOverWrite;
    private SmbSendFileToHost mSmbSendFileToHost;

    public interface Listener {
        void onFinished(
                boolean wasTerminated,
                boolean wasTotalSuccess,
                Vector<Boolean> hasSucceeded,
                Vector<String> filePathsToSend,
                Vector<String> filePathsOnHost,
                Vector<Boolean> overWrite
        );
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    public SmbSendMultipleFilesToHost(
            SmbServerInfo serverInfo,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            Vector<Boolean> overWrite,
            Listener listener){
        assert filePathsToSend.size() == filePathsOnHost.size();
        assert filePathsOnHost.size() == overWrite.size();
        mFilePathsToSend = filePathsToSend;
        mFilePathsOnHost = filePathsOnHost;
        mOverWrite = overWrite;
        mSubCount = filePathsToSend.size();
        mLastProgressReport = 0;
        mHasSucceeded = new Vector<Boolean>();
        for(int i=0; i<mSubCount; i++){mHasSucceeded.add(false);}
        mCurrentIndex= 0;
        mCurrentIndexErrorCount = 0;
        mServerInfo = serverInfo;
        mListener = listener;
        mTerminationRequested = false;

        // these are only for the subs
        mSubListener = new SmbSendFileToHost.Listener() {
            @Override
            public void onFinished() {
                mHasSucceeded.set(mCurrentIndex, (mCurrentIndexErrorCount==0));
                if(mCurrentIndex+1<mSubCount && !mTerminationRequested){
                    mCurrentIndex++;
                    RunSingleAt(mCurrentIndex);
                }else{
                    // All of the subs are executed and finished. Time to wrap up ...
                    boolean totalSuccess = true;
                    for(Boolean b:mHasSucceeded){
                        totalSuccess = totalSuccess & b;
                        if(!b)break;
                    }
                    _onFinished(
                            mTerminationRequested,
                            totalSuccess,
                            mHasSucceeded,
                            mFilePathsToSend,
                            mFilePathsOnHost,
                            mOverWrite);
                }
            }

            @Override
            public void onProgressTick(int percent) {
                _onProgressTick(
                        (100*mCurrentIndex)/mSubCount + percent/mSubCount,
                        false
                );
            }

            @Override
            public void onError(Exception e) {
                mCurrentIndexErrorCount++;
                _onError(e);
            }
        };

    }

    public SmbSendMultipleFilesToHost(
            SmbServerInfo serverInfo,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            boolean overWriteAll,
            Listener listener){
        this(
                serverInfo,
                filePathsToSend,
                filePathsOnHost,
                getOverWriteVector(overWriteAll, filePathsToSend.size()),
                listener
        );
    }

    private static Vector<Boolean> getOverWriteVector(boolean overWriteAll, int count){
        Vector<Boolean> overWrites = new Vector<Boolean>();
        for(int i=0; i<count; i++){ overWrites.add(overWriteAll); }
        return overWrites;
    }

    public boolean RunAllSequentially(){
        mTerminationRequested = false;
        return RunSingleAt(0);
    }

    private boolean RunSingleAt(int index){
        if (mSmbSendFileToHost != null) {
            if(!mSmbSendFileToHost.isTerminated()){
                return false;
            }
        }

        mSmbSendFileToHost = new SmbSendFileToHost(
                mServerInfo,
                mFilePathsToSend.get(index),
                mFilePathsOnHost.get(index),
                mOverWrite.get(index),
                mSubListener);

        mCurrentIndex = index;
        mCurrentIndexErrorCount = 0;

        mSmbSendFileToHost.RunInBackground();
        return true;
    }

    private void _onFinished(
            boolean wasTerminated,
            boolean wasTotalSuccess,
            Vector<Boolean> hasSucceeded,
            Vector<String> filePathsToSend,
            Vector<String> filePathsOnHost,
            Vector<Boolean> overWrite
    ){
        _onProgressTick(100, true);
        if(mListener!=null) {
            mListener.onFinished(
                    wasTerminated,
                    wasTotalSuccess,
                    hasSucceeded,
                    filePathsToSend,
                    filePathsOnHost,
                    overWrite);
        }
    }

    private void _onProgressTick(int percent, boolean forcedTick){
        // To avoid possible burst of progress ticks with tiny deltas.
        if(percent - mLastProgressReport > 5 || forcedTick) {
            if (mListener != null) mListener.onProgressTick(percent);
            mLastProgressReport = percent;
        }
    }

    private void _onError(Exception e){
        // This does not invoke onProgress(100%), since there could be errors in one sub, but
        // the rest might succeed.
        if(mListener!=null) mListener.onError(e);
    }

    public void RequestTermination(){
        mTerminationRequested = true;
        if(mSmbSendFileToHost!=null){
            if(!mSmbSendFileToHost.isTerminated()){
                mSmbSendFileToHost.TerminateWithForce();
            }
        }
    }

    public boolean isTerminated(){
        if(mSmbSendFileToHost!=null){
            if(mTerminationRequested) {
                return mSmbSendFileToHost.isTerminated();
            }else{
                return mSmbSendFileToHost.isTerminated() && (mCurrentIndex==(mSubCount-1));
            }
        }else{
            return mTerminationRequested;
        }
    }
}
