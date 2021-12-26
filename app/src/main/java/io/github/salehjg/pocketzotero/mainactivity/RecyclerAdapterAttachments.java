package io.github.salehjg.pocketzotero.mainactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;

public class RecyclerAdapterAttachments extends RecyclerView.Adapter<RecyclerAdapterAttachments.ViewHolder>{
    private Vector<ItemAttachment> mAttachments;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public Vector<ItemAttachment> getmAttachments() {
        return mAttachments;
    }

    public void setmAttachments(Vector<ItemAttachment> mAttachments) {
        this.mAttachments = mAttachments;
    }

    // data is passed into the constructor
    public RecyclerAdapterAttachments(Context context, Vector<ItemAttachment> attachments) {
        this.mInflater = LayoutInflater.from(context);
        this.mAttachments = attachments;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_attachments_recycler, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemAttachment attachment = mAttachments.get(position);

        int imgResId = -1;
        switch (attachment.getContentType()){
            case "application/pdf":{
                imgResId = R.drawable.ic_pdf;
                break;
            }
            case "text/html":{
                imgResId = R.drawable.ic_html;
                break;
            }
            case "video/mp4":{
                imgResId = R.drawable.ic_mp4;
                break;
            }
            default:{
                imgResId = R.drawable.ic_attachment;
            }
        }
        holder.itemTypeImage.setBackgroundResource(imgResId);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mAttachments==null ){
            return 0;
        }else{
            return mAttachments.size();
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView itemTypeImage;
        ViewHolder(View itemView) {
            super(itemView);
            itemTypeImage = itemView.findViewById(R.id.attachmentslayout_img);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
        
        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) mLongClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }

    // a method for getting data at click position
    public ItemAttachment getDataAttachment(int id) {
        return mAttachments.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // allows clicks events to be caught
    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
