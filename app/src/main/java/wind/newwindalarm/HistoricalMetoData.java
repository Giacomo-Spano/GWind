package wind.newwindalarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wind.newwindalarm.data.MeteoStationData;

/**
 * Created by Giacomo Spanò on 03/09/2016.
 */
public class HistoricalMetoData {

    private ArrayList<Data> meteoHistory;

    public HistoricalMetoData() {
        meteoHistory = new ArrayList<Data>();
    }

    public void add(long spotId, List<MeteoStationData> list)
    {
        Data data = new Data();
        data.id = spotId;
        data.list = list;
        meteoHistory.add(data);
    }

    public List<MeteoStationData> getFromId(long spotId) {

        if (meteoHistory == null)
            return null;

        Iterator<Data> iterator = meteoHistory.iterator();
        while (iterator.hasNext()) {
            Data data = (Data) iterator;
            if (data.id == spotId);
                return data.list;
        }
        return null;
    }

    private class Data {
        long id;
        List<MeteoStationData> list;
    }
}
