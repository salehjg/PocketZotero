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
    private RecyclerView mRecyclerView;
    private RecyclerAdapterStatus mRecyclerAdapterStatus;
    private AppMem mAppMem;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance() {
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

        mAppMem = ((AppMem)requireActivity().getApplication());
        mRecyclerView = view.findViewById(R.id.fragstatus_recycler);
        mRecyclerAdapterStatus = new RecyclerAdapterStatus(view.getContext(), mAppMem.getAllRecordedStatusesStrings());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerAdapterStatus.setClickListener(new RecyclerAdapterStatus.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapterStatus);
    }
}