package com.mobrand.mobrandsample;

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

import com.mobrand.json.model.Ads;
import com.mobrand.json.model.IAds;
import com.mobrand.json.model.Impression;
import com.mobrand.model.Header;
import com.mobrand.model.MobrandType;
import com.mobrand.model.Mobrando;
import com.mobrand.model.Pager;
import com.mobrand.model.ViewType;
import com.mobrand.sdk.R;
import com.mobrand.view.AppwallAdapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
    ArrayList<IAds> pagerAds = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;
    int color = 1;

    BlockingQueue<Impression> strings = new ArrayBlockingQueue<>(1000);

    int colorArray[] = new int[]{R.color.mobrand, R.color.green, R.color.orange, R.color.blue};
    private AppwallAdapter appwallAdapter;
    private boolean runAdapter;
    private FrameLayout progressBar;
    private String impressionsEndpoint = "http://api.mobrand.net";


    RestAdapter adsAdapter = new RestAdapter.Builder().setEndpoint(adsEndpoint).build();



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

        if (appId == null || placementId == null) {
            throw new RuntimeException("AppId or PlacementId is null: appid:" + appId + " placementid:" + placementId);
        }


    }

    private void configure(Bundle arguments) {
        appId = arguments.getString("appid");


        //adsEndpoint = arguments.getString("ads_endpoint");
        //clicksEndpoint = arguments.getString("clicks_endpoint");
        //ximpressionsEndpoint = arguments.getString("impressions_endpoint");

        placementId = arguments.getString("placementid");

        country = arguments.getString("country");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("appid", appId);

        outState.putString("ads_endpoint", adsEndpoint);
        outState.putString("placementid", placementId);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && color != 0) {
            ((MainActivity) getActivity()).colorizeActionBar(getResources().getColor(colorArray[getArguments().getInt("position")]));
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int color = getResources().getColor(colorArray[getArguments().getInt("position")]);
        appwallAdapter = new AppwallAdapter(ads, pagerAds, strings, color, appId, placementId);

        appwallAdapter.setCallImpressions(runAdapter);
        mRecyclerView.setAdapter(appwallAdapter);


        Callback<List<Ads>> trending = new Callback<List<Ads>>() {

            @Override
            public void success(List<Ads> adsJson, retrofit.client.Response response) {
                ads.clear();

                for (Ads list : adsJson) {

                    ads.add(new Header(list.getName()));

                    if (list.getType().equals("pager")) {

                        ads.add(new Pager());

                        for (com.mobrand.json.model.List list1 : list.getList()) {
                            pagerAds.add(list1);
                        }

                    } else {

                        for (com.mobrand.json.model.List list1 : list.getList()) {
                            ads.add(list1);
                        }

                    }

                }


                ads.add(new Mobrando());

                //ads.add(new Header("Local top"));
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

                mRecyclerView.scheduleLayoutAnimation();
                progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                progressBar.setVisibility(View.GONE);
                appwallAdapter.notifyDataSetChanged();


            }

            @Override
            public void failure(RetrofitError error) {

            }

        };

        int position = getArguments().getInt("position");

        switch (position) {
            case 0:
                Log.d("Mobrand", "getting ads for " + appId + " " + placementId);
                adsAdapter.create(Webservices.class).getAds(appId, placementId, null, country, trending);
                break;
            case 1:
                adsAdapter.create(Webservices.class).getAds(appId, placementId, "games", country, trending);
                break;
            case 2:
                adsAdapter.create(Webservices.class).getAds(appId, placementId, "apps", country, trending);
                break;

            default:
                throw new IllegalStateException("This should be happening");

        }
    }

}
