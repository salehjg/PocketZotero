package io.github.salehjg.pocketzotero.fragments.main;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.nikartm.button.FitButton;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.MainActivityRev1;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.OneTimeEvent;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.SharedViewModel;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.ViewModelFactory;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;

public class MainItemsFragment extends Fragment {
    // User Interface
    private RecyclerView mRecyclerViewItems;
    private RecyclerAdapterItems mRecyclerAdapterItems;
    private LinearLayoutManager mRecyclerLayoutManager;
    private FitButton mBtnToolbarDrawer, mBtnToolbarNewItem;

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

    // Misc
    private SharedViewModel mSharedViewModel;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireActivity().getApplication(), 1)).get(SharedViewModel.class);

        mBtnToolbarDrawer = view.findViewById(R.id.fragmainitems_btn_drawer);
        mBtnToolbarNewItem = view.findViewById(R.id.fragmainitems_btn_itemnew);

        mBtnToolbarDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSharedViewModel.getMainActivityOpenDrawer().setValue(new OneTimeEvent());
            }
        });
        mBtnToolbarNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRecyclerViewItems = view.findViewById(R.id.fragmainitems_recycler);
        mRecyclerLayoutManager = new LinearLayoutManager(view.getContext());
        if(savedInstanceState!=null){
            mDataItems = (Vector<CollectionItem>)savedInstanceState.getSerializable(STATE_DATA_ITEMS);
            mDataItemsTitles = (Vector<String>)savedInstanceState.getSerializable(STATE_DATA_TITLES);
            mDataItemsTypes = (Vector<Integer>)savedInstanceState.getSerializable(STATE_DATA_TYPES);
            mDataItemsIds = (Vector<Integer>)savedInstanceState.getSerializable(STATE_DATA_IDS);
            Parcelable state = savedInstanceState.getParcelable(STATE_RECYCLER_STATE);
            mRecyclerLayoutManager.onRestoreInstanceState(state);
        }
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
                //MainActivityRev1 mainActivity = (MainActivityRev1) requireActivity();
                CollectionItem selectedItem = mRecyclerAdapterItems.getDataCollectionItem(position);
                //mainActivity.processSelectedCollectionItem(selectedItem);
                mSharedViewModel.getSelectedItem().setValue(selectedItem);
            }
        });
        mRecyclerViewItems.setAdapter(mRecyclerAdapterItems);
        showData();

        mSharedViewModel.getRecyclerItemsItems().observe(getViewLifecycleOwner(), new Observer<Vector<CollectionItem>>() {
            @Override
            public void onChanged(Vector<CollectionItem> collectionItems) {
                mDataItems = collectionItems;
                mRecyclerAdapterItems.setItems(mDataItems);
                mRecyclerAdapterItems.notifyDataSetChanged();
            }
        });
        mSharedViewModel.getRecyclerItemsTitles().observe(getViewLifecycleOwner(), new Observer<Vector<String>>() {
            @Override
            public void onChanged(Vector<String> titles) {
                mDataItemsTitles = titles;
                mRecyclerAdapterItems.setItemsTitles(mDataItemsTitles);
                mRecyclerAdapterItems.notifyDataSetChanged();
            }
        });
        mSharedViewModel.getRecyclerItemsTypes().observe(getViewLifecycleOwner(), new Observer<Vector<Integer>>() {
            @Override
            public void onChanged(Vector<Integer> types) {
                mDataItemsTypes = types;
                mRecyclerAdapterItems.setItemsTypes(mDataItemsTypes);
                mRecyclerAdapterItems.notifyDataSetChanged();
            }
        });
        mSharedViewModel.getRecyclerItemsIds().observe(getViewLifecycleOwner(), new Observer<Vector<Integer>>() {
            @Override
            public void onChanged(Vector<Integer> ids) {
                mDataItemsIds = ids;
                mRecyclerAdapterItems.setItemsIds(mDataItemsIds);
                mRecyclerAdapterItems.notifyDataSetChanged();
            }
        });
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

    private void showData(){
        if(mRecyclerAdapterItems==null) return; // bypass if the fragment is not loaded yet.
        mRecyclerAdapterItems.setItems(mDataItems);
        mRecyclerAdapterItems.setItemsTitles(mDataItemsTitles);
        mRecyclerAdapterItems.setItemsTypes(mDataItemsTypes);
        mRecyclerAdapterItems.setItemsIds(mDataItemsIds);
        mRecyclerAdapterItems.notifyDataSetChanged();
    }

}