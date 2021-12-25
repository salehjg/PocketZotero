package io.github.salehjg.pocketzotero.zoteroengine.types;

public class CollectionItem {
    public int get_collectionId() {
        return _collectionId;
    }

    public void set_collectionId(int _collectionId) {
        this._collectionId = _collectionId;
    }

    public int get_itemId() {
        return _itemId;
    }

    public void set_itemId(int _itemId) {
        this._itemId = _itemId;
    }

    public int get_orderIndex() {
        return _orderIndex;
    }

    public void set_orderIndex(int _orderIndex) {
        this._orderIndex = _orderIndex;
    }

    protected int    _collectionId;
    protected int    _itemId;
    protected int    _orderIndex;

    public String toString() {
        return _collectionId + " -> " + _itemId;
    }
}