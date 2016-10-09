package wind.newwindalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.R;
import wind.newwindalarm.Spot;
import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.data.Forecast;

/**
 * Created by Giacomo Spanò on 09/09/2016.
 */
public class ForecastFragment extends Fragment implements MeteoForecastListener, SpotDetailsFragmentInterface {

    private ListView lv;
    private TextView mLocationTextView;
    private TextView mLastUpadteTextView;
    private TextView mPositionTextView;

    /*private TextView mPressureTextView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;*/

    MeteoForecastListener.MeteoForecastArrayAdapter adapter;
    Forecast mForecast;

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

        if (savedInstanceState != null) {
            //setMeteoData((MeteoStationData) savedInstanceState.getSerializable("meteoData"));
            //setSpotId(savedInstanceState.getLong("spotId"));
            mForecast = (Forecast) savedInstanceState.getSerializable("forecast");

            MainActivity ma = (MainActivity) getActivity();
            mCallback = ma.getForecastListener();
        }

        View v;
        v = inflater.inflate(R.layout.fragment_forecast, container, false);

        mLocationTextView = (TextView) v.findViewById(R.id.locationTextView);
        mLastUpadteTextView = (TextView) v.findViewById(R.id.lastUpdateTextView);
        mPositionTextView = (TextView) v.findViewById(R.id.postitionTextView);
        /*mPressureTextView = (TextView) v.findViewById(R.id.pressureTextView);
        mHumidityTextView = (TextView) v.findViewById(R.id.humidityTextView);
        mWindTextView = (TextView) v.findViewById(R.id.windTextView);*/

        updateHeader();

        lv = (ListView) v.findViewById(R.id.list_view);
        setForecast(mForecast);
        return v;
    }

    private void updateHeader() {

        if (mForecast != null && mForecast.sourceSpotName != null)
            mLocationTextView.setText(mForecast.sourceSpotName);
        else
            mLocationTextView.setText("---");

        if (mForecast != null && mForecast.lastUpdate != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            mLastUpadteTextView.setText(df.format(mForecast.lastUpdate));
        } else {
            mLastUpadteTextView.setText("---");
        }

        if (mForecast != null && mForecast.lat != null && mForecast.lon != null) {
            mPositionTextView.setText("" + mForecast.lat + "°," + mForecast.lon + "°,");
        } else {
            mPositionTextView.setText("---");
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("forecast", (Serializable) mForecast);
    }

    public void setForecast(Forecast forecast) {

        mForecast = forecast;

        if (forecast != null && getActivity() != null) {
            adapter = new MeteoForecastArrayAdapter(getActivity(), mForecast, this);
            lv.setAdapter(adapter);
            updateHeader();
        }
    }


    @Override
    public void onClick() {
        //mCallback.onMeteoForecastClick(spot);
    }


}
