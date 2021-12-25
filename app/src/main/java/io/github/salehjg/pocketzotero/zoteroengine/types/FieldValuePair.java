package io.github.salehjg.pocketzotero.zoteroengine.types;

public class FieldValuePair {
    public FieldValuePair(int fieldId, int valueId, String fieldName, String value){
        this._fieldId = fieldId;
        this._valueId = valueId;
        this._fieldName = fieldName;
        this._value = value;
    }

    public FieldValuePair(){
        this._fieldId = -1;
        this._valueId = -1;
        this._fieldName = "?";
        this._value = "?";
    }

    public int get_fieldId() {
        return _fieldId;
    }

    public int get_valueId() {
        return _valueId;
    }

    public String get_fieldName() {
        return _fieldName;
    }

    public String get_value() {
        return _value;
    }

    public void set_fieldId(int _fieldId) {
        this._fieldId = _fieldId;
    }

    public void set_valueId(int _valueId) {
        this._valueId = _valueId;
    }

    public void set_fieldName(String _fieldName) {
        this._fieldName = _fieldName;
    }

    public void set_value(String _value) {
        this._value = _value;
    }

    protected int _fieldId, _valueId;
    protected String _fieldName, _value;
}
