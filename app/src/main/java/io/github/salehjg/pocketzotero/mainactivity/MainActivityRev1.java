package io.github.salehjg.pocketzotero.mainactivity;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikartm.button.FitButton;
import com.google.android.material.navigation.NavigationView;
import com.lmntrx.android.library.livin.missme.ProgressDialog;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;

import java.util.List;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.Internals;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.RecordedStatus;
import io.github.salehjg.pocketzotero.fragments.about.AboutFragment;
import io.github.salehjg.pocketzotero.fragments.main.MainFragment;
import io.github.salehjg.pocketzotero.fragments.settings.SettingsFragment;
import io.github.salehjg.pocketzotero.fragments.status.StatusFragment;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.OneTimeEvent;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.SharedViewModel;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.ViewModelFactory;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

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
    private SharedViewModel mSharedViewModel;
    private boolean isThisTheFirstTimeAfterReset;

    @Override
    public void onBackPressed() {
        if(mAppMem.isProgressDialogCreated()) {
            ProgressDialog progressDialog = mAppMem.getProgressDialog();
            progressDialog.onBackPressed(new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    /// TODO: this is nasty and makes the code dependant on MainActivity.
                    /// TODO: unfortunately, the library does not support buttons on the dialog.
                    if(mAppMem.getProgressDialogListener()!=null)
                        mAppMem.getProgressDialogListener().onCanceled();
                    MainActivityRev1.super.onBackPressed();
                    return null;
                }
            });
        }else {
            if (mDoubleBackToExitPressedOnce) {
                //this.finishAffinity();
                System.exit(0);
                return;
            }

            this.mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDoubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppMem = (AppMem) (getApplicationContext());
        mSharedViewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication(), 1)).get(SharedViewModel.class);

        if(savedInstanceState==null){ // This is the first run.
            mDataSelectedCollection = null;
            mDataSelectedItem = null;
            mDataSelectedItemDetailed = null;
            mDataItems = null;
            mDataTitles = null;
            mDataTypes = null;
            mDataIds = null;
            isThisTheFirstTimeAfterReset = false;
        }else{
            mDataSelectedCollection = (Collection) savedInstanceState.getSerializable(STATE_DATA_SELECTED_COLLECTION);
            mDataSelectedItem = (CollectionItem) savedInstanceState.getSerializable(STATE_DATA_SELECTED_ITEM);
            mDataSelectedItemDetailed = (ItemDetailed) savedInstanceState.getSerializable(STATE_DATA_SELECTED_ITEM_DETAILED);
            mDataItems = (Vector<CollectionItem>) savedInstanceState.getSerializable(STATE_DATA_ITEMS);
            mDataTitles = (Vector<String>) savedInstanceState.getSerializable(STATE_DATA_TITLES);
            mDataTypes = (Vector<Integer>) savedInstanceState.getSerializable(STATE_DATA_TYPES);
            mDataIds = (Vector<Integer>) savedInstanceState.getSerializable(STATE_DATA_IDS);
            isThisTheFirstTimeAfterReset = true;
        }

        //------------------------------------------------------------------------------------------
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
                showFragment(MainFragment.newInstance(), STATE_FRAGMENT_MAIN);
            }
        });
        mImageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(SettingsFragment.newInstance(), STATE_FRAGMENT_SETTINGS);
            }
        });
        mImageButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(AboutFragment.newInstance(), STATE_FRAGMENT_ABOUT);
            }
        });
        mImageButtonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(StatusFragment.newInstance(), STATE_FRAGMENT_STATUS);
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

        mSharedViewModel.getSelectedItem().observe(this, new Observer<CollectionItem>() {
            @Override
            public void onChanged(CollectionItem selectedItem) {
                mDataSelectedItem = selectedItem;
                mDataSelectedItemDetailed = mAppMem.getPreparation().getDetailsForItemId(mDataSelectedCollection, mDataSelectedItem);
                if(!isThisTheFirstTimeAfterReset){
                    mSharedViewModel.getViewPagerSelectedTab().setValue(1);
                }else{
                    isThisTheFirstTimeAfterReset = false;
                }
                mSharedViewModel.getSelectedItemDetailed().setValue(mDataSelectedItemDetailed);
            }
        });

        mSharedViewModel.getMainActivityOpenDrawer().observe(this, new Observer<OneTimeEvent>() {
            @Override
            public void onChanged(OneTimeEvent oneTimeEvent) {
                if(oneTimeEvent.receive()){
                    if(mDrawerLayout!=null)mDrawerLayout.open();
                }
            }
        });
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

    private void showFragment(Fragment fragment, String tag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activitymain_contentarea, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commitAllowingStateLoss();
    }

    private void runStartupSequence(){
        mAppMem.getPreparation().startupSequence(getApplication(), this, mLinearLayoutCollections, new Internals.Listeners() {
            @Override
            public void onCollectionSelected(Collection selectedCollectionObject, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> ids, Vector<Integer> types) {
                mDataSelectedCollection = selectedCollectionObject;
                mDataItems = items;
                mDataTitles = titles;
                mDataTypes = types;
                mDataIds = ids;

                mSharedViewModel.getViewPagerSelectedTab().setValue(0);
                mSharedViewModel.getRecyclerItemsItems().setValue(mDataItems);
                mSharedViewModel.getRecyclerItemsTitles().setValue(mDataTitles);
                mSharedViewModel.getRecyclerItemsTypes().setValue(mDataTypes);
                mSharedViewModel.getRecyclerItemsIds().setValue(mDataIds);
            }

            @Override
            public void onStartupSequenceCompleted(int statusCode) {
                if(mAppMem.getTheFirstLaunch()){
                    mAppMem.setTheFirstLaunch(false);
                    showFragment(AboutFragment.newInstance(), STATE_FRAGMENT_ABOUT);
                }else{
                    if(
                            (statusCode == RecordedStatus.STATUS_BASE_STORAGE+9) ||
                            (statusCode == RecordedStatus.STATUS_BASE_STORAGE+10) ||
                            (statusCode == RecordedStatus.STATUS_BASE_STORAGE+11)
                    ) {
                        showFragment(MainFragment.newInstance(), STATE_FRAGMENT_MAIN);
                    }else{
                        showFragment(StatusFragment.newInstance(), STATE_FRAGMENT_STATUS);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_DATA_SELECTED_COLLECTION, mDataSelectedCollection);
        outState.putSerializable(STATE_DATA_SELECTED_ITEM, mDataSelectedItem);
        outState.putSerializable(STATE_DATA_SELECTED_ITEM_DETAILED, mDataSelectedItemDetailed);
        outState.putSerializable(STATE_DATA_ITEMS, mDataItems);
        outState.putSerializable(STATE_DATA_TITLES, mDataTitles);
        outState.putSerializable(STATE_DATA_TYPES, mDataTypes);
        outState.putSerializable(STATE_DATA_IDS, mDataIds);
    }
}