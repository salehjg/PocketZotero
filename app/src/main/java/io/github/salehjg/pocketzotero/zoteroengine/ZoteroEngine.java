package io.github.salehjg.pocketzotero.zoteroengine;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import java.util.Vector;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.TreeItemHolder;
import io.github.salehjg.pocketzotero.mainactivity.tree.model.TreeNode;
import io.github.salehjg.pocketzotero.mainactivity.tree.view.AndroidTreeView;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class ZoteroEngine {
    private ZotDroidDB mZotDroidDB;
    private Context mContext;
    private Activity mActivity;
    private Vector<Collection> mCollectionTree;
    private Listeners mListeners;
    private TreeNode mTreeRoot;

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
        this.mActivity = activity;
        this.mContext = context;
        this.mListeners = listeners;
        mZotDroidDB = new ZotDroidDB(context, sqliteDbPath);
    }

    public void getGuiCollections(LinearLayout collectionsTreeView){
        mCollectionTree = this.mZotDroidDB.getCollectionTree();
        mTreeRoot = TreeNode.root();

        // go through all of the top parents
        for(Collection collection: mCollectionTree){
            TreeNode node = getGuiCollectionRecursive(collection, 0);
            mTreeRoot.addChild(node);
        }

        AndroidTreeView tView = new AndroidTreeView(mContext, mTreeRoot);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                __TreeNodeClick(node, value);
            }
        });
        collectionsTreeView.addView(tView.getView());
    }

    public void getGuiCollectionsLast(LinearLayout collectionsTreeView){
        AndroidTreeView tView = new AndroidTreeView(mContext, mTreeRoot);
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

        Vector<CollectionItem> items = getItemsForRecyclerItems(tmp.collectionObject);
        Vector<String> titles = getTitlesForRecyclerItems(items);
        Vector<Integer> ids = getItemIdsForRecyclerItems(items);
        Vector<Integer> types = getTypesForRecyclerItems(items);

        if(mListeners !=null) {
            mListeners.onCollectionSelected(
                    tmp.collectionObject,
                    items,
                    titles,
                    ids,
                    types
            );
        }
    }

    private TreeNode getGuiCollectionRecursive(Collection crntCollection, int level){
        if(crntCollection.get_sub_collections().size()==0){
            TreeItemHolder.IconTreeItem entryData = new TreeItemHolder.IconTreeItem(
                    level==0? R.drawable.ic_arrow_drop_down: R.drawable.ic_folder,
                    crntCollection.get_collection_name(), crntCollection.get_collection_id(), crntCollection
            );
            return new TreeNode(entryData).setViewHolder(new TreeItemHolder(mContext, false, R.layout.row_tree_child, level*50));
        }else{
            TreeItemHolder.IconTreeItem entryData = new TreeItemHolder.IconTreeItem(
                    level==0? R.drawable.ic_arrow_drop_down: R.drawable.ic_folder,
                    crntCollection.get_collection_name(), crntCollection.get_collection_id(), crntCollection);
            TreeNode nodeWithChildren = new TreeNode(entryData).setViewHolder(new TreeItemHolder(mContext, false, R.layout.row_tree_child, level*50));
            for(Collection child:crntCollection.get_sub_collections()){
                nodeWithChildren.addChild(getGuiCollectionRecursive(child, level+1));
            }
            return nodeWithChildren;
        }
    }

    public Vector<CollectionItem> getItemsForRecyclerItems(Collection collection){
        return this.mZotDroidDB.getCollectionItemsFor(collection.get_collection_id());
    }

    public Vector<String> getTitlesForRecyclerItems(Vector<CollectionItem> items){
        return mZotDroidDB.getTitlesWithCollectionItems(items);
    }

    public Vector<Integer> getTypesForRecyclerItems(Vector<CollectionItem> items){
        return this.mZotDroidDB.getTypeIdsWithCollectionItems(items);
    }

    public Vector<Integer> getItemIdsForRecyclerItems(Vector<CollectionItem> items){
        Vector<Integer> itemsIds = new Vector<>();
        for(CollectionItem item:items){
            itemsIds.add(item.get_itemId());
        }
        return itemsIds;
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        return mZotDroidDB.getDetailsForItemId(parentCollection, collectionItem);
    }

}
