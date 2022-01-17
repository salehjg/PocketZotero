package io.github.salehjg.pocketzotero.mainactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nikartm.button.FitButton;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.Vector;

import io.github.salehjg.pocketzotero.AppMem;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;
import jp.wasabeef.richeditor.RichEditor;

public class TempActivity extends AppCompatActivity {
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

    private AppMem mAppMem;
    private MODES mMode;
    private SUBMODES mSubMode;

    private Spinner mSpinnerItemType, mSpinnerAuthorType, mSpinnerFieldName;
    private ArrayAdapter<String> mSpinnerAdapterItemTypes, mSpinnerAdapterAuthorType, mSpinnerAdapterFieldName;

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




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppMem = (AppMem) getApplication();

        setContentView(R.layout.fragment_main_item_detailed2);
        initGui(mMode = MODES.MODE_NEW);
    }

    private void initGui(MODES mode){
        initGuiMembers();
        initGuiMemberListeners(mode);
        setupGuiForMode(mode);
        setupGuiSpinners(mode, "");
    }

    private void initGuiMembers(){
        mExpandableAuthors = findViewById(R.id.fragmainitemdetailed2_exp_authors);
        mExpandableTags = findViewById(R.id.fragmainitemdetailed2_exp_tags);
        mExpandableNotes = findViewById(R.id.fragmainitemdetailed2_exp_notes);
        mExpandableFields = findViewById(R.id.fragmainitemdetailed2_exp_fields);

        mSpinnerItemType = findViewById(R.id.fragmainitemdetailed2_spinner_itemtype);
        mSpinnerAuthorType = findViewById(R.id.expndd_authors_spinner_type);
        mSpinnerFieldName = findViewById(R.id.expndd_fields_spinner_field);

        mBtnAuthorAdd = findViewById(R.id.expndd_authors_btn_add);
        mBtnAuthorSave = findViewById(R.id.expndd_authors_btn_save);
        mBtnAuthorDiscard = findViewById(R.id.expndd_authors_btn_discard);
        mBtnGroupAuthorEdit = findViewById(R.id.expndd_authors_ll_btns_edit);

        mBtnTagAdd = findViewById(R.id.expndd_tags_btn_add);
        mBtnTagSave = findViewById(R.id.expndd_tags_btn_save);
        mBtnTagDiscard = findViewById(R.id.expndd_tags_btn_discard);
        mBtnGroupTagEdit = findViewById(R.id.expndd_tags_ll_btns_edit);

        mBtnNoteAdd = findViewById(R.id.expndd_notes_btn_add);
        mBtnNoteSave = findViewById(R.id.expndd_notes_btn_save);
        mBtnNoteDiscard = findViewById(R.id.expndd_notes_btn_discard);
        mBtnGroupNoteEdit = findViewById(R.id.expndd_notes_ll_btns_edit);

        mBtnFieldAdd = findViewById(R.id.expndd_fields_btn_add);
        mBtnFieldSave = findViewById(R.id.expndd_fields_btn_save);
        mBtnFieldDiscard = findViewById(R.id.expndd_fields_btn_discard);
        mBtnGroupFieldEdit = findViewById(R.id.expndd_fields_ll_btns_edit);

        mEditTextAuthor = findViewById(R.id.expndd_authors_et_name);
        mEditTextTag = findViewById(R.id.expndd_tags_et_name);
        mRichTextNote = findViewById(R.id.expndd_notes_editor);
        mEditTextField = findViewById(R.id.expndd_fields_et_value);
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

    private void initGuiMemberListeners(MODES mode){
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
                setupGuiSpinners(mMode, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    private void setGuiSpinnerItemTypes(Vector<String> types){
        mSpinnerAdapterItemTypes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerItemType.setAdapter(
                mSpinnerAdapterItemTypes
        );
    }

    private void setGuiSpinnerAuthorTypes(Vector<String> types){
        mSpinnerAdapterAuthorType = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        mSpinnerAuthorType.setAdapter(
                mSpinnerAdapterAuthorType
        );
    }

    private void setGuiSpinnerFieldNames(Vector<String> names){
        mSpinnerAdapterFieldName = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                names
        );
        mSpinnerFieldName.setAdapter(
                mSpinnerAdapterFieldName
        );
    }

    private void setupGuiSpinners(MODES mode, String selectedItemTypeName){
        if(mode == MODES.MODE_VIEW){
            return;
        }
        if(mode == MODES.MODE_NEW || mode == MODES.MODE_EDIT){
            setGuiSpinnerItemTypes(getItemTypesAll());
            if(!selectedItemTypeName.isEmpty())setGuiSpinnerAuthorTypes(getPossibleCreatorTypeNames(selectedItemTypeName));
            if(!selectedItemTypeName.isEmpty())setGuiSpinnerFieldNames(getPossibleFieldNames(selectedItemTypeName));
        }
    }


}
