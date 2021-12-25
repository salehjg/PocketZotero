package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemTypes extends BaseData {
    protected final String TABLE_NAME = "itemTypes";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemTypeID\" INTEGER, " +
                        "\"typeName\" TEXT, " +
                        "\"templateItemTypeID\" INTEGER, " +
                        "\"display\" INTEGER DEFAULT 1" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public String getTypeNameFor (int itemTypeId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM \"" + get_table_name() + "\" WHERE itemTypeID=\"" + itemTypeId + "\";", null);
        assert cursor.getCount()==1;
        String tName="?";
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex("typeName");
            tName = cursor.getString(index);
        }
        cursor.close();
        return tName;
    }

    public int getItemTypeIdFor (String typeName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT itemTypeID FROM \"" + get_table_name() + "\" WHERE typeName=\"" + typeName + "\";", null);
        assert cursor.getCount()==1;
        int itemTypeID = -1;
        while (cursor.moveToNext()){
            itemTypeID = cursor.getInt(0);
        }
        cursor.close();
        return itemTypeID;
    }

}
