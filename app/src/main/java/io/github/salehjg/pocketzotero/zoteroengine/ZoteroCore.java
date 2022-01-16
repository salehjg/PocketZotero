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
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemTypeCreatorTypes;
import io.github.salehjg.pocketzotero.zoteroengine.tables.ItemTypeFields;
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

public class ZoteroCore extends SQLiteOpenHelper {

    // All Static variables
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "zotero.sqlite";

    private SQLiteDatabase mDb;

    private Collections mCollectionsTable = new Collections();
    private CollectionsItems mCollectionsItemsTable = new CollectionsItems();
    private ItemData mItemDataTable = new ItemData();
    private ItemDataValues mItemDataValueTable = new ItemDataValues();
    private Fields mFieldsTable = new Fields();
    private Items mItems = new Items();
    private ItemTypes mItemTypes = new ItemTypes();
    private ItemAttachments mItemAttachments = new ItemAttachments();
    private CreatorTypes mCreatorTypes = new CreatorTypes();
    private Creators mCreators = new Creators();
    private ItemCreators mItemCreators = new ItemCreators();
    private ItemTags mItemTags = new ItemTags();
    private Tags mTags = new Tags();
    private ItemNotes mItemNotes = new ItemNotes();
    private ItemTypeCreatorTypes mItemTypeCreatorTypes = new ItemTypeCreatorTypes();
    private ItemTypeFields mItemTypeFields = new ItemTypeFields();


    public ZoteroCore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mDb = getWritableDatabase(); // Should kickstart all the creation we need :S
        checkTablesAndCreateIfNeeded();
    }

    /**
     * Alternative constructor for if we change the database location
     * @param context
     * @param alternativeFname
     */
    public ZoteroCore(Context context, String alternativeFname) {
        // Double check we have no trailing slash
        super(context, alternativeFname, null, DATABASE_VERSION);
        this.mDb = getWritableDatabase();
        checkTablesAndCreateIfNeeded();
    }

    protected boolean checkTableExists(String tablename){
        Cursor cursor = mDb.rawQuery("SELECT tbl_name FROM sqlite_master WHERE type=\"table\" AND tbl_name = \""+ tablename +"\"", null);
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

    private void checkTablesAndCreateIfNeeded() {

        if (!checkTableExists(mItemTypeFields.get_table_name())) {
            mItemTypeFields.createTable(mDb);
        }

        if (!checkTableExists(mItemTypeCreatorTypes.get_table_name())) {
            mItemTypeCreatorTypes.createTable(mDb);
        }

        if (!checkTableExists(mItemNotes.get_table_name())) {
            mItemNotes.createTable(mDb);
        }

        if (!checkTableExists(mItemTags.get_table_name())) {
            mItemTags.createTable(mDb);
        }

        if (!checkTableExists(mTags.get_table_name())) {
            mTags.createTable(mDb);
        }

        if (!checkTableExists(mCreatorTypes.get_table_name())) {
            mCreatorTypes.createTable(mDb);
        }

        if (!checkTableExists(mCreators.get_table_name())) {
            mCreators.createTable(mDb);
        }

        if (!checkTableExists(mItemCreators.get_table_name())) {
            mItemCreators.createTable(mDb);
        }

        if (!checkTableExists(mItemAttachments.get_table_name())) {
            mItemAttachments.createTable(mDb);
        }

        if (!checkTableExists(mItems.get_table_name())) {
            mItems.createTable(mDb);
        }

        if (!checkTableExists(mItemTypes.get_table_name())) {
            mItemTypes.createTable(mDb);
        }

        if (!checkTableExists(mItemDataTable.get_table_name())) {
            mItemDataTable.createTable(mDb);
        }

        if (!checkTableExists(mItemDataValueTable.get_table_name())) {
            mItemDataValueTable.createTable(mDb);
        }

        if (!checkTableExists(mFieldsTable.get_table_name())) {
            mFieldsTable.createTable(mDb);
        }

        if (!checkTableExists(mCollectionsTable.get_table_name())) {
            mCollectionsTable.createTable(mDb);
        }

        if (!checkTableExists(mCollectionsItemsTable.get_table_name())) {
            mCollectionsItemsTable.createTable(mDb);
        }
    }

    /**
     * Destroy all the saved data and recreate if needed.
     */
    public void reset() {
        if (mDb != null){ mDb.close();}

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS \"" + mCollectionsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mCollectionsItemsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemDataTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemDataValueTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mFieldsTable.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItems.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemTypes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemAttachments.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mCreatorTypes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mCreators.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemCreators.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemTags.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mTags.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemNotes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemTypeCreatorTypes.get_table_name() + "\"");
        db.execSQL("DROP TABLE IF EXISTS \"" + mItemTypeFields.get_table_name() + "\"");

        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all the tables, presumably in memory
        // We double check to see if we have any database tables already]
        this.mDb = db;
        checkTablesAndCreateIfNeeded();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + mCollectionsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mCollectionsItemsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemDataTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemDataValueTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mFieldsTable.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItems.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemTypes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemAttachments.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mCreatorTypes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mCreators.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemCreators.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemTags.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mTags.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemNotes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemTypeCreatorTypes.get_table_name());
        db.execSQL("DROP TABLE IF EXISTS " + mItemTypeFields.get_table_name());

        // Create tables again
        onCreate(db);
    }

    protected void clearTable(String tablename){
        mDb.execSQL("DELETE from " + tablename);
    }

    // Get the number of rows in a table
    public int getNumRows(String tablename){
        int result = 0;
        Cursor cursor = mDb.rawQuery("select count(*) from \"" + tablename + "\";", null);
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
        Cursor cursor = mDb.rawQuery("select * from \"" + tablename + "\";", null);
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
        Cursor cursor = mDb.rawQuery(
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
    public boolean collectionExists(Collection c) { return mCollectionsTable.collectionExists(c, mDb); }

    // Update methods
    public void updateCollection(Collection collection) {
        mCollectionsTable.updateCollection(collection, mDb);
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
        return mCollectionsTable.getCollection(key, mDb);
    }

    public Collection getCollection(int rownum) {
        return Collections.getCollectionFromValues(readRow(mCollectionsTable.get_table_name(),rownum));
    }

    public Vector<Collection> getCollectionTree(){
        Vector<Collection> topParents = Collections.getCollectionFromVectorOfValues(
                readRowsWhere(
                        mCollectionsTable.get_table_name(),
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
                        mCollectionsTable.get_table_name(),
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
        return  mCollectionsItemsTable.getItemsForCollection(collectionId, mDb);
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
    public int getNumCollections () { return getNumRows(mCollectionsTable.get_table_name()); }
    public int getNumCollectionsItems () { return getNumRows(mCollectionsItemsTable.get_table_name()); }

    // Write methods
    public void writeCollection(Collection collection){ mCollectionsTable.writeCollection(collection, mDb); }
    public void writeCollectionItem(CollectionItem ic){ mCollectionsItemsTable.writeCollection(ic, mDb); }



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
        int fieldIdForTitle = mFieldsTable.getFieldIdFor("title", mDb);
        Vector<String> values = new Vector<String>();
        for(int id:itemIds){
            Cursor cursor = mDb.rawQuery(
                    "SELECT valueID FROM \"" + mItemDataTable.get_table_name() + "\" WHERE " +
                            "itemID=\"" + id + "\" AND fieldID=\""+ fieldIdForTitle +"\";",
                    null);
            while (cursor.moveToNext()){
                int vIndex = cursor.getColumnIndex("valueID");
                values.add(mItemDataValueTable.getValueFor(vIndex, mDb));
            }
            cursor.close();
        }

        return values;
    }

    public Vector<String> getTitlesWithCollectionItems(Vector<CollectionItem> items){
        int fieldIdForTitle = mFieldsTable.getFieldIdFor("title", mDb);
        Vector<String> values = new Vector<String>();
        for(CollectionItem item:items){
            Cursor cursor = mDb.rawQuery(
                    "SELECT valueID FROM \"" + mItemDataTable.get_table_name() + "\" WHERE " +
                            "itemID=\"" + item.get_itemId() + "\" AND fieldID=\""+ fieldIdForTitle +"\";",
                    null);
            while (cursor.moveToNext()){
                int _valueID = cursor.getInt(0);
                values.add(mItemDataValueTable.getValueFor(_valueID, mDb));
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
            int itemTypeId = mItems.getTypeIdFor(item.get_itemId(), mDb);
            typeIds.add(itemTypeId);
        }
        return typeIds;
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        ItemDetailed itemDetailed = new ItemDetailed();

        itemDetailed.setParentCollection(parentCollection);
        itemDetailed.setCollectionItem(collectionItem);
        itemDetailed.setItemTitle(getTitlesWithCollectionItems(collectionItem)); // title

        int itemTypeId__ = mItems.getTypeIdFor(collectionItem.get_itemId(), mDb);
        itemDetailed.setItemTypeId(itemTypeId__); //typeId
        itemDetailed.setItemType(mItemTypes.getTypeNameFor(itemTypeId__, mDb)); //typeName

        Vector<FieldValuePair> fields =  mItemDataTable.getFieldValuePairsFor(collectionItem.get_itemId(), mDb); //fieldId, valueId
        fields = mFieldsTable.resolveFieldNamesFor(fields, mDb); //fieldName
        fields = mItemDataValueTable.resolveFieldValuesFor(fields, mDb); //fieldValue
        itemDetailed.setItemFields(fields); //fields including abstract (index 90)

        itemDetailed.setItemCreators(getCreatorsFor(collectionItem)); // creators (authors)

        Vector<ItemTag> tags = mItemTags.getTagsFor(collectionItem.get_itemId(), mDb);
        tags= mTags.getTagsDetails(tags, mDb);
        itemDetailed.setItemTags(tags);

        Vector<ItemNote> notes = mItemNotes.getNotesFor(collectionItem.get_itemId(), mDb);
        itemDetailed.setItemNotes(notes);

        Vector<ItemAttachment> attachments = mItemAttachments.getAttachmentFor(collectionItem, mDb);
        attachments = mItems.getDetailsOfAttachmentsFor(attachments, mDb);
        for(ItemAttachment attachment:attachments){
            int __typeId = mItems.getTypeIdFor(attachment.getFileItemId(), mDb);
            attachment.setFileItemTypeId(__typeId);
            attachment.setFileItemType(mItemTypes.getTypeNameFor(__typeId, mDb));
        }
        itemDetailed.setItemAttachments(attachments);

        return itemDetailed;
    }

    public Vector<Creator> getCreatorsFor(CollectionItem collectionItem){
        Vector<Creator> creators = mItemCreators.getCreatorsIdsFor(collectionItem.get_itemId(), mDb);
        creators = mCreators.getCreatorsDetails(creators, mDb);
        creators = mCreatorTypes.getCreatorsTypes(creators, mDb);

        return creators;
    }

    public Vector<String> getItemTypeNames(){
        Vector<String> itemTypeNames = mItemTypes.getTypeNames(mDb);
        return itemTypeNames;
    }

    public Vector<Creator> getPossibleCreatorTypesFor(int itemTypeId){
        Vector<Creator> creatorTypeIds = mItemTypeCreatorTypes.getCreatorTypeIdsFor(itemTypeId, mDb);
        creatorTypeIds = mCreatorTypes.getCreatorsTypes(creatorTypeIds, mDb);
        return creatorTypeIds;
    }

    public Vector<Creator> getPossibleCreatorTypesFor(String itemTypeName){
        int itemTypeId = mItemTypes.getItemTypeIdFor(itemTypeName, mDb);
        if(itemTypeId==-1) return null;
        return getPossibleCreatorTypesFor(itemTypeId);
    }

    public Vector<FieldValuePair> getPossibleFieldsFor(int itemTypeId){
        Vector<FieldValuePair> fieldIds = mItemTypeFields.getFieldIdsFor(itemTypeId, mDb);
        fieldIds = mFieldsTable.resolveFieldNamesFor(fieldIds, mDb);
        return fieldIds;
    }

    public Vector<FieldValuePair> getPossibleFieldsFor(String itemTypeName){
        int itemTypeId = mItemTypes.getItemTypeIdFor(itemTypeName, mDb);
        if(itemTypeId==-1) return null;
        return getPossibleFieldsFor(itemTypeId);
    }

}
