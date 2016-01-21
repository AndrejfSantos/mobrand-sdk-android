package com.mobrand.model;

import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rmateus on 12/01/16.
 */
public class GroupStringsMapJson extends HashMap<Integer, List<String>> {


    public SparseArrayCompat<List<String>> convertToSparseArray(){

        SparseArrayCompat<List<String>> sparseArray = new SparseArrayCompat<>();

        for (Entry<Integer, List<String>> entry : entrySet()) {
            sparseArray.put(entry.getKey(), entry.getValue());

        }

        return sparseArray;
    }

}
