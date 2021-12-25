package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;

public class CollectionsItems extends BaseData {

    protected final String TABLE_NAME = "collectionItems";

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE_COLLECTIONS =
                "CREATE TABLE \"" +TABLE_NAME + "\" ( " +
                        "\"collectionID\" INTEGER NOT NULL, " +
                        "\"itemID\" INTEGER NOT NULL, " +
                        "\"orderIndex\" INTEGER NOT NULL DEFAULT 0 " +
                        ")";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public ContentValues getValues(CollectionItem ic) {
        ContentValues values = new ContentValues();
        values.put("itemID", ic.get_itemId());
        values.put("collectionID", ic.get_collectionId());
        values.put("orderIndex", ic.get_orderIndex());
        return values;
    }

    /**
     * Take a collection and write it to the database
     * @param ic
     */
    public void writeCollection(CollectionItem ic, SQLiteDatabase db) {
        ContentValues values = getValues(ic);
        db.insert(get_table_name(), null, values);
    }

    public static CollectionItem getCollectionItemFromValues(ContentValues values) {
        CollectionItem ic = new CollectionItem();
        ic.set_collectionId((Integer) values.get("collectionID"));
        ic.set_itemId((Integer) values.get("itemID"));
        ic.set_orderIndex((Integer) values.get("orderIndex"));
        return ic;
    }

    public String get_table_name(){
        return TABLE_NAME;
    }

    /// TODO: Implement these!
    /*
    public void deleteByRecord(Record r, SQLiteDatabase db){
        db.execSQL("DELETE FROM " + get_table_name() + " where item=\"" + r.get_zotero_key() + "\"");
    }

    public void deleteByCollection(Collection c, SQLiteDatabase db){
        db.execSQL("DELETE FROM " + get_table_name() + " where collection=\"" + c.get_key() + "\"");
    }

    public Vector<CollectionItem> getCollectionItemForItem(Record r, SQLiteDatabase db) {

        Vector<CollectionItem> citems = new Vector<>();
        ContentValues values = new ContentValues();
        Cursor cursor = db.rawQuery("select * from \"" + get_table_name() + "\" where item=\"" + r.get_zotero_key() + "\";", null);
        while (cursor.moveToNext()){
            values.clear();
            for (int i = 0; i < cursor.getColumnCount(); i++){
                values.put(cursor.getColumnName(i),cursor.getString(i));
            }
            citems.add(getCollectionItemFromValues(values));
        }

        cursor.close();
        return citems;
    }
    */

    public Vector<CollectionItem> getItemsForCollection (int collectionId, SQLiteDatabase db) {
        Vector<CollectionItem> citems = new Vector<>();
        ContentValues values = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT * FROM \"" + get_table_name() + "\" WHERE collectionID=\"" + collectionId + "\";", null);
        while (cursor.moveToNext()){
            values.clear();
            for (int i = 0; i < cursor.getColumnCount(); i++){
                values.put(cursor.getColumnName(i),cursor.getInt(i));
            }
            citems.add(getCollectionItemFromValues(values));
        }

        cursor.close();
        return citems;
    }


}
