package io.github.salehjg.pocketzotero;

import android.app.Application;
import android.content.Context;
import android.widget.LinearLayout;

import io.github.salehjg.pocketzotero.mainactivity.MainActivity;
import io.github.salehjg.pocketzotero.mainactivity.RecyclerAdapterItems;
import io.github.salehjg.pocketzotero.zoteroengine.ZoteroEngine;

// This class is used to hold global stuff in memory.
public class AppMem extends Application {
    private ZoteroEngine zoteroEngine;
    private RecyclerAdapterItems recyclerAdapterItems;


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
}
