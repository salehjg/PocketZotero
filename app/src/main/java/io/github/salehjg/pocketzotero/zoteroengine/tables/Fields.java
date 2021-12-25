package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;

public class Fields extends BaseData {
    protected final String TABLE_NAME = "fields";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"fieldID\" INTEGER, " +
                        "\"fieldName\" TEXT, " +
                        "\"fieldFormatID\" TEXT " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public String getFieldNameFor (int fieldId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM \"" + get_table_name() + "\" WHERE fieldID=\"" + fieldId + "\";", null);
        assert cursor.getCount()==1;
        String fName="?";
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex("fieldName");
            fName = cursor.getString(index);
        }
        cursor.close();
        return fName;
    }

    public int getFieldIdFor (String fieldName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT fieldID FROM \"" + get_table_name() + "\" WHERE fieldName=\"" + fieldName + "\";", null);
        assert cursor.getCount()==1;
        int fieldID = -1;
        while (cursor.moveToNext()){
            fieldID = cursor.getInt(0);
        }
        cursor.close();
        return fieldID;
    }

    public Vector<FieldValuePair> resolveFieldNamesFor(Vector<FieldValuePair> pairs, SQLiteDatabase db){
        for(FieldValuePair pair:pairs){
            pair.set_fieldName(getFieldNameFor(pair.get_fieldId(), db));
        }
        return pairs;
    }

}
