package io.github.salehjg.pocketzotero.fragments.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private RecyclerView mRecyclerViewItems;
    private RecyclerAdapterItems mRecyclerAdapterItems;
    private AppMem mAppMem;

    public MainItemsFragment() {
        // Required empty public constructor
    }

    public static MainItemsFragment newInstance() {
        return new MainItemsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppMem = (AppMem) requireActivity().getApplication();

        mRecyclerViewItems = view.findViewById(R.id.fragmainitems_recycler);
        mRecyclerViewItems.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mRecyclerAdapterItems = mAppMem.getRecyclerAdapterItems();
        mRecyclerAdapterItems.setClickListener(new RecyclerAdapterItems.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Show the details of the selected Item of the recyclerView for CollectionItems
                CollectionItem item = mRecyclerAdapterItems.getDataCollectionItem(position);
                ZoteroEngine engine = mAppMem.getZoteroEngine();
                if(mAppMem.getSelectedCollection()!=null){
                    ItemDetailed itemDetailed = engine.getDetailsForItemId(mAppMem.getSelectedCollection(), item);
                    mAppMem.setSelectedItemDetailed(itemDetailed);
                }
                ((AppMem) requireActivity().getApplication()).getViewPager().setCurrentItem(1);
            }
        });

        mRecyclerViewItems.setAdapter(mRecyclerAdapterItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_items, container, false);
    }
}