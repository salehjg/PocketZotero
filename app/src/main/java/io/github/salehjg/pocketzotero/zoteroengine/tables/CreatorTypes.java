package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;

public class CreatorTypes extends BaseData{
    protected final String TABLE_NAME = "creatorTypes";

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"creatorTypeID\" INTEGER, " +
                        "\"creatorType\" TEXT" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public String getCreatorTypeFor(int creatorTypeId, SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT creatorType FROM \"" + get_table_name() + "\" WHERE creatorTypeID=\"" + creatorTypeId + "\";", null);
        assert cursor.getCount()==1;
        String creatorType = "?";
        while (cursor.moveToNext()){
            creatorType = cursor.getString(0);
        }
        cursor.close();
        return creatorType;
    }

    public Vector<Creator> getCreatorsTypes(Vector<Creator> creators, SQLiteDatabase db){
        for(Creator creator:creators){
            Cursor cursor = db.rawQuery("SELECT creatorType FROM \"" + get_table_name() + "\" WHERE creatorTypeID=\"" + creator.getCreatorTypeId() + "\";", null);
            while (cursor.moveToNext()){
                creator.setType(cursor.getString(0));
            }
            cursor.close();
        }
        return creators;
    }

    @Override
    public String get_table_name() {
        return TABLE_NAME;
    }
}
