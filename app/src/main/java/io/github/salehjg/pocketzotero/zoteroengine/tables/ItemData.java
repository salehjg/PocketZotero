package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;

public class ItemData extends BaseData {
    protected final String TABLE_NAME = "itemData";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER, " +
                        "\"fieldID\" INTEGER, " +
                        "\"valueID\" INTEGER" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<FieldValuePair> getFieldValuePairsFor(int itemId, SQLiteDatabase db){
        Vector<FieldValuePair> pairs = new Vector<>();

        Cursor cursor = db.rawQuery("SELECT * FROM \"" + get_table_name() + "\" WHERE itemID=\"" + itemId + "\";", null);
        while (cursor.moveToNext()){
            //for (int i = 0; i < cursor.getColumnCount(); i++){
                int fIndex = cursor.getColumnIndex("fieldID");
                int vIndex = cursor.getColumnIndex("valueID");
                pairs.add(
                        new FieldValuePair(
                                cursor.getInt(fIndex),
                                cursor.getInt(vIndex),
                                "",
                                ""
                        )
                );
            //}
        }
        cursor.close();
        return pairs;
    }
}
