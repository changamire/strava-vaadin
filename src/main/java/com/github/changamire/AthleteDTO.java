package com.github.changamire;

public class AthleteDTO {

    private final int id;
    private final String name;

    public AthleteDTO(int id, String firstName, final String lastName) {
        super();
        this.id = id;
        this.name = firstName + " " + lastName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }

}