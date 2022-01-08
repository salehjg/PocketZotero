package io.github.salehjg.pocketzotero.mainactivity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.nikartm.button.FitButton;
import com.google.android.material.navigation.NavigationView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.Preparation;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.fragments.about.AboutFragment;
import io.github.salehjg.pocketzotero.fragments.main.MainFragment;
import io.github.salehjg.pocketzotero.fragments.settings.SettingsFragment;
import io.github.salehjg.pocketzotero.fragments.status.StatusFragment;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainActivityRev1 extends AppCompatActivity {

    // User Interface
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private FitButton mImageButtonMain, mImageButtonSettings, mImageButtonAbout, mImageButtonStatus;
    private LinearLayout mLinearLayoutCollections;

    // State Keys
    private static final String STATE_FRAGMENT_MAIN = "STATE.KEY.mainFragment";
    private static final String STATE_FRAGMENT_SETTINGS = "STATE.KEY.settingsFragment";
    private static final String STATE_FRAGMENT_ABOUT = "STATE.KEY.aboutFragment";
    private static final String STATE_FRAGMENT_STATUS = "STATE.KEY.statusFragment";
    private static final String STATE_DATA_SELECTED_COLLECTION = "STATE.KEY.selectedCollection";
    private static final String STATE_DATA_SELECTED_ITEM = "STATE.KEY.selectedItem";
    private static final String STATE_DATA_SELECTED_ITEM_DETAILED = "STATE.KEY.selectedItemDetailed";
    private static final String STATE_DATA_ITEMS = "STATE.KEY.items";
    private static final String STATE_DATA_TITLES = "STATE.KEY.titles";
    private static final String STATE_DATA_TYPES = "STATE.KEY.types";
    private static final String STATE_DATA_IDS = "STATE.KEY.ids";

    // States
    private MainFragment mFragmentMain;
    private SettingsFragment mFragmentSettings;
    private AboutFragment mFragmentAbout;
    private StatusFragment mFragmentStatus;
    private Collection mDataSelectedCollection;
    private CollectionItem mDataSelectedItem;
    private ItemDetailed mDataSelectedItemDetailed;
    private Vector<CollectionItem> mDataItems;
    private Vector<String> mDataTitles;
    private Vector<Integer> mDataTypes;
    private Vector<Integer> mDataIds;

    // Misc
    private boolean mDoubleBackToExitPressedOnce = false;
    private Boolean mTwoPane;
    private AppMem mAppMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppMem = (AppMem) (getApplicationContext());
        FragmentManager fm = this.getSupportFragmentManager();

        if(savedInstanceState==null){ // This is the first run.
            mFragmentMain = MainFragment.newInstance(null,null,null,null,null);
            mFragmentSettings = SettingsFragment.newInstance();
            mFragmentAbout = AboutFragment.newInstance();
            mFragmentStatus = StatusFragment.newInstance();
            mDataSelectedCollection = null;
            mDataSelectedItem = null;
            mDataSelectedItemDetailed = null;
            mDataItems = null;
            mDataTitles = null;
            mDataTypes = null;
            mDataIds = null;
        }else{
            mFragmentMain = (MainFragment) fm.getFragment(savedInstanceState, STATE_FRAGMENT_MAIN);
            if(mFragmentMain==null) mFragmentMain = MainFragment.newInstance(null,null,null,null,null);
            mFragmentSettings = (SettingsFragment) fm.getFragment(savedInstanceState, STATE_FRAGMENT_SETTINGS);
            if(mFragmentSettings==null) mFragmentSettings = SettingsFragment.newInstance();
            mFragmentAbout = (AboutFragment) fm.getFragment(savedInstanceState, STATE_FRAGMENT_ABOUT);
            if(mFragmentAbout==null) mFragmentAbout = AboutFragment.newInstance();
            mFragmentStatus = (StatusFragment) fm.getFragment(savedInstanceState, STATE_FRAGMENT_STATUS);
            if(mFragmentStatus==null) mFragmentStatus = StatusFragment.newInstance();
            mDataSelectedCollection = (Collection) savedInstanceState.getSerializable(STATE_DATA_SELECTED_COLLECTION);
            mDataSelectedItem = (CollectionItem) savedInstanceState.getSerializable(STATE_DATA_SELECTED_ITEM);
            mDataSelectedItemDetailed = (ItemDetailed) savedInstanceState.getSerializable(STATE_DATA_SELECTED_ITEM_DETAILED);
            mDataItems = (Vector<CollectionItem>) savedInstanceState.getSerializable(STATE_DATA_ITEMS);
            mDataTitles = (Vector<String>) savedInstanceState.getSerializable(STATE_DATA_TITLES);
            mDataTypes = (Vector<Integer>) savedInstanceState.getSerializable(STATE_DATA_TYPES);
            mDataIds = (Vector<Integer>) savedInstanceState.getSerializable(STATE_DATA_IDS);
        }

        //------------------------------------------------------------------------------------------
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.activitymain_drawer) != null){
            mTwoPane = false;
            mDrawerLayout = findViewById(R.id.activitymain_drawer);
            setUpNavDrawer();
        }else{
            mTwoPane = true;
        }
        mNavigationView = findViewById(R.id.activitymain_navview);
        mImageButtonMain = findViewById(R.id.activitymain_btn_main);
        mImageButtonSettings = findViewById(R.id.activitymain_btn_settings);
        mImageButtonAbout = findViewById(R.id.activitymain_btn_about);
        mImageButtonStatus = findViewById(R.id.activitymain_btn_status);
        mImageButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(mFragmentMain);
            }
        });
        mImageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(mFragmentSettings);
            }
        });
        mImageButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(mFragmentAbout);
            }
        });
        mImageButtonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(mFragmentStatus);
            }
        });

        mLinearLayoutCollections = findViewById(R.id.activitymain_collections_linearlayout);

        List<String> permissions = new Vector<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.INTERNET);
        if (SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        PermissionX.init(this)
                .permissions(permissions)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "These permissions are required for PocketZotero to function correctly.","OK", "CANCEL");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            if(savedInstanceState==null) {
                                runStartupSequence();
                            }else{
                                mAppMem.getPreparation().getGuiCollectionsLast(mLinearLayoutCollections);
                            }
                        } else {
                            String msg = "These permissions are denied: " + deniedList.toString();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            mAppMem.recordStatusSingle(msg);
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getSupportFragmentManager();
        if(mFragmentMain.isAdded()) fm.putFragment(outState, STATE_FRAGMENT_MAIN, mFragmentMain);
        if(mFragmentSettings.isAdded()) fm.putFragment(outState, STATE_FRAGMENT_SETTINGS, mFragmentSettings);
        if(mFragmentAbout.isAdded()) fm.putFragment(outState, STATE_FRAGMENT_ABOUT, mFragmentAbout);
        if(mFragmentStatus.isAdded()) fm.putFragment(outState, STATE_FRAGMENT_STATUS, mFragmentStatus);

        outState.putSerializable(STATE_DATA_SELECTED_COLLECTION, mDataSelectedCollection);
        outState.putSerializable(STATE_DATA_SELECTED_ITEM, mDataSelectedItem);
        outState.putSerializable(STATE_DATA_SELECTED_ITEM_DETAILED, mDataSelectedItemDetailed);
        outState.putSerializable(STATE_DATA_ITEMS, mDataItems);
        outState.putSerializable(STATE_DATA_TITLES, mDataTitles);
        outState.putSerializable(STATE_DATA_TYPES, mDataTypes);
        outState.putSerializable(STATE_DATA_IDS, mDataIds);
    }

    private void setUpNavDrawer(){
        if(mToolbar != null){
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private void setUpToolbar(){
        mToolbar = findViewById(R.id.toolbar);
        if(mToolbar!= null){
            setSupportActionBar(mToolbar);
        }
    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activitymain_contentarea, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void runStartupSequence(){
        mAppMem.getPreparation().startupSequence(getApplication(), this, mLinearLayoutCollections, new Preparation.Listeners() {
            @Override
            public void onCollectionSelected(Collection selectedCollectionObject, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> ids, Vector<Integer> types) {
                mDataSelectedCollection = selectedCollectionObject;
                mDataItems = items;
                mDataTitles = titles;
                mDataTypes = types;
                mDataIds = ids;
                mFragmentMain.updateData(mDataItems, mDataTitles, mDataTypes, mDataIds);
            }
        });
    }

    public void processSelectedCollectionItem(CollectionItem selectedItem){
        mDataSelectedItem = selectedItem;
        mDataSelectedItemDetailed = mAppMem.getPreparation().getDetailsForItemId(mDataSelectedCollection, mDataSelectedItem);
        mFragmentMain.updateData(mDataSelectedItemDetailed);
    }
}