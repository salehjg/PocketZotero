package io.github.salehjg.pocketzotero.mainactivity;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.github.nikartm.button.FitButton;
import com.google.android.material.navigation.NavigationView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import java.util.List;
import java.util.Vector;
import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.fragments.about.AboutFragment;
import io.github.salehjg.pocketzotero.fragments.main.MainFragment;
import io.github.salehjg.pocketzotero.fragments.settings.settingsFragment;
import io.github.salehjg.pocketzotero.fragments.status.StatusFragment;
import io.github.salehjg.pocketzotero.fragments.welcome.WelcomeFragment;


public class MainActivity extends AppCompatActivity {
    static Boolean mTwoPane;
    Toolbar mToolbar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    FitButton mImageButtonMain, mImageButtonSettings, mImageButtonAbout, mImageButtonStatus;
    LinearLayout mLinearLayoutCollections;
    ProgressBar mProgressBar;
    AppMem mAppMem;
    boolean mDoubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
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
                mDoubleBackToExitPressedOnce =false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setUpToolbar();

        mAppMem = (AppMem)getApplication();

        if(findViewById(R.id.activitymain_drawer) != null){
            mTwoPane = false;
            mDrawerLayout = findViewById(R.id.activitymain_drawer);
            setUpNavDrawer();
        }else{
            mTwoPane = true;
        }

        mNavigationView = findViewById(R.id.activitymain_navview);

        mProgressBar = findViewById(R.id.activitymain_progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mAppMem.setProgressBar(mProgressBar);

        mImageButtonMain = findViewById(R.id.activitymain_btn_main);
        mImageButtonSettings = findViewById(R.id.activitymain_btn_settings);
        mImageButtonAbout = findViewById(R.id.activitymain_btn_about);
        mImageButtonStatus = findViewById(R.id.activitymain_btn_status);
        mImageButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new MainFragment());
            }
        });
        mImageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new settingsFragment());
            }
        });
        mImageButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new AboutFragment());
            }
        });
        mImageButtonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new StatusFragment());
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
                        scope.showRequestReasonDialog(deniedList, "These permissions are required for PcoketZotero to function correctly.","OK", "CANCEL");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            runStartupSequence();
                        } else {
                            String msg = "These permissions are denied: " + deniedList.toString();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            mAppMem.recordStatusSingle(msg);
                        }
                    }
                });

        if(!mAppMem.getPreparation().isInitialized()){
            showFragment(new WelcomeFragment());
        }else{
            showFragment(new MainFragment());
        }

        /*
        ((AppMem) this.getApplication()).setZoteroEngine(
                new ZoteroEngine(
                        this,
                        getApplicationContext(),
                        linearLayoutCollections,
                        "")
        );

        ((AppMem) this.getApplication()).getZoteroEngine().GuiCollections();
        */

        /*
        SmbReceiveFileFromHost smbReceiveFileFromHost = new SmbReceiveFileFromHost(
                new SmbServerInfo("fooname", "xxxxx", "xxxxxxx", "192.168.1.7"),
                "Test1/zotero-from-android.sqlite" ,
                "/storage/emulated/0/PocketZotero/testReceived.sqlite",
                new SmbReceiveFileFromHost.Listener() {
                    @Override
                    public void onFinished() {
                        Toast.makeText(getApplicationContext(), "copyR: FINISHED", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        Toast.makeText(getApplicationContext(), "copyR: " + percent, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), "copyR: ERROR" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        smbReceiveFileFromHost.RunInBackground();

         */
    }

    private void runStartupSequence(){
        // Forced = true : force to re-run the sequence even if it has been run before. (inc. downloading db, decoding collections, ...)
        // Forced = false: in case the sequence has been run before, re-use the data. This is useful for situations like display rotation, dynamic ui, ...
        mAppMem.getPreparation().startupSequence(getApplication(), this, mLinearLayoutCollections, false);

    }

    private void showFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activitymain_contentarea, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Toast.makeText(getApplicationContext(), "on Pause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "on Resume", Toast.LENGTH_LONG).show();
    }
}