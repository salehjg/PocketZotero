package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemNote;

public class ItemNotes extends BaseData {
    protected final String TABLE_NAME = "itemNotes";

    public String get_table_name(){
        return TABLE_NAME;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"itemID\" INTEGER, " +
                        "\"parentItemID\" INTEGER, " +
                        "\"note\" TEXT," +
                        "\"title\" TEXT" +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public Vector<ItemNote> getNotesFor(int parentItemId, SQLiteDatabase db){
        Vector<ItemNote> notes = new Vector<>();
        Cursor cursor = db.rawQuery("SELECT itemID, parentItemID, note, title FROM \"" + get_table_name() + "\" WHERE parentItemID=\"" + parentItemId + "\";", null);
        while (cursor.moveToNext()){
            ItemNote note = new ItemNote();
            note.setNoteItemId(cursor.getInt(0));
            note.setParentItemId(cursor.getInt(1));
            note.setNote(cursor.getString(2));
            note.setTitle(cursor.getString(3));
            notes.add(note);
        }
        cursor.close();
        return notes;
    }
}
