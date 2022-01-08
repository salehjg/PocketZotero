package io.github.salehjg.pocketzotero.mainactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Vector;
import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;

public class RecyclerAdapterItems extends RecyclerView.Adapter<RecyclerAdapterItems.ViewHolder>{
    private Vector<String> mItemsTitles;
    private Vector<Integer> mItemsTypes;
    private Vector<CollectionItem> mItems;

    public Vector<CollectionItem> getmItems() {
        return mItems;
    }

    public void setItems(Vector<CollectionItem> mItems) {
        this.mItems = mItems;
    }

    public void setItemsTitles(Vector<String> mItemsTitles) {
        this.mItemsTitles = mItemsTitles;
    }

    public void setItemsTypes(Vector<Integer> mItemsTypes) {
        this.mItemsTypes = mItemsTypes;
    }

    public void setItemsIds(Vector<Integer> mItemsIDs) {
        this.mItemsIDs = mItemsIDs;
    }

    private Vector<Integer> mItemsIDs;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerAdapterItems(Context context, Vector<CollectionItem> items, Vector<String> itemsTitles, Vector<Integer> itemsTypes, Vector<Integer> itemsIds) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
        this.mItemsTitles = itemsTitles;
        this.mItemsTypes = itemsTypes;
        this.mItemsIDs = itemsIds;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_items_recycler, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String strItemTitle = mItemsTitles.get(position);
        holder.itemTitle.setText(strItemTitle);

        int imgResId = -1;

        switch (mItemsTypes.get(position)){
            case 33:{//conference
                imgResId = R.drawable.ic_conference;
                break;
            }
            case 2:{//book
                imgResId = R.drawable.ic_book;
                break;
            }
            case 4:{//journal
                imgResId = R.drawable.ic_journal;
                break;
            }
            case 27:{//presentation
                imgResId = R.drawable.ic_presentation;
                break;
            }
            case 7:{//thesis
                imgResId = R.drawable.ic_thesis;
                break;
            }
            default:{
                imgResId = R.drawable.ic_other;
            }
        }
        holder.itemTypeImage.setBackgroundResource(imgResId);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mItemsTitles==null || mItemsIDs==null )//|| mItemsTypes==null)
        {
            return 0;
        }else{
            assert mItemsTitles.size() == mItemsIDs.size();
            //assert mItemsTypes.size() == mItemsIDs.size();
            return mItemsTitles.size();
        }

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemTitle;
        ImageView itemTypeImage;

        ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemsrowlayout_title);
            itemTypeImage = itemView.findViewById(R.id.itemsrowlayout_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // a method for getting data at click position
    int getDataId(int id) {
        return mItemsIDs.get(id);
    }

    // a method for getting data at click position
    int getDataType(int id) {
        return mItemsTypes.get(id);
    }

    // a method for getting data at click position
    String getDataTitle(int id) {
        return mItemsTitles.get(id);
    }

    // a method for getting data at click position
    public CollectionItem getDataCollectionItem(int id) {
        return mItems.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
