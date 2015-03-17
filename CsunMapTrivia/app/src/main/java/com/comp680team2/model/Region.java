package com.comp680team2.model;

import java.util.List;
import java.util.Vector;

public class Region {
    private int id;
    private String label;
    private List<Coordinate> coordinates;

    public Region(int id, String label, List<Coordinate> coordinates) {
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

    public Coordinate getCoordinate(int index) {
        return coordinates.get(index);
    }
}
