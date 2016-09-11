package wind.newwindalarm;

import java.util.List;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncRequestMeteoDataResponse {

    void processFinish(List<MeteoStationData> list, boolean error, String errorMessage);
    void processFinishHistory(List<MeteoStationData> list, /*long spotID, */boolean error, String errorMessage);
    void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage);
    void processFinishAddFavorite(long spotId,boolean error, String errorMessage);
    void processFinishRemoveFavorite(long spotId,boolean error, String errorMessage);
}