package com.github.changamire;

import javastrava.api.v3.model.StravaActivity;

public class StravaActivityDTO {

    private final StravaActivity stravaActivity;

    public StravaActivityDTO(final StravaActivity stravaActivity) {
        this.stravaActivity = stravaActivity;
    }

    public int getId() {
        return stravaActivity.getId();
    }

    public String getName() {
        return stravaActivity.getAthlete().getFirstname() + " "
                + stravaActivity.getAthlete().getLastname();
    }

    public String getSummaryPolyline() {
        return stravaActivity.getMap().getSummaryPolyline();
    }

    public String getType() {
        return stravaActivity.getType().getDescription();
    }

    public Float getDistance() {
        return stravaActivity.getDistance();
    }

    public Float getAverageHeartrate() {
        return stravaActivity.getAverageHeartrate();
    }

    public Float getAverageSpeed() {
        return stravaActivity.getAverageSpeed();
    }

    public int getMovingTime() {
        return stravaActivity.getMovingTime();
    }

    public StravaActivity getStravaActivity() {
        return stravaActivity;
    }

}
