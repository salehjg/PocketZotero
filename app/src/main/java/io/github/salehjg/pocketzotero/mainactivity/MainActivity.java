package io.github.salehjg.pocketzotero.mainactivity;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.nikartm.button.FitButton;
import com.google.android.material.navigation.NavigationView;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import java.io.File;
import java.util.List;
import java.util.Vector;
import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.Preparation;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.fragments.about.AboutFragment;
import io.github.salehjg.pocketzotero.fragments.main.MainFragment;
import io.github.salehjg.pocketzotero.fragments.settings.SettingsFragment;
import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbSendFileToHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;

public class MainActivity extends AppCompatActivity {
    static Boolean mTwoPane;
    Toolbar mToolbar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;
    FitButton btnMain, btnSettings, btnAbout;
    LinearLayout linearLayoutCollections;
    ProgressBar mProgressBar;
    Preparation mPreparation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();

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
        ((AppMem)getApplication()).setProgressBar(mProgressBar);

        btnMain = findViewById(R.id.activitymain_btn_main);
        btnSettings = findViewById(R.id.activitymain_btn_settings);
        btnAbout = findViewById(R.id.activitymain_btn_about);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFragment(new MainFragment());
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFragment(new SettingsFragment());
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowFragment(new AboutFragment());
            }
        });

        linearLayoutCollections = findViewById(R.id.activitymain_collections_linearlayout);

        List<String> permissions = new Vector<>();
        if (SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.INTERNET);
        }else{
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.INTERNET);
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
                            StartStartupSequence();
                        } else {
                            Toast.makeText(getApplicationContext(), "These permissions are denied: " + deniedList.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
        SmbSendFileToHost smbSendFileToHost = new SmbSendFileToHost(
                new SmbServerInfo("fooname", "xxxxx", "xxxxxx", "192.168.1.7"),
                "/storage/emulated/0/PocketZotero/zotero.sqlite",
                "Test1/zotero-from-android.sqlite",
                true,
                new SmbSendFileToHost.Listener() {
                    @Override
                    public void onFinished() {
                        Toast.makeText(getApplicationContext(), "copy: FINISHED", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        Toast.makeText(getApplicationContext(), "copy: " + percent, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), "copy: ERROR" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        smbSendFileToHost.RunInBackground();*/


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

    private void StartStartupSequence(){
        // Forced = true : force to re-run the sequence even if it has been run before. (inc. downloading db, decoding collections, ...)
        // Forced = false: in case the sequence has been run before, re-use the data. This is useful for situations like display rotation, dynamic ui, ...
        ((AppMem)getApplication()).getPreparation().StartupSequence(getApplication(), this, linearLayoutCollections, false);
    }

    private void ShowFragment(Fragment fragment){
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
}