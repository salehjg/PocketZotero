package io.github.salehjg.pocketzotero;

import static android.os.Build.VERSION.SDK_INT;

import static io.github.salehjg.pocketzotero.RecordedStatus.STATUS_BASE_PERMISSIONS;
import static io.github.salehjg.pocketzotero.RecordedStatus.STATUS_BASE_PREFERENCES;
import static io.github.salehjg.pocketzotero.RecordedStatus.STATUS_BASE_STORAGE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Vector;

import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;

public class Preparation {
    private AppMem mAppMem;
    private Context mContext;
    private boolean mIsInitialized;

    private Activity mActivity;
    private ProgressBar mProgressBar;

    public Preparation(){
        mIsInitialized = false;
    }

    public boolean isInitialized(){return mIsInitialized;}

    public void StartupSequence(
            Application application,
            Activity mainActivity,
            LinearLayout linearLayoutCollections,
            boolean forced){
        mAppMem = (AppMem) application;
        mProgressBar = mAppMem.getProgressBar();
        mContext = application.getApplicationContext();
        mActivity = mainActivity;

        if(!mIsInitialized || forced) {
            if (SDK_INT >= 30) {
                if (Environment.isExternalStorageManager()) {
                    StartupSequencePermissionGranted(linearLayoutCollections);
                } else {
                    mAppMem.RecordStatusSingle(STATUS_BASE_PERMISSIONS);
                }
            }
        }else{
            mAppMem.getZoteroEngine().GuiCollectionsLast(linearLayoutCollections);
        }
    }

    public void StartupSequencePermissionGranted(LinearLayout linearLayoutCollections){
        int state;

        // -----------------------------------------------------------------------------------------
        mAppMem.RecordStatusSingle(state = CheckTheAppDirectories());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mAppMem.RecordStatusSingle(state = CheckTheDatabases());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mAppMem.RecordStatusSingle(state = CheckPreferencesSmb());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mIsInitialized = true;
        if(mAppMem.getStorageModeIsLocal()){
            // LOAD THE DATABASE AT `DbFileNameLocal`
            String dbPathLocal = getExternalStorage()+ File.separator +
                    getResourceString(R.string.DirNameApp)+ File.separator +
                    getResourceString(R.string.DirNameLocal);
            StartZoteroEngine(dbPathLocal + File.separator + getResourceString(R.string.DbFileNameLocal), linearLayoutCollections);
            mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE+11);
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
                                        mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE + 7);
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
                                    StartZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb), linearLayoutCollections);
                                    mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE+10);
                                }else{
                                    // ERROR FAILED TO RENAME `DbFileNameSmbTemp` TO `DbFileNameSmb`
                                    mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE + 6);
                                    return;
                                }
                            }else{
                                //ERROR CANNOT FIND THE NEWLY DOWNLOADED `DbFileNameSmbTemp`
                                mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE + 8);
                                return;
                            }
                        }

                        @Override
                        public void onProgressTick(int percent) {
                            mProgressBar.setProgress(percent);
                        }

                        @Override
                        public void onError(Exception e) {
                            String msg = "Failed to download the database over SMB network with error: " + e.toString();
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            mAppMem.RecordStatusSingle(msg);
                            File oldSmbDbFile = new File(getExternalStorage()+ File.separator +
                                    getResourceString(R.string.DirNameApp)+ File.separator +
                                    getResourceString(R.string.DirNameSmb)+ File.separator +
                                    getResourceString(R.string.DbFileNameSmb));
                            if(oldSmbDbFile.exists()){
                                StartZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb), linearLayoutCollections);
                                mAppMem.RecordStatusSingle(STATUS_BASE_STORAGE+9);
                            }
                        }
                    }
            );
            smbReceiveFileFromHost.RunInBackground();
        }

    }

    private void StartZoteroEngine(String dbPath, LinearLayout linearLayoutCollections){
        StartZoteroEngine(new File(dbPath), linearLayoutCollections);
    }

    private void StartZoteroEngine(File dbFile, LinearLayout linearLayoutCollections){
        mAppMem.setZoteroEngine(
                new ZoteroEngine(
                        mActivity,
                        mContext,
                        dbFile.getPath(),
                        new ZoteroEngine.Listeners() {
                            @Override
                            public void onCollectionSelected(Collection selectedCollectionObject, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> ids, Vector<Integer> types) {
                                mAppMem.setSelectedCollection(selectedCollectionObject);
                                RecyclerAdapterItems recyclerAdapterItems = mAppMem.getRecyclerAdapterItems();
                                recyclerAdapterItems.setmItems(items);
                                recyclerAdapterItems.setmItemsTitles(titles);
                                recyclerAdapterItems.setmItemsIDs(ids);
                                recyclerAdapterItems.setmItemsTypes(types);
                                recyclerAdapterItems.notifyDataSetChanged();
                            }
                        })
        );
        mAppMem.getZoteroEngine().GuiCollections(linearLayoutCollections);
    }

    private int CheckPreferencesSmb(){
        boolean isLocal = mAppMem.getStorageModeIsLocal();
        if(!isLocal) {
            String serverIp = mAppMem.getStorageSmbServerIp();
            String serverUser = mAppMem.getStorageSmbServerUsername();
            String serverPass = mAppMem.getStorageSmbServerPassword();

            if (serverUser.isEmpty()) return STATUS_BASE_PREFERENCES;
            if (serverPass.isEmpty()) return STATUS_BASE_PREFERENCES + 1;
            if (serverIp.isEmpty()) return STATUS_BASE_PREFERENCES + 2;
            return 0;
        }
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
            if(!localDb.exists()) return STATUS_BASE_STORAGE + 5;
        } else {

        }
        return 0;
    }

    private int CheckTheAppDirectories(){
        if(!isStorageReadyLocal()) return STATUS_BASE_STORAGE;
        File baseStorage = getExternalStorage();

        File appDir = new File(baseStorage, getResourceString(R.string.DirNameApp));
        if(!CreateDirectory(appDir)) return STATUS_BASE_STORAGE +1;

        File localDbDir = new File(appDir, getResourceString(R.string.DirNameLocal));
        if(!CreateDirectory(localDbDir)) return STATUS_BASE_STORAGE +2;

        File pendingDir = new File(appDir, getResourceString(R.string.DirNamePending));
        if(!CreateDirectory(pendingDir)) return STATUS_BASE_STORAGE +3;

        File SmbDbDir = new File(appDir, getResourceString(R.string.DirNameSmb));
        if(!CreateDirectory(SmbDbDir)) return STATUS_BASE_STORAGE +4;

        return 0;
    }

    public String getResourceString(int id){
        return mContext.getResources().getString(id);
    }

    private boolean isStorageReadyLocal(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    public File getExternalStorage(){
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

    public boolean CreateDirectory(String baseDir, String dirName){
        File folder = new File(new File(baseDir) + File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    public boolean CreateDirectory(File dir){
        boolean retVal = true;
        if (!dir.exists()) {
            retVal = dir.mkdirs();
        }
        return retVal;
    }

    public String GetAppDirPath(){
        File baseStorage = getExternalStorage();
        File appDir = new File(baseStorage, getResourceString(R.string.DirNameApp));
        return appDir.getPath();
    }

    public String GetPendingDirPath(){
        File baseStorage = getExternalStorage();
        File appDir = new File(baseStorage, getResourceString(R.string.DirNameApp));
        File pendingDir = new File(appDir, getResourceString(R.string.DirNamePending));
        return pendingDir.getPath();
    }

    public String GetSmbDataDir(){
        String dbPath = mAppMem.getStorageSmbServerSharedPath();
        return dbPath.substring(0,dbPath.lastIndexOf(File.separator));
    }
}
