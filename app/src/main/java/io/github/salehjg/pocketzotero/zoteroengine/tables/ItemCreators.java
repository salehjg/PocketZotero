package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;

public class ItemCreators extends BaseData{
    protected final String TABLE_NAME = "itemCreators";

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER NOT NULL, " +
                        "\"creatorID\" INTEGER NOT NULL, " +
                        "\"creatorTypeID\" INTEGER NOT NULL DEFAULT 1, " +
                        "\"orderIndex\" INTEGER NOT NULL DEFAULT 0 " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<Creator> getCreatorsIdsFor(int itemId, SQLiteDatabase db){
        Vector<Creator> authors = new Vector<>();

        Cursor cursor = db.rawQuery("SELECT creatorID, creatorTypeID, orderIndex FROM \"" + get_table_name() + "\" WHERE itemID=\"" + itemId + "\";", null);
        while (cursor.moveToNext()){
            Creator author = new Creator();
            author.setCreatorId(cursor.getInt(0));
            author.setCreatorTypeId(cursor.getInt(1));
            author.setOrderIndex(cursor.getInt(2));
            authors.add(author);
        }
        cursor.close();

        return authors;
    }

    @Override
    public String get_table_name() {
        return TABLE_NAME;
    }
}
