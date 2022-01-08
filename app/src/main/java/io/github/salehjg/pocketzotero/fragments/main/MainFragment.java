package io.github.salehjg.pocketzotero.fragments.main;

import android.content.ClipData;
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

import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainFragment extends Fragment {

    // User Interface
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;

    // State Keys
    private static final String STATE_MAIN_ITEMS = "STATE.KEY.mainItems";
    private static final String STATE_ITEM_DETAILED = "STATE.KEY.mainItemDetailed";
    private static final String STATE_SEL_TAB_INDEX = "STATE.KEY.selectedTabIndex";

    // States
    private MainItemsFragment mFragmentMainItems;
    private MainItemDetailedFragment mFragmentMainItemDetailed;
    private int mSelectedTabIndex;

    // Fragment Arguments
    public static final String ARG_ITEM_DETAILED = "ARG.KEY.itemDetailed";
    private ItemDetailed mDataItemDetailed;
    public static final String ARG_ITEMS = "ARG.KEY.items";
    private Vector<CollectionItem> mDataItems;
    public static final String ARG_ITEMS_TITLES = "ARG.KEY.itemsTitles";
    private Vector<String> mDataItemsTitles;
    public static final String ARG_ITEMS_TYPES = "ARG.KEY.itemsTypes";
    private Vector<Integer> mDataItemsTypes;
    public static final String ARG_ITEMS_IDS = "ARG.KEY.itemsIds";
    private Vector<Integer> mDataItemsIds;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(
            Vector<CollectionItem> items,
            Vector<String> titles,
            Vector<Integer> types,
            Vector<Integer> ids,
            ItemDetailed itemDetailed) {
        MainFragment fragment = new MainFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_DETAILED, itemDetailed);
        args.putSerializable(ARG_ITEMS, items);
        args.putSerializable(ARG_ITEMS_TITLES, titles);
        args.putSerializable(ARG_ITEMS_TYPES, types);
        args.putSerializable(ARG_ITEMS_IDS, ids);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDataItemDetailed = (ItemDetailed) getArguments().getSerializable(ARG_ITEM_DETAILED);
            mDataItems = (Vector<CollectionItem>) getArguments().getSerializable(ARG_ITEMS);
            mDataItemsTitles = (Vector<String>) getArguments().getSerializable(ARG_ITEMS_TITLES);
            mDataItemsTypes = (Vector<Integer>) getArguments().getSerializable(ARG_ITEMS_TYPES);
            mDataItemsIds = (Vector<Integer>) getArguments().getSerializable(ARG_ITEMS_IDS);
        }
    }

    public void updateData(ItemDetailed itemDetailed){
        mDataItemDetailed = itemDetailed;
        mViewPager.setCurrentItem(1);
        if(mFragmentMainItemDetailed!=null) mFragmentMainItemDetailed.updateData(mDataItemDetailed);
    }

    public void updateData(Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> types, Vector<Integer> ids){
        mDataItems = items;
        mDataItemsTitles = titles;
        mDataItemsTypes = types;
        mDataItemsIds = ids;
        if(mFragmentMainItems!=null) mFragmentMainItems.updateData(mDataItems, mDataItemsTitles, mDataItemsTypes, mDataItemsIds);
    }

    public void updateData(ItemDetailed itemDetailed, Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> types, Vector<Integer> ids){
        updateData(itemDetailed);
        updateData(items, titles, types, ids);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();

        if(savedInstanceState==null){
            // no prior saved state
            mFragmentMainItems = MainItemsFragment.newInstance();
            mFragmentMainItemDetailed = MainItemDetailedFragment.newInstance();
            mSelectedTabIndex = 0;
        }else{
            mFragmentMainItems = (MainItemsFragment) fm.getFragment(savedInstanceState, STATE_MAIN_ITEMS);
            if(mFragmentMainItems==null) mFragmentMainItems = MainItemsFragment.newInstance();
            mFragmentMainItemDetailed = (MainItemDetailedFragment) fm.getFragment(savedInstanceState, STATE_ITEM_DETAILED);
            if(mFragmentMainItemDetailed==null) mFragmentMainItemDetailed = MainItemDetailedFragment.newInstance();
            mSelectedTabIndex = savedInstanceState.getInt(STATE_SEL_TAB_INDEX);
        }

        //https://stackoverflow.com/a/62062623/8296604
        // get the reference of FrameLayout and TabLayout
        // https://stackoverflow.com/a/58391219/8296604
        // java.lang.IllegalStateException: FragmentManager is already executing transactions
        // Solution : use `getChildFragmentManager` instead of `getParentFragmentManager`
        mViewPager = view.findViewById(R.id.fragmain_viewpager);
        mViewPager.setOffscreenPageLimit(2); // without this, the related fragments won't be added until its really necessary (we don't want that to happen)
        mTabLayout = view.findViewById(R.id.fragmain_tablayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addTab(mTabLayout.newTab().setText("Items"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Details"));
        ViewStateAdapter viewStateAdapter = new ViewStateAdapter(fm, getLifecycle(), mFragmentMainItems, mFragmentMainItemDetailed);
        mViewPager.setAdapter(viewStateAdapter);
        mViewPager.setCurrentItem(mSelectedTabIndex);
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
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fm = getChildFragmentManager();
        if(mFragmentMainItems.isAdded()) fm.putFragment(outState, STATE_MAIN_ITEMS, mFragmentMainItems);
        if(mFragmentMainItemDetailed.isAdded()) fm.putFragment(outState, STATE_ITEM_DETAILED, mFragmentMainItemDetailed);
        outState.putInt(STATE_SEL_TAB_INDEX, mSelectedTabIndex);
    }
}