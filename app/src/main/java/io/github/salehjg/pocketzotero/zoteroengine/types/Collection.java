package io.github.salehjg.pocketzotero.zoteroengine.types;
import java.util.Vector;

public class Collection {
    public void add_sub_collection(Collection c) { mSub_collections.add(c); }
    public Vector<Collection> get_sub_collections() { return mSub_collections;}

    public int get_collection_id(){ return mCollection_id;}
    public void set_collection_id(int collectionID){ mCollection_id = collectionID;}

    public String get_collection_name(){ return mCollection_name;}
    public void set_collection_name(String collectionName){ mCollection_name = collectionName;}

    public int get_parent_id(){ return !this.mIs_top_parent ? mParent_collection_id :-1;}
    public void set_parent_id(int parentCollectionID){ mParent_collection_id = parentCollectionID; mIs_top_parent =false;}
    public void set_is_top_parent(boolean val){ this.mIs_top_parent = val;}
    public boolean has_parent() { return !mIs_top_parent; }
    public boolean is_top_parent() { return mIs_top_parent; }

    public String get_client_date_modified(){return this.mClient_date_modified;}
    public void set_client_date_modified(String _client_date_modified){this.mClient_date_modified = _client_date_modified;}

    public int get_library_id(){return this.mLibrary_id;}
    public void set_library_id(int _library_id){this.mLibrary_id = _library_id;}

    public String get_key(){return this.mKey;}
    public void set_key(String _key){this.mKey = _key;}

    public int get_version(){return this.mVersion;}
    public void set_version(int _version){this.mVersion = _version;}

    public int get_synced(){return this.mSynced;}
    public void set_synced(int _synced){this.mSynced = _synced;}

    protected int mCollection_id;
    protected String mCollection_name;
    protected int mParent_collection_id; // Nullable
    protected boolean mIs_top_parent;        // true if parentCollectionID set to null.
    protected String mClient_date_modified;
    protected int mLibrary_id;
    protected String mKey;
    protected int mVersion;
    protected int mSynced;
    protected Vector<Collection> mSub_collections; // In-efficient in memory terms but whatever

    public String toString() {
        return mCollection_name;
    }
    public Collection(){
        mCollection_id = 0;
        mCollection_name = "";
        mParent_collection_id = 0;
        mIs_top_parent = true;
        mClient_date_modified = "";
        mLibrary_id = 0;
        mKey = "";
        mVersion = 0;
        mSynced = 0;
        mSub_collections = new Vector<Collection>();
    }
}