package io.github.salehjg.pocketzotero.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.smbutils.SmbScanner;

public class SettingsFragment extends Fragment {
    RadioButton radioButtonLocal, radioButtonSmb;
    Spinner spinnerSmbServerIp;
    EditText editTextSmbServerIp, editTextSmbServerUser, editTextSmbServerPass;
    ImageButton imageButtonSearchServer, imageButtonStopSearching;
    Button buttonSave;
    EditText editTextSharedPath;

    boolean isLocal;
    List<String> spinnerSmbServerIpItems;
    ArrayAdapter<String> spinnerSmbServerIpAdapter;
    SmbScanner smbScanner;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerSmbServerIpItems = new Vector<>();
        
        radioButtonLocal = view.findViewById(R.id.fragsettings_radio_local);
        radioButtonSmb = view.findViewById(R.id.fragsettings_radio_smb);
        spinnerSmbServerIp = view.findViewById(R.id.fragsettings_spinner_smbserver);
        editTextSmbServerIp = view.findViewById(R.id.fragsettings_edittext_smbserver);
        imageButtonSearchServer = view.findViewById(R.id.fragsettings_searchserver_btn);
        imageButtonStopSearching = view.findViewById(R.id.fragsettings_stopsearching_btn);
        buttonSave = view.findViewById(R.id.fragsettings_save_btn);
        editTextSharedPath = view.findViewById(R.id.fragsettings_text_sharedpath);
        editTextSmbServerUser = view.findViewById(R.id.fragsettings_text_user);
        editTextSmbServerPass = view.findViewById(R.id.fragsettings_text_pass);

        radioButtonLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isLocal = b;
                radioButtonSmb.setChecked(!b);
            }
        });
        radioButtonSmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isLocal = !b;
                radioButtonLocal.setChecked(!b);
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
                        if(percent % 5==0){
                            Toast.makeText(
                                    requireContext(),
                                    "SMB Server Search Progress: " + percent + "%",
                                    Toast.LENGTH_SHORT).show();
                        }
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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppMem appMem = ((AppMem)getActivity().getApplication());
                    appMem.setStorageModeIsLocal(isLocal);
                    appMem.setStorageSmbServerIp(editTextSmbServerIp.getText().toString());
                    appMem.setStorageSmbServerSharedPath(editTextSharedPath.getText().toString());
                    appMem.setStorageSmbServerUsername(editTextSmbServerUser.getText().toString());
                    appMem.setStorageSmbServerPassword(editTextSmbServerPass.getText().toString());
                    Toast.makeText(
                            requireContext(),
                            "The settings are saved.",
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
            AppMem appMem = ((AppMem)getActivity().getApplication());

            isLocal = appMem.getStorageModeIsLocal();
            radioButtonLocal.setChecked(isLocal);
            radioButtonSmb.setChecked(!isLocal);

            String ip = appMem.getStorageSmbServerIp();
            if(!ip.isEmpty()) {
                editTextSmbServerIp.setText(ip);
            }

            String sharedPath = appMem.getStorageSmbServerSharedPath();
            editTextSharedPath.setText(sharedPath);

            String smbUser = appMem.getStorageSmbServerUsername();
            editTextSmbServerUser.setText(smbUser);

            String smbPass = appMem.getStorageSmbServerPassword();
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
}