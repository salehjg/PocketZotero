package io.github.salehjg.pocketzotero.zoteroengine.types;

import java.util.Vector;

import io.github.salehjg.pocketzotero.utils.UnifiedTypes;

public class ItemDetailed {
    private Collection parentCollection;
    private CollectionItem collectionItem;
    private String itemTitle, itemType;
    private int itemTypeId;
    private Vector<FieldValuePair> itemFields;
    private Vector<Creator> itemCreators;
    private Vector<ItemTag> itemTags;
    private Vector<ItemNote> itemNotes;
    private Vector<ItemAttachment> itemAttachments;

    public Vector<ItemNote> getItemNotes() {
        return itemNotes;
    }

    public void setItemNotes(Vector<ItemNote> itemNotes) {
        this.itemNotes = itemNotes;
    }

    public Vector<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(Vector<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }

    public Vector<Creator> getItemCreators() {
        return itemCreators;
    }

    public void setItemCreators(Vector<Creator> itemCreators) {
        this.itemCreators = itemCreators;
    }

    public Collection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(Collection parentCollection) {
        this.parentCollection = parentCollection;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public int getAbstractsFieldIndex(){
        boolean hasAbstract = false;
        int i=0;
        for(FieldValuePair pair:itemFields){
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
            return itemFields.get(abstractFieldIndexIfExists).get_value();
        }else{
            return "";
        }
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Vector<FieldValuePair> getItemFields() {
        return itemFields;
    }

    public void setItemFields(Vector<FieldValuePair> itemFields) {
        this.itemFields = itemFields;
    }

    public Vector<ItemAttachment> getItemAttachments() {
        return itemAttachments;
    }

    public void setItemAttachments(Vector<ItemAttachment> itemAttachments) {
        this.itemAttachments = itemAttachments;
    }

    public CollectionItem getCollectionItem() {
        return collectionItem;
    }

    public void setCollectionItem(CollectionItem collectionItem) {
        this.collectionItem = collectionItem;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(int itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public ItemDetailed(){
        parentCollection = null;
        collectionItem = null;
        itemTitle = itemType = "";
        itemFields = new Vector<>();
        itemAttachments = new Vector<>();
        itemTags = new Vector<>();
        itemNotes = new Vector<>();
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
        parentCollection = collection;
        this.collectionItem = collectionItem;
        itemTitle = title;
        itemType = type;
        itemTypeId = typeId;
        itemFields = fields;
        itemCreators = creators;
        this.itemTags = itemTags;
        this.itemNotes = itemNotes;
        itemAttachments = attachments;
    }

    public Vector<UnifiedElement> getUnifiedElements(){

        // Unifies the Vector members into a single vector of type UnifiedElement
        Vector<UnifiedElement> elements = new Vector<>();

        if(this.itemCreators!=null){
            for(Creator creator:itemCreators) {
                elements.add(new UnifiedElement(UnifiedTypes.AUTHOR, creator, null, null, null));
            }
        }

        if(this.itemFields!=null){
            for(FieldValuePair field:itemFields) {
                elements.add(new UnifiedElement(UnifiedTypes.FIELD, null, field, null, null));
            }
        }

        if(this.itemTags!=null){
            for(ItemTag itemTag:itemTags) {
                elements.add(new UnifiedElement(UnifiedTypes.TAG, null, null, itemTag, null));
            }
        }

        if(this.itemNotes!=null){
            for(ItemNote itemNote:itemNotes) {
                elements.add(new UnifiedElement(UnifiedTypes.NOTE, null, null, null, itemNote));
            }
        }

        return elements;
    }
}
