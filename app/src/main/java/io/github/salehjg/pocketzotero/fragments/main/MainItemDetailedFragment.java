package io.github.salehjg.pocketzotero.fragments.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.RecordedStatus;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterAttachments;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterElements;
import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class MainItemDetailedFragment extends Fragment {
    ImageView imageView;
    TextView textViewTitle, textViewType, textViewAbstract;
    RecyclerView recyclerViewElements;
    RecyclerAdapterElements recyclerAdapterElements;
    RecyclerView recyclerViewAttachments;
    RecyclerAdapterAttachments recyclerAdapterAttachments;
    AppMem mAppMem;
    
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

        mAppMem = (AppMem)(requireActivity().getApplication());

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
                ItemAttachment attachment =  recyclerAdapterAttachments.getDataAttachment(position);
                OpenAttachmentFile(attachment);
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

        ((AppMem) requireActivity().getApplication()).setOnSelectedItemDetailedChangedListener(
                new AppMem.ItemDetailedChangedListener() {
                    @Override
                    public void onItemDetailedChanged(ItemDetailed itemDetailed) {
                        updateGuiItemDetailed(itemDetailed);
                    }
                }
        );

        // To update the GUI when the fragment is instantiated just now.
        updateGuiItemDetailed(((AppMem) requireActivity().getApplication()).getSelectedItemDetailed());
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

    ActivityResultLauncher<Intent> mIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //if(result.getResultCode() == Activity.RESULT_OK){}
        }
    });

    private void OpenAttachmentFile(ItemAttachment attachment){
        boolean isLocal = mAppMem.getStorageModeIsLocalScoped();
        if(isLocal){
            OpenLocalAttachmentFile(attachment);
        }else{
            OpenSmbAttachmentFile(attachment);
        }
    }

    private void OpenSmbAttachmentFile(ItemAttachment attachment){
        File dirPendingAbs = mAppMem.getPreparation().getPredefinedPrivateStoragePending();

        String extractedFileName = attachment.ExtractFileName();

        if(!
            mAppMem.getPreparation().MakeDirAtPrivateBase(
                    mAppMem.getPreparation().getPredefinedPrivateStorageDirNamePending(),
                    attachment.ExtractStorageDirName() + "." + attachment.getFileKey()
            )
        ){
            mAppMem.RecordStatusSingle(RecordedStatus.STATUS_BASE_STORAGE+13);
            return;
        }

        String dirDest = dirPendingAbs.getPath() + File.separator + attachment.ExtractStorageDirName() + "." + attachment.getFileKey();
        String fileDestPath = dirDest + File.separator + extractedFileName;

        String fileSrcSmb =
                mAppMem.getPreparation().getSharedSmbBase()+File.separator+
                        attachment.ExtractStorageDirName()+File.separator+
                        attachment.getFileKey()+File.separator+
                        extractedFileName;

        SmbServerInfo serverInfo = new SmbServerInfo(
                "foo",
                mAppMem.getStorageSmbServerUsername(),
                mAppMem.getStorageSmbServerPassword(),
                mAppMem.getStorageSmbServerIp());

        SmbReceiveFileFromHost receiveFileFromHost = new SmbReceiveFileFromHost(
                serverInfo,
                fileSrcSmb,
                fileDestPath,
                new SmbReceiveFileFromHost.Listener() {
                    @Override
                    public void onFinished() {
                        try {
                            Gson gson = new Gson();
                            String strJsonAttachment = gson.toJson(attachment);
                            File fileJson = new File(dirDest, "attachment.json");
                            FileWriter writer = new FileWriter(fileJson);
                            writer.append(strJsonAttachment);
                            writer.flush();
                            writer.close();

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(FileProvider.getUriForFile(requireContext(), requireContext().getPackageName()+".provider", new File(fileDestPath)));
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            mIntentResultLauncher.launch(intent);
                        }catch (Exception e){
                            String msg = "Failed to write attachment.json or to open the downloaded SMB attachment with: " + e.toString();
                            mAppMem.RecordStatusSingle(msg);
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        mAppMem.getProgressBar().setProgress(percent);
                    }

                    @Override
                    public void onError(Exception e) {
                        String msg = "Failed to download the SMB attachment with: " + e.toString();
                        mAppMem.RecordStatusSingle(msg);
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }
        );
        receiveFileFromHost.RunInBackground();
    }

    private void OpenLocalAttachmentFile(ItemAttachment attachment) {
        String storageFolderName = attachment.ExtractStorageDirName();
        String fileName = attachment.ExtractFileName();
        String key = attachment.getFileKey();

        File path = new File( mAppMem.getPreparation().getPredefinedPrivateStorageLocalScoped(), storageFolderName);
        File targetFile = new File(path, key + File.separator + fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(FileProvider.getUriForFile(requireActivity().getApplicationContext(), requireContext().getPackageName()+".provider", targetFile));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        mIntentResultLauncher.launch(intent);

        /*
        openPathIntent(
                requireActivity(),
                file.getPath(),
                false,
                requireContext().getPackageName(),
                "",
                new HashMap<>(0)
        );
        */
    }

    @Override
    public void onDestroy() {
        ((AppMem) requireActivity().getApplication()).setOnSelectedItemDetailedChangedListener(null);
        super.onDestroy();
    }
}