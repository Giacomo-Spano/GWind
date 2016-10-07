package wind.newwindalarm.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.List;

import wind.newwindalarm.FullChartActivity;
import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.cardui.ChartCard;
import wind.newwindalarm.cardui.ChartCardItem;
import wind.newwindalarm.cardui.ChartCardListener;
import wind.newwindalarm.chart.HistoryChart;

public class SpotDetailsChartFragment extends Fragment implements SpotDetailsFragmentInterface, ChartCardListener {

    private LineChart mWindChart, mTrendChart, mTemperatureChart;
    private long spotID;
    MeteoStationData meteoData;
    List<MeteoStationData> meteoDataList;
    HistoryChart hc;
    private ChartCardItem mWindCard, mTrendCard, mTemperatureCard;
    //private ProgressBar mWindProgressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
        refreshData();
    }

    public void setHistoryMeteoData(List<MeteoStationData> meteoData) {
        this.meteoDataList = meteoData;
        refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_chart, container, false);

        mWindCard = new ChartCardItem(1,this);
        mWindCard.card = (ChartCard) v.findViewById(R.id.windcard);
        mWindCard.card.init();
        mWindChart = mWindCard.getChart();
        //mWindProgressBar = mWindCard.getProgressBar();

        mTemperatureCard = new ChartCardItem(2,this);
        mTemperatureCard.card = (ChartCard) v.findViewById(R.id.temperaturecard);
        mTemperatureCard.card.init();
        mTemperatureChart = mTemperatureCard.getChart();
        //mWindProgressBar = mWindCard.getProgressBar();

        mTrendCard = new ChartCardItem(3,this);
        mTrendCard.card = (ChartCard) v.findViewById(R.id.trendcard);
        mTrendCard.card.init();
        mTrendChart = mTrendCard.getChart();
        //mWindProgressBar = mWindCard.getProgressBar();

        hc = new HistoryChart(getActivity(), mWindChart, mTrendChart, mTemperatureChart);
        if (meteoDataList != null) {

            refreshData();
            //hc.drawChart(meteoDataList);
        }


        mWindChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                startFullChartActivity();
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
            }
        });

        return v;
    }

    public void refreshData() {

        if (hc != null) {
            mWindCard.hideProgressBar();
            mTemperatureCard.hideProgressBar();
            mTrendCard.hideProgressBar();

            hc.drawChart(meteoDataList);
        }
    }

    private void startFullChartActivity() {

        Intent resultIntent = new Intent(getActivity(), FullChartActivity.class);
        int request = 0;
        startActivityForResult(resultIntent, request);
    }

    @Override
    public void chartCardSelected(long index) {
    }
}
