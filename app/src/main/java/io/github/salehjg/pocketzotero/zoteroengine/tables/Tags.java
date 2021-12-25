package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemTag;

public class Tags extends BaseData {
    protected final String TABLE_NAME = "tags";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"tagID\" INTEGER, " +
                        "\"name\" TEXT NOT NULL " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<ItemTag> getTagsDetails(Vector<ItemTag> tags, SQLiteDatabase db){
        for(ItemTag tag:tags){
            Cursor cursor = db.rawQuery("SELECT name FROM \"" + get_table_name() + "\" WHERE tagID=\"" + tag.getTagId() + "\";", null);
            while (cursor.moveToNext()){
                tag.setTagName(cursor.getString(0));
            }
            cursor.close();
        }
        return tags;
    }
}
