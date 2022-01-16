package io.github.salehjg.pocketzotero.fragments.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import io.github.salehjg.pocketzotero.mainactivity.MainActivityRev1;

public class ViewStateAdapter extends FragmentStateAdapter {
    private final Fragment[] mFragments = new Fragment[] {
            new MainItemsFragment(),
            //new MainItemDetailedFragment(),
            new MainItemDetailed2Fragment(),
    };

    public final String[] mFragmentNames = new String[] {
            "Items",
            "Details"
    };

    public ViewStateAdapter(@NonNull Fragment parentFragment){
        super(parentFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        assert position<mFragments.length;
        return mFragments[position];
    }

    public Fragment getRegisteredFragment(int position){
        return mFragments[position];
    }

    @Override
    public int getItemCount() {
        return mFragments.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }






}
