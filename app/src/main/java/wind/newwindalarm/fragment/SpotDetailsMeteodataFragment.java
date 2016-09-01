package wind.newwindalarm.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;

public class SpotDetailsMeteodataFragment extends Fragment {

    private long spotID;
    private MeteoStationData meteoData;

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

        spotID = getArguments().getLong("spotID");
        String str = getArguments().getString("meteodata");

        if (str != null) {
            try {
                JSONObject json = new JSONObject(str);
                meteoData = new MeteoStationData(json);
                refreshData();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return v;
    }

    private void refreshData() {

    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
    }
}
