package io.github.salehjg.pocketzotero.zoteroengine.types;

public class ItemAttachment {
    private int fileItemId, parentItemId, linkMode, syncState;                      // table: itemAttachments
    private String contentType, filePath, storageModTime, storageHash, charsetId;   // table: itemAttachments

    private int fileItemTypeId, fileLibraryId, fileVersion, fileSynced;                             // table: items
    private String fileDateAdded, fileItemType, fileKey, fileDateModified, fileClientModified;      // table: items

    public String getFileItemType() {
        return fileItemType;
    }

    public void setFileItemType(String fileItemType) {
        this.fileItemType = fileItemType;
    }

    public int getFileItemId() {
        return fileItemId;
    }

    public void setFileItemId(int fileItemId) {
        this.fileItemId = fileItemId;
    }

    public int getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(int parentItemId) {
        this.parentItemId = parentItemId;
    }

    public int getLinkMode() {
        return linkMode;
    }

    public void setLinkMode(int linkMode) {
        this.linkMode = linkMode;
    }

    public int getSyncState() {
        return syncState;
    }

    public void setSyncState(int syncState) {
        this.syncState = syncState;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String ExtractFileName(){
        String storageFileNamePair = getFilePath();
        if(!storageFileNamePair.isEmpty()) {
            int i = storageFileNamePair.indexOf(":");
            return storageFileNamePair.substring(i + 1);
        }else{
            return "";
        }
    }

    public String ExtractStorageDirName(){
        String storageFileNamePair = getFilePath();
        if(!storageFileNamePair.isEmpty()) {
            int i = storageFileNamePair.indexOf(":");
            return storageFileNamePair.substring(0, i);
        }else{
            return "";
        }
    }

    public String getStorageModTime() {
        return storageModTime;
    }

    public void setStorageModTime(String storageModTime) {
        this.storageModTime = storageModTime;
    }

    public String getStorageHash() {
        return storageHash;
    }

    public void setStorageHash(String storageHash) {
        this.storageHash = storageHash;
    }

    public String getCharsetId() {
        return charsetId;
    }

    public void setCharsetId(String charsetId) {
        this.charsetId = charsetId;
    }

    public int getFileItemTypeId() {
        return fileItemTypeId;
    }

    public void setFileItemTypeId(int fileItemTypeId) {
        this.fileItemTypeId = fileItemTypeId;
    }

    public int getFileLibraryId() {
        return fileLibraryId;
    }

    public void setFileLibraryId(int fileLibraryId) {
        this.fileLibraryId = fileLibraryId;
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    public int getFileSynced() {
        return fileSynced;
    }

    public void setFileSynced(int fileSynced) {
        this.fileSynced = fileSynced;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileDateAdded() {
        return fileDateAdded;
    }

    public void setFileDateAdded(String fileDateAdded) {
        this.fileDateAdded = fileDateAdded;
    }

    public String getFileDateModified() {
        return fileDateModified;
    }

    public void setFileDateModified(String fileDateModified) {
        this.fileDateModified = fileDateModified;
    }

    public String getFileClientModified() {
        return fileClientModified;
    }

    public void setFileClientModified(String fileClientModified) {
        this.fileClientModified = fileClientModified;
    }

}
