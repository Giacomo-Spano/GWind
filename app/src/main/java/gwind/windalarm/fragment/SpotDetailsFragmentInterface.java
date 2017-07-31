package gwind.windalarm.fragment;

import gwind.windalarm.data.MeteoStationData;

/**
 * Created by Giacomo Spanò on 24/09/2016.
 */

public interface SpotDetailsFragmentInterface {

    public void setMeteoData(MeteoStationData meteoData);
    public void setSpotId(long id);
    public void refreshData();
}
