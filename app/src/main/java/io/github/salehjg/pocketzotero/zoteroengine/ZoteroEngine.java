package io.github.salehjg.pocketzotero.zoteroengine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.master.permissionhelper.PermissionHelper;

import java.io.File;
import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.mainactivity.TreeItemHolder;
import io.github.salehjg.pocketzotero.mainactivity.tree.model.TreeNode;
import io.github.salehjg.pocketzotero.mainactivity.tree.view.AndroidTreeView;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class ZoteroEngine {
    private ZotDroidDB zotDroidDB;
    private PermissionHelper permissionHelper;

    private Context context;
    private LinearLayout collectionsTreeView;
    private RecyclerAdapterItems recyclerAdapterItems;
    private Activity activity;

    private Vector<Collection> collectionTree;
    private Collection __selectedCollection = null;

    public ZoteroEngine(Activity activity, Context context, LinearLayout collectionsTreeView, RecyclerAdapterItems recyclerAdapterItems, String sqliteDbPath){
        this.activity = activity;
        this.context = context;
        this.collectionsTreeView = collectionsTreeView;
        this.recyclerAdapterItems = recyclerAdapterItems;

        permissionHelper = new PermissionHelper(
                activity,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                },
                100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // The rest of the constructor for TopClass is here.

                if(!RunAllChecks()){
                    Toast.makeText(context, "Check the permissions.", Toast.LENGTH_LONG).show();
                    System.exit(1);
                }

                String fname;
                if(sqliteDbPath.equals("")){
                    fname = context.getResources().getString(R.string.default_path_db);
                }else{
                    fname = sqliteDbPath;
                }

                zotDroidDB = new ZotDroidDB(context, fname);
            }

            @Override public void onIndividualPermissionGranted(String[] grantedPermission) {}

            @Override
            public void onPermissionDenied() {
                Toast.makeText(context, "Check the permissions.", Toast.LENGTH_LONG).show();
                System.exit(1);
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Toast.makeText(context, "Check the permissions.", Toast.LENGTH_LONG).show();
                System.exit(1);
            }
        });
    }

    public Collection getSelectedCollection() {
        return __selectedCollection;
    }

    public boolean RunAllChecks(){
        return CreateStorageFolderIfNeeded();
    }

    public boolean CreateStorageFolderIfNeeded(){
        File f = new File(Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.default_name_dir));
        if (!f.exists()) {
            return f.mkdirs();
        }else{
            return true;
        }
    }

    public void GuiCollections(){
        collectionTree = this.zotDroidDB.getCollectionTree();
        TreeNode root = TreeNode.root();

        // go through all of the top parents
        for(Collection collection:collectionTree){
            TreeNode node = GuiCollectionRecursive(collection, 0);
            root.addChild(node);
        }

        AndroidTreeView tView = new AndroidTreeView(context, root);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                TreeItemHolder.IconTreeItem tmp = (TreeItemHolder.IconTreeItem) value;

                __selectedCollection = tmp.collectionObject;
                Vector<CollectionItem> items = GetItemsForRecyclerItems(tmp.collectionObject);
                Vector<String> titles = GetTitlesForRecyclerItems(items);
                Vector<Integer> ids = GetItemIdsForRecyclerItems(items);
                Vector<Integer> types = GetTypesForRecyclerItems(items);

                recyclerAdapterItems.setmItems(items);
                recyclerAdapterItems.setmItemsTitles(titles);
                recyclerAdapterItems.setmItemsIDs(ids);
                recyclerAdapterItems.setmItemsTypes(types);

                recyclerAdapterItems.notifyDataSetChanged();

                //Toast.makeText(context, "Click on the node with collectionID " + tmp.collectionId + " and title of " + tmp.text, Toast.LENGTH_SHORT).show();
            }
        });
        this.collectionsTreeView.addView(tView.getView());
    }

    private TreeNode GuiCollectionRecursive(Collection crntCollection, int level){
        if(crntCollection.get_sub_collections().size()==0){
            TreeItemHolder.IconTreeItem entryData = new TreeItemHolder.IconTreeItem(
                    level==0? R.drawable.ic_arrow_drop_down: R.drawable.ic_folder,
                    crntCollection.get_collection_name(), crntCollection.get_collection_id(), crntCollection
            );
            return new TreeNode(entryData).setViewHolder(new TreeItemHolder(context, false, R.layout.row_tree_child, level*50));
        }else{
            TreeItemHolder.IconTreeItem entryData = new TreeItemHolder.IconTreeItem(
                    level==0? R.drawable.ic_arrow_drop_down: R.drawable.ic_folder,
                    crntCollection.get_collection_name(), crntCollection.get_collection_id(), crntCollection);
            TreeNode nodeWithChildren = new TreeNode(entryData).setViewHolder(new TreeItemHolder(context, false, R.layout.row_tree_child, level*50));
            for(Collection child:crntCollection.get_sub_collections()){
                nodeWithChildren.addChild(GuiCollectionRecursive(child, level+1));
            }
            return nodeWithChildren;
        }
    }

    public Vector<CollectionItem> GetItemsForRecyclerItems(Collection collection){
        return this.zotDroidDB.getCollectionItemsFor(collection.get_collection_id());
    }

    public Vector<String> GetTitlesForRecyclerItems(Vector<CollectionItem> items){
        return zotDroidDB.getTitlesWithCollectionItems(items);
    }

    public Vector<Integer> GetTypesForRecyclerItems(Vector<CollectionItem> items){
        return this.zotDroidDB.getTypeIdsWithCollectionItems(items);
    }

    public Vector<Integer> GetItemIdsForRecyclerItems(Vector<CollectionItem> items){
        Vector<Integer> itemsIds = new Vector<>();
        for(CollectionItem item:items){
            itemsIds.add(item.get_itemId());
        }
        return itemsIds;
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        return zotDroidDB.getDetailsForItemId(parentCollection, collectionItem);
    }

}
