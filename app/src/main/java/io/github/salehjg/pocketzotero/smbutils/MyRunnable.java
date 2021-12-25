package io.github.salehjg.pocketzotero.smbutils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyRunnable{
    public interface Listener {
        void onFinished(String myResults);
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private String myArgData;
    private Listener listener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    public MyRunnable(String myArgData, Listener listener){
        this.myArgData = myArgData;
        this.listener = listener;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _onProgressTick(0);
                    ///TODO use myArgData

                    ///TODO Background Processing Here
                    for(int i =0; i<100; i++){
                        if(i%10==0)
                            _onProgressTick(i);
                        Thread.sleep(100);
                    }
                }
                catch (Exception e) {
                    _onError(e);
                }
                _onProgressTick(100);
                _onFinished("myResults");
                mExecutor.shutdown();
            }
        };
        mExecutor.execute(mRunnable);
    }

    private void _onFinished(String myResults){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFinished(myResults);
            }
        });
    }

    private void _onProgressTick(int percent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onProgressTick(percent);
            }
        });
    }
    private void _onError(Exception e){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onError(e);
            }
        });
    }
}