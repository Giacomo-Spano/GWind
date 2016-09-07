package wind.newwindalarm.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;

public class SpotDetailsMeteodataFragment extends Fragment {

    private long spotID;
    private MeteoStationData meteoData;
    private TextView speedTextView;
    private TextView avspeedTextView;
    private TextView temperatureTextView;
    private TextView pressureTextView;
    private TextView humidityTextView;
    private TextView rainrateTextView;
    private TextView lastupdateTextView;
    private TextView sourceTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_spotdetails_meteodata, container, false);

        speedTextView = (TextView) v.findViewById(R.id.speedTextView);
        avspeedTextView = (TextView) v.findViewById(R.id.avspeedTextView);
        temperatureTextView = (TextView) v.findViewById(R.id.temperatureTextView);
        pressureTextView = (TextView) v.findViewById(R.id.pressureTextView);
        humidityTextView = (TextView) v.findViewById(R.id.humidityTextView);
        rainrateTextView = (TextView) v.findViewById(R.id.rainrateTextView);
        lastupdateTextView = (TextView) v.findViewById(R.id.lastupdateTextView);
        sourceTextView = (TextView) v.findViewById(R.id.sourceTextView);

        refreshData();
        return v;
    }

    public void refreshData() {

        if (meteoData != null) {


            if (speedTextView != null && meteoData.speed != null)
                speedTextView.setText(meteoData.speed + "Km/h");
            if (avspeedTextView != null && meteoData.averagespeed != null)
                avspeedTextView.setText(meteoData.averagespeed + "Km/h");
            if (temperatureTextView != null && meteoData.temperature != null)
                temperatureTextView.setText(meteoData.temperature + "°C");
            if (pressureTextView != null && meteoData.pressure != null)
                pressureTextView.setText(meteoData.pressure + "hPa");
            if (humidityTextView != null && meteoData.humidity != null)
                humidityTextView.setText(meteoData.humidity + "%");
            if (rainrateTextView != null && meteoData.rainrate != null)
                rainrateTextView.setText(meteoData.rainrate + "mm");
            if (lastupdateTextView != null && meteoData.sampledatetime != null) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                lastupdateTextView.setText(df.format(meteoData.sampledatetime));
            }
        }
        //if (sourceTextView != null)
        //sourceTextView.setText(meteoData. + "Km/h");
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
        refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }
}
