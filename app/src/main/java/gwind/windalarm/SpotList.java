package gwind.windalarm;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import gwind.windalarm.data.Location;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.data.Forecast;
import gwind.windalarm.request.requestMeteoDataTask;

/**
 * Created by giacomo on 06/09/2015.
 */
public class SpotList {

    public static int spotlist_remove = 0;
    public static int spotlist_add = 1;
    public static int spotlist_reorder = 2;

    public List<Spot> spotList = new ArrayList<Spot>();

    public String getSpotName(long id) {
        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id)
                return spotList.get(i).spotName;
        }
        return null;
    }

    public void add(Spot spot) {
        spotList.add(spot);
    }

    public Spot getSpotFromId(long id) {

        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id) {

                return (Spot) spotList.get(i);
            }
        }
        return null;
    }


    public List<Spot> getSpotFavorites() {

        if (spotList == null)
            return null;

        List<Spot> favorites = new ArrayList<Spot>();
        for (Spot spot : spotList) {
            if (spot.favorites)
                favorites.add(spot);
        }
        return favorites;
    }

    public boolean isSpotFavorites(long spotId) {

        if (spotList == null)
            return false;

        for (Spot spot : spotList) {
            if (spot.id == spotId) {
                if (spot.favorites)
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    public void updateFavorites(Activity activity, long spotId, String userId, int command, String spotlist) {


        int requestType;
        if (command == spotlist_add)
            requestType = requestMeteoDataTask.REQUEST_ADDFAVORITE;
        else if (command == spotlist_remove)
            requestType = requestMeteoDataTask.REQUEST_REMOVEFAVORITE;
        else if (command == spotlist_reorder)
            requestType = requestMeteoDataTask.REQUEST_FAVORITESSORDER;
        else
            return;

        new requestMeteoDataTask(activity, new AsyncRequestMeteoDataResponse() {
            @Override
            public void processFinish(List<MeteoStationData> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishAddFavorite(long spotId, boolean error, String errorMessage) {

                for (Spot spot : spotList) {
                    if (spot.id == spotId) {
                        spot.favorites = true;
                        return;
                    }
                }
            }

            @Override
            public void processFinishRemoveFavorite(long spotId, boolean error, String errorMessage) {

                for (Spot spot : spotList) {
                    if (spot.id == spotId) {
                        spot.favorites = false;
                        return;
                    }
                }
            }

            @Override
            public void processFinishForecast(int requestId, Forecast forecast, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishForecastLocation(List<Location> locations, boolean error, String errorMessage) {

            }
        }, requestType).execute(spotId,userId, spotlist);
    }

    public void addToFavorites(Activity activity, long spotId, String userId) {
        updateFavorites(activity,spotId,userId,spotlist_add,null);
    }

    public void removeFromFavorites(Activity activity, long spotId, String userId) {
        updateFavorites(activity,spotId,userId,spotlist_remove,null);
    }

    public List<Spot> getSpotList() {
        return spotList;
    }
}
