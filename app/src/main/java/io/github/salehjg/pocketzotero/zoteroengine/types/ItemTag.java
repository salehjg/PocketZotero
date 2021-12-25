package io.github.salehjg.pocketzotero.zoteroengine.types;

public class ItemTag {
    private String tagName;
    private int tagId, itemId, type;

    public ItemTag(){
        this.itemId = -1;
        this.tagId = -1;
        this.type = -1;
        this.tagName = "?";
    }

    public ItemTag(
            int itemId,
            int tagId,
            String tagName,
            int type){
        this.itemId = itemId;
        this.tagId = tagId;
        this.type = type;
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
