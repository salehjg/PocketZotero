package io.github.salehjg.pocketzotero.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.fragments.about.AboutFragment;
import io.github.salehjg.pocketzotero.fragments.main.MainFragment;
import io.github.salehjg.pocketzotero.fragments.settings.SettingsFragment;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;

public class MainActivity extends AppCompatActivity {
    static Boolean mTwoPane;
    private int mCurrentSelectedPosition = 0;

    Toolbar mToolbar;
    NavigationView mNavigationView;
    DrawerLayout mDrawerLayout;

    ImageButton btnMain, btnSettings, btnAbout;
    LinearLayout linearLayoutCollections;

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

        ((AppMem) this.getApplication()).setRecyclerAdapterItems(
                new RecyclerAdapterItems(
                        this,
                        null,
                        null,
                        null,
                        null)
        );

        ((AppMem) this.getApplication()).setZoteroEngine(
                new ZoteroEngine(
                        this,
                        getApplicationContext(),
                        linearLayoutCollections,
                        ((AppMem) this.getApplication()).getRecyclerAdapterItems(),
                        "")
        );

        ((AppMem) this.getApplication()).getZoteroEngine().GuiCollections();
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