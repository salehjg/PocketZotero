package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemTag;

public class ItemTags extends BaseData {
    protected final String TABLE_NAME = "itemTags";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER NOT NULL, " +
                        "\"tagID\" INTEGER NOT NULL, " +
                        "\"type\" INTEGER NOT NULL " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<ItemTag> getTagsFor(int itemId, SQLiteDatabase db){
        Vector<ItemTag> tags = new Vector<>();
        Cursor cursor = db.rawQuery("SELECT tagID, type FROM \"" + get_table_name() + "\" WHERE itemID=\"" + itemId + "\";", null);
        while (cursor.moveToNext()){
            ItemTag tag = new ItemTag();
            tag.setTagId(cursor.getInt(0));
            tag.setType(cursor.getInt(1));
            tag.setItemId(itemId);
            tags.add(tag);
        }
        cursor.close();
        return tags;
    }
}
