package io.github.salehjg.pocketzotero.zoteroengine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.tables.Collections;
import io.github.salehjg.pocketzotero.zoteroengine.tables.CollectionsItems;
import io.github.salehjg.pocketzotero.zoteroengine.tables.CreatorTypes;
import io.github.salehjg.pocketzotero.zoteroengine.tables.Creators;
import io.github.salehjg.pocketzotero.zoteroengine.tables.Fields;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemAttachments;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemCreators;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemData;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemDataValues;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemNotes;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemTags;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemTypes;
import io.github.salehjg.pocketzotero.zoteroengine.tables.Items;
import io.github.salehjg.pocketzotero.zoteroengine.tables.Tags;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemNote;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemTag;

/**
 * Created by oni on 11/07/2017.
 */

public class ZotDroidDB extends SQLiteOpenHelper {

    // All Static variables
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "zotdroid.sqlite";

    public static final String TAG = "zotdroid.ZotDroidDB";
    private SQLiteDatabase _db;

    private Collections _collectionsTable               = new Collections();
    private CollectionsItems _collectionsItemsTable     = new CollectionsItems();
    private ItemData _itemDataTable                     = new ItemData();
    private ItemDataValues _itemDataValueTable          = new ItemDataValues();
    private Fields _fieldsTable                         = new Fields();
    private Items _items                                = new Items();
    private ItemTypes _itemTypes                        = new ItemTypes();
    private ItemAttachments _itemAttachments            = new ItemAttachments();
    private CreatorTypes _creatorTypes                  = new CreatorTypes();
    private Creators _creators                          = new Creators();
    private ItemCreators _itemCreators                  = new ItemCreators();
    private ItemTags _itemTags                          = new ItemTags();
    private Tags _tags                                  = new Tags();
    private ItemNotes _itemNotes                        = new ItemNotes();

    // A small class to hold caching info
    private class SearchCache {
        public String last_search;
        public int last_num_results;
        public boolean valid = false;
    }

    private SearchCache             _cache = new SearchCache();

    public ZotDroidDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._db = getWritableDatabase(); // Should kickstart all the creation we need :S
        _check_and_create();
    }

    /**
     * Alternative constructor for if we change the database location
     * @param context
     * @param alternativeFname
     */
    public ZotDroidDB(Context context, String alternativeFname) {
        // Double check we have no trailing slash
        super(context, alternativeFname, null, DATABASE_VERSION);
        this._db = getWritableDatabase();
        _check_and_create();
    }

    protected boolean checkTableExists(String tablename){
        Cursor cursor = _db.rawQuery("SELECT tbl_name FROM sqlite_master WHERE type=\"table\" AND tbl_name = \""+ tablename +"\"", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * Check if our database exists and all the tables. If not then create
     * TODO - odd split of requirements here, with passing this db into the table classes
     */

    private void _check_and_create() {

        if (!checkTableExists(_itemNotes.get_table_name())) {
            _itemNotes.createTable(_db);
        }

        if (!checkTableExists(_itemTags.get_table_name())) {
            _itemTags.createTable(_db);
        }

        if (!checkTableExists(_tags.get_table_name())) {
            _tags.createTable(_db);
        }

        if (!checkTableExists(_creatorTypes.get_table_name())) {
            _creatorTypes.createTable(_db);
        }

        if (!checkTableExists(_creators.get_table_name())) {
            _creators.createTable(_db);
        }

        if (!checkTableExists(_itemCreators.get_table_name())) {
            _itemCreators.createTable(_db);
        }

        if (!checkTableExists(_itemAttachments.get_table_name())) {
            _itemAttachments.createTable(_db);
        }

        if (!checkTableExists(_items.get_table_name())) {
            _items.createTable(_db);
        }

        if (!checkTableExists(_itemTypes.get_table_name())) {
            _itemTypes.createTable(_db);
        }

        if (!checkTableExists(_itemDataTable.get_table_name())) {
            _itemDataTable.createTable(_db);
        }

        if (!checkTableExists(_itemDataValueTable.get_table_name())) {
            _itemDataValueTable.createTable(_db);
        }

        if (!checkTableExists(_fieldsTable.get_table_name())) {
            _fieldsTable.createTable(_db);
        }

        if (!checkTableExists(_collectionsTable.get_table_name())) {
            _collectionsTable.createTable(_db);
        }

        if (!checkTableExists(_collectionsItemsTable.get_table_name())) {
            _collectionsItemsTable.createTable(_db);
        }
    }

    /**
     * Destroy all the saved data and recreate if needed.
     */
    public void reset() {
        if (_db != null){ _db.close();}

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS \"" + _collectionsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _collectionsItemsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemDataTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemDataValueTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _fieldsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _items.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemTypes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemAttachments.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _creatorTypes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _creators.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemCreators.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemTags.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _tags.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + _itemNotes.get_table_name() + "\"");

        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all the tables, presumably in memory
        // We double check to see if we have any database tables already]
        this._db = db;
        _check_and_create();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + _collectionsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _collectionsItemsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemDataTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemDataValueTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _fieldsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _items.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemTypes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemAttachments.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _creatorTypes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _creators.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemCreators.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemTags.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _tags.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + _itemNotes.get_table_name());

        // Create tables again
        onCreate(db);
    }

    protected void clearTable(String tablename){
        _db.execSQL("DELETE from " + tablename);
    }

    // Get the number of rows in a table
    public int getNumRows(String tablename){
        int result = 0;
        Cursor cursor = _db.rawQuery("select count(*) from \"" + tablename + "\";", null);
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    // reads the first cursor
    // TODO - might be a faster way when we are grabbing them all?
    public ContentValues readRow(String tablename, int rownumber) {
        int result = 0;
        ContentValues values = new ContentValues();
        Cursor cursor = _db.rawQuery("select * from \"" + tablename + "\";", null);
        cursor.moveToPosition(rownumber);

        for (int i = 0; i < cursor.getColumnCount(); i++){
            values.put(cursor.getColumnName(i),cursor.getString(i));
        }

        cursor.close();
        return values;
    }

    public Vector<ContentValues> readRowsWhere(String tablename, String whereClause) {
        int result = 0;
        Vector<ContentValues> vecOfValues = new Vector<ContentValues>();
        Cursor cursor = _db.rawQuery(
                "SELECT * FROM \"" + tablename + "\" WHERE "+ whereClause +";", null);

        cursor.moveToFirst();
        while(cursor.getCount()!=0){
            ContentValues values = new ContentValues();
            for (int i = 0; i < cursor.getColumnCount(); i++){
                values.put(cursor.getColumnName(i),cursor.getString(i));
            }
            vecOfValues.add(values);
            if(cursor.isLast())break;
            cursor.moveToNext();
        }

        cursor.close();
        return vecOfValues;
    }

    // Existence methods
    public boolean collectionExists(Collection c) { return _collectionsTable.collectionExists(c,_db); }

    // Update methods
    public void updateCollection(Collection collection) {
        _collectionsTable.updateCollection(collection,_db);
    }

    ///TODO IMPLEMENT
    /*
    // Records also need to update authors and tags and all the rest
    // Easiest way to do this is to delete everything related and re-write (with the exception
    // of attachments which are themselves separate
    public void updateRecord(Record record) {
        deleteRecord(record);
        writeRecord(record);
    }
    */

    public Collection getCollection(String key) {
        return _collectionsTable.getCollection(key,_db);
    }

    public Collection getCollection(int rownum) {
        return Collections.getCollectionFromValues(readRow(_collectionsTable.get_table_name(),rownum));
    }

    public Vector<Collection> getCollectionTree(){
        Vector<Collection> topParents = Collections.getCollectionFromVectorOfValues(
                readRowsWhere(
                        _collectionsTable.get_table_name(),
                        "parentCollectionID is NULL"
                )
        );

        for(int i = 0; i<topParents.size(); i++){
            topParents.set(i, getCollectionTreeRecursive(topParents.get(i)));
        }

        return topParents;
    }

    private Collection getCollectionTreeRecursive(Collection current){
        int id = current.get_collection_id();

        Vector<Collection> children = Collections.getCollectionFromVectorOfValues(
                readRowsWhere(
                        _collectionsTable.get_table_name(),
                        "parentCollectionID="+id
                )
        );

        if(children.size()==0){
            return current;
        }else{
            for(Collection child:children){
                current.add_sub_collection(getCollectionTreeRecursive(child));
            }
            return current;
        }
    }

    /// TODO Implement
    /*
    public Vector<Collection> getCollectionForItem(Record record){
        Vector<CollectionItem> tci = _collectionsItemsTable.getCollectionItemForItem(record,_db);
        Vector<Collection> tc = new Vector<>();

        for (CollectionItem ci : tci) {
            tc.add(_collectionsTable.getCollection(ci.get_collection(),_db));
        }
        return tc;
    }
    */

    /// TODO Implement
    /*
    public Vector<Record> getItemsForCollection (Collection collection){
        Vector<CollectionItem> tci = _collectionsItemsTable.getItemsForCollection(collection.get_collection_id(),_db);
        Vector<Record> tc = new Vector<>();

        for (CollectionItem ci : tci) {
            tc.add(_recordsTable.getRecordByKey(ci.get_item(),_db));
        }
        return tc;
    }
    */

    public Vector<CollectionItem> getCollectionItemsFor (int collectionId){
        return  _collectionsItemsTable.getItemsForCollection(collectionId, _db);
    }

    ///TODO IMPLEMENT
    /*
    public Vector<Record> searchRecords (Collection collection, String searchterm, int end){
        Vector<Record> records = new Vector<Record>();
        _cache.last_num_results = 0;

        // Annoyingly we must search the entire collection - this could be slow in certain cases
        if (collection == null){
            ContentValues values = new ContentValues();
            Cursor cursor = _db.rawQuery("select * from \"" + _recordsTable.get_table_name() + "\";", null);
            while (cursor.moveToNext()){
                values.clear();
                for (int i = 0; i < cursor.getColumnCount(); i++){
                    values.put(cursor.getColumnName(i),cursor.getString(i));
                }

                Record r = _recordsTable.getRecordFromValues(values);
                if (r.search(searchterm) || searchterm.isEmpty()) {
                    _cache.last_num_results +=1;
                    if (records.size() < end) {
                        records.add(r);
                    }
                };
            }
            cursor.close();
        } else {
            Vector<CollectionItem> tci = _collectionsItemsTable.getItemsForCollection(collection.get_key(),_db);
            for (CollectionItem ci : tci) {
                Record r = _recordsTable.getRecordByKey(ci.get_item(),_db);
                if (r.search(searchterm) || searchterm.isEmpty()) {
                    _cache.last_num_results += 1;
                    if (records.size() < end) {
                        records.add(r);
                    }
                }
            }
        }
        _cache.valid = true;
        _cache.last_search = searchterm;

        return records;
    }

    // This is slow, so we cache the results of the previous search operation for extra speed
    public int getNumRecordsSearch (Collection collection, String searchterm) {
        int tt = 0;
        if (_cache.last_search.equals(searchterm)) { return _cache.last_num_results; }

        if (collection == null){
            ContentValues values = new ContentValues();
            Cursor cursor = _db.rawQuery("select count(*) from \"" + _recordsTable.get_table_name() + "\";", null);
            while (cursor.moveToNext() ){
                values.clear();
                for (int i = 0; i < cursor.getColumnCount(); i++){
                    values.put(cursor.getColumnName(i),cursor.getString(i));
                }
                Record r = _recordsTable.getRecordFromValues(values);
                if (r.search(searchterm) || searchterm.isEmpty()) { tt+=1; };
            }

            cursor.close();
        } else {
            Vector<CollectionItem> tci = _collectionsItemsTable.getItemsForCollection(collection.get_key(),_db);

            for (CollectionItem ci : tci) {
                Record r = _recordsTable.getRecordByKey(ci.get_item(),_db);
                if (r.search(searchterm) || searchterm.isEmpty()) { tt+=1; };
            }
        }
        return tt;
    }
    */

    // Get number methods
    public int getNumCollections () { return getNumRows(_collectionsTable.get_table_name()); }
    public int getNumCollectionsItems () { return getNumRows(_collectionsItemsTable.get_table_name()); }

    // Write methods
    public void writeCollection(Collection collection){ _collectionsTable.writeCollection(collection,_db); }
    public void writeCollectionItem(CollectionItem ic){ _collectionsItemsTable.writeCollection(ic,_db); }



    ///TODO IMPLEMENT
    /*
    public void deleteRecord(Record r) {
        _recordsTable.deleteRecord(r,_db);
        _collectionsItemsTable.deleteByRecord(r,_db);
        _authorsTable.deleteByRecord(r,_db);
        _tagsTable.deleteByRecord(r,_db);
        _notesTable.deleteByRecord(r,_db);
    }
    public void deleteCollection(Collection c) {
        _collectionsTable.deleteCollection(c,_db);
        _collectionsItemsTable.deleteByCollection(c,_db);
    }

    public void removeRecordFromCollections(Record r){
        _collectionsItemsTable.deleteByRecord(r,_db);
    }
    */

    public Vector<String> getTitlesWithItemIds(Vector<Integer> itemIds){
        int fieldIdForTitle = _fieldsTable.getFieldIdFor("title", _db);
        Vector<String> values = new Vector<String>();
        for(int id:itemIds){
            Cursor cursor = _db.rawQuery(
                    "SELECT valueID FROM \"" + _itemDataTable.get_table_name() + "\" WHERE " +
                            "itemID=\"" + id + "\" AND fieldID=\""+ fieldIdForTitle +"\";",
                    null);
            while (cursor.moveToNext()){
                int vIndex = cursor.getColumnIndex("valueID");
                values.add(_itemDataValueTable.getValueFor(vIndex,_db));
            }
            cursor.close();
        }

        return values;
    }

    public Vector<String> getTitlesWithCollectionItems(Vector<CollectionItem> items){
        int fieldIdForTitle = _fieldsTable.getFieldIdFor("title", _db);
        Vector<String> values = new Vector<String>();
        for(CollectionItem item:items){
            Cursor cursor = _db.rawQuery(
                    "SELECT valueID FROM \"" + _itemDataTable.get_table_name() + "\" WHERE " +
                            "itemID=\"" + item.get_itemId() + "\" AND fieldID=\""+ fieldIdForTitle +"\";",
                    null);
            while (cursor.moveToNext()){
                int _valueID = cursor.getInt(0);
                values.add(_itemDataValueTable.getValueFor(_valueID,_db));
            }
            cursor.close();
        }

        return values;
    }

    public String getTitlesWithCollectionItems(CollectionItem item){
        Vector<CollectionItem> collectionItems = new Vector<>();
        collectionItems.add(item);
        Vector<String> retVals = getTitlesWithCollectionItems(collectionItems);
        return retVals.get(0);
    }

    // gets the type of the each document. (not files)
    public Vector<Integer> getTypeIdsWithCollectionItems(Vector<CollectionItem> items){
        Vector<Integer> typeIds = new Vector<Integer>();
        for(CollectionItem item:items){
            // we are getting typeID for the item not it's attachments so we use _items.getTypeIdFor and not _itemAttachments.getFileItemIdFast
            int itemTypeId = _items.getTypeIdFor(item.get_itemId(), _db);
            typeIds.add(itemTypeId);
        }
        return typeIds;
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        ItemDetailed itemDetailed = new ItemDetailed();

        itemDetailed.setParentCollection(parentCollection);
        itemDetailed.setCollectionItem(collectionItem);
        itemDetailed.setItemTitle(getTitlesWithCollectionItems(collectionItem)); // title

        int itemTypeId__ = _items.getTypeIdFor(collectionItem.get_itemId(), _db);
        itemDetailed.setItemTypeId(itemTypeId__); //typeId
        itemDetailed.setItemType(_itemTypes.getTypeNameFor(itemTypeId__, _db)); //typeName

        Vector<FieldValuePair> fields =  _itemDataTable.getFieldValuePairsFor(collectionItem.get_itemId(), _db); //fieldId, valueId
        fields = _fieldsTable.resolveFieldNamesFor(fields,_db); //fieldName
        fields = _itemDataValueTable.resolveFieldValuesFor(fields, _db); //fieldValue
        itemDetailed.setItemFields(fields); //fields including abstract (index 90)

        itemDetailed.setItemCreators(getCreatorsFor(collectionItem)); // creators (authors)

        Vector<ItemTag> tags = _itemTags.getTagsFor(collectionItem.get_itemId(), _db);
        tags= _tags.getTagsDetails(tags, _db);
        itemDetailed.setItemTags(tags);

        Vector<ItemNote> notes = _itemNotes.getNotesFor(collectionItem.get_itemId(), _db);
        itemDetailed.setItemNotes(notes);

        Vector<ItemAttachment> attachments = _itemAttachments.getAttachmentFor(collectionItem, _db);
        attachments = _items.getDetailsOfAttachmentsFor(attachments, _db);
        for(ItemAttachment attachment:attachments){
            int __typeId = _items.getTypeIdFor(attachment.getFileItemId(), _db);
            attachment.setFileItemTypeId(__typeId);
            attachment.setFileItemType(_itemTypes.getTypeNameFor(__typeId, _db));
        }
        itemDetailed.setItemAttachments(attachments);

        return itemDetailed;
    }

    public Vector<Creator> getCreatorsFor(CollectionItem collectionItem){
        Vector<Creator> creators = _itemCreators.getCreatorsIdsFor(collectionItem.get_itemId(), _db);
        creators = _creators.getCreatorsDetails(creators, _db);
        creators = _creatorTypes.getCreatorsTypes(creators, _db);

        return creators;
    }

}
