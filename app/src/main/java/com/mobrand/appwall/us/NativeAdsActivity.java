package com.mobrand.appwall.us;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobrand.sdk.core.ClickCallback;
import com.mobrand.sdk.core.GetAdsCallback;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.CategoryEnum;

import java.util.List;

/**
 * Created by rmateus on 04/05/16.
 */
public class NativeAdsActivity extends AppCompatActivity {

    private MobrandCore mobrandCore;
    private static String TAG = NativeAdsActivity.class.getName();
    private final static String PLACEMENT = "MegaPlacement";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mobrandCore = new MobrandCore(this);
        //This is needed to MobrandCore configure itself with the app, and get configuration
        //parameters.
        mobrandCore.create(new MobrandCallback() {
            @Override
            public void onReady(final MobrandCore mobrandCore) {

                //Will make an async call to the server and return the list to the callback
                //
                //Check other types of calls:
                //getAds (sync)
                //
                //For interstitial native ad:
                //getAd (single ad / sync)
                //getAdAsync (single ad / async)

                mobrandCore.getAdsAsync(PLACEMENT, new GetAdsCallback() {

                    @Override
                    public void onSuccess(List<Ad> list) {

                        //We get the first element of the list for demo purposes.
                        Ad ad = list.get(0);

                        //CategoryEnum can be APPS, GAMES, or MOBRAND
                        CategoryEnum category = CategoryEnum.forValue(ad.getCategory());

                        //The adid from the offer.
                        String adid = ad.getAdid();

                        //Description of the app to be promoted
                        String description = ad.getDescription();

                        //PackageName of the app to be promoted
                        String packageName = ad.getPackageName();

                        //Name of the app to be promoted
                        String name = ad.getName();

                        //IconURL of the app to be promoted
                        String icon = ad.getIcon();

                        //AddImpression will mark the ad as Viewed and send to the impression pool.
                        //This is also used for optimization purposes.
                        mobrandCore.addImpression(ad, PLACEMENT);


                        //Click will handle all the lifecycle needed to make a click.
                        //Will contact the server, request a click, and follow the required
                        //redirects. On successful click Google Play will be called immediately.
                        //The most common use is on a ClickListener.
                        mobrandCore.click(ad, PLACEMENT, 0, new ClickCallback() {

                            @Override
                            public void onReady() {
                                //This method will run when an offer was successfully sent to Google Play.
                                //Useful to clean up resources like loaders.
                            }

                            @Override
                            public void onError(String s) {
                                //Will happen when a click couldn't be loaded due to a timeout, or the offer has expired.
                                Log.d(TAG, "Couldn't load the click redirection" + s);
                            }

                        });
                    }

                    @Override
                    public void onFailure() {
                        //Will happen when Core can't get any ads.
                        Log.d(TAG, "A failure has occurred. Internet?");
                    }

                });
            }

            @Override
            public void onError() {
                //Will happen when Mobrand Core can't configure itself
                Log.d(TAG, "An error has occurred. Internet?");
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mobrandCore.destroy();
    }

}
