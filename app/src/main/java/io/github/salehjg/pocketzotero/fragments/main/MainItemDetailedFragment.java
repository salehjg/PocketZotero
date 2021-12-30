package io.github.salehjg.pocketzotero.fragments.main;

import static com.simplemobiletools.commons.extensions.ActivityKt.appLaunched;
import static com.simplemobiletools.commons.extensions.ActivityKt.openPathIntent;
import static com.simplemobiletools.commons.extensions.ContextKt.getMediaContentUri;
import static com.simplemobiletools.commons.extensions.ContextKt.getMediaStoreLastModified;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hierynomus.smbj.server.ServerList;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
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

    private void OpenAttachmentFile(ItemAttachment attachment){
        boolean isLocal = mAppMem.getStorageModeIsLocal();
        if(isLocal){
            OpenLocalAttachmentFile(attachment);
        }else{
            OpenSmbAttachmentFile(attachment);
        }
    }

    private void OpenSmbAttachmentFile(ItemAttachment attachment){
        String dirPending = mAppMem.getPreparation().GetPendingDirPath();
        String extractedFileName = attachment.ExtractFileName();

        mAppMem.getPreparation().CreateDirectory(dirPending, attachment.getFileKey());

        String dirDest = dirPending + File.separator + attachment.getFileKey();
        String fileDestPath = dirDest + File.separator + extractedFileName;

        String fileSrcSmb =
                mAppMem.getPreparation().GetSmbDataDir()+File.separator+
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
                            OpenLocalAttachmentFile(fileDestPath);
                        }catch (Exception e){
                            String msg = "Failed to write attachment.json or open the downloaded SMB attachment with: " + e.toString();
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

        String fileLocalStorageDir =
                Environment.getExternalStorageDirectory().getPath() + File.separator +
                getResources().getString(R.string.DirNameApp) + File.separator +
                getResources().getString(R.string.DirNameLocal) + File.separator +
                        storageFolderName + File.separator;

        File path = new File( fileLocalStorageDir);
        File file = new File(path, key + File.separator + fileName);

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

    private void OpenLocalAttachmentFile(String customFilePath) {
        File file = new File(customFilePath);

        {
            Uri myUri = getMediaContentUri(requireContext(), file.getPath());
            if(myUri != null){
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
        ((AppMem) requireActivity().getApplication()).setOnSelectedItemDetailedChangedListener(null);
        super.onDestroy();
    }
}