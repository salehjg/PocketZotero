package io.github.salehjg.pocketzotero.zoteroengine.types;

import java.io.Serializable;

public class FieldValuePair implements Serializable {
    public FieldValuePair(int fieldId, int valueId, String fieldName, String value){
        this.mFieldId = fieldId;
        this.mValueId = valueId;
        this.mFieldName = fieldName;
        this.mValue = value;
    }

    public FieldValuePair(){
        this.mFieldId = -1;
        this.mValueId = -1;
        this.mFieldName = "?";
        this.mValue = "?";
    }

    public int get_fieldId() {
        return mFieldId;
    }

    public int get_valueId() {
        return mValueId;
    }

    public String get_fieldName() {
        return mFieldName;
    }

    public String get_value() {
        return mValue;
    }

    public void set_fieldId(int _fieldId) {
        this.mFieldId = _fieldId;
    }

    public void set_valueId(int _valueId) {
        this.mValueId = _valueId;
    }

    public void set_fieldName(String _fieldName) {
        this.mFieldName = _fieldName;
    }

    public void set_value(String _value) {
        this.mValue = _value;
    }

    protected int mFieldId, mValueId;
    protected String mFieldName, mValue;
}
