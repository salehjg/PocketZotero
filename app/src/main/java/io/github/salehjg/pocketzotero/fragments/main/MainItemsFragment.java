package io.github.salehjg.pocketzotero.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainItemsFragment extends Fragment {

    private ViewPager2 viewPager;
    private RecyclerView recyclerViewItems;
    private RecyclerAdapterItems recyclerAdapterItems;

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

        recyclerViewItems = view.findViewById(R.id.fragmainitems_recycler);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerAdapterItems = ((AppMem) getActivity().getApplication()).getRecyclerAdapterItems();
        recyclerAdapterItems.setClickListener(new RecyclerAdapterItems.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Show the details of the selected Item of the recyclerView for CollectionItems
                CollectionItem item = recyclerAdapterItems.getDataCollectionItem(position);
                ZoteroEngine engine = ((AppMem) getActivity().getApplication()).getZoteroEngine();
                if(engine.getSelectedCollection()!=null){
                    ItemDetailed itemDetailed = engine.getDetailsForItemId(engine.getSelectedCollection(), item);
                    ((AppMem) getActivity().getApplication()).setSelectedItemDetailed(itemDetailed);
                }
                ((AppMem) getActivity().getApplication()).getViewPager().setCurrentItem(1);
            }
        });

        recyclerViewItems.setAdapter(recyclerAdapterItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_items, container, false);
    }
}