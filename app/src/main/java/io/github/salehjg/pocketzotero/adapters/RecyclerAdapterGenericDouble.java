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

public abstract class RecyclerAdapterGenericDouble<T> extends RecyclerView.Adapter<RecyclerAdapterGenericDouble<T>.ViewHolder> {
    private Vector<T> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerAdapterGenericDouble(Context context, Vector<T> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_detailed_double_recycler, parent, false);
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
        TextView mTv1, mTv2;
        FitButton mBtnEdit, mBtnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            mTv1 = itemView.findViewById(R.id.row_detailed_double_tv1);
            mTv2= itemView.findViewById(R.id.row_detailed_double_tv2);
            mBtnEdit = itemView.findViewById(R.id.row_detailed_double_btn_edit);
            mBtnRemove = itemView.findViewById(R.id.row_detailed_double_btn_remove);

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

        public TextView getGuiTv1() {
            return mTv1;
        }

        public TextView getGuiTv2() {
            return mTv2;
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