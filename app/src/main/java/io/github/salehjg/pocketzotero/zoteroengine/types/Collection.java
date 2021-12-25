package io.github.salehjg.pocketzotero.zoteroengine.types;
import java.util.Vector;

public class Collection {
    public void add_sub_collection(Collection c) { _sub_collections.add(c); }
    public Vector<Collection> get_sub_collections() { return _sub_collections;}

    public int get_collection_id(){ return _collection_id;}
    public void set_collection_id(int collectionID){ _collection_id = collectionID;}

    public String get_collection_name(){ return _collection_name;}
    public void set_collection_name(String collectionName){ _collection_name = collectionName;}

    public int get_parent_id(){ return !this._is_top_parent?_parent_collection_id:-1;}
    public void set_parent_id(int parentCollectionID){ _parent_collection_id = parentCollectionID; _is_top_parent=false;}
    public void set_is_top_parent(boolean val){ this._is_top_parent = val;}
    public boolean has_parent() { return !_is_top_parent; }
    public boolean is_top_parent() { return _is_top_parent; }

    public String get_client_date_modified(){return this._client_date_modified;}
    public void set_client_date_modified(String _client_date_modified){this._client_date_modified = _client_date_modified;}

    public int get_library_id(){return this._library_id;}
    public void set_library_id(int _library_id){this._library_id = _library_id;}

    public String get_key(){return this._key;}
    public void set_key(String _key){this._key = _key;}

    public int get_version(){return this._version;}
    public void set_version(int _version){this._version = _version;}

    public int get_synced(){return this._synced;}
    public void set_synced(int _synced){this._synced = _synced;}

    protected int       _collection_id;
    protected String    _collection_name;
    protected int       _parent_collection_id; // Nullable
    protected boolean   _is_top_parent;        // true if parentCollectionID set to null.
    protected String    _client_date_modified;
    protected int       _library_id;
    protected String    _key;
    protected int       _version;
    protected int       _synced;
    protected Vector<Collection> _sub_collections; // In-efficient in memory terms but whatever

    public String toString() {
        return _collection_name;
    }
    public Collection(){
        _collection_id = 0;
        _collection_name = "";
        _parent_collection_id = 0;
        _is_top_parent = true;
        _client_date_modified = "";
        _library_id = 0;
        _key = "";
        _version = 0;
        _synced = 0;
        _sub_collections = new Vector<Collection>();
    }
}