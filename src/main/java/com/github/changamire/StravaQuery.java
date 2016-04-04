package com.github.changamire;

import java.util.ArrayList;
import java.util.List;

import javastrava.api.v3.model.StravaActivity;
import javastrava.api.v3.model.StravaStatistics;
import javastrava.api.v3.service.Strava;
import javastrava.util.Paging;

import org.vaadin.addons.lazyquerycontainer.Query;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class StravaQuery implements Query {

    final Strava strava;
    final Integer athleteId;

    public StravaQuery(final Strava strava, final Integer athleteId) {
        this.strava = strava;
        this.athleteId = athleteId;
    }

    @Override
    public Item constructItem() {
        return new BeanItem<StravaActivity>(new StravaActivity());
    }

    @Override
    public boolean deleteAllItems() {
        return false;
    }

    @Override
    public List<Item> loadItems(int startIndex, int count) {
        List<StravaActivity> activityPage = strava
                .listAuthenticatedAthleteActivities(new Paging(
                        (startIndex / count), count));

        List<Item> results = new ArrayList<Item>();
        for (StravaActivity activity : activityPage) {
            results.add(new BeanItem<StravaActivityDTO>(new StravaActivityDTO(activity)));
        }
        return results;
    }

    @Override
    public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public int size() {
        StravaStatistics myStats = strava.statistics(athleteId);

        int count = myStats.getAllRunTotals().getCount()
                + myStats.getAllRideTotals().getCount()
                + myStats.getAllSwimTotals().getCount();
        return count;
    }

}
