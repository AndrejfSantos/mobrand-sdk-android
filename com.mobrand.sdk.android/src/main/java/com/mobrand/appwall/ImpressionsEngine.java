package com.mobrand.appwall;

import com.mobrand.json.model.Impression;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;

/**
 * Created by rmateus on 11/08/15.
 */
public class ImpressionsEngine {


    static ExecutorService service;
    static BlockingQueue<Impression> impressions = new ArrayBlockingQueue<>(5000);


    private ImpressionsEngine() {
    }


    private static boolean isRunning = true;

    public synchronized static void init(String impressionsEndpoint) {
        if (service == null) {
            service = Executors.newSingleThreadExecutor();
            isRunning = true;
        }
        RestAdapter impressionsAdapter = new RestAdapter.Builder().setEndpoint(impressionsEndpoint).build();


        final ArrayList<Impression> impressionArrayList = new ArrayList<>();

        final Webservices webservices = impressionsAdapter.create(Webservices.class);


        service.submit(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {

                    try {
                        Thread.sleep(5000);
                        Impression take = impressions.take();
                        impressionArrayList.add(take);
                        impressions.drainTo(impressionArrayList);
                        webservices.postImpression(impressionArrayList);
                        impressionArrayList.clear();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        });
    }

    public static void addImpression(Impression impression) {
        impressions.add(impression);
    }


    public synchronized static void stop() {

        if (!service.isShutdown()) {
            isRunning = false;
            service.shutdown();
            service = null;

        }

    }

}
