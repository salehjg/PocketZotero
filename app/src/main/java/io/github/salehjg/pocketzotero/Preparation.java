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
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class Preparation {
    private AppMem mAppMem;
    private Context mContext;
    private Activity mActivity;
    private Listeners mListeners;
    private ZoteroEngine mZoteroEngine;
    private int mStartupSequenceStatus;

    public interface Listeners{
        void onCollectionSelected(Collection selectedCollectionObject, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> ids, Vector<Integer> types);
        void onStartupSequenceCompleted(int statusCode);
    }

    public Preparation(){
    }

    public void startupSequence(
            Application application,
            Activity mainActivity,
            LinearLayout linearLayoutCollections,
            Listeners listeners){
        mAppMem = (AppMem) application;
        mContext = application.getApplicationContext();
        mActivity = mainActivity;
        mListeners = listeners;

        // The permission requests are done in the MainActivity.
        // So if we are here, the permissions are granted.
        startupSequencePermissionGranted(linearLayoutCollections);
    }

    public void getGuiCollectionsLast(LinearLayout linearLayoutCollections){
        mZoteroEngine.getGuiCollectionsLast(linearLayoutCollections);
    }

    public void startupSequencePermissionGranted(LinearLayout linearLayoutCollections){
        mAppMem.createProgressDialog(mActivity, false, false, "Running the startup sequence ...", null);
        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(mStartupSequenceStatus = checkTheAppDirectories());
        if(mStartupSequenceStatus!=0) {
            mAppMem.closeProgressDialog();
            mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
            return;
        }

        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(mStartupSequenceStatus = checkTheDatabases());
        if(mStartupSequenceStatus!=0) {
            mAppMem.closeProgressDialog();
            mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
            return;
        }

        // -----------------------------------------------------------------------------------------
        mAppMem.recordStatusSingle(mStartupSequenceStatus = checkPreferencesSmb());
        if(mStartupSequenceStatus!=0) {
            mAppMem.closeProgressDialog();
            mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
            return;
        }

        // -----------------------------------------------------------------------------------------
        if(mAppMem.getStorageModeIsLocalScoped()){
            // LOAD THE DATABASE AT `DbFileNameLocal`
            String dbPathLocal = AppDirs.getPredefinedPrivateStorageBase(mContext)+ File.separator +
                    getResourceString(R.string.PrivateDirNameLocal);
            startZoteroEngine(dbPathLocal + File.separator + getResourceString(R.string.DbFileNameLocal), linearLayoutCollections);
            mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE+11);
            mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
            mAppMem.closeProgressDialog();
        }else{
            String serverIp = mAppMem.getStorageSmbServerIp();
            String serverUser = mAppMem.getStorageSmbServerUsername();
            String serverPass = mAppMem.getStorageSmbServerPassword();

            String dbPathSmb = AppDirs.getPredefinedPrivateStorageBase(mContext)+ File.separator +
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
                                        mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE + 7);
                                        mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
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
                                    mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE+10);
                                    mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
                                    mAppMem.closeProgressDialog();

                                    // Since we are managed to connect to the host, lets process all the pending attachments
                                    processPendingAttachments();
                                }else{
                                    // ERROR FAILED TO RENAME `DbFileNameSmbTemp` TO `DbFileNameSmb`
                                    mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE + 6);
                                    mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
                                    mAppMem.closeProgressDialog();
                                    return;
                                }
                            }else{
                                //ERROR CANNOT FIND THE NEWLY DOWNLOADED `DbFileNameSmbTemp`
                                mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE + 8);
                                mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
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
                                    AppDirs.getPredefinedPrivateStorageBase(mContext),
                                    getResourceString(R.string.PrivateDirNameSmb)+ File.separator +
                                    getResourceString(R.string.DbFileNameSmb)
                            );
                            if(oldSmbDbFile.exists()){
                                startZoteroEngine(dbPathSmb + File.separator + getResourceString(R.string.DbFileNameSmb), linearLayoutCollections);
                                mAppMem.recordStatusSingle(mStartupSequenceStatus = STATUS_BASE_STORAGE+9);
                                mListeners.onStartupSequenceCompleted(mStartupSequenceStatus);
                                mAppMem.closeProgressDialog();
                            }
                        }
                    }
            );
            smbReceiveFileFromHost.runInBackground();
        }

    }

    public int getStartupSequenceStatus(){
        return mStartupSequenceStatus;
    }

    private void startZoteroEngine(String dbPath, LinearLayout linearLayoutCollections){
        startZoteroEngine(new File(dbPath), linearLayoutCollections);
    }

    private void startZoteroEngine(File dbFile, LinearLayout linearLayoutCollections){
        mZoteroEngine =
                new ZoteroEngine(
                        mActivity,
                        mContext,
                        dbFile.getPath(),
                        new ZoteroEngine.Listeners() {
                            @Override
                            public void onCollectionSelected(Collection selectedCollectionObject, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> ids, Vector<Integer> types) {
                                if(mListeners!=null){
                                    mListeners.onCollectionSelected(selectedCollectionObject, items, titles, ids, types);
                                }
                            }
                        })
        ;
        mZoteroEngine.getGuiCollections(linearLayoutCollections);
    }

    public void processPendingAttachments(){
        File[] pendingsDir = AppDirs.getPredefinedPrivateStoragePending(mContext).listFiles();
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
                    filePathsOnHost.add(AppDirs.getSharedSmbBase(mContext) + File.separator +
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
                                        if(!AppDirs.deleteDirAndContentAtPrivateBase(mContext, AppDirs.getPredefinedPrivateStorageDirNamePending(mContext), filePendingDirNames.get(i))){
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
                    AppDirs.getPredefinedPrivateStorageBase(mContext) + File.separator +
                            getResourceString(R.string.PrivateDirNameLocal) + File.separator +
                            getResourceString(R.string.DbFileNameLocal)
            );
            if(!localDb.exists()) return STATUS_BASE_STORAGE + 5;
        } else {

        }
        return 0;
    }

    private int checkTheAppDirectories(){
        if(!AppDirs.makeDirAtPrivateBase(mContext, AppDirs.getPredefinedPrivateStorageDirNameLocalScoped(mContext))) return STATUS_BASE_STORAGE +2;
        if(!AppDirs.makeDirAtPrivateBase(mContext, AppDirs.getPredefinedPrivateStorageDirNamePending(mContext))) return STATUS_BASE_STORAGE +3;
        if(!AppDirs.makeDirAtPrivateBase(mContext, AppDirs.getPredefinedPrivateStorageDirNameSharedSmb(mContext))) return STATUS_BASE_STORAGE +4;
        return 0;
    }

    private String getResourceString(int id){
        return mContext.getResources().getString(id);
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        return mZoteroEngine.getDetailsForItemId(parentCollection, collectionItem);
    }

}
