package com.github.changamire;

public class FriendActivityDTO {

    private String name;
    private String type;
    private Float distance;
    private String polyline;

    public FriendActivityDTO(String firstName, String lastName, String type, Float distance, String polyline) {
        super();
        this.name = firstName + " " + lastName;
        this.type = type;
        this.distance = distance;
        this.polyline = polyline;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Float getDistance() {
        return distance;
    }

    public String getPolyLine() {
        return polyline;
    }
}
