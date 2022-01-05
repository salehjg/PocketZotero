package io.github.salehjg.pocketzotero.zoteroengine.types;

public class ItemAttachment {
    protected int mFileItemId, mParentItemId, mLinkMode, mSyncState;                      // table: itemAttachments
    protected String mContentType, mFilePath, mStorageModTime, mStorageHash, mCharsetId;   // table: itemAttachments
    protected int mFileItemTypeId, mFileLibraryId, mFileVersion, mFileSynced;                             // table: items
    protected String mFileDateAdded, mFileItemType, mFileKey, mFileDateModified, mFileClientModified;      // table: items

    public String getFileItemType() {
        return mFileItemType;
    }

    public void setFileItemType(String fileItemType) {
        this.mFileItemType = fileItemType;
    }

    public int getFileItemId() {
        return mFileItemId;
    }

    public void setFileItemId(int fileItemId) {
        this.mFileItemId = fileItemId;
    }

    public int getParentItemId() {
        return mParentItemId;
    }

    public void setParentItemId(int parentItemId) {
        this.mParentItemId = parentItemId;
    }

    public int getLinkMode() {
        return mLinkMode;
    }

    public void setLinkMode(int linkMode) {
        this.mLinkMode = linkMode;
    }

    public int getSyncState() {
        return mSyncState;
    }

    public void setSyncState(int syncState) {
        this.mSyncState = syncState;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        this.mContentType = contentType;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public String extractFileName(){
        String storageFileNamePair = getFilePath();
        if(!storageFileNamePair.isEmpty()) {
            int i = storageFileNamePair.indexOf(":");
            return storageFileNamePair.substring(i + 1);
        }else{
            return "";
        }
    }

    public String extractStorageDirName(){
        String storageFileNamePair = getFilePath();
        if(!storageFileNamePair.isEmpty()) {
            int i = storageFileNamePair.indexOf(":");
            return storageFileNamePair.substring(0, i);
        }else{
            return "";
        }
    }

    public String getStorageModTime() {
        return mStorageModTime;
    }

    public void setStorageModTime(String storageModTime) {
        this.mStorageModTime = storageModTime;
    }

    public String getStorageHash() {
        return mStorageHash;
    }

    public void setStorageHash(String storageHash) {
        this.mStorageHash = storageHash;
    }

    public String getCharsetId() {
        return mCharsetId;
    }

    public void setCharsetId(String charsetId) {
        this.mCharsetId = charsetId;
    }

    public int getFileItemTypeId() {
        return mFileItemTypeId;
    }

    public void setFileItemTypeId(int fileItemTypeId) {
        this.mFileItemTypeId = fileItemTypeId;
    }

    public int getFileLibraryId() {
        return mFileLibraryId;
    }

    public void setFileLibraryId(int fileLibraryId) {
        this.mFileLibraryId = fileLibraryId;
    }

    public int getFileVersion() {
        return mFileVersion;
    }

    public void setFileVersion(int fileVersion) {
        this.mFileVersion = fileVersion;
    }

    public int getFileSynced() {
        return mFileSynced;
    }

    public void setFileSynced(int fileSynced) {
        this.mFileSynced = fileSynced;
    }

    public String getFileKey() {
        return mFileKey;
    }

    public void setFileKey(String fileKey) {
        this.mFileKey = fileKey;
    }

    public String getFileDateAdded() {
        return mFileDateAdded;
    }

    public void setFileDateAdded(String fileDateAdded) {
        this.mFileDateAdded = fileDateAdded;
    }

    public String getFileDateModified() {
        return mFileDateModified;
    }

    public void setFileDateModified(String fileDateModified) {
        this.mFileDateModified = fileDateModified;
    }

    public String getFileClientModified() {
        return mFileClientModified;
    }

    public void setFileClientModified(String fileClientModified) {
        this.mFileClientModified = fileClientModified;
    }

}
