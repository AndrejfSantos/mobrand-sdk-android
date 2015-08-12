package com.mobrand.mobrandsample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by rmateus on 04-04-2015.
 */
public class SavingPagerAdapter extends FragmentStatePagerAdapter {

    private final String placementid;
    private final String appid;

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("appid", appid);
        bundle.putString("placementid", placementid);
        FragmentPage baseFragment = new FragmentPage();
        baseFragment.setArguments(bundle);
        return baseFragment;
    }


    @Override
    public int getCount() {
        return 3;
    }

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public SavingPagerAdapter(FragmentManager fragmentManager, String appid, String placementid) {
        super(fragmentManager);
        this.appid = appid;
        this.placementid = placementid;

    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

}
