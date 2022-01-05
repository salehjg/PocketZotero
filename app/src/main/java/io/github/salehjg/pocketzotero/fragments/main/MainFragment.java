package io.github.salehjg.pocketzotero.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;

public class MainFragment extends Fragment {
    TabLayout mTabLayout;
    ViewPager2 mViewPager;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //https://stackoverflow.com/a/62062623/8296604

        // get the reference of FrameLayout and TabLayout
        // https://stackoverflow.com/a/58391219/8296604
        // java.lang.IllegalStateException: FragmentManager is already executing transactions
        // Solution : use `getChildFragmentManager` instead of `getParentFragmentManager`
        mViewPager = (ViewPager2) view.findViewById(R.id.fragmain_viewpager);
        FragmentManager fm = getChildFragmentManager();
        ViewStateAdapter viewStateAdapter = new ViewStateAdapter(fm, getLifecycle());
        mViewPager.setAdapter(viewStateAdapter);

        mTabLayout = (TabLayout) view.findViewById(R.id.fragmain_tablayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addTab(mTabLayout.newTab().setText("Items"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Details"));

        // perform setOnTabSelectedListener event on TabLayout
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position));
            }
        });

        ((AppMem) requireActivity().getApplication()).setViewPager(mViewPager);
    }

    @Override
    public void onDestroy() {
        ((AppMem) requireActivity().getApplication()).setViewPager(null);
        super.onDestroy();
    }
}