package io.github.salehjg.pocketzotero;

import static android.os.Build.VERSION.SDK_INT;

import static io.github.salehjg.pocketzotero.RecordedStatus.STATUS_BASE_PREFERENCES;
import static io.github.salehjg.pocketzotero.RecordedStatus.STATUS_BASE_STORAGE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbSendMultipleToHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;

public class Preparation {
    private AppMem mAppMem;
    private Context mContext;
    private boolean mIsInitialized;
    private Activity mActivity;

    public Preparation(){
        mIsInitialized = false;
    }

    public boolean isInitialized(){return mIsInitialized;}

    public void startupSequence(
            Application application,
            Activity mainActivity,
            LinearLayout linearLayoutCollections,
            boolean forced){
        mAppMem = (AppMem) application;
        mContext = application.getApplicationContext();
        mActivity = mainActivity;

        if(!mIsInitialized || forced) {
            // The permission requests are done in the MainActivity.
            // So if we are here, the permissions are granted.
            startupSequencePermissionGranted(linearLayoutCollections);
        }else{
            mAppMem.getZoteroEngine().getGuiCollectionsLast(linearLayoutCollections);
        }
    }

    public void startupSequencePermissionGranted(LinearLayout linearLayoutCollections){
        int state;

        mAppMem.createProgressDialog(mActivity, false, false, "Running the startup sequence ...", null);
        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(state = checkTheAppDirectories());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(state = checkTheDatabases());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(state = checkPreferencesSmb());
        if(state!=0) return;

        // -----------------------------------------------------------------------------------------
        mIsInitialized = true;
        if(mAppMem.getStorageModeIsLocalScoped()){
            // LOAD THE DATABASE AT `DbFileNameLocal`
            String dbPathLocal = getPredefinedPrivateStorageBase()+ File.separator +
                    getResourceString(R.string.PrivateDirNameLocal);
            startZoteroEngine(dbPathLocal + File.separator + getResourceString(R.string.DbFileNameLocal), linearLayoutCollections);
            mAppMem.recordStatusSingle(STATUS_BASE_STORAGE+11);
            mAppMem.closeProgressDialog();
        }else{
            String serverIp = mAppMem.getStorageSmbServerIp();
            String serverUser = mAppMem.getStorageSmbServerUsername();
            String serverPass = mAppMem.getStorageSmbServerPassword();

            String dbPathSmb = getPredefinedPrivateStorageBase()+ File.separator +
                    getResourceString(R.string.PrivateDirNameSmb);

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
                                        mAppMem.recordStatusSingle(STATUS_BASE_STORAGE + 7);
                                        mAppMem.closeProgressDialog();
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
                                    startZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb), linearLayoutCollections);
                                    mAppMem.recordStatusSingle(STATUS_BASE_STORAGE+10);
                                    mAppMem.closeProgressDialog();

                                    // Since we are managed to connect to the host, lets process all the pending attachments
                                    processPendingAttachments();
                                }else{
                                    // ERROR FAILED TO RENAME `DbFileNameSmbTemp` TO `DbFileNameSmb`
                                    mAppMem.recordStatusSingle(STATUS_BASE_STORAGE + 6);
                                    mAppMem.closeProgressDialog();
                                    return;
                                }
                            }else{
                                //ERROR CANNOT FIND THE NEWLY DOWNLOADED `DbFileNameSmbTemp`
                                mAppMem.recordStatusSingle(STATUS_BASE_STORAGE + 8);
                                mAppMem.closeProgressDialog();
                                return;
                            }
                        }

                        @Override
                        public void onProgressTick(int percent) {
                            mAppMem.setProgressDialogValue(percent);
                        }

                        @Override
                        public void onError(Exception e) {
                            String msg = "Failed to download the database over SMB network with error: " + e.toString();
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            mAppMem.recordStatusSingle(msg);
                            File oldSmbDbFile = new File(
                                    getPredefinedPrivateStorageBase(),
                                    getResourceString(R.string.PrivateDirNameSmb)+ File.separator +
                                    getResourceString(R.string.DbFileNameSmb)
                            );
                            if(oldSmbDbFile.exists()){
                                startZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb), linearLayoutCollections);
                                mAppMem.recordStatusSingle(STATUS_BASE_STORAGE+9);
                                mAppMem.closeProgressDialog();
                            }
                        }
                    }
            );
            smbReceiveFileFromHost.runInBackground();
        }

    }

    private void startZoteroEngine(String dbPath, LinearLayout linearLayoutCollections){
        startZoteroEngine(new File(dbPath), linearLayoutCollections);
    }

    private void startZoteroEngine(File dbFile, LinearLayout linearLayoutCollections){
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
        mAppMem.getZoteroEngine().getGuiCollections(linearLayoutCollections);
    }

    public void processPendingAttachments(){
        File[] pendingsDir = getPredefinedPrivateStoragePending().listFiles();
        Gson gson = new Gson();
        if(pendingsDir!=null) {
            Vector<String> filePathsToSend = new Vector<>();
            Vector<String> filePendingDirNames = new Vector<>();
            Vector<String> filePathsOnHost = new Vector<>();

            mAppMem.createProgressDialog(mActivity, false,false, "Processing the pending attachments ...", null);

            for (File pendingAttachmentDir : pendingsDir) {
                try {
                    if (!pendingAttachmentDir.isDirectory()) {
                        throw new Exception("Malformed pending attachment directory with key: " + pendingAttachmentDir.getName() + ". ");
                    }
                    File gsonFile = new File(pendingAttachmentDir, "attachment.json");
                    if (!gsonFile.exists()) {
                        throw new Exception("Missing the json file for the pending attachment with key: " + pendingAttachmentDir.getName() + ". ");
                    }
                    JsonReader reader = new JsonReader(new FileReader(gsonFile));
                    ItemAttachment itemAttachment = gson.fromJson(reader, new TypeToken<ItemAttachment>() {
                    }.getType());

                    filePathsToSend.add(pendingAttachmentDir + File.separator + itemAttachment.extractFileName());
                    filePendingDirNames.add(pendingAttachmentDir.getName());
                    filePathsOnHost.add(getSharedSmbBase() + File.separator +
                            itemAttachment.extractStorageDirName() + File.separator +
                            itemAttachment.getFileKey() + File.separator +
                            itemAttachment.extractFileName()
                    );
                }catch (Exception e){
                    mAppMem.recordStatusSingle(e.toString());
                }
            }

            if(filePathsToSend.size() != filePathsOnHost.size()){
                mAppMem.recordStatusSingle(STATUS_BASE_STORAGE+14);
            }

            if(filePathsToSend.size()>0) {
                mAppMem.recordStatusSingle("Pending attachments found: " + filePathsToSend.size());

                SmbSendMultipleToHost smbSendMultipleToHost = new SmbSendMultipleToHost(
                        new SmbServerInfo("foo", mAppMem.getStorageSmbServerUsername(), mAppMem.getStorageSmbServerPassword(), mAppMem.getStorageSmbServerIp()),
                        filePathsToSend,
                        filePathsOnHost,
                        true,
                        1,
                        new SmbSendMultipleToHost.Listener() {
                            @Override
                            public void onFinished(boolean wasTotalSuccess, Vector<Boolean> hasSucceeded, Vector<String> filePathsToSend, Vector<String> filePathsOnHost, Vector<Boolean> overWrite) {
                                Toast.makeText(mContext, "Finished processing pendings" + ", wasTotalSuccess=" + wasTotalSuccess, Toast.LENGTH_LONG).show();
                                int count = hasSucceeded.size();
                                for(int i=0; i<count; i++){
                                    if(hasSucceeded.get(i)){
                                        if(!deleteDirAndContentAtPrivateBase(getPredefinedPrivateStorageDirNamePending(), filePendingDirNames.get(i))){
                                            mAppMem.recordStatusSingle("Failed to remove the processed pending attachment: " + filePendingDirNames.get(i));
                                        }
                                    }
                                }
                                mAppMem.closeProgressDialog();
                            }

                            @Override
                            public void onProgressTick(int percent) {
                                mAppMem.setProgressDialogValue(percent);
                            }

                            @Override
                            public void onError(Exception e) {
                                mAppMem.recordStatusSingle("Pending attachment processing: " + e.toString());
                            }
                        }
                );
                smbSendMultipleToHost.enqueueToRunAll();

            }else{
                mAppMem.closeProgressDialog();
            }
        }
    }

    private int checkPreferencesSmb(){
        boolean isLocal = mAppMem.getStorageModeIsLocalScoped();
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

    private int checkTheDatabases(){
        boolean isLocal = mAppMem.getStorageModeIsLocalScoped();
        if(isLocal){
            File localDb = new File(
                    getPredefinedPrivateStorageBase() + File.separator +
                            getResourceString(R.string.PrivateDirNameLocal) + File.separator +
                            getResourceString(R.string.DbFileNameLocal)
            );
            if(!localDb.exists()) return STATUS_BASE_STORAGE + 5;
        } else {

        }
        return 0;
    }

    private int checkTheAppDirectories(){
        if(!makeDirAtPrivateBase(getPredefinedPrivateStorageDirNameLocalScoped())) return STATUS_BASE_STORAGE +2;
        if(!makeDirAtPrivateBase(getPredefinedPrivateStorageDirNamePending())) return STATUS_BASE_STORAGE +3;
        if(!makeDirAtPrivateBase(getPredefinedPrivateStorageDirNameSharedSmb())) return STATUS_BASE_STORAGE +4;
        return 0;
    }

    public String getResourceString(int id){
        return mContext.getResources().getString(id);
    }

    public File getPredefinedPrivateStorageBase(){
        // getCacheDir : the data could be lost if the device is running low on storage.
        // getFilesDir : the data will only be lost if the user uninstalls the application.
        // getExternalFilesDir(null): the data will persist even after app's uninstallation.
        return mContext.getFilesDir();
    }

    public File getPredefinedPrivateStorageLocalScoped(){
        return new File(getPredefinedPrivateStorageBase(), getResourceString(R.string.PrivateDirNameLocal));
    }

    public File getPredefinedPrivateStorageSharedSmb(){
        return new File(getPredefinedPrivateStorageBase(), getResourceString(R.string.PrivateDirNameSmb));
    }

    public File getPredefinedPrivateStoragePending(){
        return new File(getPredefinedPrivateStorageBase(), getResourceString(R.string.PrivateDirNamePending));
    }

    public String getPredefinedPrivateStorageDirNameLocalScoped(){
        return getResourceString(R.string.PrivateDirNameLocal);
    }

    public String getPredefinedPrivateStorageDirNameSharedSmb(){
        return getResourceString(R.string.PrivateDirNameSmb);
    }

    public String getPredefinedPrivateStorageDirNamePending(){
        return getResourceString(R.string.PrivateDirNamePending);
    }

    public String getSharedSmbBase(){
        String dbPath = mAppMem.getStorageSmbServerSharedPath();
        return dbPath.substring(0, dbPath.lastIndexOf(File.separator));
    }

    private boolean makeDirAtPrivateBase(String dirName){
        File folder = new File(getPredefinedPrivateStorageBase(), dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    public boolean makeDirAtPrivateBase(String parentDir, String dirName){
        File folder = new File(getPredefinedPrivateStorageBase().getPath() + File.separator + parentDir + File.separator + dirName);
        boolean retVal = true;
        if (!folder.exists()) {
            retVal = folder.mkdirs();
        }
        return retVal;
    }

    public boolean deleteDirAndContentAtPrivateBase(String parentDir, String dirName) {
        File folder = new File(getPredefinedPrivateStorageBase().getPath() + File.separator + parentDir + File.separator + dirName);
        return _deleteDirAndContentAbsPath(folder);
    }

    private boolean _deleteDirAndContentAbsPath(File folder){
        File[] files = folder.listFiles();
        boolean result = true;
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    result &= _deleteDirAndContentAbsPath(f);
                } else {
                    result &= f.delete();
                }
            }
        }
        result &= folder.delete();
        return result;
    }

    public boolean makeDirAtPrivateBase(File dir){
        return makeDirAtPrivateBase(dir.getPath());
    }


}
