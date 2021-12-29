package io.github.salehjg.pocketzotero;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;

import java.util.List;
import java.util.Vector;
import java.util.logging.ErrorManager;

import io.github.salehjg.pocketzotero.mainactivity.MainActivity;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

// This class is used to hold global stuff in memory.
public class AppMem extends Application {

    private Vector<RecordedStatus> recordedStatuses;
    private ZoteroEngine zoteroEngine;

    private ViewPager2 viewPager;
    private ItemDetailed selectedItemDetailed;
    private ItemDetailedChangedListener selectedItemDetailedChangedListener = null;
    private ProgressBar progressBar;
    private RecyclerAdapterItems recyclerAdapterItems;
    private Collection selectedCollection;
    private Preparation preparation;

    @Override
    public void onCreate() {
        recyclerAdapterItems = new RecyclerAdapterItems(
                getApplicationContext(),
                null,
                null,
                null,
                null);

        preparation = new Preparation();
        recordedStatuses = new Vector<>();
        super.onCreate();
    }

    public void RecordStatusSingle(int statusCode){
        recordedStatuses.add(new RecordedStatus(statusCode));
    }

    public void RecordStatusSingle(RecordedStatus status){
        recordedStatuses.add(status);
    }

    public Vector<RecordedStatus> getAllRecordedStatuses(){
        return recordedStatuses;
    }

    public Vector<String> getAllRecordedStatusesStrings(){
        Vector<String> s = new Vector<>();
        for(RecordedStatus r:recordedStatuses){
            s.add(r.toString());
        }
        return s;
    }

    public Preparation getPreparation() {
        return preparation;
    }

    public Collection getSelectedCollection() {
        return selectedCollection;
    }

    public void setSelectedCollection(Collection selectedCollection) {
        this.selectedCollection = selectedCollection;
    }

    public RecyclerAdapterItems getRecyclerAdapterItems() {
        return recyclerAdapterItems;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ZoteroEngine getZoteroEngine() {
        return zoteroEngine;
    }

    public void setZoteroEngine(ZoteroEngine zoteroEngine) {
        this.zoteroEngine = zoteroEngine;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public ItemDetailed getSelectedItemDetailed() {
        return selectedItemDetailed;
    }

    public void setSelectedItemDetailed(ItemDetailed selectedItemDetailed) {
        this.selectedItemDetailed = selectedItemDetailed;
        if(this.selectedItemDetailedChangedListener!=null) {
            this.selectedItemDetailedChangedListener.onItemDetailedChanged(this.selectedItemDetailed);
        }
    }

    public interface ItemDetailedChangedListener{
        void onItemDetailedChanged(ItemDetailed itemDetailed);
    }

    public void setOnSelectedItemDetailedChangedListener(ItemDetailedChangedListener selectedItemDetailedChangedListener) {
        this.selectedItemDetailedChangedListener = selectedItemDetailedChangedListener;
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

    public void setStorageModeIsLocal(boolean isLocal){
        // local vs SMB shared
        SharedPreferences.Editor writer = getPreferenceEditor();
        writer.putBoolean(getString(R.string.prefKey_storageModeIsLocal_bool), isLocal);
        writer.apply();
    }

    public boolean getStorageModeIsLocal(){
        // local vs SMB shared
        SharedPreferences reader = getPreference();
        return reader.getBoolean(getString(R.string.prefKey_storageModeIsLocal_bool), true);
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

}
