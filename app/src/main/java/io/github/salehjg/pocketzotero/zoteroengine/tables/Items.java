package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;

public class Items extends BaseData {
    protected final String TABLE_NAME = "items";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER, " +
                        "\"itemTypeID\" INTEGER NOT NULL, " +
                        "\"dateAdded\" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "\"dateModified\" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "\"clientDateModified\" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "\"libraryID\" INTEGER NOT NULL, " +
                        "\"key\" TEXT, " +
                        "\"version\" INTEGER NOT NULL DEFAULT 0, " +
                        "\"synced\" INTEGER NOT NULL DEFAULT 0 " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public int getTypeIdFor (int fileItemId, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT itemTypeID FROM \"" + get_table_name() + "\" WHERE itemID=\"" + fileItemId + "\";", null);
        int typeId=-1;
        while (cursor.moveToNext()){
            assert cursor.getCount()==1;
            typeId = cursor.getInt(0);
        }
        cursor.close();
        return typeId;
    }

    public Vector<ItemAttachment> getDetailsOfAttachmentsFor (Vector<ItemAttachment> attachments, SQLiteDatabase db) {
        for(ItemAttachment attachment:attachments){
            Cursor cursor = db.rawQuery("SELECT itemTypeID, dateAdded, dateModified, clientDateModified, libraryID, \"key\", version, synced FROM \"" + get_table_name() + "\" WHERE itemID=\"" + attachment.getFileItemId() + "\";", null);
            assert cursor.getCount()==1;
            cursor.moveToFirst();
            attachment.setFileItemTypeId(cursor.getInt(0)); // itemTypeID - 0
            attachment.setFileDateAdded(cursor.getString(1)); // dateAdded - 1
            attachment.setFileDateModified(cursor.getString(2)); // dateModified - 2
            attachment.setFileClientModified(cursor.getString(3)); // clientDateModified - 3
            attachment.setFileLibraryId(cursor.getInt(4)); // libraryID - 4
            attachment.setFileKey(cursor.getString(5)); // key - 5
            attachment.setFileVersion(cursor.getInt(6)); // version - 6
            attachment.setFileSynced(cursor.getInt(7)); // synced - 7
            cursor.close();
        }

        return attachments;
    }

}
