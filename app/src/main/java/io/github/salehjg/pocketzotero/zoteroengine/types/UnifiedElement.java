package io.github.salehjg.pocketzotero.zoteroengine.types;


import io.github.salehjg.pocketzotero.utils.UnifiedTypes;

public class UnifiedElement {
    private UnifiedTypes mType;
    private Creator mCreator;
    private ItemTag mTag;
    private ItemNote mNote;
    private FieldValuePair mField;

    public UnifiedElement(
            UnifiedTypes type,
            Creator creator,
            FieldValuePair field,
            ItemTag tag,
            ItemNote note){
        this.mType = type;
        this.mCreator = creator;
        this.mField = field;
        this.mTag = tag;
        this.mNote = note;
    }

    public ItemNote getNote() {
        return mNote;
    }

    public void setNote(ItemNote mNote) {
        this.mNote = mNote;
    }

    public ItemTag getTag() {
        return mTag;
    }

    public void setTag(ItemTag mTag) {
        this.mTag = mTag;
    }

    public UnifiedTypes getType() {
        return mType;
    }

    public void setType(UnifiedTypes mType) {
        this.mType = mType;
    }

    public Creator getCreator() {
        return mCreator;
    }

    public void setCreator(Creator mCreator) {
        this.mCreator = mCreator;
    }

    public FieldValuePair getField() {
        return mField;
    }

    public void setField(FieldValuePair mField) {
        this.mField = mField;
    }

    public String getStringName(){
        String retVal;
        switch (mType){
            case AUTHOR:    retVal = "Author";                  break;
            case NOTE:      retVal = mNote.getTitle();          break;
            case TAG:       retVal = "Tag";                     break;
            case FIELD:     retVal = mField.get_fieldName();    break;
            default:        retVal = "?";                       break;
        }
        return retVal;
    }

    public String getStringValue(){
        String retVal;
        switch (mType){
            case AUTHOR: {
                retVal = mCreator.getFirstName() + " " + mCreator.getLastName().toUpperCase();
                break;
            }
            case NOTE: {
                retVal = mNote.getNote();
                break;
            }
            case TAG: {
                retVal = mTag.getTagName();
                break;
            }
            case FIELD: {
                retVal = mField.get_value();
                break;
            }
            default: {
                retVal = "??";
                break;
            }
        }
        return retVal;
    }
}
