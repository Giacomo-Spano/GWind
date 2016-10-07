package wind.newwindalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.R;
import wind.newwindalarm.Spot;
import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.data.WindForecast;

/**
 * Created by Giacomo Spanò on 09/09/2016.
 */
public class ForecastFragment extends Fragment implements MeteoForecastListener, SpotDetailsFragmentInterface {

    // List view
    private ListView lv;
    // Listview Adapter
    MeteoForecastListener.MeteoForecastArrayAdapter adapter;
    //SearchSpotListener mListener;
    WindForecast mForecast;

    OnMeteoForecastClickListener mCallback;

    public void refreshData() {

    }

    public void setSpotId(long spotId) {

    }

    public void setMeteoData(MeteoStationData data) {

    }

    // Container Activity must implement this interface
    public interface OnMeteoForecastClickListener {
        void onMeteoForecastClick(Spot spot);
    }

    public void setListener(OnMeteoForecastClickListener listener) {
        mCallback = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        v = inflater.inflate(R.layout.fragment_forecast, container, false);

        lv = (ListView) v.findViewById(R.id.list_view);

    return v;
    }

    public void setForecast(WindForecast forecast) {

        mForecast = forecast;

        adapter = new MeteoForecastArrayAdapter(getActivity(), mForecast, this);
        lv.setAdapter(adapter);
    }

    @Override
    public void onClickCheckBox(Spot spot, boolean selected) {
        MainActivity a = (MainActivity) getActivity();

        if (selected)
            a.addToFavorites(spot.id);
        else
            a.removeFromFavorites(spot.id);
    }

    @Override
    public void onClick(Spot spot) {
        mCallback.onMeteoForecastClick(spot);
    }


}
