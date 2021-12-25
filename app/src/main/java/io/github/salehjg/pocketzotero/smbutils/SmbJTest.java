package io.github.salehjg.pocketzotero.smbutils;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN;
import android.os.Handler;
import android.os.Looper;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.salehjg.pocketzotero.zoteroengine.types.ItemTag;

public class SmbJTest {
    private SMBClient client;
    private String user, pass, ipAddress, serviceName;


    // Create some member variables for the ExecutorService
    // and for the Handler that will update the UI from the main thread
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    // Create an interface to respond with the result after processing
    public interface OnProcessedListener {
        public void onProcessed(ItemTag myData);
    }

    private void processInBg(final String argData, final boolean finished){

        final OnProcessedListener listener = new OnProcessedListener(){
            @Override
            public void onProcessed(ItemTag myData){
                // Use the handler so we're not trying to update the UI from the bg thread
                mHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        // Update the UI here
                        ///TODO USE myData here

                        // ...

                        // If we're done with the ExecutorService, shut it down.
                        // (If you want to re-use the ExecutorService,
                        // make sure to shut it down whenever everything's completed
                        // and you don't need it any more.)
                        if(finished){
                            mExecutor.shutdown();
                        }
                    }
                });
            }
        };

        Runnable backgroundRunnable = new Runnable(){
            @Override
            public void run(){
                // Perform your background operation(s) and set the result(s)
                ItemTag myData = new ItemTag();
                String useArgs = argData;



                SMBClient client = new SMBClient();

                try (Connection connection = client.connect("192.168.1.10")) {
                    AuthenticationContext ac = new AuthenticationContext("useeeeeeeeer", "paaaaaaaaaaaaasssss".toCharArray(), "192.168.1.11");
                    Session session = connection.authenticate(ac);

                    // Connect to Share
                    try (DiskShare share = (DiskShare) session.connectShare("Test1")) {

                        File myFile = share.openFile("my.txt",
                                EnumSet.of(AccessMask.FILE_EXECUTE),
                                null,
                                SMB2ShareAccess.ALL,
                                FILE_OPEN,
                                null);

                        InputStream stream = myFile.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        if (stream != null) {
                            String str;
                            while ((str = reader.readLine()) != null) {
                                System.out.println(str + "\n" );
                            }
                        }

                        for (FileIdBothDirectoryInformation f : share.list("", "*.*")) {
                            System.out.println("File : " + f.getFileName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Use the interface to pass along the result
                listener.onProcessed(myData);
            }
        };

        mExecutor.execute(backgroundRunnable);
    }

    public SmbJTest(String user, String pass, String ipAddress, String serviceName){
        client = new SMBClient();
        this.user = user;
        this.pass = pass;
        this.ipAddress = ipAddress;
        this.serviceName=  serviceName;
        processInBg("sss", false);
    }



}
