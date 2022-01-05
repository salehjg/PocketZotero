package io.github.salehjg.pocketzotero.zoteroengine.types;

public class CollectionItem {
    public int get_collectionId() {
        return mCollectionId;
    }

    public void set_collectionId(int _collectionId) {
        this.mCollectionId = _collectionId;
    }

    public int get_itemId() {
        return mItemId;
    }

    public void set_itemId(int _itemId) {
        this.mItemId = _itemId;
    }

    public int get_orderIndex() {
        return mOrderIndex;
    }

    public void set_orderIndex(int _orderIndex) {
        this.mOrderIndex = _orderIndex;
    }

    protected int mCollectionId;
    protected int mItemId;
    protected int mOrderIndex;

    public String toString() {
        return mCollectionId + " -> " + mItemId;
    }
}