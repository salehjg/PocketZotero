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
        void onFinished(Vector<String> myResults);
        void onProgressTick(int percent);
        void onError(Exception e);
    }

    private Listener mListener;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    private Vector<String> mServersFound;
    private final boolean mEarlyTermination;

    public SmbScanner(boolean terminateOnFirstServerFound, Listener mListener){
        this.mEarlyTermination = terminateOnFirstServerFound;
        this.mListener = mListener;
        this.mServersFound = new Vector<>();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    _onProgressTick(0);
                    mServersFound.clear();
                    String localIp = getLocalIpAddress();
                    if(localIp==null) {
                        _onProgressTick(100);
                        _onFinished(mServersFound);
                        mExecutor.shutdown();
                        return;
                    }
                    String subIp = localIp.substring(0,localIp.lastIndexOf(".")+1);
                    for(int i=0; i<256; i++){
                        _onProgressTick((i*100)/256);
                        String currentLocalIpToTry = subIp + i;
                        if(isThisServer(currentLocalIpToTry )){
                            mServersFound.add(currentLocalIpToTry);
                            if(mEarlyTermination) break;
                        }
                    }
                }
                catch (Exception e) {
                    _onError(e);
                }
                _onProgressTick(100);
                _onFinished(mServersFound);
                mExecutor.shutdown();
            }
        };
        mExecutor.execute(mRunnable);
    }

    private void _onFinished(Vector<String> serversFound){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFinished(serversFound);
            }
        });
    }

    private void _onProgressTick(int percent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onProgressTick(percent);
            }
        });
    }
    private void _onError(Exception e){
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