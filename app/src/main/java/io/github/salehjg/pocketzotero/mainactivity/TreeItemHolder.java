package io.github.salehjg.pocketzotero.mainactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.mainactivity.tree.model.TreeNode;
import io.github.salehjg.pocketzotero.zoteroengine.types.Collection;

public class TreeItemHolder extends TreeNode.BaseNodeViewHolder<TreeItemHolder.IconTreeItem> {
    public static final int DEFAULT = 0;

    ImageView img;
    TextView tvValue;
    boolean toggle;
    int child;
    int leftMargin;

    public TreeItemHolder(Context context, boolean toggle, int child, int leftMargin) {
        super(context);
        this.toggle = toggle;
        this.child = child;
        this.leftMargin = leftMargin;
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view;
        if (child == DEFAULT) {
            view = inflater.inflate(R.layout.tab_rename_pattern, null, false);
        } else {
            view = inflater.inflate(child, null, false);
        }

        if (leftMargin == DEFAULT) {
            leftMargin = getDimens(R.dimen.treeview_left_padding);
        }
        view.setPadding(leftMargin, getDimens(R.dimen.treeview_top_padding), getDimens(R.dimen.treeview_right_padding), getDimens(R.dimen.treeview_bottom_padding));

        img = (ImageView) view.findViewById(R.id.image);
        tvValue = (TextView) view.findViewById(R.id.text);
        img.setImageResource(value.icon);
        tvValue.setText(value.text);
        return view;
    }

    public void toggle(boolean active) {
        if (toggle)
            img.setImageResource(active ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public int collectionId;
        public Collection collectionObject;

        public IconTreeItem(int icon, String text, int collectionId, Collection collection) {
            this.icon = icon;
            this.text = text;
            this.collectionId = collectionId;
            this.collectionObject = collection;
        }
    }

    private int getDimens(int resId) {
        return (int) (context.getResources().getDimension(resId) / context.getResources().getDisplayMetrics().density)
                ;
    }
}
