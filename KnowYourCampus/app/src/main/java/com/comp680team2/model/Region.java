/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * Region.java
 */

package com.comp680team2.model;

import java.util.List;

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
