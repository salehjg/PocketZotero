package io.github.salehjg.pocketzotero;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.lmntrx.android.library.livin.missme.ProgressDialog;

import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

// This class is used to hold global stuff in memory.
public class AppMem extends Application implements Application.ActivityLifecycleCallbacks {

    private Vector<RecordedStatus> mRecordedStatuses;

    private ViewPager2 mViewPager;
    private ItemDetailed mSelectedItemDetailed;
    private ItemDetailedChangedListener mSelectedItemDetailedChangedListener = null;
    private Preparation mPreparation;

    private ProgressDialog mProgressDialog;
    private boolean mProgressDialogCreated;
    private ProgressDialogListener mProgressDialogListener;

    private int mActivityReferences;
    private boolean mIsActivityChangingConfigurations;
    private boolean mIsThisAppsStartup;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreparation = new Preparation();
        mRecordedStatuses = new Vector<>();
        mActivityReferences = 0;
        mIsActivityChangingConfigurations = false;
        mIsThisAppsStartup = false;
        registerActivityLifecycleCallbacks(this);
    }

    public interface ProgressDialogListener{
        public void onCanceled();
    }
    public void createProgressDialog(Activity activity, boolean isIndeterminate, boolean isCancellable, String msg, ProgressDialogListener listener){
        if(!mProgressDialogCreated) {
            mProgressDialogCreated = true;
            mProgressDialogListener = listener;
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage(msg);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(0);
            mProgressDialog.setIndeterminate(isIndeterminate);
            mProgressDialog.setCancelable(isCancellable);
            mProgressDialog.show();
        }else{
            closeProgressDialog();
            createProgressDialog(activity, isIndeterminate, isCancellable, msg, listener);
        }
    }

    public void closeProgressDialog(){
        if(mProgressDialogCreated) {
            mProgressDialogCreated = false;
            mProgressDialog.dismiss();
            mProgressDialogListener = null;
        }
    }

    public void setProgressDialogValue(int percent){
        if(mProgressDialogCreated) {
            mProgressDialog.setProgress(percent);
        }
    }

    public void setProgressDialogMessage(String msg){
        if(mProgressDialogCreated) {
            mProgressDialog.setMessage(msg);
        }
    }

    public boolean isProgressDialogCreated(){
        return mProgressDialogCreated;
    }

    public ProgressDialog getProgressDialog(){
        return mProgressDialog;
    }

    public ProgressDialogListener getProgressDialogListener(){
        if(mProgressDialogCreated)
            return mProgressDialogListener;
        else
            return null;
    }

    public void recordStatusSingle(int statusCode){
        mRecordedStatuses.add(new RecordedStatus(statusCode));
    }

    public void recordStatusSingle(String customStatusMessage){
        mRecordedStatuses.add(new RecordedStatus(customStatusMessage));
    }

    public void recordStatusSingle(RecordedStatus status){
        mRecordedStatuses.add(status);
    }

    public Vector<RecordedStatus> getAllRecordedStatuses(){
        return mRecordedStatuses;
    }

    public Vector<String> getAllRecordedStatusesStrings(){
        Vector<String> s = new Vector<>();
        for(RecordedStatus r: mRecordedStatuses){
            s.add(r.toString());
        }
        return s;
    }

    public Preparation getPreparation() {
        return mPreparation;
    }

    public ViewPager2 getViewPager() {
        return mViewPager;
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.mViewPager = viewPager;
    }

    public ItemDetailed getSelectedItemDetailed() {
        return mSelectedItemDetailed;
    }

    public void setSelectedItemDetailed(ItemDetailed selectedItemDetailed) {
        this.mSelectedItemDetailed = selectedItemDetailed;
        if(this.mSelectedItemDetailedChangedListener !=null) {
            this.mSelectedItemDetailedChangedListener.onItemDetailedChanged(this.mSelectedItemDetailed);
        }
    }

    public interface ItemDetailedChangedListener{
        void onItemDetailedChanged(ItemDetailed itemDetailed);
    }

    public void setOnSelectedItemDetailedChangedListener(ItemDetailedChangedListener selectedItemDetailedChangedListener) {
        this.mSelectedItemDetailedChangedListener = selectedItemDetailedChangedListener;
    }

    private SharedPreferences.Editor getPreferenceEditor(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.DefaultPreferenceFile),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        return editor;
    }

    private SharedPreferences getPreference(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.DefaultPreferenceFile),
                Context.MODE_PRIVATE);
        return sharedPref;
    }

    public void setTheFirstLaunch(boolean val){
        // is this the first launch?
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putBoolean(getString(R.string.prefKey_isThisTheFirstLaunch_bool), val);
        writer.apply();
    }

    public boolean getTheFirstLaunch(){
        // is this the first launch?
        SharedPreferences reader = getPreference();
        return reader.getBoolean(getString(R.string.prefKey_isThisTheFirstLaunch_bool), true);
    }

    public void setStorageMode(boolean isLocalScoped, boolean isSharedSmb){
        // localExt vs localScoped vs SMB shared
        int storageMode =
                        isLocalScoped & !isSharedSmb ? 1:
                        !isLocalScoped & isSharedSmb ? 2:
                        0;

        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putInt(getString(R.string.prefKey_storageMode_int), storageMode);
        writer.apply();
    }

    public int getStorageMode(){
        // local vs SMB shared
        SharedPreferences reader = getPreference();
        return reader.getInt(getString(R.string.prefKey_storageMode_int), 2);
    }

    public boolean getStorageModeIsLocalScoped(){
        SharedPreferences reader = getPreference();
        return reader.getInt(getString(R.string.prefKey_storageMode_int), 2) == 1;
    }

    public boolean getStorageModeIsSharedSmb(){
        SharedPreferences reader = getPreference();
        return reader.getInt(getString(R.string.prefKey_storageMode_int), 2) == 2;
    }

    public void setStorageSmbServerIp(String ip){
        // SMB Server IP String
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putString(getString(R.string.prefKey_storageSmbServerIp_string), ip);
        writer.apply();
    }

    public String getStorageSmbServerIp(){
        // SMB Server IP String
        SharedPreferences reader = getPreference();
        return reader.getString(getString(R.string.prefKey_storageSmbServerIp_string), "");
    }

    public void setStorageSmbServerSharedPath(String path){
        // SMB Server Shared Path
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putString(getString(R.string.prefKey_storageSmbServerSharedPath_string), path);
        writer.apply();
    }

    public String getStorageSmbServerSharedPath(){
        // SMB Server Shared Path
        SharedPreferences reader = getPreference();
        return reader.getString(getString(R.string.prefKey_storageSmbServerSharedPath_string), "");
    }

    public void setStorageSmbServerUsername(String username){
        // SMB Server Username
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putString(getString(R.string.prefKey_storageSmbServerUsername_string), username);
        writer.apply();
    }

    public String getStorageSmbServerUsername(){
        // SMB Server Username
        SharedPreferences reader = getPreference();
        return reader.getString(getString(R.string.prefKey_storageSmbServerUsername_string), "");
    }

    public void setStorageSmbServerPassword(String password){
        // SMB Server Password
        /// TODO: STORE THE PASSWORD SAFELY WITH ITS PROPER PROCEDURE!
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putString(getString(R.string.prefKey_storageSmbServerPassword_string), password);
        writer.apply();
    }

    public String getStorageSmbServerPassword(){
        // SMB Server Password
        /// TODO: STORE THE PASSWORD SAFELY WITH ITS PROPER PROCEDURE!
        SharedPreferences reader = getPreference();
        return reader.getString(getString(R.string.prefKey_storageSmbServerPassword_string), "");
    }

    // ========================= App Life Cycle Trackers Below =====================================

    @Override
    public void onActivityStarted(Activity activity) {
        if (++mActivityReferences == 1 && !mIsActivityChangingConfigurations) {
            if(!mIsThisAppsStartup){
                // App enters foreground and its not the app's launch.
                Toast.makeText(getApplicationContext(), "Processing the pending attachments", Toast.LENGTH_SHORT).show();
                mPreparation.processPendingAttachments(1000);
            }else{
                mIsThisAppsStartup = false; // bypass if this callback is fired cuz of the app's startup.
            }
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        mIsActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--mActivityReferences == 0 && !mIsActivityChangingConfigurations) {
            // App enters background
            //Toast.makeText(getApplicationContext(), "Is in background.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if(!mIsActivityChangingConfigurations) {
            //Toast.makeText(getApplicationContext(), "Activity created.", Toast.LENGTH_LONG).show();
            mIsThisAppsStartup = true;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { }

    @Override
    public void onActivityPaused(@NonNull Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { }

}
