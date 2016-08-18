package wind.newwindalarm;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

public class FullChartFragment extends Fragment {

    private LineChart mLineChart;
    private long spotID;
    MeteoStationData meteoData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


        View v;
        v = inflater.inflate(R.layout.fragment_fullchart, container, false);

        mLineChart = (LineChart) v.findViewById(R.id.chart);

        return v;
    }

    public void onBackPressed() {
        // super.onBackPressed();
        // myFragment.onBackPressed();
    }

}
