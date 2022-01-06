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
import io.github.salehjg.pocketzotero.mainactivity.MainActivity;
import io.github.salehjg.pocketzotero.smbutils.SmbScanner;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class settingsFragment extends Fragment {
    RadioButton mRadioButtonLocalScoped, mRadioButtonSmb;
    Spinner mSpinnerSmbServerIp;
    EditText mEditTextSmbServerIp, mEditTextSmbServerUser, mEditTextSmbServerPass;
    FitButton mImageButtonSearchServer, mImageButtonStopSearching;
    FitButton mImageButtonImportLocalScoped;
    FitButton mButtonSave;
    EditText mEditTextSharedPath;

    boolean mIsLocalScoped;
    boolean mIsSharedSmb;

    List<String> mSpinnerSmbServerIpItems;
    ArrayAdapter<String> mSpinnerSmbServerIpAdapter;
    SmbScanner mSmbScanner;

    AppMem mAppMem;

    public settingsFragment() {
        // Required empty public constructor
    }

    public static settingsFragment newInstance() {
        settingsFragment fragment = new settingsFragment();
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
        mIsLocalScoped = mRadioButtonLocalScoped.isChecked();
        mIsSharedSmb = mRadioButtonSmb.isChecked();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppMem = ((AppMem)requireActivity().getApplication());

        mSpinnerSmbServerIpItems = new Vector<>();
        mRadioButtonLocalScoped = view.findViewById(R.id.fragsettings_radio_local_scoped);
        mRadioButtonSmb = view.findViewById(R.id.fragsettings_radio_smb);
        mSpinnerSmbServerIp = view.findViewById(R.id.fragsettings_spinner_smbserver);
        mEditTextSmbServerIp = view.findViewById(R.id.fragsettings_edittext_smbserver);
        mImageButtonSearchServer = view.findViewById(R.id.fragsettings_searchserver_btn);
        mImageButtonStopSearching = view.findViewById(R.id.fragsettings_stopsearching_btn);
        mImageButtonImportLocalScoped = view.findViewById(R.id.fragsettings_import_localscp_btn);
        mButtonSave = view.findViewById(R.id.fragsettings_save_btn);
        mEditTextSharedPath = view.findViewById(R.id.fragsettings_text_sharedpath);
        mEditTextSmbServerUser = view.findViewById(R.id.fragsettings_text_user);
        mEditTextSmbServerPass = view.findViewById(R.id.fragsettings_text_pass);

        mRadioButtonLocalScoped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scanRadioButtons();
                mRadioButtonSmb.setChecked(!b);
            }
        });
        mRadioButtonSmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scanRadioButtons();
                mRadioButtonLocalScoped.setChecked(!b);
            }
        });
        mSpinnerSmbServerIpAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mSpinnerSmbServerIpItems);
        mSpinnerSmbServerIp.setAdapter(mSpinnerSmbServerIpAdapter);
        mSpinnerSmbServerIp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mEditTextSmbServerIp.setText(mSpinnerSmbServerIpAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mImageButtonSearchServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSmbScanner !=null){
                    // skip if there is a scan already running in background!
                    if(!mSmbScanner.isTerminated()) return;
                }
                mSmbScanner = new SmbScanner(false, new SmbScanner.Listener() {
                    @Override
                    public void onFinished(Vector<String> serversFound) {
                        mSpinnerSmbServerIpItems.clear();
                        mSpinnerSmbServerIpItems.addAll(serversFound);
                        mSpinnerSmbServerIpAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Finished searching for SMB servers.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onServerFound(String theNewServer, Vector<String> serversFound) {
                        mSpinnerSmbServerIpItems.clear();
                        mSpinnerSmbServerIpItems.addAll(serversFound);
                        mSpinnerSmbServerIpAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Found a new SMB host at " + theNewServer, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        Toast.makeText(requireContext(), "Searching for SMB servers: " + percent + "% complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(
                                requireContext(),
                                "Searching for SMB servers failed with error: " + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });

                mSmbScanner.runInBackground();
            }
        });
        mImageButtonStopSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSmbScanner !=null){
                    mSmbScanner.shutdownNow();
                }
            }
        });
        mImageButtonImportLocalScoped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:{
                                //Yes button clicked
                                importZippedZoteroDbAndroid10Friendly();
                                break;
                            }

                            case DialogInterface.BUTTON_NEGATIVE: {
                                //No button clicked
                                ///TODO : IMPLEMENT EXPORT DATABASE
                                Toast.makeText(requireContext(), mAppMem.getPreparation().getResourceString(R.string.MsgSettingsImportDb2), Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(mAppMem.getPreparation().getResourceString(R.string.MsgSettingsImportDb1)).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mAppMem.setStorageMode(mIsLocalScoped, mIsSharedSmb);
                    mAppMem.setStorageSmbServerIp(mEditTextSmbServerIp.getText().toString());
                    mAppMem.setStorageSmbServerSharedPath(mEditTextSharedPath.getText().toString());
                    mAppMem.setStorageSmbServerUsername(mEditTextSmbServerUser.getText().toString());
                    mAppMem.setStorageSmbServerPassword(mEditTextSmbServerPass.getText().toString());
                    Toast.makeText(
                            requireContext(),
                            mAppMem.getPreparation().getResourceString(R.string.MsgSettingsSave1),
                            Toast.LENGTH_SHORT
                    ).show();

                }catch (Exception e){
                    Toast.makeText(
                            requireContext(),
                            mAppMem.getPreparation().getResourceString(R.string.MsgSettingsSave2),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        loadSettings();
    }

    private void loadSettings(){
        try {
            mIsLocalScoped = mAppMem.getStorageModeIsLocalScoped();
            mIsSharedSmb = mAppMem.getStorageModeIsSharedSmb();
            mRadioButtonLocalScoped.setChecked(mIsLocalScoped);
            mRadioButtonSmb.setChecked(mIsSharedSmb);

            String ip = mAppMem.getStorageSmbServerIp();
            if(!ip.isEmpty()) {
                mEditTextSmbServerIp.setText(ip);
            }

            String sharedPath = mAppMem.getStorageSmbServerSharedPath();
            mEditTextSharedPath.setText(sharedPath);

            String smbUser = mAppMem.getStorageSmbServerUsername();
            mEditTextSmbServerUser.setText(smbUser);

            String smbPass = mAppMem.getStorageSmbServerPassword();
            mEditTextSmbServerPass.setText(smbPass);
        }
        catch (Exception e){
            Toast.makeText(
                    requireContext(),
                    mAppMem.getPreparation().getResourceString(R.string.MsgSettingsLoad1),
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
                    mAppMem.recordStatusSingle(RecordedStatus.STATUS_BASE_STORAGE+12);
                    return;
                }
                Uri uriToZip = data.getData();
                mAppMem.createProgressDialog(requireActivity(), true,false, "Importing the selected database archive ...", null);
                ExtractLocalZipFile extractLocalZipFile = new ExtractLocalZipFile(
                        requireActivity().getContentResolver(),
                        uriToZip,
                        mAppMem.getPreparation().getPredefinedPrivateStorageLocalScoped(),
                        true,
                        new ExtractLocalZipFile.Listener() {
                    @Override
                    public void onFinished() {
                        Toast.makeText(requireContext(), mAppMem.getPreparation().getResourceString(R.string.MsgSettingsImportDb3), Toast.LENGTH_LONG).show();
                        mAppMem.closeProgressDialog();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        mAppMem.setProgressDialogValue(percent);
                    }

                    @Override
                    public void onError(Exception e) {
                        mAppMem.recordStatusSingle("Exception during extraction: " + e.toString());
                    }
                });
                extractLocalZipFile.runInBackground();
            }
        }
    });

    private void importZippedZoteroDbAndroid10Friendly(){
        try{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            mIntentResultLauncher.launch(intent);
        }catch (Exception e){
            mAppMem.recordStatusSingle(
                    "Failed to launch ACTION_GET_CONTENT to import Zotero DB to the predefined private storage with: " +
                            e.toString()
            );
        }
    }
}