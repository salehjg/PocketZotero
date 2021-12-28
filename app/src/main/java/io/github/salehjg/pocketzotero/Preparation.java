package io.github.salehjg.pocketzotero;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Vector;

import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;

public class Preparation {
    static final int ERR_BASE_STORAGE = 100;
    static final int ERR_BASE_PREFERENCES = 200;
    static final int ERR_BASE_PERMISSIONS = 300;

    private AppMem mAppMem;
    private Context mContext;
    private Vector<Integer> mRecordedStates;

    private Activity mActivity;
    private LinearLayout mLinearLayoutCollections;
    private ProgressBar mProgressBar;

    public Preparation(
            Application application,
            Activity mainActivity,
            LinearLayout linearLayoutCollections
    ){
        mAppMem = (AppMem) application;
        mContext = application.getApplicationContext();
        mRecordedStates = new Vector<>();
        mActivity = mainActivity;
        mLinearLayoutCollections = linearLayoutCollections;
        mProgressBar = mAppMem.getProgressBar();
    }

    private void RecordState(int retVal){
        mRecordedStates.add(retVal);
    }

    public void StartupSequence(){
        if(SDK_INT >= 30) {
            if (Environment.isExternalStorageManager()) {
                StartupSequencePermissionGranted();
            } else {
                RecordState(ERR_BASE_PERMISSIONS);
            }
        }
    }

    public void StartupSequencePermissionGranted(){
        int state;

        // -----------------------------------------------------------------------------------------
        RecordState(state = CheckTheAppDirectories());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        RecordState(state = CheckTheDatabases());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        RecordState(state = CheckPreferencesSmb());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        if(mAppMem.getStorageModeIsLocal()){
            // LOAD THE DATABASE AT `DbFileNameLocal`
            String dbPathLocal = getExternalStorage()+ File.separator +
                    getResourceString(R.string.DirNameApp)+ File.separator +
                    getResourceString(R.string.DirNameLocal);
            StartZoteroEngine(dbPathLocal + File.separator + getResourceString(R.string.DbFileNameLocal));
        }else{
            String serverIp = mAppMem.getStorageSmbServerIp();
            String serverUser = mAppMem.getStorageSmbServerUsername();
            String serverPass = mAppMem.getStorageSmbServerPassword();

            String dbPathSmb = getExternalStorage()+ File.separator +
                    getResourceString(R.string.DirNameApp)+ File.separator +
                    getResourceString(R.string.DirNameSmb);

            SmbReceiveFileFromHost smbReceiveFileFromHost = new SmbReceiveFileFromHost(
                    new SmbServerInfo("fooname", serverUser, serverPass, serverIp),
                    mAppMem.getStorageSmbServerSharedPath(),
                    dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmbTemp),
                    new SmbReceiveFileFromHost.Listener() {
                        @Override
                        public void onFinished() {
                            File dbSavedSmbTempFile = new File(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmbTemp));
                            File dbOldSmbFile = new File(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb));

                            if(dbSavedSmbTempFile.exists()){
                                if(dbOldSmbFile.exists()){
                                    if(!dbOldSmbFile.delete()){
                                        // ERROR FAILED TO DELETE THE OLD `DbFileNameSmb`
                                        RecordState(ERR_BASE_STORAGE + 7);
                                        return;
                                    }
                                }
                                boolean retVal = dbSavedSmbTempFile.renameTo(
                                        new File(
                                                dbPathSmb + File.separator +
                                                        getResourceString(R.string.DbFileNameSmb)
                                        )
                                );
                                if(retVal){
                                    // LOAD THE DATABASE AT `DbFileNameSmb`
                                    StartZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb));

                                }else{
                                    // ERROR FAILED TO RENAME `DbFileNameSmbTemp` TO `DbFileNameSmb`
                                    RecordState(ERR_BASE_STORAGE + 6);
                                    return;
                                }
                            }else{
                                //ERROR CANNOT FIND THE NEWLY DOWNLOADED `DbFileNameSmbTemp`
                                RecordState(ERR_BASE_STORAGE + 8);
                                return;
                            }
                        }

                        @Override
                        public void onProgressTick(int percent) {
                            mProgressBar.setProgress(percent);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(mContext, "Loading the database from SMB failed with: " + e.toString(), Toast.LENGTH_LONG).show();
                            File oldSmbDbFile = new File(getExternalStorage()+ File.separator +
                                    getResourceString(R.string.DirNameApp)+ File.separator +
                                    getResourceString(R.string.DirNameSmb)+ File.separator +
                                    getResourceString(R.string.DbFileNameSmb));
                            if(oldSmbDbFile.exists()){
                                Toast.makeText(mContext, "Loading the OLD CACHED SMB database ..." + e.toString(), Toast.LENGTH_LONG).show();
                                StartZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmbTemp));
                            }
                        }
                    }
            );
            smbReceiveFileFromHost.RunInBackground();
        }

    }

    private void StartZoteroEngine(String dbPath){
        StartZoteroEngine(new File(dbPath));
    }

    private void StartZoteroEngine(File dbFile){
        mAppMem.setZoteroEngine(
                new ZoteroEngine(
                        mActivity,
                        mContext,
                        mLinearLayoutCollections,
                        dbFile.getPath())
        );
        mAppMem.getZoteroEngine().GuiCollections();
    }

    private int CheckPreferencesSmb(){
        String serverIp = mAppMem.getStorageSmbServerIp();
        String serverUser = mAppMem.getStorageSmbServerUsername();
        String serverPass = mAppMem.getStorageSmbServerPassword();

        if(serverUser.isEmpty()) return ERR_BASE_PREFERENCES;
        if(serverPass.isEmpty()) return ERR_BASE_PREFERENCES+1;
        if(serverIp.isEmpty()) return ERR_BASE_PREFERENCES+2;

        return 0;
    }

    private int CheckTheDatabases(){
        boolean isLocal = mAppMem.getStorageModeIsLocal();
        if(isLocal){
            File localDb = new File(
                    getExternalStorage() + File.separator +
                            getResourceString(R.string.DirNameApp) + File.separator +
                            getResourceString(R.string.DirNameLocal) + File.separator +
                            getResourceString(R.string.DbFileNameLocal)
            );
            if(!localDb.exists()) return ERR_BASE_STORAGE + 5;
        } else {

        }
        return 0;
    }

    private int CheckTheAppDirectories(){
        if(!isStorageReadyLocal()) return ERR_BASE_STORAGE;
        File baseStorage = getExternalStorage();

        File appDir = new File(baseStorage, getResourceString(R.string.DirNameApp));
        if(!CreateDirectory(appDir)) return ERR_BASE_STORAGE+1;

        File localDbDir = new File(appDir, getResourceString(R.string.DirNameLocal));
        if(!CreateDirectory(localDbDir)) return ERR_BASE_STORAGE+2;

        File pendingDir = new File(appDir, getResourceString(R.string.DirNamePending));
        if(!CreateDirectory(pendingDir)) return ERR_BASE_STORAGE+3;

        File SmbDbDir = new File(appDir, getResourceString(R.string.DirNameSmb));
        if(!CreateDirectory(SmbDbDir)) return ERR_BASE_STORAGE+4;

        return 0;
    }

    private String getResourceString(int id){
        return mContext.getResources().getString(id);
    }

    private boolean isStorageReadyLocal(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    private File getExternalStorage(){
        return Environment.getExternalStorageDirectory();
    }

    private boolean CreateDirectory(File baseDir, String dirName){
        File folder = new File(
                Environment.getExternalStorageDirectory() +
                File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    private boolean CreateDirectory(String baseDir, String dirName){
        File folder = new File(new File(baseDir) + File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    private boolean CreateDirectory(File dir){
        boolean retVal = true;
        if (!dir.exists()) {
            retVal = dir.mkdirs();
        }
        return retVal;
    }

    public static Vector<String> TranslateReturnCode(int retVal){
        String msgBase, msgDetailed = "";
        int baseError = retVal / 100;
        switch (baseError){
            case 0: {
                msgBase = "Others";
                break;
            }
            case ERR_BASE_STORAGE/100: {
                msgBase = "External Storage";
                break;
            }
            case ERR_BASE_PREFERENCES/100: {
                msgBase = "Preferences";
                break;
            }
            default:{
                msgBase = "Unknown Base.";
                break;
            }
        }

        if(baseError == ERR_BASE_STORAGE/100)
            switch (retVal%ERR_BASE_STORAGE){
                case 0: {
                    msgDetailed = "The external storage has invalid state (not mounted).";
                    break;
                }
                case 1: {
                    msgDetailed = "Failed to create the app directory.";
                    break;
                }
                case 2: {
                    msgDetailed = "Failed to create the app/local directory.";
                    break;
                }
                case 3: {
                    msgDetailed = "Failed to create the app/pending directory.";
                    break;
                }
                case 4: {
                    msgDetailed = "Failed to create the app/smb directory.";
                    break;
                }
                case 5: {
                    msgDetailed = "The operation mode is set to Local but the database does not exist at app/local.";
                    break;
                }
                case 6: {
                    msgDetailed = "Failed to rename `DbFileNameSmbTemp` to `DbFileNameSmb`.";
                    break;
                }
                case 7: {
                    msgDetailed = "Failed to delete the old `DbFileNameSmb`.";
                    break;
                }
                case 8: {
                    msgDetailed = "Failed to find the newly downloaded `DbFileNameSmbTemp`.";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }

        if(baseError == ERR_BASE_PREFERENCES/100)
            switch (retVal%ERR_BASE_PREFERENCES){
                case 0: {
                    msgDetailed = "Empty username for the SMB server.";
                    break;
                }
                case 1: {
                    msgDetailed = "Empty password for the SMB server.";
                    break;
                }
                case 2: {
                    msgDetailed = "Empty IP address for the SMB server.";
                    break;
                }
                case 3: {
                    msgDetailed = "Malformed IP address for the SMB server.";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }
        if(baseError == ERR_BASE_PERMISSIONS/100)
            switch (retVal%ERR_BASE_PREFERENCES){
                case 0: {
                    msgDetailed = "Need ALL FILES permission (ANDROID 11).";
                    break;
                }
                default:{
                    msgDetailed = "Unknown Details.";
                    break;
                }
            }


        Vector<String> vec = new Vector<String>();
        vec.add(msgBase);
        vec.add(msgDetailed);
        return vec;
    }
}
