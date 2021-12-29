package io.github.salehjg.pocketzotero.fragments.status;

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

public class RecyclerAdapterStatus extends RecyclerView.Adapter<RecyclerAdapterStatus.ViewHolder>{
    private Vector<String> mStatusStrings;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerAdapterStatus(Context context, Vector<String> statuses) {
        this.mInflater = LayoutInflater.from(context);
        this.mStatusStrings = statuses;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_status_recycler, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String statusString = mStatusStrings.get(position);
        holder.editTextStatus.setText(statusString);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mStatusStrings==null ) {
            return 0;
        }else{
            return mStatusStrings.size();
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView editTextStatus;

        ViewHolder(View itemView) {
            super(itemView);
            editTextStatus = itemView.findViewById(R.id.statusrowlayout_msg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
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
