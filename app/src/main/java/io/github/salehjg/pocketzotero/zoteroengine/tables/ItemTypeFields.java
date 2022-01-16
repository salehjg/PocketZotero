package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;

public class ItemTypeFields extends BaseData {
    protected final String TABLE_NAME = "itemTypeFields";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \""+TABLE_NAME+"\" (\n" +
                        "\t\"itemTypeID\"\tINT,\n" +
                        "\t\"fieldID\"\tINT,\n" +
                        "\t\"hide\"\tINT,\n" +
                        "\t\"orderIndex\"\tINT,\n" +
                        "\tPRIMARY KEY(\"itemTypeID\",\"orderIndex\"),\n" +
                        "\tFOREIGN KEY(\"itemTypeID\") REFERENCES \"itemTypes\"(\"itemTypeID\"),\n" +
                        "\tUNIQUE(\"itemTypeID\",\"fieldID\"),\n" +
                        "\tFOREIGN KEY(\"fieldID\") REFERENCES \"fields\"(\"fieldID\")\n" +
                        ");";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<FieldValuePair> getFieldIdsFor (int itemTypeId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT fieldID FROM \"" + get_table_name() + "\" WHERE itemTypeID=\"" + itemTypeId + "\";", null);
        Vector<FieldValuePair> fieldIds = new Vector<>();
        while (cursor.moveToNext()){
            FieldValuePair fieldValuePair = new FieldValuePair();
            fieldValuePair.set_fieldId(cursor.getInt(0));
            fieldIds.add(fieldValuePair);
        }
        cursor.close();
        return fieldIds;
    }

}
