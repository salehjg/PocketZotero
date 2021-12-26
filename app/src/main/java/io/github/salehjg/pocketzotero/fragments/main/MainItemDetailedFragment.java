package io.github.salehjg.pocketzotero.fragments.main;

import static com.simplemobiletools.commons.extensions.ActivityKt.openPathIntent;
import static com.simplemobiletools.commons.extensions.ContextKt.getMediaContentUri;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterAttachments;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterElements;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainItemDetailedFragment extends Fragment {
    ImageView imageView;
    TextView textViewTitle, textViewType, textViewAbstract;
    RecyclerView recyclerViewElements;
    RecyclerAdapterElements recyclerAdapterElements;
    RecyclerView recyclerViewAttachments;
    RecyclerAdapterAttachments recyclerAdapterAttachments;
    
    public MainItemDetailedFragment() {
        // Required empty public constructor
    }
    
    public static MainItemDetailedFragment newInstance() {
        MainItemDetailedFragment fragment = new MainItemDetailedFragment();
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
        return inflater.inflate(R.layout.fragment_main_item_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.fragmainitemdetailed_titleimage);
        textViewType = view.findViewById(R.id.fragmainitemdetailed_itemtype);
        textViewTitle = view.findViewById(R.id.fragmainitemdetailed_itemtitle);
        textViewAbstract = view.findViewById(R.id.fragmainitemdetailed_itemabstract);
        recyclerViewElements = view.findViewById(R.id.fragmainitemdetailed_listelements);
        recyclerViewAttachments = view.findViewById(R.id.fragmainitemdetailed_listattachements);

        // Elements Recycler's Stuff
        recyclerViewElements.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerAdapterElements = new RecyclerAdapterElements(view.getContext(), null, false);
        recyclerAdapterElements.setClickListener(new RecyclerAdapterElements.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ///TODO COPY THE CLICKED FIELD PAIR TO THE CLIPBOARD
            }
        });
        recyclerViewElements.setAdapter(recyclerAdapterElements);

        // Attachments Recycler's Stuff
        LinearLayoutManager layoutManagerAttachments = new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAttachments.setLayoutManager(layoutManagerAttachments);
        recyclerAdapterAttachments = new RecyclerAdapterAttachments(view.getContext(), null);
        recyclerAdapterAttachments.setClickListener(new RecyclerAdapterAttachments.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String fileName = recyclerAdapterAttachments.getDataAttachment(position).getFilePath();
                fileName = fileName.substring(fileName.indexOf("storage:")+8);
                String key = recyclerAdapterAttachments.getDataAttachment(position).getFileKey();
                OpenAttachmentFile(key+"/"+fileName);
                //OpenAttachmentFile(key);
            }
        });
        recyclerAdapterAttachments.setLongClickListener(new RecyclerAdapterAttachments.ItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(
                        view.getContext(),
                        recyclerAdapterAttachments.getDataAttachment(position).getFilePath(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        recyclerViewAttachments.setAdapter(recyclerAdapterAttachments);

        ((AppMem) getActivity().getApplication()).setOnSelectedItemDetailedChangedListener(
                new AppMem.ItemDetailedChangedListener() {
                    @Override
                    public void onItemDetailedChanged(ItemDetailed itemDetailed) {
                        updateGuiItemDetailed(itemDetailed);
                    }
                }
        );

        // To update the GUI when the fragment is instantiated just now.
        updateGuiItemDetailed(((AppMem) getActivity().getApplication()).getSelectedItemDetailed());
    }

    private void updateGuiItemDetailed(ItemDetailed itemDetailed){
        if(itemDetailed==null) return;
        int imgResId = -1;
        switch (itemDetailed.getItemTypeId() ){
            case 33:{//conference
                imgResId = R.drawable.ic_conference;
                break;
            }
            case 2:{//book
                imgResId = R.drawable.ic_book;
                break;
            }
            case 4:{//journal
                imgResId = R.drawable.ic_journal;
                break;
            }
            case 27:{//presentation
                imgResId = R.drawable.ic_presentation;
                break;
            }
            case 7:{//thesis
                imgResId = R.drawable.ic_thesis;
                break;
            }
            default:{
                imgResId = R.drawable.ic_other;
            }
        }
        imageView.setBackgroundResource(imgResId);

        textViewTitle.setText(itemDetailed.getItemTitle());
        textViewType.setText(itemDetailed.getItemType());
        textViewAbstract.setText(itemDetailed.getAbstractTry());

        recyclerAdapterElements.setElements(itemDetailed.getUnifiedElements());
        recyclerAdapterAttachments.setmAttachments(itemDetailed.getItemAttachments());

        recyclerAdapterElements.notifyDataSetChanged();
        recyclerAdapterAttachments.notifyDataSetChanged();
    }

    public void OpenAttachmentFile(String key) {
        File path = new File( getResources().getString(R.string.default_path_dir) + "storage");
        File file = new File(path, key);

        {
            Uri myUri = getMediaContentUri(requireContext(), file.getPath());
            String myUriStr = myUri.getPath();
            if (!myUriStr.contains("/external/file/")) {
                Toast.makeText(
                        requireContext(),
                        "Failed to get a valid URI for android 10+. " +
                                "Restart the device and try again. " +
                                "Be aware that continuing in this state will result in invalid " +
                                "write-access to the file and " +
                                "all of the file modifications by the third-party app will be discarded.",
                        Toast.LENGTH_LONG).show();
            }
        }

        openPathIntent(
                requireActivity(),
                file.getPath(),
                false,
                requireContext().getPackageName(),
                "",
                new HashMap<>(0)
        );
    }

    @Override
    public void onDestroy() {
        ((AppMem) getActivity().getApplication()).setOnSelectedItemDetailedChangedListener(null);
        super.onDestroy();
    }
}