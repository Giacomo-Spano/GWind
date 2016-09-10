package wind.newwindalarm;

import java.util.List;

/**
 * Created by giacomo on 01/07/2015.
 */
public interface AsyncRequestMeteoDataResponse {

    void processFinish(List<Object> list/*, long spotID*/, boolean error, String errorMessage);
    void processFinishHistory(List<MeteoStationData> list, /*long spotID, */boolean error, String errorMessage);
    void processFinishSpotList(List<Object> list, boolean error, String errorMessage);
    void processFinishFavorites(boolean error, String errorMessage);
}