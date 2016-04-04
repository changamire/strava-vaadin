package com.github.changamire;

import javastrava.api.v3.service.Strava;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

public class StravaQueryFactory implements QueryFactory {

    private final Strava strava;
    private Integer athleteId;

    public StravaQueryFactory(final Strava strava, final Integer athleteId) {
        this.strava = strava;
        this.athleteId = athleteId;
    }

    @Override
    public Query constructQuery(QueryDefinition queryDefinition) {
        return new StravaQuery(strava, athleteId);
    }

    public void setAthleteId(final int athleteId) {
        this.athleteId = athleteId;
    }

}
