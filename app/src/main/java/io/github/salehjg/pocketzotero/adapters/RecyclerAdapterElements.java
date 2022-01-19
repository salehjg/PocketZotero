package io.github.salehjg.pocketzotero.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import io.github.salehjg.pocketzotero.R;
import io.github.salehjg.pocketzotero.zoteroengine.types.UnifiedElement;

public class RecyclerAdapterElements extends RecyclerView.Adapter<RecyclerAdapterElements.ViewHolder>{

    private Vector<UnifiedElement> mElements;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean mMasterEditEnabled;

    public RecyclerAdapterElements(
            Context context,
            Vector<UnifiedElement> elements,
            boolean masterEditEnabled) {
        this.mInflater = LayoutInflater.from(context);
        this.mElements = elements;
        this.mMasterEditEnabled = masterEditEnabled;
    }

    public Vector<UnifiedElement> getElements() {
        return mElements;
    }

    public void setElements(Vector<UnifiedElement> mElements) {
        this.mElements = mElements;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_elements_recycler, parent, false);
        return new ViewHolder(view);
    }

    private void changeEditTextEnabled(EditText editText, boolean isEnabled) {
        editText.setFocusable(isEnabled);
        editText.setEnabled(isEnabled);
        editText.setCursorVisible(isEnabled);
        //editText.setKeyListener(null);
        editText.setBackgroundColor(isEnabled ? Color.BLACK : Color.TRANSPARENT);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UnifiedElement element = this.mElements.get(position);

        holder.textViewName.setText(element.getStringName());
        holder.textViewValue.setText(element.getStringValue());

        switch (element.getType()){
            case AUTHOR:{
                holder.textViewName.setTextColor(Color.RED);
                holder.textViewValue.setTextColor(Color.RED);

                changeEditTextEnabled(holder.textViewName, false);
                changeEditTextEnabled(holder.textViewValue, this.mMasterEditEnabled);
                break;
            }
            case NOTE:{
                holder.textViewName.setTextColor(Color.BLUE);
                holder.textViewValue.setTextColor(Color.BLUE);

                changeEditTextEnabled(holder.textViewName, false);
                changeEditTextEnabled(holder.textViewValue, this.mMasterEditEnabled);
                break;
            }
            case TAG:{
                holder.textViewName.setTextColor(Color.GREEN);
                holder.textViewValue.setTextColor(Color.GREEN);

                changeEditTextEnabled(holder.textViewName, false);
                changeEditTextEnabled(holder.textViewValue, false); ///TODO how to add tags?
                break;
            }
            case FIELD:{
                holder.textViewName.setTextColor(Color.BLACK);
                holder.textViewValue.setTextColor(Color.BLACK);

                changeEditTextEnabled(holder.textViewName, this.mMasterEditEnabled);
                changeEditTextEnabled(holder.textViewValue, this.mMasterEditEnabled);
                break;
            }
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mElements==null ) {
            return 0;
        }else{
            return mElements.size();
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText textViewName, textViewValue;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.fieldsrowlayout_fieldname);
            textViewValue = itemView.findViewById(R.id.fieldsrowlayout_fieldvalue);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // a method for getting data at click position
    UnifiedElement getDataElement(int id) {
        return mElements.get(id);
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
