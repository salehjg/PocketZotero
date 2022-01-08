package io.github.salehjg.pocketzotero.zoteroengine.types;

import java.io.Serializable;

public class ItemNote implements Serializable {
    protected int mNoteItemId, mParentItemId;
    protected String mNote, mTitle;

    public ItemNote(int noteItemId, int parentItemId, String note, String title){
        this.mNoteItemId = noteItemId;
        this.mParentItemId = parentItemId;
        this.mNote = note;
        this.mTitle = title;
    }

    public ItemNote(){
        this.mNoteItemId = -1;
        this.mParentItemId = -1;
        this.mNote = "?";
        this.mTitle = "?";
    }

    public int getNoteItemId() {
        return mNoteItemId;
    }

    public void setNoteItemId(int noteItemId) {
        this.mNoteItemId = noteItemId;
    }

    public int getParentItemId() {
        return mParentItemId;
    }

    public void setParentItemId(int parentItemId) {
        this.mParentItemId = parentItemId;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
