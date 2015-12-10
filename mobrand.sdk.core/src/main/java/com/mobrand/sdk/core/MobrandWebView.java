package com.mobrand.sdk.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Base64;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mobrand.sdk.core.model.Config;
import com.mobrand.sdk.core.model.TimeoutStats;
import com.mobrand.sdk.core.model.TrackingOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by rmateus on 30/11/15.
 */
public class MobrandWebView extends WebView {

    private final Client client;
    Timer timer;

    public MobrandWebView(Context context, Config config, DeviceInfo info) {
        super(context);
        setWebChromeClient(new WebChromeClient());
        client = new Client(config, info, this);
        setWebViewClient(client);

    }

    public Client getClient(){
        return client;
    }

    public MobrandWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new IllegalStateException("this view cannot be instanciated use MobrandWebView(Context, Config, DeviceInfo)");
    }

    public MobrandWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        throw new IllegalStateException("this view cannot be instanciated use MobrandWebView(Context, Config, DeviceInfo)");

    }

    public MobrandWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        throw new IllegalStateException("this view cannot be instanciated use MobrandWebView(Context, Config, DeviceInfo)");
    }

    private static class Stats{
        public String adid;
        public long time;
        public ArrayList<String> urls = new ArrayList<>();
        public int position;
    }

    public class MyTimerTask extends TimerTask {

        private Stats stats;
        private Config config;

        public MyTimerTask(Stats stats, Config config) {
            this.stats = stats;
            this.config = config;
        }

        @Override
        public void run() {

            RestAdapter adapter = new RestAdapter.Builder().setEndpoint(config.getImpressionsEndpoint()).build();

            TimeoutStats timeoutStats = new TimeoutStats(
                    stats.adid,
                    stats.time,
                    stats.position,
                    stats.urls);

            try {
                adapter.create(Webservices.class).postTimeoutStats(timeoutStats);
            } catch (RetrofitError e){
                e.printStackTrace();
            }

        }

    }

    private void startTimer(Stats adid, Config config) {

        if( timer != null ) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new MyTimerTask(adid, config ), 10000);

    }

    public static class Client extends WebViewClient{

        private final DeviceInfo deviceInfo;
        private static String AAID;
        private static String ANDROID_ID;
        private static String MAC_ADDRESS;
        private static String DEVICE_ID;
        private final MobrandWebView webView;

        private long startTime;
        private String adid;
        private Stats stats;
        private Config config;
        private MobrandCore.ClickCallback callback;

        public Client(Config config, DeviceInfo info, MobrandWebView webView) {

            this.config = config;
            this.deviceInfo = info;
            this.webView = webView;
        }

        public void load(String adid,
                         String placementid,
                         int position, MobrandCore.ClickCallback callback) {

            String url = Utils.buildClickUrl(config, deviceInfo, adid, placementid);

            startTime = System.currentTimeMillis();

            this.adid = adid;

            stats = new Stats();
            stats.adid = adid;
            stats.urls.add(url);
            stats.position = position;
            stats.time = startTime;

            AAID = deviceInfo.getAdvId();
            ANDROID_ID = deviceInfo.getaId();
            MAC_ADDRESS = deviceInfo.getMacAddr();
            DEVICE_ID = deviceInfo.getMacAddr();
            this.callback = callback;


            webView.loadUrl(url);


        }



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            MobrandWebView webView = (MobrandWebView) view;

            System.out.println(url);

            if (url.contains("market://") || url.contains("https://play.google.com")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.android.vending");
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String packageName = url.split("id=")[1].split("&")[0];

                PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit().putString(packageName, adid).apply();

                view.getContext().startActivity(intent);
                //Toast.makeText(view.getContext(), "Sending to Play with package " + packageName, Toast.LENGTH_LONG).show();
                stats.time = (System.currentTimeMillis() - startTime);
                view.stopLoading();
                callback.onReady();

            }else{


                Uri uri = Uri.parse(url);

                String host = uri.getHost();

                String domain = host.replaceAll(".*\\.(?=.*\\.)", "");

                System.out.println(domain);

                if(config.getTrackMap().containsKey(domain)){

                    TrackingOptions options = config.getTrackMap().get(domain);

                    HashMap<String, String> parametersToAdd = initParameters(options);

                    for (Map.Entry<String, String> entry : parametersToAdd.entrySet()) {

                        if(entry.getKey() != null && entry.getValue()!= null) {
                            uri = uri.buildUpon().appendQueryParameter(entry.getKey(), entry.getValue()).build();
                        }
                    }

                }

                view.loadUrl(uri.toString());

            }

            stats.urls.add(Base64.encodeToString(url.getBytes(), Base64.URL_SAFE).replaceAll("\n", ""));

            webView.startTimer(stats, config);

            return true;
        }

        private  HashMap<String, String> initParameters(TrackingOptions options) {

            HashMap<String, String> map = new HashMap<>();


            String advId = options.getAdvId();
            String aid = options.getaId();
            String deviceId = options.getDeviceId();
            String macAddr = options.getMacAddr();

            if(advId != null){
                map.put(advId, AAID);
            }

            if(aid != null){
                map.put(aid, ANDROID_ID);
            }

            if(deviceId != null){
                map.put(deviceId, DEVICE_ID);
            }

            if(macAddr != null){
                map.put(macAddr, MAC_ADDRESS);
            }

            return map;
        }
    }





}
