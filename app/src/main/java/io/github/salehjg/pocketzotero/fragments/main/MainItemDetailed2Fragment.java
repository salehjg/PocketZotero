package io.github.salehjg.pocketzotero.fragments.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.haozhang.lib.SlantedTextView;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterElements;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterGenericDouble;
import io.github.salehjg.pocketzotero.adapters.RecyclerAdapterGenericSingle;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.OneTimeEvent;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.SharedViewModel;
import io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel.ViewModelFactory;
import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;
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
    private EditText mEditTextAuthor, mEditTextTag, mEditTextField;
    private RichEditor mRichTextNote;
    private ExpandableLayout mExpandableAuthors, mExpandableTags, mExpandableNotes, mExpandableFields;
    private FitButton mBtnToolbarDrawer, mBtnToolbarDeleteItem, mBtnToolbarEditItem;
    private RecyclerView mRecyclerCreators, mRecyclerTags, mRecyclerNotes, mRecyclerFields;
    private TextView mTextViewTitle;
    private ChipGroup mChipGroupCreators, mChipGroupTags;
    private SlantedTextView mSlantedTextViewItemType;

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
        setupGuiSpinners(view, mode, "");
    }

    private void populateGuiWithInputData(ItemDetailed itemDetailed){
        mTextViewTitle.setText(itemDetailed.getItemTitle());
        mSlantedTextViewItemType.setText(itemDetailed.getItemType());

        mRecyclerAdapterCreators.setData(itemDetailed.getItemCreators());
        mRecyclerAdapterTags.setData(itemDetailed.getItemTags());
        mRecyclerAdapterNotes.setData(itemDetailed.getItemNotes());
        mRecyclerAdapterFields.setData(itemDetailed.getItemFields());

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

        GenerateChipsForCreators(mChipGroupCreators.getContext(), itemDetailed.getItemCreators());
        GenerateChipsForTags(mChipGroupTags.getContext(), itemDetailed.getItemTags());
    }

    private void GenerateChipsForCreators(Context chipGroupContext, Vector<Creator> creators){
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

    private void GenerateChipsForTags(Context chipGroupContext, Vector<ItemTag> tags){
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

        mEditTextAuthor = view.findViewById(R.id.expndd_authors_et_name);
        mEditTextTag = view.findViewById(R.id.expndd_tags_et_name);
        mRichTextNote = view.findViewById(R.id.expndd_notes_editor);
        mEditTextField = view.findViewById(R.id.expndd_fields_et_value);

        mRecyclerCreators = view.findViewById(R.id.expndd_authors_recycler);
        mRecyclerTags = view.findViewById(R.id.expndd_tags_recycler);
        mRecyclerNotes = view.findViewById(R.id.expndd_notes_rcycler);
        mRecyclerFields = view.findViewById(R.id.expndd_fields_recycler);

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
    }

    private void setupGuiForMode(MODES mode){
        mBtnGroupAuthorEdit.setVisibility(View.INVISIBLE);
        mBtnGroupTagEdit.setVisibility(View.INVISIBLE);
        mBtnGroupNoteEdit.setVisibility(View.INVISIBLE);
        mBtnGroupFieldEdit.setVisibility(View.INVISIBLE);

        switch (mode){
            case MODE_VIEW:{
                mBtnAuthorAdd.setVisibility(View.INVISIBLE);
                mBtnTagAdd.setVisibility(View.INVISIBLE);
                mBtnNoteAdd.setVisibility(View.INVISIBLE);
                mBtnFieldAdd.setVisibility(View.INVISIBLE);

                mSpinnerItemType.setVisibility(View.INVISIBLE);
                mSpinnerAuthorType.setVisibility(View.INVISIBLE);
                mSpinnerFieldName.setVisibility(View.INVISIBLE);

                mEditTextAuthor.setVisibility(View.INVISIBLE);
                mEditTextTag.setVisibility(View.INVISIBLE);
                mRichTextNote.setVisibility(View.INVISIBLE);
                mEditTextField.setVisibility(View.INVISIBLE);

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

                mEditTextAuthor.setVisibility(View.VISIBLE);
                mEditTextTag.setVisibility(View.VISIBLE);
                mRichTextNote.setVisibility(View.VISIBLE);
                mEditTextField.setVisibility(View.VISIBLE);
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

                mEditTextAuthor.setVisibility(View.VISIBLE);
                mEditTextTag.setVisibility(View.VISIBLE);
                mRichTextNote.setVisibility(View.VISIBLE);
                mEditTextField.setVisibility(View.VISIBLE);
                break;
            }
            default:{
                break;
            }
        }
    }

    private void initGuiMemberListeners(@NonNull View view, MODES mode){
        mBtnToolbarDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSharedViewModel.getMainActivityOpenDrawer().setValue(new OneTimeEvent());
            }
        });
        mBtnToolbarEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                setupGuiSpinners(view, mMode, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                populateGuiWithInputData(itemDetailed);
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

    private void setGuiSpinnerItemTypes(@NonNull View view, Vector<String> types){
        mSpinnerAdapterItemTypes = new ArrayAdapter<String>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerItemType.setAdapter(
                mSpinnerAdapterItemTypes
        );
    }

    private void setGuiSpinnerAuthorTypes(@NonNull View view, Vector<String> types){
        mSpinnerAdapterAuthorType = new ArrayAdapter<String>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerAuthorType.setAdapter(
                mSpinnerAdapterAuthorType
        );
    }

    private void setGuiSpinnerFieldNames(@NonNull View view, Vector<String> names){
        mSpinnerAdapterFieldName = new ArrayAdapter<String>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        mSpinnerFieldName.setAdapter(
                mSpinnerAdapterFieldName
        );
    }

    private void setupGuiSpinners(@NonNull View view, MODES mode, String selectedItemTypeName){
        if(mode == MODES.MODE_VIEW){
            return;
        }
        if(mode == MODES.MODE_NEW || mode == MODES.MODE_EDIT){
            setGuiSpinnerItemTypes(view, getItemTypesAll());
            if(!selectedItemTypeName.isEmpty())setGuiSpinnerAuthorTypes(view, getPossibleCreatorTypeNames(selectedItemTypeName));
            if(!selectedItemTypeName.isEmpty())setGuiSpinnerFieldNames(view, getPossibleFieldNames(selectedItemTypeName));
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
        }
    }

}