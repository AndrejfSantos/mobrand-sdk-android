package com.mobrand.model;

import android.support.v4.util.SparseArrayCompat;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rmateus on 12/01/16.
 */
public class DefaultGroups {


    private static final String[] generic = {"Recommended Downloads", "Don't miss these ones", "Weekly arrivals", "Best apps of the month", "Most searched", "Must have"};
    private static final String[] games = {"Recommended Games", "Powerful Games", "Best games of the month", "Most searched games", "Must have games"};
    private static final String[] apps = {"Recommended Apps", "High utility apps", "For trend setters", "Powerful Apps", "Best apps of the month"};


    public static SparseArrayCompat<List<String>> getSparseArray() {


        SparseArrayCompat<List<String>> defaultSparseArray = new SparseArrayCompat<>();

        defaultSparseArray.put(0, Arrays.asList(generic));
        defaultSparseArray.put(1, Arrays.asList(games));
        defaultSparseArray.put(2, Arrays.asList(apps));
        defaultSparseArray.put(3, Arrays.asList(generic));

        return defaultSparseArray;
    }


}
