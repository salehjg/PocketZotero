package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;

public class ItemTypeCreatorTypes extends BaseData {
    protected final String TABLE_NAME = "itemTypeCreatorTypes";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \""+TABLE_NAME+"\" (\n" +
                        "\t\"itemTypeID\"\tINT,\n" +
                        "\t\"creatorTypeID\"\tINT,\n" +
                        "\t\"primaryField\"\tINT,\n" +
                        "\tFOREIGN KEY(\"itemTypeID\") REFERENCES \"itemTypes\"(\"itemTypeID\"),\n" +
                        "\tPRIMARY KEY(\"itemTypeID\",\"creatorTypeID\"),\n" +
                        "\tFOREIGN KEY(\"creatorTypeID\") REFERENCES \"creatorTypes\"(\"creatorTypeID\")\n" +
                        ");";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<Creator> getCreatorTypeIdsFor (int itemTypeId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT creatorTypeID FROM \"" + get_table_name() + "\" WHERE itemTypeID=\"" + itemTypeId + "\";", null);
        Vector<Creator> creatorTypeIds = new Vector<>();
        while (cursor.moveToNext()) {
            Creator creator = new Creator();
            creator.setCreatorTypeId(cursor.getInt(0));
            creatorTypeIds.add(creator);
        }
        cursor.close();
        return creatorTypeIds;
    }

}
