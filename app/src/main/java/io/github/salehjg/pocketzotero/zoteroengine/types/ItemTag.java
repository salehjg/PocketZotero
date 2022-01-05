package io.github.salehjg.pocketzotero.zoteroengine.types;

public class ItemTag {
    protected String mTagName;
    protected int mTagId, mItemId, mType;

    public ItemTag(){
        this.mItemId = -1;
        this.mTagId = -1;
        this.mType = -1;
        this.mTagName = "?";
    }

    public ItemTag(
            int itemId,
            int tagId,
            String tagName,
            int type){
        this.mItemId = itemId;
        this.mTagId = tagId;
        this.mType = type;
        this.mTagName = tagName;
    }

    public String getTagName() {
        return mTagName;
    }

    public void setTagName(String tagName) {
        this.mTagName = tagName;
    }

    public int getTagId() {
        return mTagId;
    }

    public void setTagId(int tagId) {
        this.mTagId = tagId;
    }

    public int getItemId() {
        return mItemId;
    }

    public void setItemId(int itemId) {
        this.mItemId = itemId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }
}
