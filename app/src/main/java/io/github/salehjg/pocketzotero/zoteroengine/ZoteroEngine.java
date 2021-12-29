package io.github.salehjg.pocketzotero.zoteroengine;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
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

    private Context context;
    private Activity activity;
    private Vector<Collection> collectionTree;
    private Listeners listeners;
    private TreeNode treeRoot;

    public interface Listeners{
        void onCollectionSelected(
                Collection selectedCollectionObject,
                Vector<CollectionItem> items,
                Vector<String> titles,
                Vector<Integer> ids,
                Vector<Integer> types
        );
    }

    public ZoteroEngine(
            Activity activity,
            Context context,
            String sqliteDbPath,
            Listeners listeners){
        this.activity = activity;
        this.context = context;
        this.listeners = listeners;
        zotDroidDB = new ZotDroidDB(context, sqliteDbPath);
    }

    public void GuiCollections(LinearLayout collectionsTreeView){
        collectionTree = this.zotDroidDB.getCollectionTree();
        treeRoot = TreeNode.root();

        // go through all of the top parents
        for(Collection collection:collectionTree){
            TreeNode node = GuiCollectionRecursive(collection, 0);
            treeRoot.addChild(node);
        }

        AndroidTreeView tView = new AndroidTreeView(context, treeRoot);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                __TreeNodeClick(node, value);
            }
        });
        collectionsTreeView.addView(tView.getView());
    }

    public void GuiCollectionsLast(LinearLayout collectionsTreeView){
        AndroidTreeView tView = new AndroidTreeView(context, treeRoot);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                __TreeNodeClick(node, value);
            }
        });
        collectionsTreeView.addView(tView.getView());
    }

    private void __TreeNodeClick(TreeNode node, Object value){
        TreeItemHolder.IconTreeItem tmp = (TreeItemHolder.IconTreeItem) value;

        Vector<CollectionItem> items = GetItemsForRecyclerItems(tmp.collectionObject);
        Vector<String> titles = GetTitlesForRecyclerItems(items);
        Vector<Integer> ids = GetItemIdsForRecyclerItems(items);
        Vector<Integer> types = GetTypesForRecyclerItems(items);

        if(listeners!=null) {
            listeners.onCollectionSelected(
                    tmp.collectionObject,
                    items,
                    titles,
                    ids,
                    types
            );
        }
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
