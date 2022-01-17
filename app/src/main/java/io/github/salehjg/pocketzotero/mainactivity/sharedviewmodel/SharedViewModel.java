package io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Vector;

import io.github.salehjg.pocketzotero.zoteroengine.types.CollectionItem;
import io.github.salehjg.pocketzotero.zoteroengine.types.ItemDetailed;

public class SharedViewModel extends AndroidViewModel {
    private final long id;

    private MutableLiveData<Vector<CollectionItem>> mRecyclerItemsItems;
    private MutableLiveData<Vector<String>> mRecyclerItemsTitles;
    private MutableLiveData<Vector<Integer>> mRecyclerItemsTypes;
    private MutableLiveData<Vector<Integer>> mRecyclerItemsIds;

    private MutableLiveData<CollectionItem> mSelectedItem;
    private MutableLiveData<ItemDetailed> mSelectedItemDetailed;

    private MutableLiveData<Integer> mViewPagerSelectedTab;

    private MediatorLiveData<OneTimeEvent> mMainActivityOpenDrawer = new MediatorLiveData<>();
    private MediatorLiveData<OneTimeEvent> mTabItemsHideDrawerButton = new MediatorLiveData<>();
    private MediatorLiveData<OneTimeEvent> mTabItemDetailedHideDrawerButton = new MediatorLiveData<>();


    public SharedViewModel(@NonNull Application application, final long id) {
        super(application);
        this.id = id;
    }

    public MutableLiveData<Vector<CollectionItem>> getRecyclerItemsItems() {
        if (mRecyclerItemsItems == null) {
            mRecyclerItemsItems = new MutableLiveData<>();
        }
        return mRecyclerItemsItems;
    }

    public MutableLiveData<Vector<String>> getRecyclerItemsTitles() {
        if (mRecyclerItemsTitles == null) {
            mRecyclerItemsTitles = new MutableLiveData<>();
        }
        return mRecyclerItemsTitles;
    }

    public MutableLiveData<Vector<Integer>> getRecyclerItemsTypes() {
        if (mRecyclerItemsTypes == null) {
            mRecyclerItemsTypes = new MutableLiveData<>();
        }
        return mRecyclerItemsTypes;
    }

    public MutableLiveData<Vector<Integer>> getRecyclerItemsIds() {
        if (mRecyclerItemsIds == null) {
            mRecyclerItemsIds = new MutableLiveData<>();
        }
        return mRecyclerItemsIds;
    }

    public MutableLiveData<CollectionItem> getSelectedItem() {
        if (mSelectedItem == null) {
            mSelectedItem = new MutableLiveData<>();
        }
        return mSelectedItem;
    }

    public MutableLiveData<ItemDetailed> getSelectedItemDetailed() {
        if (mSelectedItemDetailed == null) {
            mSelectedItemDetailed = new MutableLiveData<>();
        }
        return mSelectedItemDetailed;
    }

    public MutableLiveData<Integer> getViewPagerSelectedTab() {
        if (mViewPagerSelectedTab == null) {
            mViewPagerSelectedTab = new MutableLiveData<>(-1);
        }
        return mViewPagerSelectedTab;
    }

    public MediatorLiveData<OneTimeEvent> getMainActivityOpenDrawer() {
        return mMainActivityOpenDrawer;
    }

    public MediatorLiveData<OneTimeEvent> getTabItemsHideDrawerButton() {
        return mTabItemsHideDrawerButton;
    }

    public MediatorLiveData<OneTimeEvent> getTabItemDetailedHideDrawerButton() {
        return mTabItemDetailedHideDrawerButton;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("SharedViewModel", "on cleared called");
    }
}