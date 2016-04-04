package com.github.changamire;

import javastrava.api.v3.service.Strava;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

public class StravaFriendActivityQueryFactory implements QueryFactory {

    private final Strava strava;

    public StravaFriendActivityQueryFactory(final Strava strava) {
        this.strava = strava;
    }

    @Override
    public Query constructQuery(QueryDefinition queryDefinition) {
        return new StravaFriendActivityQuery(strava);
    }
}