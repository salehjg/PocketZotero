package io.github.salehjg.pocketzotero.fragments.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.MainActivityRev1;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;

public class MainItemsFragment extends Fragment {
    // User Interface
    private RecyclerView mRecyclerViewItems;
    private RecyclerAdapterItems mRecyclerAdapterItems;
    private LinearLayoutManager mRecyclerLayoutManager;

    // State Keys
    private static final String STATE_RECYCLER_STATE = "STATE.KEY.recyclerLayoutMngrState";
    private static final String STATE_DATA_ITEMS = "STATE.KEY.items";
    private static final String STATE_DATA_TITLES = "STATE.KEY.titles";
    private static final String STATE_DATA_TYPES = "STATE.KEY.types";
    private static final String STATE_DATA_IDS = "STATE.KEY.ids";

    // States
    private Vector<CollectionItem> mDataItems;
    private Vector<String> mDataItemsTitles;
    private Vector<Integer> mDataItemsTypes;
    private Vector<Integer> mDataItemsIds;

    // Fragment Arguments

    public MainItemsFragment() {
        // Required empty public constructor
    }

    public static MainItemsFragment newInstance() {
        MainItemsFragment fragment = new MainItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewItems = view.findViewById(R.id.fragmainitems_recycler);
        mRecyclerLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerViewItems.setLayoutManager(mRecyclerLayoutManager);
        mRecyclerAdapterItems =  new RecyclerAdapterItems(
                requireContext(),
                mDataItems,
                mDataItemsTitles,
                mDataItemsTypes,
                mDataItemsIds);
        mRecyclerAdapterItems.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerAdapterItems.setClickListener(new RecyclerAdapterItems.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MainFragment mainFragment = (MainFragment) requireParentFragment();
                MainActivityRev1 mainActivity = (MainActivityRev1) mainFragment.requireActivity();
                CollectionItem selectedItem = mRecyclerAdapterItems.getDataCollectionItem(position);
                mainActivity.processSelectedCollectionItem(selectedItem);
            }
        });

        mRecyclerViewItems.setAdapter(mRecyclerAdapterItems);
        if(savedInstanceState!=null){
            mDataItems = (Vector<CollectionItem>)savedInstanceState.getSerializable(STATE_DATA_ITEMS);
            mDataItemsTitles = (Vector<String>)savedInstanceState.getSerializable(STATE_DATA_TITLES);
            mDataItemsTypes = (Vector<Integer>)savedInstanceState.getSerializable(STATE_DATA_TYPES);
            mDataItemsIds = (Vector<Integer>)savedInstanceState.getSerializable(STATE_DATA_IDS);
            Parcelable state = savedInstanceState.getParcelable(STATE_RECYCLER_STATE);
            mRecyclerLayoutManager.onRestoreInstanceState(state);
        }
        showData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_items, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RECYCLER_STATE, mRecyclerLayoutManager.onSaveInstanceState());
        outState.putSerializable(STATE_DATA_ITEMS, mDataItems);
        outState.putSerializable(STATE_DATA_TITLES, mDataItemsTitles);
        outState.putSerializable(STATE_DATA_TYPES, mDataItemsTypes);
        outState.putSerializable(STATE_DATA_IDS, mDataItemsIds);
    }

    public void updateData(Vector<CollectionItem> items, Vector<String> titles, Vector<Integer> types, Vector<Integer> ids){
        mDataItems = items;
        mDataItemsTitles = titles;
        mDataItemsTypes = types;
        mDataItemsIds = ids;
        showData();
    }

    private void showData(){
        mRecyclerAdapterItems.setItems(mDataItems);
        mRecyclerAdapterItems.setItemsTitles(mDataItemsTitles);
        mRecyclerAdapterItems.setItemsTypes(mDataItemsTypes);
        mRecyclerAdapterItems.setItemsIds(mDataItemsIds);
        mRecyclerAdapterItems.notifyDataSetChanged();
    }

}