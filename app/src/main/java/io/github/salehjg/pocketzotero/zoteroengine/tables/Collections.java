package io.github.salehjg.pocketzotero.zoteroengine.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;

/**
 CREATE TABLE collections (
 "collectionID" INTEGER,
 "collectionName" TEXT NOT NULL,
 "parentCollectionID" INTEGER,
 "clientDateModified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 "libraryID" INTEGER NOT NULL,
 "key" TEXT NOT NULL,
 "version" INTEGER NOT NULL DEFAULT 0,
 "synced" INTEGER NOT NULL DEFAULT 0
 );

 UPDATE "collections" SET
 "collectionID"=IIII,
 "collectionName"="SSSS",
 "parentCollectionID"=IIII,
 "clientDateModified"="SSSS",
 "libraryID"=IIII,
 "version"=IIII,
 "synced"=IIII
 WHERE "key"="SSSS";
 */

public class Collections extends BaseData {

    protected final String TABLE_NAME = "collections";

    protected final String TAG= "Collections";

    public void createTable(SQLiteDatabase db) {

        String CREATE_TABLE_COLLECTIONS =
            "CREATE TABLE "+TABLE_NAME+" ( " +
                    "\"collectionID\" INTEGER," +
                    "\"collectionName\" TEXT NOT NULL," +
                    "\"parentCollectionID\" INTEGER," +
                    "\"clientDateModified\" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "\"libraryID\" INTEGER NOT NULL," +
                    "\"key\" TEXT NOT NULL," +
                    "\"version\" INTEGER NOT NULL DEFAULT 0," +
                    "\"synced\" INTEGER NOT NULL DEFAULT 0" +
                    ");";
        db.execSQL(CREATE_TABLE_COLLECTIONS);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + get_table_name());
    }

    public ContentValues getValues(Collection collection) {
        ContentValues values = new ContentValues();
        values.put("collectionID", collection.get_collection_id());
        values.put("collectionName", collection.get_collection_name());
        values.put("parentCollectionID", collection.is_top_parent()? null:collection.get_parent_id());
        values.put("clientDateModified", collection.get_client_date_modified());
        values.put("libraryID", collection.get_library_id());
        values.put("key", collection.get_key());
        values.put("version", collection.get_version());
        values.put("synced", collection.get_synced());
        return values;
    }

    /**
     * Take a collection and write it to the database
     * @param collection
     */
    public void writeCollection(Collection collection, SQLiteDatabase db) {
        ContentValues values = getValues(collection);
        db.insert(get_table_name(), null, values);
    }

    static public Collection getCollectionFromValues(ContentValues values) {
        Collection collection = new Collection();

        collection.set_collection_id((Integer) values.get("collectionID"));
        collection.set_collection_name((String) values.get("collectionName"));
        collection.set_parent_id((Integer) values.get("parentCollectionID"));
        collection.set_client_date_modified((String) values.get("clientDateModified"));
        collection.set_library_id((Integer) values.get("libraryID"));
        collection.set_key((String) values.get("key"));
        collection.set_version((Integer) values.get("version"));
        collection.set_synced((Integer) values.get("synced"));

        return collection;
    }

    static public Vector<Collection> getCollectionFromVectorOfValues(Vector<ContentValues> vecOfValues) {
        Vector<Collection> vecOfCollections = new Vector<Collection>();
        for(ContentValues values: vecOfValues){
            Collection collection = new Collection();

            collection.set_collection_id(Integer.valueOf((String)values.get("collectionID")));
            collection.set_collection_name((String) values.get("collectionName"));

            String strParentId = (String)values.get("parentCollectionID");
            boolean dontHaveParentId = (strParentId==null);
            collection.set_parent_id(Integer.valueOf(dontHaveParentId?"-1":strParentId));
            collection.set_is_top_parent(dontHaveParentId);

            collection.set_client_date_modified((String) values.get("clientDateModified"));
            collection.set_library_id(Integer.valueOf((String)values.get("libraryID")));
            collection.set_key((String) values.get("key"));
            collection.set_version(Integer.valueOf((String)values.get("version")));
            collection.set_synced(Integer.valueOf((String)values.get("synced")));

            vecOfCollections.add(collection);
        }
        return vecOfCollections;
    }

    public String get_table_name(){
        return TABLE_NAME;
    }

    public boolean collectionExists(Collection c, SQLiteDatabase db){
        return exists(get_table_name(), c.get_key(), db);
    }

    public boolean collectionExists(String key, SQLiteDatabase db){
        return exists(get_table_name(), key, db);
    }

    public Collection getCollection(String key, SQLiteDatabase db){
        if (collectionExists(key, db)) {
            ContentValues values = getSingle(db, key);
            return getCollectionFromValues(values);
        }
        return null;
    }

    public void deleteCollection(Collection c, SQLiteDatabase db){
        db.execSQL("DELETE FROM " + get_table_name() + " WHERE key=\"" + c.get_key() + "\";");
    }

    public void updateCollection(Collection collection, SQLiteDatabase db) {
        db.execSQL(
                "UPDATE \""+ this.TABLE_NAME +"\" SET" +
                "\"collectionID\"="+ collection.get_collection_id() +"," +
                "\"collectionName\"=\""+ collection.get_collection_name() +"\"," +
                "\"parentCollectionID\"="+ (collection.is_top_parent()?"NULL":collection.get_parent_id()) +"," +
                "\"clientDateModified\"=\""+ collection.get_client_date_modified() +"\"," +
                "\"libraryID\"="+ collection.get_library_id() +"," +
                "\"version\"="+ collection.get_version() +"," +
                "\"synced\"=" + collection.get_synced() +
                "WHERE \"key\"=\""+ collection.get_key() +"\";"
        );
    }

}
