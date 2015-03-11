package com.mobrand.mobrandsample;

import android.app.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;


public class MainActivity extends ActionBarActivity {

    public interface Webservice{
        @GET("/ads")
        public void getAds(Callback<List<AdsJson>> callback);
    }

    public static class Response{
        public List<String> list;
    }

    ArrayList<AdsJson> ads = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Picasso build = new Picasso.Builder(this).memoryCache(new LruCache(25000)).build();
        Picasso.setSingletonInstance(build);

        RecyclerView content = (RecyclerView) findViewById(R.id.content);

        content.setLayoutManager(new GridLayoutManager(this, 3));
        content.setAdapter(new AppwallAdapter(ads));

        RotateAnimation anim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2500);

        // Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.mobrando);
        splash.startAnimation(anim);
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://192.168.1.10:8080").build();

        adapter.create(Webservice.class).getAds(new Callback<List<AdsJson>>() {
            @Override
            public void success(List<AdsJson> adsJson, retrofit.client.Response response) {
                ads.clear();
                ads.addAll(adsJson);
                findViewById(R.id.please_wait).setVisibility(View.GONE);
                findViewById(R.id.content).setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error);
            }
        });

    }

    public static class AppwallAdapter extends RecyclerView.Adapter<AppwallAdapter.AppwallViewHolder>{

        ArrayList<AdsJson> ads = new ArrayList<>();

        public AppwallAdapter(ArrayList<AdsJson> ads) {
            this.ads = ads;
        }

        @Override
        public AppwallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_item, parent, false);

            return new AppwallViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(AppwallViewHolder holder, int position) {

            holder.name.setText(ads.get(position).name);
            holder.rating.setRating(ads.get(position).rating.floatValue());
            Picasso.with(holder.itemView.getContext()).load(ads.get(position).icon_url).into(holder.appicon);

        }

        @Override
        public int getItemCount() {
            return ads.size();
        }

        public static class AppwallViewHolder extends RecyclerView.ViewHolder{


            public TextView name;
            public ImageView appicon;
            public RatingBar rating;

            public AppwallViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                appicon = (ImageView) itemView.findViewById(R.id.appicon);
                rating = (RatingBar) itemView.findViewById(R.id.rating);
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
