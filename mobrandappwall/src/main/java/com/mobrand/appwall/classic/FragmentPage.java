package com.mobrand.appwall.classic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.mobrand.appwall.event.NewAdsEvent;
import com.mobrand.appwall.view.SpacesItemDecoration;
import com.mobrand.model.WallAd;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.CategoryEnum;
import com.mobrand.appwall.view.model.Header;
import com.mobrand.appwall.view.model.MobrandType;
import com.mobrand.appwall.view.model.Mobrando;
import com.mobrand.appwall.view.model.Pager;
import com.mobrand.appwall.view.model.ViewType;
import com.mobrand.appwall.adapters.AppwallAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by rmateus on 11/08/15.
 */
public class FragmentPage extends Fragment {

    private String mAppId;
    private String mPlacementId;
    private RecyclerView mRecyclerView;
    private ArrayList<MobrandType> mAppWallAds = new ArrayList<>();

    private GridLayoutManager mGridLayoutManager;

    private AppwallAdapter mAppwallAdapter;
    private boolean callImpressions;
    private FrameLayout mProgressBar;

    private AppWall activity;
    private int mCategory;
    private int mActionColor;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.activity = (AppWall) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            Bundle arguments = getArguments();
            configure(arguments);

        } else {

            configure(savedInstanceState);

        }

        mActionColor = ColorUtils.getCategoryColor(getActivity())[mCategory % 4];




    }



    private void configure(Bundle arguments) {
        mAppId = arguments.getString("appid");
        mPlacementId = arguments.getString("placementid");
        mCategory = arguments.getInt("category");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("appid", mAppId);
        outState.putString("placementid", mPlacementId);
        outState.putInt("category", mCategory);

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (menuVisible) {

            if (mAppwallAdapter != null) {
                mAppwallAdapter.setCallImpressions(true);
                mAppwallAdapter.notifyDataSetChanged();
            } else {
                callImpressions = true;
            }

        } else {

            if (mAppwallAdapter != null) mAppwallAdapter.setCallImpressions(false);

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            int mActionColor = ColorUtils.getCategoryColor(getActivity())[mCategory % 4];
            ((AppWall) getActivity()).colorizeActionBar(mActionColor);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mGridLayoutManager = new GridLayoutManager(inflater.getContext(), 6);

        mProgressBar = (FrameLayout) v.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics())));

        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppwallAdapter = new AppwallAdapter(mAppWallAds, activity.getMobrandCore(), mActionColor, mPlacementId);

        setApps(activity.getmNativeAdList(), activity.getHeadersList());
        EventBus.getDefault().register(this);

        mAppwallAdapter.setCallImpressions(callImpressions);

        mRecyclerView.setAdapter(mAppwallAdapter);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {

                int ordinal = mRecyclerView.getAdapter().getItemViewType(position);

                ViewType itemViewType = ViewType.values()[ordinal];

                switch (itemViewType) {
                    case PAGER:
                    case HEADER:
                    case MOBRANDO:
                        return mGridLayoutManager.getSpanCount();
                    default:
                        return mGridLayoutManager.getSpanCount() / 3;
                }

            }

        });

    }

    public void onEvent(NewAdsEvent event){
        setApps(event.getList(), event.getHeaders());
    }

    public void setApps(List<Ad> list, SparseArrayCompat<List<String>> headers){

        mAppWallAds.clear();

        if(list.isEmpty()){

            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        } else {

            mProgressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

        }

        switch (mCategory){

            case 0:
                createAds(list, mAppWallAds, null, CategoryEnum.MOBRAND, headers.get(mCategory));
                break;
            default:
                createAds(list, mAppWallAds, CategoryEnum.forValue(mCategory), null, headers.get(mCategory));
                break;

        }

        mAppWallAds.add(new Mobrando());
        mRecyclerView.scheduleLayoutAnimation();
        mAppwallAdapter.notifyDataSetChanged();

    }


    private void createAds(List<Ad> ads, ArrayList<MobrandType> adsToShow, CategoryEnum onlyThisCategory, CategoryEnum exceptThisCategory, List<String> headers) {
        if(headers.isEmpty()){
            return;
        }
        String firstHeader = headers.get(0);
        String[] strings = headers.subList(1, headers.size()).toArray(new String[headers.size() - 1]);
        int groupCount = 0;

        if (exceptThisCategory != null) {

            ArrayList<Ad> list1 = new ArrayList<>();


            for (Ad ad : ads) {
                int category1 = ad.getCategory();

                CategoryEnum categoryEnum = CategoryEnum.forValue(category1);

                if (categoryEnum != exceptThisCategory) {
                    list1.add(ad);
                }

            }

            ads = list1;
        }


        if (onlyThisCategory != null) {

            ArrayList<Ad> list1 = new ArrayList<>();


            for (Ad ad : ads) {
                int category1 = ad.getCategory();

                CategoryEnum categoryEnum = CategoryEnum.forValue(category1);

                if (categoryEnum == onlyThisCategory) {
                    list1.add(ad);
                }
            }


            ads = list1;
        }

        LinkedList<Ad> list = new LinkedList<>(ads);

        if (!list.isEmpty()) {
            adsToShow.add(new Header(firstHeader));
            fillAds(list, adsToShow, 6);
        }

        if (!list.isEmpty()) {
            adsToShow.add(new Header(strings[groupCount % strings.length]));
            groupCount++;

            ArrayList<MobrandType> pagerAds = new ArrayList<>();

            fillAds(list, pagerAds, 4);

            adsToShow.add(new Pager(pagerAds));
        }

        while (!list.isEmpty()) {
            adsToShow.add(new Header(strings[groupCount % strings.length]));
            fillAds(list, adsToShow, 6);
            groupCount++;
        }


    }

    private void fillAds(LinkedList<Ad> list, ArrayList<MobrandType> ads, int limit) {


        for(int i = 0; i < limit && !list.isEmpty(); i++ ){

            Ad nativeAd = list.removeFirst();

            CategoryEnum categoryEnum = CategoryEnum.forValue(nativeAd.getCategory());

            WallAd wallAd = new WallAd();

            wallAd.setAdid(nativeAd.getAdid());
            wallAd.setCategory(categoryEnum);
            wallAd.setDescription(nativeAd.getDescription());
            wallAd.setName(nativeAd.getName());
            wallAd.setPackageName(nativeAd.getPackageName());
            wallAd.setIcon(nativeAd.getIcon());

            ads.add(wallAd);

        }

    }


    public int getAppBarColor() {
        return mActionColor;
    }
}
