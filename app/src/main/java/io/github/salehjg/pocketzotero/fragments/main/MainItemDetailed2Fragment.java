package io.github.salehjg.pocketzotero.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.haozhang.lib.SlantedTextView;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import io.github.salehjg.pocketzotero.AppDirs;
import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.RecordedStatus;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterAttachments;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterAttachments2;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterGenericDouble;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterGenericSingle;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.OneTimeEvent;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.SharedViewModel;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.ViewModelFactory;
import io.github.salehjg.pocketzotero.smbutils.SmbReceiveFileFromHost;
import io.github.salehjg.pocketzotero.smbutils.SmbServerInfo;
import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemNote;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemTag;
import jp.wasabeef.richeditor.RichEditor;

public class MainItemDetailed2Fragment extends Fragment {

    private enum MODES{
        MODE_VIEW,
        MODE_NEW,
        MODE_EDIT
    };

    private enum SUBMODES{
        NONE,
        EDIT_TYPE,
        EDIT_AUTHOR,
        EDIT_TAG,
        EDIT_NOTE,
        EDIT_FIELD
    };

    // User Interface
    private Spinner mSpinnerItemType, mSpinnerAuthorType, mSpinnerFieldName;
    private FitButton mBtnAuthorAdd, mBtnAuthorSave, mBtnAuthorDiscard;
    private LinearLayout mBtnGroupAuthorEdit;
    private FitButton mBtnTagAdd, mBtnTagSave, mBtnTagDiscard;
    private LinearLayout mBtnGroupTagEdit;
    private FitButton mBtnNoteAdd, mBtnNoteSave, mBtnNoteDiscard;
    private LinearLayout mBtnGroupNoteEdit;
    private FitButton mBtnFieldAdd, mBtnFieldSave, mBtnFieldDiscard;
    private LinearLayout mBtnGroupFieldEdit;
    private EditText mEditTextAuthorFirstName, mEditTextAuthorLastName, mEditTextTag, mEditTextField;
    private RichEditor mRichTextNote;
    private ExpandableLayout mExpandableAuthors, mExpandableTags, mExpandableNotes, mExpandableFields;
    private FitButton mBtnToolbarDrawer, mBtnToolbarDeleteItem, mBtnToolbarEditItem;
    private RecyclerView mRecyclerAttachments, mRecyclerCreators, mRecyclerTags, mRecyclerNotes, mRecyclerFields;
    private TextView mTextViewTitle;
    private ChipGroup mChipGroupCreators, mChipGroupTags;
    private SlantedTextView mSlantedTextViewItemType;
    private RelativeLayout mCoverRelativeLayout;
    private FitButton mCoverSave, mCoverDiscard;
    private FitButton mBtnAttachmentsAdd;

    // State Keys
    private static final String STATE_zzzzzzz_STATE = "STATE.KEY.zzzzzzzz";

    // States
    private ItemDetailed mDataItemDetailed;
    private ArrayAdapter<String> mSpinnerAdapterItemTypes, mSpinnerAdapterAuthorType, mSpinnerAdapterFieldName;

    private RecyclerAdapterCreators mRecyclerAdapterCreators;
    private LinearLayoutManager mRecyclerLayoutManagerCreators;

    private RecyclerAdapterTags mRecyclerAdapterTags;
    private LinearLayoutManager mRecyclerLayoutManagerTags;

    private RecyclerAdapterNotes mRecyclerAdapterNotes;
    private LinearLayoutManager mRecyclerLayoutManagerNotes;

    private RecyclerAdapterFields mRecyclerAdapterFields;
    private LinearLayoutManager mRecyclerLayoutManagerFields;

    private RecyclerAdapterAttachments2 mRecyclerAdapterAttachments;
    private LinearLayoutManager mRecyclerLayoutManagerAttachments;

    // Misc
    private AppMem mAppMem;
    private SharedViewModel mSharedViewModel;
    private MODES mMode;
    private SUBMODES mSubMode;

    private final int ID_BASE_CHIP_CREATORS = 12345;
    private final int ID_BASE_CHIP_TAGS = 123450;

    private Vector<Chip> mChipsCreators, mChipsTags;
    private View.OnClickListener mChipsOnClickListenerCreators, mChipsOnClickListenerTags;


    public MainItemDetailed2Fragment() {
        // Required empty public constructor
    }

    public static MainItemDetailed2Fragment newInstance() {
        MainItemDetailed2Fragment fragment = new MainItemDetailed2Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_item_detailed2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSharedViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireActivity().getApplication(), 1)).get(SharedViewModel.class);
        mAppMem = (AppMem)(requireActivity().getApplication());

        initGui(view, mMode=MODES.MODE_VIEW);
    }

    private void initGui(@NonNull View view, MODES mode){
        initGuiMembers(view);
        initGuiMemberListeners(view, mode);
        setupGuiForMode(mode);
        setupGuiSpinners(mode, "", false);
    }

    private void populateGuiWithInputData(ItemDetailed itemDetailed){
        mTextViewTitle.setText(itemDetailed.getItemTitle());
        mSlantedTextViewItemType.setText(itemDetailed.getItemType());

        mRecyclerAdapterCreators.setData(itemDetailed.getItemCreators());
        mRecyclerAdapterTags.setData(itemDetailed.getItemTags());
        mRecyclerAdapterNotes.setData(itemDetailed.getItemNotes());
        mRecyclerAdapterFields.setData(itemDetailed.getItemFields());
        mRecyclerAdapterAttachments.setAttachments(itemDetailed.getItemAttachments());

        mChipsOnClickListenerCreators = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId() - ID_BASE_CHIP_CREATORS;
                Toast.makeText(view.getContext(), "Chip Creator: " + mChipsCreators.get(index).getText(), Toast.LENGTH_SHORT).show();
            }
        };

        mChipsOnClickListenerTags = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId() - ID_BASE_CHIP_TAGS;
                Toast.makeText(view.getContext(), "Chip Tags: " + mChipsTags.get(index).getText(), Toast.LENGTH_SHORT).show();
            }
        };

        generateChipsForCreators(mChipGroupCreators.getContext(), itemDetailed.getItemCreators());
        generateChipsForTags(mChipGroupTags.getContext(), itemDetailed.getItemTags());
    }

    private void generateChipsForCreators(Context chipGroupContext, Vector<Creator> creators){
        mChipGroupCreators.removeAllViews();
        mChipsCreators = new Vector<>();
        int i=0;
        for(Creator creator:creators){
            Chip chip = new Chip(chipGroupContext);
            String creatorStr = creator.getCreatorId() + ": " + creator.extractFullName();
            chip.setText(creatorStr);
            chip.setId(ID_BASE_CHIP_CREATORS + i++);
            chip.setChipBackgroundColorResource(R.color.chipsCreators);
            chip.setChipEndPadding(5);
            mChipsCreators.add(chip);
        }

        for(Chip chip:mChipsCreators){
            mChipGroupCreators.addView(chip);
            chip.setOnClickListener(mChipsOnClickListenerCreators);
        }
    }

    private void generateChipsForTags(Context chipGroupContext, Vector<ItemTag> tags){
        mChipGroupTags.removeAllViews();
        mChipsTags = new Vector<>();
        int i=0;
        for(ItemTag tag:tags){
            Chip chip = new Chip(chipGroupContext);
            String tagStr = tag.getTagId() + ": " + tag.getTagName();
            chip.setText(tagStr);
            chip.setId(ID_BASE_CHIP_TAGS + i++);
            chip.setChipBackgroundColorResource(R.color.chipsTags);
            mChipsTags.add(chip);
        }

        for(Chip chip:mChipsTags){
            mChipGroupTags.addView(chip);
            chip.setOnClickListener(mChipsOnClickListenerTags);
        }
    }

    private void initGuiMembers(@NonNull View view){
        mTextViewTitle = view.findViewById(R.id.fragmainitemdetailed2_tv_title);
        mSlantedTextViewItemType = view.findViewById(R.id.fragmainitemdetailed2_slanted_itemtype);

        mChipGroupCreators = view.findViewById(R.id.fragmainitemdetailed2_chipgroup_authors);
        mChipGroupTags = view.findViewById(R.id.fragmainitemdetailed2_chipgroup_tags);

        mBtnToolbarDrawer = view.findViewById(R.id.fragmainitemdetailed2_btn_drawer);
        mBtnToolbarEditItem = view.findViewById(R.id.fragmainitemdetailed2_btn_itemedit);
        mBtnToolbarDeleteItem = view.findViewById(R.id.fragmainitemdetailed2_btn_itemdelete);

        mExpandableAuthors = view.findViewById(R.id.fragmainitemdetailed2_exp_authors);
        mExpandableTags = view.findViewById(R.id.fragmainitemdetailed2_exp_tags);
        mExpandableNotes = view.findViewById(R.id.fragmainitemdetailed2_exp_notes);
        mExpandableFields = view.findViewById(R.id.fragmainitemdetailed2_exp_fields);

        mSpinnerItemType = view.findViewById(R.id.fragmainitemdetailed2_spinner_itemtype);
        mSpinnerAuthorType = view.findViewById(R.id.expndd_authors_spinner_type);
        mSpinnerFieldName = view.findViewById(R.id.expndd_fields_spinner_field);

        mBtnAuthorAdd = view.findViewById(R.id.expndd_authors_btn_add);
        mBtnAuthorSave = view.findViewById(R.id.expndd_authors_btn_save);
        mBtnAuthorDiscard = view.findViewById(R.id.expndd_authors_btn_discard);
        mBtnGroupAuthorEdit = view.findViewById(R.id.expndd_authors_ll_btns_edit);

        mBtnTagAdd = view.findViewById(R.id.expndd_tags_btn_add);
        mBtnTagSave = view.findViewById(R.id.expndd_tags_btn_save);
        mBtnTagDiscard = view.findViewById(R.id.expndd_tags_btn_discard);
        mBtnGroupTagEdit = view.findViewById(R.id.expndd_tags_ll_btns_edit);

        mBtnNoteAdd = view.findViewById(R.id.expndd_notes_btn_add);
        mBtnNoteSave = view.findViewById(R.id.expndd_notes_btn_save);
        mBtnNoteDiscard = view.findViewById(R.id.expndd_notes_btn_discard);
        mBtnGroupNoteEdit = view.findViewById(R.id.expndd_notes_ll_btns_edit);

        mBtnFieldAdd = view.findViewById(R.id.expndd_fields_btn_add);
        mBtnFieldSave = view.findViewById(R.id.expndd_fields_btn_save);
        mBtnFieldDiscard = view.findViewById(R.id.expndd_fields_btn_discard);
        mBtnGroupFieldEdit = view.findViewById(R.id.expndd_fields_ll_btns_edit);
        mBtnAttachmentsAdd = view.findViewById(R.id.fragmainitemdetailed2_attachments_btn_add);

        mEditTextAuthorFirstName = view.findViewById(R.id.expndd_authors_et_name_first);
        mEditTextAuthorLastName = view.findViewById(R.id.expndd_authors_et_name_last);
        mEditTextTag = view.findViewById(R.id.expndd_tags_et_name);
        mRichTextNote = view.findViewById(R.id.expndd_notes_editor);
        mEditTextField = view.findViewById(R.id.expndd_fields_et_value);

        mRecyclerCreators = view.findViewById(R.id.expndd_authors_recycler);
        mRecyclerTags = view.findViewById(R.id.expndd_tags_recycler);
        mRecyclerNotes = view.findViewById(R.id.expndd_notes_rcycler);
        mRecyclerFields = view.findViewById(R.id.expndd_fields_recycler);
        mRecyclerAttachments = view.findViewById(R.id.fragmainitemdetailed2_attachments_recycler);

        mRecyclerLayoutManagerCreators = new LinearLayoutManager(view.getContext());
        mRecyclerCreators.setLayoutManager(mRecyclerLayoutManagerCreators);
        mRecyclerAdapterCreators = new RecyclerAdapterCreators(view.getContext(), null);
        mRecyclerAdapterCreators.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerCreators.setAdapter(mRecyclerAdapterCreators);

        mRecyclerLayoutManagerTags = new LinearLayoutManager(view.getContext());
        mRecyclerTags.setLayoutManager(mRecyclerLayoutManagerTags);
        mRecyclerAdapterTags = new RecyclerAdapterTags(view.getContext(), null);
        mRecyclerAdapterTags.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerTags.setAdapter(mRecyclerAdapterTags);

        mRecyclerLayoutManagerNotes = new LinearLayoutManager(view.getContext());
        mRecyclerNotes.setLayoutManager(mRecyclerLayoutManagerNotes);
        mRecyclerAdapterNotes = new RecyclerAdapterNotes(view.getContext(), null);
        mRecyclerAdapterNotes.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerNotes.setAdapter(mRecyclerAdapterNotes);

        mRecyclerLayoutManagerFields = new LinearLayoutManager(view.getContext());
        mRecyclerFields.setLayoutManager(mRecyclerLayoutManagerFields);
        mRecyclerAdapterFields = new RecyclerAdapterFields(view.getContext(), null);
        mRecyclerAdapterFields.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerFields.setAdapter(mRecyclerAdapterFields);

        mRecyclerLayoutManagerAttachments = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerAttachments.setLayoutManager(mRecyclerLayoutManagerAttachments);
        mRecyclerAdapterAttachments = new RecyclerAdapterAttachments2(view.getContext(), null);
        mRecyclerAdapterAttachments.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        mRecyclerAttachments.setAdapter(mRecyclerAdapterAttachments);

        mCoverRelativeLayout = view.findViewById(R.id.fragmainitemdetailed2_mode_cover);
        mCoverSave = view.findViewById(R.id.fragmainitemdetailed2_btn_modifications_save);
        mCoverDiscard = view.findViewById(R.id.fragmainitemdetailed2_btn_modifications_discard);
    }

    private void setupGuiForMode(MODES mode){
        mBtnGroupAuthorEdit.setVisibility(View.GONE);
        mBtnGroupTagEdit.setVisibility(View.GONE);
        mBtnGroupNoteEdit.setVisibility(View.GONE);
        mBtnGroupFieldEdit.setVisibility(View.GONE);

        switch (mode){
            case MODE_VIEW:{
                mBtnAuthorAdd.setVisibility(View.GONE);
                mBtnTagAdd.setVisibility(View.GONE);
                mBtnNoteAdd.setVisibility(View.GONE);
                mBtnFieldAdd.setVisibility(View.GONE);

                mSpinnerItemType.setVisibility(View.GONE);
                mSpinnerAuthorType.setVisibility(View.GONE);
                mSpinnerFieldName.setVisibility(View.GONE);

                mEditTextAuthorFirstName.setVisibility(View.GONE);
                mEditTextAuthorLastName.setVisibility(View.GONE);
                mEditTextTag.setVisibility(View.GONE);
                mRichTextNote.setVisibility(View.GONE);
                mEditTextField.setVisibility(View.GONE);

                mCoverRelativeLayout.setVisibility(View.INVISIBLE);

                mBtnAttachmentsAdd.setVisibility(View.INVISIBLE);
                break;
            }
            case MODE_NEW:{
                mBtnAuthorAdd.setVisibility(View.VISIBLE);
                mBtnTagAdd.setVisibility(View.VISIBLE);
                mBtnNoteAdd.setVisibility(View.VISIBLE);
                mBtnFieldAdd.setVisibility(View.VISIBLE);

                mSpinnerItemType.setVisibility(View.VISIBLE);
                mSpinnerAuthorType.setVisibility(View.VISIBLE);
                mSpinnerFieldName.setVisibility(View.VISIBLE);

                mEditTextAuthorFirstName.setVisibility(View.VISIBLE);
                mEditTextAuthorLastName.setVisibility(View.VISIBLE);
                mEditTextTag.setVisibility(View.VISIBLE);
                mRichTextNote.setVisibility(View.VISIBLE);
                mEditTextField.setVisibility(View.VISIBLE);

                mCoverRelativeLayout.setVisibility(View.VISIBLE);

                mBtnAttachmentsAdd.setVisibility(View.VISIBLE);
                break;
            }
            case MODE_EDIT:{
                mBtnAuthorAdd.setVisibility(View.VISIBLE);
                mBtnTagAdd.setVisibility(View.VISIBLE);
                mBtnNoteAdd.setVisibility(View.VISIBLE);
                mBtnFieldAdd.setVisibility(View.VISIBLE);

                mSpinnerItemType.setVisibility(View.VISIBLE);
                mSpinnerAuthorType.setVisibility(View.VISIBLE);
                mSpinnerFieldName.setVisibility(View.VISIBLE);

                mEditTextAuthorFirstName.setVisibility(View.VISIBLE);
                mEditTextAuthorLastName.setVisibility(View.VISIBLE);
                mEditTextTag.setVisibility(View.VISIBLE);
                mRichTextNote.setVisibility(View.VISIBLE);
                mEditTextField.setVisibility(View.VISIBLE);

                mCoverRelativeLayout.setVisibility(View.VISIBLE);

                mBtnAttachmentsAdd.setVisibility(View.VISIBLE);
                break;
            }
            default:{
                break;
            }
        }

        // To force refresh recyclers to show/hide the buttons for editing or removing an item.
        mRecyclerAdapterCreators.notifyDataSetChanged();
        mRecyclerAdapterTags.notifyDataSetChanged();
        mRecyclerAdapterFields.notifyDataSetChanged();
        mRecyclerAdapterNotes.notifyDataSetChanged();

        mRecyclerAdapterAttachments.setVisibilityButtonItemRemove(mode!=MODES.MODE_VIEW);
    }

    private void initGuiMemberListeners(@NonNull View view, MODES mode){
        mBtnAttachmentsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mBtnToolbarDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSharedViewModel.getMainActivityOpenDrawer().setValue(new OneTimeEvent());
            }
        });
        mBtnToolbarEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMode = MODES.MODE_EDIT;
                setupGuiForMode(mMode);
                setupGuiSpinners(mMode, mDataItemDetailed.getItemType(), false);
            }
        });
        mBtnToolbarDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mExpandableAuthors.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mExpandableAuthors.isExpanded()){
                    mExpandableAuthors.collapse();
                }else{
                    mExpandableAuthors.expand();
                }
            }
        });
        mExpandableTags.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mExpandableTags.isExpanded()){
                    mExpandableTags.collapse();
                }else{
                    mExpandableTags.expand();
                }
            }
        });
        mExpandableNotes.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mExpandableNotes.isExpanded()){
                    mExpandableNotes.collapse();
                }else{
                    mExpandableNotes.expand();
                }
            }
        });
        mExpandableFields.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mExpandableFields.isExpanded()){
                    mExpandableFields.collapse();
                }else{
                    mExpandableFields.expand();
                }
            }
        });
        mSpinnerItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                setupGuiSpinners(mMode, (String) mSpinnerItemType.getSelectedItem(), true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCoverSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Save", Toast.LENGTH_LONG).show();
            }
        });
        mCoverDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMode = MODES.MODE_VIEW;
                setupGuiForMode(mMode);
            }
        });

        mSharedViewModel.getTabItemDetailedHideDrawerButton().observe(getViewLifecycleOwner(), new Observer<OneTimeEvent>() {
            @Override
            public void onChanged(OneTimeEvent oneTimeEvent) {
                if(oneTimeEvent.receive()){
                    mBtnToolbarDrawer.setVisibility(View.INVISIBLE);
                }
            }
        });
        mSharedViewModel.getSelectedItemDetailed().observe(getViewLifecycleOwner(), new Observer<ItemDetailed>() {
            @Override
            public void onChanged(ItemDetailed itemDetailed) {
                if(itemDetailed!=null) {
                    mDataItemDetailed = itemDetailed;
                    populateGuiWithInputData(mDataItemDetailed);
                    mMode=MODES.MODE_VIEW;
                    setupGuiForMode(mMode);
                }
            }
        });
        mRecyclerAdapterCreators.setOnClickListener(new RecyclerAdapterGenericDouble.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemRemoveClick(View view, int position) {

            }

            @Override
            public void onItemEditClick(View view, int position) {

            }
        });
        mRecyclerAdapterTags.setOnClickListener(new RecyclerAdapterGenericSingle.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemRemoveClick(View view, int position) {

            }

            @Override
            public void onItemEditClick(View view, int position) {

            }
        });
        mRecyclerAdapterNotes.setOnClickListener(new RecyclerAdapterGenericSingle.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemRemoveClick(View view, int position) {

            }

            @Override
            public void onItemEditClick(View view, int position) {

            }
        });
        mRecyclerAdapterFields.setOnClickListener(new RecyclerAdapterGenericDouble.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemRemoveClick(View view, int position) {

            }

            @Override
            public void onItemEditClick(View view, int position) {

            }
        });
        mRecyclerAdapterAttachments.setOnClickListener(new RecyclerAdapterAttachments2.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemAttachment attachment =  mRecyclerAdapterAttachments.getDataAttachment(position);
                openAttachmentFile(attachment);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemRemoveClick(View view, int position) {

            }

            @Override
            public void onItemRemoveLongClick(View view, int position) {

            }
        });
    }

    private Vector<String> getItemTypesAll(){
        return mAppMem.getPreparation().getItemTypeNames();
    }

    private Vector<String> getPossibleCreatorTypeNames(String selectedItemTypeName){
        Vector<Creator> possibilities = mAppMem.getPreparation().getPossibleCreatorTypesFor(selectedItemTypeName);
        Vector<String> creatorTypeNames = new Vector<>();
        for(Creator creator:possibilities){
            creatorTypeNames.add(creator.getType());
        }
        return creatorTypeNames;
    }

    private Vector<String> getPossibleFieldNames(String selectedItemTypeName){
        Vector<FieldValuePair> possibilities = mAppMem.getPreparation().getPossibleFieldsFor(selectedItemTypeName);
        Vector<String> fieldNames = new Vector<>();
        for(FieldValuePair pair:possibilities){
            fieldNames.add(pair.get_fieldName());
        }
        return fieldNames;
    }

    private void setGuiSpinnerItemTypes(@NonNull Context context, Vector<String> types, String preselectedItemTypeName){
        mSpinnerAdapterItemTypes = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerItemType.setAdapter(
                mSpinnerAdapterItemTypes
        );
        if(!preselectedItemTypeName.isEmpty()){
            if(types.contains(preselectedItemTypeName)){
                mSpinnerItemType.setSelection(types.indexOf(preselectedItemTypeName));
            }
        }
    }

    private void setGuiSpinnerAuthorTypes(@NonNull Context context, Vector<String> types){
        mSpinnerAdapterAuthorType = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerAuthorType.setAdapter(
                mSpinnerAdapterAuthorType
        );
    }

    private void setGuiSpinnerFieldNames(@NonNull Context context, Vector<String> names){
        mSpinnerAdapterFieldName = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                names
        );
        mSpinnerFieldName.setAdapter(
                mSpinnerAdapterFieldName
        );
    }

    private void setupGuiSpinners(MODES mode, String itemTypeName, boolean excludeItemTypeSpinner){
        if(mode == MODES.MODE_VIEW){
            return;
        }
        if(mode == MODES.MODE_NEW || mode == MODES.MODE_EDIT){

            if(!excludeItemTypeSpinner)setGuiSpinnerItemTypes(requireContext(), getItemTypesAll(), itemTypeName);
            if(!itemTypeName.isEmpty())setGuiSpinnerAuthorTypes(requireContext(), getPossibleCreatorTypeNames(itemTypeName));
            if(!itemTypeName.isEmpty())setGuiSpinnerFieldNames(requireContext(), getPossibleFieldNames(itemTypeName));
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable(STATE_zzzzzzz_STATE, mRecyclerViewElementsLayoutManager.onSaveInstanceState());
    }

    //==============================================================================================

    private class RecyclerAdapterCreators extends RecyclerAdapterGenericDouble<Creator>{
        public RecyclerAdapterCreators(Context context, Vector<Creator> data) {
            super(context, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Creator creator = getItem(position);
            holder.getGuiTv1().setText(creator.getType());
            holder.getGuiTv2().setText(creator.extractFullName());
            holder.setVisibilityBtnAll(mMode==MODES.MODE_EDIT);
        }
    }

    private class RecyclerAdapterTags extends RecyclerAdapterGenericSingle<ItemTag> {
        public RecyclerAdapterTags(Context context, Vector<ItemTag> data) {
            super(context, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemTag tag = getItem(position);
            holder.getGuiTv().setText(tag.getTagName());
            holder.setVisibilityBtnAll(mMode==MODES.MODE_EDIT);
        }
    }

    private class RecyclerAdapterNotes extends RecyclerAdapterGenericSingle<ItemNote> {
        public RecyclerAdapterNotes(Context context, Vector<ItemNote> data) {
            super(context, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemNote note = getItem(position);
            holder.getGuiTv().setText(note.getTitle());
            holder.setVisibilityBtnAll(mMode==MODES.MODE_EDIT);
        }
    }

    private class RecyclerAdapterFields extends RecyclerAdapterGenericDouble<FieldValuePair> {
        public RecyclerAdapterFields(Context context, Vector<FieldValuePair> data) {
            super(context, data);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FieldValuePair pair = getItem(position);
            holder.getGuiTv1().setText(pair.get_fieldName());
            holder.getGuiTv2().setText(pair.get_value());
            holder.setVisibilityBtnAll(mMode==MODES.MODE_EDIT);
        }
    }

    private void openAttachmentFile(ItemAttachment attachment){
        boolean isLocal = mAppMem.getStorageModeIsLocalScoped();
        if(isLocal){
            openLocalAttachmentFile(attachment);
        }else{
            openSmbAttachmentFile(attachment);
        }
    }

    ActivityResultLauncher<Intent> mIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //if(result.getResultCode() == Activity.RESULT_OK){}
        }
    });

    private void openSmbAttachmentFile(ItemAttachment attachment){
        File dirPendingAbs = AppDirs.getPredefinedPrivateStoragePending(requireContext());

        String extractedFileName = attachment.extractFileName();

        if(!
                AppDirs.makeDirAtPrivateBase(
                        requireContext(),
                        AppDirs.getPredefinedPrivateStorageDirNamePending(requireContext()),
                        attachment.extractStorageDirName() + "." + attachment.getFileKey()
                )
        ){
            mAppMem.recordStatusSingle(RecordedStatus.STATUS_BASE_STORAGE+13);
            return;
        }

        String dirDest = dirPendingAbs.getPath() + File.separator + attachment.extractStorageDirName() + "." + attachment.getFileKey();
        String fileDestPath = dirDest + File.separator + extractedFileName;

        String fileSrcSmb =
                AppDirs.getSharedSmbBase(requireContext())+File.separator+
                        attachment.extractStorageDirName()+File.separator+
                        attachment.getFileKey()+File.separator+
                        extractedFileName;

        SmbServerInfo serverInfo = new SmbServerInfo(
                "foo",
                mAppMem.getStorageSmbServerUsername(),
                mAppMem.getStorageSmbServerPassword(),
                mAppMem.getStorageSmbServerIp());

        mAppMem.createProgressDialog(requireActivity(), false,false,"Downloading the requested attachment from the SMB host ...", null);
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
                            mAppMem.recordStatusSingle(msg);
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        }
                        mAppMem.closeProgressDialog();
                    }

                    @Override
                    public void onProgressTick(int percent) {
                        mAppMem.setProgressDialogValue(percent);
                    }

                    @Override
                    public void onError(Exception e) {
                        String msg = "Failed to download the SMB attachment with: " + e.toString();
                        mAppMem.recordStatusSingle(msg);
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        mAppMem.closeProgressDialog();
                    }
                }
        );
        receiveFileFromHost.runInBackground();
    }

    private void openLocalAttachmentFile(ItemAttachment attachment) {
        String storageFolderName = attachment.extractStorageDirName();
        String fileName = attachment.extractFileName();
        String key = attachment.getFileKey();

        File path = new File( AppDirs.getPredefinedPrivateStorageLocalScoped(requireContext()), storageFolderName);
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

}