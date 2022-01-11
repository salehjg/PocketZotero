package io.github.salehjg.pocketzotero.fragments.main;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.MainActivityRev1;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.SharedViewModel;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.ViewModelFactory;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainFragment extends Fragment {

    // User Interface
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;

    // State Keys
    private static final String STATE_SEL_TAB_INDEX = "STATE.KEY.selectedTabIndex";

    // States
    private int mSelectedTabIndex;

    // Fragment Arguments

    // Misc
    private ViewStateAdapter mViewStateAdapter;
    private SharedViewModel mSharedViewModel;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTheSelectedTab(int index){
        mViewPager.setCurrentItem(index);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireActivity().getApplication(), 1)).get(SharedViewModel.class);


        if(savedInstanceState==null){
            mSelectedTabIndex = 0;
        }else{
            mSelectedTabIndex = savedInstanceState.getInt(STATE_SEL_TAB_INDEX);
        }

        //https://stackoverflow.com/a/62062623/8296604
        // get the reference of FrameLayout and TabLayout
        // https://stackoverflow.com/a/58391219/8296604
        // java.lang.IllegalStateException: FragmentManager is already executing transactions
        // Solution : use `getChildFragmentManager` instead of `getParentFragmentManager`

        mViewStateAdapter = new ViewStateAdapter(this);
        mViewPager = view.findViewById(R.id.fragmain_viewpager);
        mTabLayout = view.findViewById(R.id.fragmain_tablayout);
        mViewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT); // without this, the related fragments won't be added until its really necessary (we don't want that to happen)
        //mViewPager.setSaveEnabled(false);
        //mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPager.setAdapter(mViewStateAdapter);

        new TabLayoutMediator(mTabLayout, mViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.
                        tab.setText(((ViewStateAdapter)(Objects.requireNonNull(mViewPager.getAdapter()))).mFragmentNames[position]);
                    }
                }
        ).attach();

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSelectedTabIndex = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mSharedViewModel.getViewPagerSelectedTab().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {
                if(position!=-1) {
                    mSelectedTabIndex = position;
                    mViewPager.setCurrentItem(mSelectedTabIndex);
                    mSharedViewModel.getViewPagerSelectedTab().setValue(-1);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SEL_TAB_INDEX, mSelectedTabIndex);
    }
}