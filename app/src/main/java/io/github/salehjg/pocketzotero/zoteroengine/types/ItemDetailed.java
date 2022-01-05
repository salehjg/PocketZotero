package io.github.salehjg.pocketzotero.zoteroengine.types;

import java.util.Vector;

import io.github.salehjg.pocketzotero.utils.UnifiedTypes;

public class ItemDetailed {
    protected Collection mParentCollection;
    protected CollectionItem mCollectionItem;
    protected String mItemTitle, mItemType;
    protected int mItemTypeId;
    protected Vector<FieldValuePair> mItemFields;
    protected Vector<Creator> mItemCreators;
    protected Vector<ItemTag> mItemTags;
    protected Vector<ItemNote> mItemNotes;
    protected Vector<ItemAttachment> mItemAttachments;

    public Vector<ItemNote> getItemNotes() {
        return mItemNotes;
    }

    public void setItemNotes(Vector<ItemNote> itemNotes) {
        this.mItemNotes = itemNotes;
    }

    public Vector<ItemTag> getItemTags() {
        return mItemTags;
    }

    public void setItemTags(Vector<ItemTag> itemTags) {
        this.mItemTags = itemTags;
    }

    public Vector<Creator> getItemCreators() {
        return mItemCreators;
    }

    public void setItemCreators(Vector<Creator> itemCreators) {
        this.mItemCreators = itemCreators;
    }

    public Collection getParentCollection() {
        return mParentCollection;
    }

    public void setParentCollection(Collection parentCollection) {
        this.mParentCollection = parentCollection;
    }

    public String getItemTitle() {
        return mItemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.mItemTitle = itemTitle;
    }

    public int getAbstractsFieldIndex(){
        boolean hasAbstract = false;
        int i=0;
        for(FieldValuePair pair: mItemFields){
            if(pair.get_fieldId()==90) { //abstract
                hasAbstract = true;
                break;
            }
            i++;
        }
        return hasAbstract?i:-1;
    }

    public String getAbstractTry(){
        int abstractFieldIndexIfExists = getAbstractsFieldIndex();
        if(abstractFieldIndexIfExists!=-1){
            return mItemFields.get(abstractFieldIndexIfExists).get_value();
        }else{
            return "";
        }
    }

    public String getItemType() {
        return mItemType;
    }

    public void setItemType(String itemType) {
        this.mItemType = itemType;
    }

    public Vector<FieldValuePair> getItemFields() {
        return mItemFields;
    }

    public void setItemFields(Vector<FieldValuePair> itemFields) {
        this.mItemFields = itemFields;
    }

    public Vector<ItemAttachment> getItemAttachments() {
        return mItemAttachments;
    }

    public void setItemAttachments(Vector<ItemAttachment> itemAttachments) {
        this.mItemAttachments = itemAttachments;
    }

    public CollectionItem getCollectionItem() {
        return mCollectionItem;
    }

    public void setCollectionItem(CollectionItem collectionItem) {
        this.mCollectionItem = collectionItem;
    }

    public int getItemTypeId() {
        return mItemTypeId;
    }

    public void setItemTypeId(int itemTypeId) {
        this.mItemTypeId = itemTypeId;
    }

    public ItemDetailed(){
        mParentCollection = null;
        mCollectionItem = null;
        mItemTitle = mItemType = "";
        mItemFields = new Vector<>();
        mItemAttachments = new Vector<>();
        mItemTags = new Vector<>();
        mItemNotes = new Vector<>();
    }

    public ItemDetailed(
            Collection collection,
            CollectionItem collectionItem,
            String title,
            String type,
            int typeId,
            Vector<FieldValuePair> fields,
            Vector<Creator> creators,
            Vector<ItemTag> itemTags,
            Vector<ItemNote> itemNotes,
            Vector<ItemAttachment> attachments){
        mParentCollection = collection;
        this.mCollectionItem = collectionItem;
        mItemTitle = title;
        mItemType = type;
        mItemTypeId = typeId;
        mItemFields = fields;
        mItemCreators = creators;
        this.mItemTags = itemTags;
        this.mItemNotes = itemNotes;
        mItemAttachments = attachments;
    }

    public Vector<UnifiedElement> getUnifiedElements(){

        // Unifies the Vector members into a single vector of type UnifiedElement
        Vector<UnifiedElement> elements = new Vector<>();

        if(this.mItemCreators !=null){
            for(Creator creator: mItemCreators) {
                elements.add(new UnifiedElement(UnifiedTypes.AUTHOR, creator, null, null, null));
            }
        }

        if(this.mItemFields !=null){
            for(FieldValuePair field: mItemFields) {
                elements.add(new UnifiedElement(UnifiedTypes.FIELD, null, field, null, null));
            }
        }

        if(this.mItemTags !=null){
            for(ItemTag itemTag: mItemTags) {
                elements.add(new UnifiedElement(UnifiedTypes.TAG, null, null, itemTag, null));
            }
        }

        if(this.mItemNotes !=null){
            for(ItemNote itemNote: mItemNotes) {
                elements.add(new UnifiedElement(UnifiedTypes.NOTE, null, null, null, itemNote));
            }
        }

        return elements;
    }
}
