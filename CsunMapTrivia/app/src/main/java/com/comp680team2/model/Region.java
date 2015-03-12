package com.comp680team2.model;

import java.util.List;
import java.util.Vector;

/**
 * Created by Russell on 3/11/2015.
 */
public class Region {
    private int id;
    private String label;
    private List<Vector<Double>> coordinates;

    public Region(int id, String label, List<Vector<Double>> coordinates) {
        this.id = id;
        this.label = label;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Vector<Double> getCoordinate(int index) {
        return coordinates.get(index);
    }
}
