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

public class MainItemsFragment extends Fragment {

    private RecyclerView recyclerViewItems;
    private RecyclerAdapterItems recyclerAdapterItems;

    public MainItemsFragment() {
        // Required empty public constructor
    }

    public static MainItemsFragment newInstance(String param1, String param2) {
        MainItemsFragment fragment = new MainItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerViewItems = view.findViewById(R.id.fragmainitems_recycler);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerAdapterItems = ((AppMem) getActivity().getApplication()).getRecyclerAdapterItems();
        recyclerAdapterItems.setClickListener(new RecyclerAdapterItems.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

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