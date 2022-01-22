package io.github.salehjg.pocketzotero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemAttachment;

public class RecyclerAdapterAttachments2 extends RecyclerView.Adapter<RecyclerAdapterAttachments2.ViewHolder>{
    private Vector<ItemAttachment> mAttachments;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean mVisibilityBtnRemove;

    public RecyclerAdapterAttachments2(Context context, Vector<ItemAttachment> attachments) {
        this.mInflater = LayoutInflater.from(context);
        this.mAttachments = attachments;
        this.mVisibilityBtnRemove = false;
    }

    public void setVisibilityButtonItemRemove(boolean visibility){
        this.mVisibilityBtnRemove = visibility;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_attachments_recycler2, parent, false);
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
        holder.mBtnFile.setIcon( holder.mBtnFile.getContext().getResources().getDrawable(imgResId));
        holder.mBtnRemove.setVisibility(mVisibilityBtnRemove?View.VISIBLE:View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FitButton mBtnFile, mBtnRemove;
        ViewHolder(View itemView) {
            super(itemView);
            mBtnFile = itemView.findViewById(R.id.row_attachment_file);
            mBtnRemove = itemView.findViewById(R.id.row_attachment_remove);
            mBtnFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemClick(view, getBindingAdapterPosition());
                }
            });
            mBtnFile.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemLongClick(view, getBindingAdapterPosition());
                    return false;
                }
            });
            mBtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemRemoveClick(view, getBindingAdapterPosition());
                }
            });
            mBtnRemove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemRemoveLongClick(view, getBindingAdapterPosition());
                    return false;
                }
            });
        }
    }

    public ItemAttachment getDataAttachment(int id) {
        return mAttachments.get(id);
    }

    public void setAttachments(Vector<ItemAttachment> mAttachments) {
        this.mAttachments = mAttachments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mAttachments!=null ){
            return mAttachments.size();
        }else{
            return -1;
        }
    }

    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onItemRemoveClick(View view, int position);

        void onItemRemoveLongClick(View view, int position);
    }
}
