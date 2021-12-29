package io.github.salehjg.pocketzotero.fragments.status;

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

public class StatusFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerAdapterStatus recyclerAdapterStatus;
    private AppMem appMem;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
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
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appMem = ((AppMem)requireActivity().getApplication());
        recyclerView = view.findViewById(R.id.fragstatus_recycler);
        recyclerAdapterStatus = new RecyclerAdapterStatus(view.getContext(), appMem.getAllRecordedStatusesStrings());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerAdapterStatus.setClickListener(new RecyclerAdapterStatus.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(recyclerAdapterStatus);
    }
}