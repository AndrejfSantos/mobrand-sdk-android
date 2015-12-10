package com.mobrand.sdk.core;


import com.mobrand.sdk.core.model.Config;
import com.mobrand.sdk.core.model.Impression;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by rmateus on 11/08/15.
 */
public class ImpressionsEngine {

    private static ImpressionsEngine impressionsEngine;
    ExecutorService service;

    private boolean isRunning = true;

    public static ImpressionsEngine getInstance(DeviceInfo info, Config config){
        if(impressionsEngine == null){
            impressionsEngine = new ImpressionsEngine(info, config);
        }
        return impressionsEngine;
    }

    private static class ImpressionPair{

        public final String placementId;
        public final String adId;

        public ImpressionPair(String placementId, String adId) {

            this.placementId = placementId;
            this.adId = adId;
        }
    }

    BlockingQueue<ImpressionPair> queue = new ArrayBlockingQueue<ImpressionPair>(5000);

    private ImpressionsEngine(final DeviceInfo info, Config config) {

        if (service == null) {
            service = Executors.newSingleThreadExecutor();
            isRunning = true;
        }


        RestAdapter impressionsAdapter = new RestAdapter.Builder().setEndpoint(config.getImpressionsEndpoint()).build();

        final Webservices webservices = impressionsAdapter.create(Webservices.class);


        service.execute(new Runnable() {
            @Override
            public void run() {

                System.out.println("on run");

                while (isRunning) {
                    System.out.println("on while");

                    try {

                        System.out.println("after sleep");

                        HashMap<String, ArrayList<String>> map = new HashMap<>();


                        ArrayList<ImpressionPair> impressionPairs = new ArrayList<ImpressionPair>();

                        System.out.println(queue.size());
                        impressionPairs.add(queue.take());
                        Thread.sleep(5000);
                        queue.drainTo(impressionPairs);

                        for (ImpressionPair impressionPair : impressionPairs) {

                            if(map.get(impressionPair.placementId)==null){
                                map.put(impressionPair.placementId, new ArrayList<String>());
                            }
                            map.get(impressionPair.placementId ).add(impressionPair.adId);
                        }


                        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {

                            System.out.println("posting " + map);


                            Impression impression = new Impression();

                            impression.setAppId(info.getAppId());
                            impression.setAndroidId(info.getaId());
                            impression.setPlacementId(entry.getKey());
                            impression.setAdvId(info.getAdvId());
                            impression.setImpressions(entry.getValue());

                            System.out.println("posting " + impression);


                            webservices.postImpression(impression);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RetrofitError e){
                        RetrofitError.Kind kind = e.getKind();
                        System.out.println(kind);
                    }

                }

            }
        });
    }

    public void addImpression(String adId, String placementId) {

       queue.add(new ImpressionPair(placementId, adId));

    }


    public synchronized  void stop() {

        if (!service.isShutdown()) {
            isRunning = false;
            service.shutdown();
            service = null;
            impressionsEngine = null;
        }

    }

}
