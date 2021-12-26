package io.github.salehjg.pocketzotero;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import io.github.salehjg.pocketzotero.mainactivity.MainActivity;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

// This class is used to hold global stuff in memory.
public class AppMem extends Application {
    private ZoteroEngine zoteroEngine;
    private RecyclerAdapterItems recyclerAdapterItems;
    private ViewPager2 viewPager;
    private ItemDetailed selectedItemDetailed;
    private ItemDetailedChangedListener selectedItemDetailedChangedListener = null;

    public ZoteroEngine getZoteroEngine() {
        return zoteroEngine;
    }

    public void setZoteroEngine(ZoteroEngine zoteroEngine) {
        this.zoteroEngine = zoteroEngine;
    }

    public RecyclerAdapterItems getRecyclerAdapterItems() {
        return recyclerAdapterItems;
    }

    public void setRecyclerAdapterItems(RecyclerAdapterItems recyclerAdapterItems) {
        this.recyclerAdapterItems = recyclerAdapterItems;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public ItemDetailed getSelectedItemDetailed() {
        return selectedItemDetailed;
    }

    public void setSelectedItemDetailed(ItemDetailed selectedItemDetailed) {
        this.selectedItemDetailed = selectedItemDetailed;
        if(this.selectedItemDetailedChangedListener!=null) {
            this.selectedItemDetailedChangedListener.onItemDetailedChanged(this.selectedItemDetailed);
        }
    }

    public interface ItemDetailedChangedListener{
        void onItemDetailedChanged(ItemDetailed itemDetailed);
    }

    public void setOnSelectedItemDetailedChangedListener(ItemDetailedChangedListener selectedItemDetailedChangedListener) {
        this.selectedItemDetailedChangedListener = selectedItemDetailedChangedListener;
    }
}
