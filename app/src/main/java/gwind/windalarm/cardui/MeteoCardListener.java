package gwind.windalarm.cardui;

import gwind.windalarm.data.MeteoStationData;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface MeteoCardListener {
    // you can define any parameter as per your requirement
    public void meteocardselected(long index, final MeteoStationData meteoStationData);
}
