package io.github.salehjg.pocketzotero.fragments.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewStateAdapter extends FragmentStateAdapter {

    private MainItemsFragment mFragmentMainItems;
    private MainItemDetailedFragment mFragmentMainItemDetailed;

    public ViewStateAdapter(
            @NonNull FragmentManager fragmentManager,
            @NonNull Lifecycle lifecycle,
            @NonNull MainItemsFragment mainItemsFragment,
            @NonNull MainItemDetailedFragment mainItemDetailedFragment
            ){
        super(fragmentManager, lifecycle);
        mFragmentMainItems = mainItemsFragment;
        mFragmentMainItemDetailed = mainItemDetailedFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // make sure the titles match
        if (position == 0) {
            return mFragmentMainItems;
        }else if (position == 1){
            return mFragmentMainItemDetailed;
        }else{
            assert false;
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
