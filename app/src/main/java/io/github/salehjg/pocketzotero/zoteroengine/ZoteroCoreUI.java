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
import io.github.salehjg.pocketzotero.zoteroengine.types.Creator;
import io.github.salehjg.pocketzotero.zoteroengine.types.FieldValuePair;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class ZoteroCoreUI {
    private ZoteroCore mZoteroCore;
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

    public ZoteroCoreUI(
            Activity activity,
            Context context,
            String sqliteDbPath,
            Listeners listeners){
        this.mActivity = activity;
        this.mContext = context;
        this.mListeners = listeners;
        mZoteroCore = new ZoteroCore(context, sqliteDbPath);
    }

    public void getGuiCollections(LinearLayout collectionsTreeView){
        mCollectionTree = this.mZoteroCore.getCollectionTree();
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
        return this.mZoteroCore.getCollectionItemsFor(collection.get_collection_id());
    }

    public Vector<String> getTitlesForRecyclerItems(Vector<CollectionItem> items){
        return mZoteroCore.getTitlesWithCollectionItems(items);
    }

    public Vector<Integer> getTypesForRecyclerItems(Vector<CollectionItem> items){
        return this.mZoteroCore.getTypeIdsWithCollectionItems(items);
    }

    public Vector<Integer> getItemIdsForRecyclerItems(Vector<CollectionItem> items){
        Vector<Integer> itemsIds = new Vector<>();
        for(CollectionItem item:items){
            itemsIds.add(item.get_itemId());
        }
        return itemsIds;
    }

    public ItemDetailed getDetailsForItemId(Collection parentCollection, CollectionItem collectionItem){
        return mZoteroCore.getDetailsForItemId(parentCollection, collectionItem);
    }

    public Vector<Creator> getPossibleCreatorTypesFor(int itemTypeId){
        return mZoteroCore.getPossibleCreatorTypesFor(itemTypeId);
    }

    public Vector<Creator> getPossibleCreatorTypesFor(String itemTypeName){
        return mZoteroCore.getPossibleCreatorTypesFor(itemTypeName);
    }

    public Vector<FieldValuePair> getPossibleFieldsFor(int itemTypeId){
        return mZoteroCore.getPossibleFieldsFor(itemTypeId);
    }

    public Vector<FieldValuePair> getPossibleFieldsFor(String itemTypeName){
        return mZoteroCore.getPossibleFieldsFor(itemTypeName);
    }

    public Vector<String> getItemTypeNames(){
        return mZoteroCore.getItemTypeNames();
    }

}
