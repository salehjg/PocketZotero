package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;

public class ItemAttachments {
    protected final String TABLE_NAME = "itemAttachments";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER, " +
                        "\"parentItemID\" INTEGER, " +
                        "\"linkMode\" INTEGER, " +
                        "\"contentType\" TEXT, " +
                        "\"charsetID\" INTEGER, " +
                        "\"path\" TEXT, " +
                        "\"syncState\" INTEGER, " +
                        "\"storageModTime\" INTEGER, " +
                        "\"storageHash\" TEXT" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<ItemAttachment> getAttachmentFor(CollectionItem item, SQLiteDatabase db){
        Vector<ItemAttachment> files = new Vector<>();

        Cursor cursor = db.rawQuery("SELECT itemID, parentItemID, linkMode, contentType, charsetID, path, syncState, storageModTime, storageHash FROM \"" + get_table_name() + "\" WHERE parentItemID=\"" + item.get_itemId() + "\";", null);
        while (cursor.moveToNext()){
            ItemAttachment file = new ItemAttachment();
            file.setFileItemId(cursor.getInt(0));       // itemID - 0
            file.setParentItemId(cursor.getInt(1));       // parentItemID - 1
            file.setLinkMode(cursor.getInt(2));       // linkMode - 2
            file.setContentType(cursor.getString(3));   // contentType - 3
            file.setCharsetId(cursor.getString(4));     // charsetID - 4
            file.setFilePath(cursor.getString(5));      // path - 5
            file.setSyncState(cursor.getInt(6));        // syncState - 6
            file.setStorageModTime(cursor.getString(7));// storageModTime - 7
            file.setStorageHash(cursor.getString(8));// storageHash - 8
            files.add(file);
        }
        cursor.close();
        return files;
    }

    public Vector<Integer> getFileItemIdFast(CollectionItem item, SQLiteDatabase db){
        Vector<Integer> fileItemIds = new Vector<>();
        Cursor cursor = db.rawQuery("SELECT itemID FROM \"" + get_table_name() + "\" WHERE parentItemID=\"" + item.get_itemId() + "\";", null);
        while (cursor.moveToNext()){
            fileItemIds.add(cursor.getInt(0));       // itemID - 0
        }
        cursor.close();
        return fileItemIds;
    }
}
