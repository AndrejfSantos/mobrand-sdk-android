package com.mobrand.appwall.A;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.mobrand.appwall.event.NewAdsEvent;
import com.mobrand.model.AppWallAd;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.CategoryEnum;
import com.mobrand.sdk.core.model.NativeAd;
import com.mobrand.view.model.Header;
import com.mobrand.view.model.MobrandType;
import com.mobrand.view.model.Mobrando;
import com.mobrand.view.model.Pager;
import com.mobrand.view.model.ViewType;
import com.mobrand.view.AppwallAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by rmateus on 11/08/15.
 */
public class FragmentPage extends Fragment {

    private String appId;
    private String placementId;
    private String adsEndpoint = "http://api.mobrand.net";

    private String country;


    ArrayList<MobrandType> ads = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;
    int color = 1;


    int colorArray[] = new int[]{R.color.mobrand, R.color.green, R.color.orange, R.color.blue};
    private AppwallAdapter appwallAdapter;
    private boolean runAdapter;
    private FrameLayout progressBar;

    private String impressionsEndpoint = "http://api.mobrand.net/";


    RestAdapter adsAdapter = new RestAdapter.Builder().setEndpoint(adsEndpoint).build();
    private AppWall activity;

    @Override
    public void onAttach(Activity activity) {
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

        initCheck();

    }

    private void initCheck() {




    }

    private void configure(Bundle arguments) {
        appId = arguments.getString("appid");
        placementId = arguments.getString("placementid");
        country = arguments.getString("country");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("appid", appId);
        outState.putString("placementid", placementId);
        outState.putString("country", country);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && color != 0) {
            ((AppWall) getActivity()).colorizeActionBar(getResources().getColor(colorArray[getArguments().getInt("position")]));
        }

        if (isVisibleToUser) {

            if (appwallAdapter != null) {
                appwallAdapter.setCallImpressions(true);
            } else {
                runAdapter = true;
            }

        } else {
            if (appwallAdapter != null) appwallAdapter.setCallImpressions(false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        gridLayoutManager = new GridLayoutManager(inflater.getContext(), 6);

        progressBar = (FrameLayout) v.findViewById(R.id.progressBar2);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics())));

        return v;
    }

    RecyclerView mRecyclerView;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        int color = getResources().getColor(colorArray[getArguments().getInt("position")]);
        appwallAdapter = new AppwallAdapter(ads, activity.getMobrandCore(), color, placementId);

        setApps(activity.getNativeAdList());
        EventBus.getDefault().register(this);


        appwallAdapter.setCallImpressions(runAdapter);
        mRecyclerView.setAdapter(appwallAdapter);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {

                int ordinal = mRecyclerView.getAdapter().getItemViewType(position);

                ViewType itemViewType = ViewType.values()[ordinal];

                switch (itemViewType) {
                    case PAGER:
                    case HEADER:
                    case MOBRANDO:
                    case VIDEO:
                        return gridLayoutManager.getSpanCount();
                    default:
                        return gridLayoutManager.getSpanCount() / 3;
                }

            }

        });




    }

    public void onEvent(NewAdsEvent event){
        setApps(event.getList());
    }

    public void setApps(List<Ad> list){
        ads.clear();
        int position = getArguments().getInt("position");



        switch (position){

            case 0:

                createAllAds(list, ads);

                break;
            case 1:

                createGamesAds(list, ads);

                break;
            case 2:
                createAppsAds(list, ads);
                break;

        }


/*
        ads.addAll(list);

*/
        ads.add(new Mobrando());

        //ads.add(new Header("Local top"));

        mRecyclerView.scheduleLayoutAnimation();
        progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
        progressBar.setVisibility(View.GONE);
        appwallAdapter.notifyDataSetChanged();


    }

    private void createAllAds(List<Ad> list, ArrayList<MobrandType> ads) {

        String firstHeader = "Recommended Downloads";
        String[] strings = {"Don't miss these ones", "Weekly arrivals", "Best apps of the month", "Most searched", "Must have"};

        int counter=0;
        int groupCount=0;
        if(!list.isEmpty()){
            ads.add(new Header(firstHeader));
            counter += fillAds(list, ads, counter, 6);

        }

        while (counter  < list.size()){
            ads.add(new Header(strings[groupCount%strings.length]));
            counter += fillAds(list, ads, counter, 6);
            groupCount++;
        }

        System.out.println(list.size() + " " + counter);




    }

    private void createGamesAds(List<Ad> list, ArrayList<MobrandType> ads) {

        String firstHeader = "Recommended Games";
        String[] strings = {"Powerful Games", "Best games of the month", "Most searched games", "Must have games"};
        int counter=0;
        int groupCount=0;

        ArrayList<Ad> list1 = new ArrayList<>();

        for(Ad ad: list){
            int category1 = ad.getCategory();

            CategoryEnum categoryEnum = CategoryEnum.forValue(category1);

            if(categoryEnum == CategoryEnum.GAMES) {
                list1.add(ad);
            }
        }

        list = list1;

        if(!list.isEmpty()){
            ads.add(new Header(firstHeader));
            counter += fillAds(list, ads, counter, 6);
        }

        while (counter< list.size()){
            ads.add(new Header(strings[groupCount%strings.length]));
            counter += fillAds(list, ads, counter, 6);
            groupCount++;
        }

        System.out.println(list.size() + " " + counter);



    }

    private void createAppsAds(List<Ad> list, ArrayList<MobrandType> ads) {

        String firstHeader = "Recommended Apps";
        String[] strings = {"High utility apps", "For trend setters", "Powerful Apps", "Best apps of the month"};

        int counter=0;
        int groupCount = 0;

        ArrayList<Ad> list1 = new ArrayList<>();

        for (Ad ad : list) {
            int category1 = ad.getCategory();

            CategoryEnum categoryEnum = CategoryEnum.forValue(category1);

            if (categoryEnum == CategoryEnum.APPS) {
                list1.add(ad);
            }
        }

        list = list1;


        if(!list.isEmpty()){
            ads.add(new Header(firstHeader));
            counter += fillAds(list, ads, counter, 6);
        }

        while (counter < list.size()){
            ads.add(new Header(strings[groupCount%strings.length]));
            counter += fillAds(list, ads, counter, 6);
            groupCount++;
        }

        System.out.println(list.size() + " " + counter);


    }

    private int fillAds(List<Ad> list, ArrayList<MobrandType> ads, int counter, int limit) {

        limit = counter + limit ;

        for(; counter < limit && counter < list.size(); counter++ ){


            Ad nativeAd = list.get(counter);


            CategoryEnum categoryEnum = CategoryEnum.forValue(nativeAd.getCategory());

            AppWallAd appWallAd = new AppWallAd();

            appWallAd.setAdid(nativeAd.getAdid());
            appWallAd.setCategory(categoryEnum);
            appWallAd.setDescription(nativeAd.getDescription());
            appWallAd.setName(nativeAd.getName());
            appWallAd.setPackageName(nativeAd.getPackageName());
            appWallAd.setIcon(nativeAd.getIcon());

            ads.add(appWallAd);


        }


        return counter;

    }


}
