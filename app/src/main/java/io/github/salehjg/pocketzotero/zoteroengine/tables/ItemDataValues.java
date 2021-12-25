package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;

public class ItemDataValues extends BaseData {
    protected final String TABLE_NAME = "itemDataValues";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"valueID\" INTEGER, " +
                        "\"value\" TEXT " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public String getValueFor(int valueId, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT * FROM \"" + get_table_name() + "\" WHERE valueID=\"" + valueId + "\";", null);
        assert cursor.getCount()==1;
        String value = "?";
        while (cursor.moveToNext()){
            int valueIndex = cursor.getColumnIndex("value");
            value = cursor.getString(valueIndex);
        }
        cursor.close();
        return value;
    }

    public Vector<FieldValuePair> resolveFieldValuesFor(Vector<FieldValuePair> pairs, SQLiteDatabase db){
        for(FieldValuePair pair:pairs){
            pair.set_value(getValueFor(pair.get_valueId(), db));
        }
        return pairs;
    }

}
