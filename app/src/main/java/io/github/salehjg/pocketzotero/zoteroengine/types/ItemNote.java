package io.github.salehjg.pocketzotero.zoteroengine.types;

public class ItemNote {
    private int noteItemId, parentItemId;
    private String note, title;

    public ItemNote(int noteItemId, int parentItemId, String note, String title){
        this.noteItemId = noteItemId;
        this.parentItemId = parentItemId;
        this.note = note;
        this.title = title;
    }

    public ItemNote(){
        this.noteItemId = -1;
        this.parentItemId = -1;
        this.note = "?";
        this.title = "?";
    }

    public int getNoteItemId() {
        return noteItemId;
    }

    public void setNoteItemId(int noteItemId) {
        this.noteItemId = noteItemId;
    }

    public int getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(int parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
