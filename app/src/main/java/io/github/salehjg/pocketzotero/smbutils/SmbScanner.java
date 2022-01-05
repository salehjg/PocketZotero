package io.github.salehjg.pocketzotero.smbutils;

import android.os.Handler;
import android.os.Looper;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmbScanner {
    public interface Listener {
        void onFinished(Vector<String> serversFound);
        void onServerFound(String theNewServer, Vector<String> serversFound);
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private Listener mListener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    private Vector<String> mServersFound;
    private final boolean mEarlyTermination;
    private int mLastProgressReport;

    public SmbScanner(boolean terminateOnFirstServerFound, Listener mListener){
        this.mEarlyTermination = terminateOnFirstServerFound;
        this.mListener = mListener;
        this.mServersFound = new Vector<>();
        this.mLastProgressReport = 0;

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _onProgressTick(0, true);
                    mServersFound.clear();
                    String localIp = getLocalIpAddress();
                    if(localIp==null) {
                        _onProgressTick(100, true);
                        _onFinished(mServersFound);
                        mExecutor.shutdown();
                        return;
                    }
                    String subIp = localIp.substring(0,localIp.lastIndexOf(".")+1);
                    for(int i=0; i<256; i++){
                        if(Thread.currentThread().isInterrupted()) break;
                        _onProgressTick((i*100)/256, false);
                        String currentLocalIpToTry = subIp + i;
                        if(isThisServer(currentLocalIpToTry)){
                            mServersFound.add(currentLocalIpToTry);
                            _onServerFound(currentLocalIpToTry, mServersFound);
                            if(mEarlyTermination) break;
                        }
                    }
                    _onFinished(mServersFound);
                }
                catch (Exception e) {
                    _onError(e);
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

    private void _onFinished(Vector<String> serversFound){
        _onProgressTick(100, true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFinished(serversFound);
            }
        });
    }

    private void _onServerFound(String theNewServer, Vector<String> serversFound){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onServerFound(theNewServer, serversFound);
            }
        });
    }

    private void _onProgressTick(int percent, boolean forcedTick){
        if(percent - mLastProgressReport > 5 || forcedTick) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onProgressTick(percent);
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
                mListener.onError(e);
            }
        });
    }

    public Vector<String> getServersFound() {
        return mServersFound;
    }

    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean isThisServer(String localIp) {
        SMBClient client = new SMBClient();
        try {
            Connection connection = client.connect(localIp);
            connection.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}