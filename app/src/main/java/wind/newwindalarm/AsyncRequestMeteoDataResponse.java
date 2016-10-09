package wind.newwindalarm;

import java.util.List;

import wind.newwindalarm.data.Location;
import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.data.Forecast;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncRequestMeteoDataResponse {

    void processFinish(List<MeteoStationData> list, boolean error, String errorMessage);
    void processFinishHistory(long spotId,List<MeteoStationData> list, /*long spotID, */boolean error, String errorMessage);
    void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage);
    void processFinishAddFavorite(long spotId,boolean error, String errorMessage);
    void processFinishRemoveFavorite(long spotId,boolean error, String errorMessage);

    // ritorna le previsioni del tempo per la location richiest
    void processFinishForecast(int requestId, Forecast forecast, boolean error, String errorMessage);
    void processFinishForecastLocation(List<Location> locations, boolean error, String errorMessage);
}