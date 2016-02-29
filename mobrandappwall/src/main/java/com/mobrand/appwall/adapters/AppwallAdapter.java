package com.mobrand.appwall.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobrand.appwall.classic.R;
import com.mobrand.appwall.view.model.MobrandType;
import com.mobrand.appwall.view.model.ViewType;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.appwall.view.model.Pager;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 11/08/15.
 */
public class AppwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final int color;
    private final MobrandCore core;

    Handler handler = new Handler(Looper.getMainLooper());

    private boolean isClickable = true;

    ArrayList<MobrandType> ads = new ArrayList<>();

    private boolean callimpressions;
    private String placementId;

    public AppwallAdapter(ArrayList<MobrandType> ads, MobrandCore core,  int color, String placementId) {

        this.ads = ads;
        this.color = color;
        this.core = core;
        this.placementId = placementId;


    }

    @Override
    public int getItemViewType(int position) {
        return ads.get(position).getViewType().ordinal();
    }

    public void setCallImpressions(boolean callImpressions){
        this.callimpressions = callImpressions;
    }

    public void setIsClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    public class RepeatingTask implements Runnable{

        private ViewPager pager;

        public RepeatingTask(ViewPager pager) {
            this.pager = pager;
        }

        @Override
        public void run() {

            Pager.MobrandPagerAdapter adapter = (Pager.MobrandPagerAdapter) pager.getAdapter();

            if (!adapter.isTouching()) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            }

            handler.postDelayed(this, 3000);
        }
    }


    public ViewPager createViewPager(Context context){
        ViewPager viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) dipToPixels(context, 150)));
        return viewPager;
    }

    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int type) {

        View inflate;
        ViewType viewType = ViewType.values()[type];


        switch (viewType) {

            case PAGER:
                final ViewPager pager = createViewPager(parent.getContext());
                pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getResources().getDisplayMetrics()));

                final Runnable repeatingTask = new RepeatingTask(pager);
                pager.setClipChildren(false);
                pager.setClipToPadding(false);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
                    initViewPagerRepeatingTask(pager, repeatingTask);
                }

                return new RecyclerView.ViewHolder(pager) {
                };
            case HEADER:
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.mb_header, parent, false);
                break;
            default:
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.mb_small_item, parent, false);
                break;
            case MOBRANDO:
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mb_mobrando, parent, false)) {
                };

        }

        return new AppwallViewHolder(inflate);
    }



    @TargetApi(13)
    private void initViewPagerRepeatingTask(ViewPager pager, final Runnable repeatingTask) {

        pager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                handler.postDelayed(repeatingTask, 3000);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                handler.removeCallbacks(repeatingTask);
            }
        });

        pager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_DOWN:
                    default:
                        handler.removeCallbacks(repeatingTask);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.postDelayed(repeatingTask, 3000);
                        break;

                }

                return false;
            }
        });
    }

    public boolean isClickable() {
        return isClickable;
    }

    public boolean isCallimpressions() {
        return callimpressions;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ads.get(position).onBindViewHolder(this, holder, position);

    }


    @Override
    public int getItemCount() {
        return ads.size();
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public int getColor() {
        return color;
    }

    public MobrandCore getMobrandCore() {
        return core;
    }

    public static class AppwallViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView appicon;
        public SmoothProgressBar progressBar;

        public AppwallViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            appicon = (ImageView) itemView.findViewById(R.id.appicon);
            progressBar = (SmoothProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }



}
