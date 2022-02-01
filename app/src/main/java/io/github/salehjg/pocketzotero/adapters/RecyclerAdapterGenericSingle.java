package io.github.salehjg.pocketzotero.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;

public abstract class RecyclerAdapterGenericSingle<T> extends RecyclerView.Adapter<RecyclerAdapterGenericSingle<T>.ViewHolder> {
    private Vector<T> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerAdapterGenericSingle(Context context, Vector<T> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_detailed_single_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(mData!=null) {
            return mData.size();
        }else {
            return -1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTv;
        FitButton mBtnEdit, mBtnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.row_detailed_single_tv1);
            mBtnEdit = itemView.findViewById(R.id.row_detailed_single_btn_edit);
            mBtnRemove = itemView.findViewById(R.id.row_detailed_single_btn_remove);

            mBtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemEditClick(view, getBindingAdapterPosition());
                }
            });

            mBtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mClickListener!=null) mClickListener.onItemRemoveClick(view, getBindingAdapterPosition());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener!=null) mClickListener.onItemClick(view, getBindingAdapterPosition());
        }

        public TextView getGuiTv() {
            return mTv;
        }

        public FitButton getGuiBtnEdit() {
            return mBtnEdit;
        }

        public FitButton getGuiBtnRemove() {
            return mBtnRemove;
        }

        public void setVisibilityBtnAll(boolean isVisible){
            setVisibilityBtnEdit(isVisible);
            setVisibilityBtnRemove(isVisible);
        }

        public void setVisibilityBtnEdit(boolean isVisible){
            mBtnEdit.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }

        public void setVisibilityBtnRemove(boolean isVisible){
            mBtnRemove.setVisibility(isVisible?View.VISIBLE:View.INVISIBLE);
        }
    }

    public T getItem(int id) {
        if(mData!=null) {
            return mData.get(id);
        }else{
            return null;
        }
    }

    public void setData(Vector<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemRemoveClick(View view, int position);
        void onItemEditClick(View view, int position);
    }
}