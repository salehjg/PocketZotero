package io.github.salehjg.pocketzotero.fragments.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.nikartm.button.FitButton;

import java.util.List;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.RecordedStatus;
import io.github.salehjg.pocketzotero.smbutils.SmbScanner;

public class SettingsFragment extends Fragment {
    RadioButton radioButtonLocalScoped, radioButtonSmb;
    Spinner spinnerSmbServerIp;
    EditText editTextSmbServerIp, editTextSmbServerUser, editTextSmbServerPass;
    FitButton imageButtonSearchServer, imageButtonStopSearching;
    FitButton imageButtonImportLocalScoped;
    FitButton buttonSave;
    EditText editTextSharedPath;

    boolean isLocalScoped;
    boolean isSharedSmb;

    List<String> spinnerSmbServerIpItems;
    ArrayAdapter<String> spinnerSmbServerIpAdapter;
    SmbScanner smbScanner;
    ProgressBar progressBar;

    AppMem mAppMem;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private void scanRadioButtons(){
        isLocalScoped = radioButtonLocalScoped.isChecked();
        isSharedSmb = radioButtonSmb.isChecked();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppMem = ((AppMem)requireActivity().getApplication());

        spinnerSmbServerIpItems = new Vector<>();
        radioButtonLocalScoped = view.findViewById(R.id.fragsettings_radio_local_scoped);
        radioButtonSmb = view.findViewById(R.id.fragsettings_radio_smb);
        spinnerSmbServerIp = view.findViewById(R.id.fragsettings_spinner_smbserver);
        editTextSmbServerIp = view.findViewById(R.id.fragsettings_edittext_smbserver);
        imageButtonSearchServer = view.findViewById(R.id.fragsettings_searchserver_btn);
        imageButtonStopSearching = view.findViewById(R.id.fragsettings_stopsearching_btn);
        imageButtonImportLocalScoped = view.findViewById(R.id.fragsettings_import_localscp_btn);
        buttonSave = view.findViewById(R.id.fragsettings_save_btn);
        editTextSharedPath = view.findViewById(R.id.fragsettings_text_sharedpath);
        editTextSmbServerUser = view.findViewById(R.id.fragsettings_text_user);
        editTextSmbServerPass = view.findViewById(R.id.fragsettings_text_pass);
        progressBar = mAppMem.getProgressBar();

        radioButtonLocalScoped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scanRadioButtons();
                radioButtonSmb.setChecked(!b);
            }
        });
        radioButtonSmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scanRadioButtons();
                radioButtonLocalScoped.setChecked(!b);
            }
        });
        spinnerSmbServerIpAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, spinnerSmbServerIpItems);
        spinnerSmbServerIp.setAdapter(spinnerSmbServerIpAdapter);
        spinnerSmbServerIp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editTextSmbServerIp.setText(spinnerSmbServerIpAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imageButtonSearchServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(smbScanner!=null){
                    // skip if there is a scan already running in background!
                    if(!smbScanner.isTerminated()) return;
                }
                smbScanner = new SmbScanner(false, new SmbScanner.Listener() {
                    @Override
                    public void onFinished(Vector<String> serversFound) {
                        spinnerSmbServerIpItems.clear();
                        spinnerSmbServerIpItems.addAll(serversFound);
                        spinnerSmbServerIpAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onServerFound(String theNewServer, Vector<String> serversFound) {
                        spinnerSmbServerIpItems.clear();
                        spinnerSmbServerIpItems.addAll(serversFound);
                        spinnerSmbServerIpAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        progressBar.setProgress(percent);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(
                                requireContext(),
                                "SMB Server Search Error: " + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });

                smbScanner.RunInBackground();
            }
        });
        imageButtonStopSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(smbScanner!=null){
                    smbScanner.TerminateWithForce();
                }
            }
        });
        imageButtonImportLocalScoped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:{
                                //Yes button clicked
                                ImportZippedZoteroDbAndroid10Friendly();
                                break;
                            }

                            case DialogInterface.BUTTON_NEGATIVE: {
                                //No button clicked
                                ///TODO : IMPLEMENT EXPORT DATABASE
                                Toast.makeText(requireContext(), "In case you do not have a backup of the currently loaded database, please use the export database button to get a compressed zip file of it", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("All the data belonging to any previously imported database will be lost. Do you still want to continue?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAppMem.setStorageMode(isLocalScoped, isSharedSmb);
                    mAppMem.setStorageSmbServerIp(editTextSmbServerIp.getText().toString());
                    mAppMem.setStorageSmbServerSharedPath(editTextSharedPath.getText().toString());
                    mAppMem.setStorageSmbServerUsername(editTextSmbServerUser.getText().toString());
                    mAppMem.setStorageSmbServerPassword(editTextSmbServerPass.getText().toString());
                    Toast.makeText(
                            requireContext(),
                            "The settings are saved. Please restart the application.",
                            Toast.LENGTH_SHORT
                    ).show();

                }catch (Exception e){
                    Toast.makeText(
                            requireContext(),
                            "Failed to save the settings.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        LoadSettings();
    }

    private void LoadSettings(){
        try {
            isLocalScoped = mAppMem.getStorageModeIsLocalScoped();
            isSharedSmb = mAppMem.getStorageModeIsSharedSmb();
            radioButtonLocalScoped.setChecked(isLocalScoped);
            radioButtonSmb.setChecked(isSharedSmb);

            String ip = mAppMem.getStorageSmbServerIp();
            if(!ip.isEmpty()) {
                editTextSmbServerIp.setText(ip);
            }

            String sharedPath = mAppMem.getStorageSmbServerSharedPath();
            editTextSharedPath.setText(sharedPath);

            String smbUser = mAppMem.getStorageSmbServerUsername();
            editTextSmbServerUser.setText(smbUser);

            String smbPass = mAppMem.getStorageSmbServerPassword();
            editTextSmbServerPass.setText(smbPass);
        }
        catch (Exception e){
            Toast.makeText(
                    requireContext(),
                    "Failed to retrieve the settings.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    ActivityResultLauncher<Intent> mIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data==null){
                    mAppMem.RecordStatusSingle(RecordedStatus.STATUS_BASE_STORAGE+12);
                    return;
                }
                Uri uriToZip = data.getData();
                ExtractLocalZipFile extractLocalZipFile = new ExtractLocalZipFile(
                        requireActivity().getContentResolver(),
                        uriToZip,
                        mAppMem.getPreparation().getPredefinedPrivateStorageLocalScoped(),
                        true,
                        new ExtractLocalZipFile.Listener() {
                    @Override
                    public void onFinished() {
                        Toast.makeText(requireContext(), "Finished importing the database.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        mAppMem.getProgressBar().setProgress(percent);
                    }

                    @Override
                    public void onError(Exception e) {
                        mAppMem.RecordStatusSingle("Exception during extraction: " + e.toString());
                    }
                });
                extractLocalZipFile.RunInBackground();
            }
        }
    });

    private void ImportZippedZoteroDbAndroid10Friendly(){
        try{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            mIntentResultLauncher.launch(intent);
        }catch (Exception e){
            mAppMem.RecordStatusSingle(
                    "Failed to launch ACTION_GET_CONTENT to import Zotero DB to the predefined private storage with: " +
                            e.toString()
            );
        }
    }
}