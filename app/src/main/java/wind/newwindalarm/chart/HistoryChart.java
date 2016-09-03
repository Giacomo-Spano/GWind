package wind.newwindalarm.chart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wind.newwindalarm.AsyncRequestMeteoDataResponse;
import wind.newwindalarm.MeteoStationData;

/**
 * Created by Giacomo Spanò on 26/06/2016.
 */
public class HistoryChart {

    Context mContext;
    private LineChart mWindChart;
    private LineChart mTrendChart;
    private LineChart mTemperatureChart;

    LineData windData;
    LineData trendData;
    LineData temperatureData;

    public HistoryChart(Context context, LineChart windChart, LineChart trendChart, LineChart temperatureChart) {
        mContext = context;
        mWindChart = windChart;
        mTrendChart = trendChart;
        mTemperatureChart = temperatureChart;
    }

    public LineData getWindLineData() {
        return windData;
    }

    public void drawChart(List<MeteoStationData> meteoDataList) {

        if (meteoDataList == null)
            return;
        List<Entry> valsCompSpeed = new ArrayList<Entry>();
        List<Entry> valsCompAvSpeed = new ArrayList<Entry>();
        List<Entry> valsCompDirection = new ArrayList<Entry>();
        List<Entry> valsCompTrend = new ArrayList<Entry>();
        List<Entry> valsCompTemperature = new ArrayList<Entry>();

        String lastTime = "";
        float x = 0f;
        for (int i = 0; i < meteoDataList.size(); i++) {

            MeteoStationData md = (MeteoStationData) meteoDataList.get(i);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = null;
                if (md.date == null)
                    continue;
                date = sdf.parse(md.date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());

                if (!md.date.equals(lastTime)) {

                    x = date.getTime();
                    if (md.speed != null) {
                        Entry speedEntry = new Entry(x,Float.valueOf(String.valueOf(md.speed))); // 0 == quarter 1
                        valsCompSpeed.add(speedEntry);
                    }

                    if (md.averagespeed != null) {
                        Entry avspeedEntry = new Entry(x,Float.valueOf(String.valueOf(md.averagespeed))); // 0 == quarter 1
                        valsCompAvSpeed.add(avspeedEntry);
                    }

                    if (md.directionangle != null) {
                        Entry direction = new Entry(x,Float.valueOf(String.valueOf(md.directionangle)));
                        valsCompDirection.add(direction);
                    }

                    if (md.trend != null) {
                        Entry trend = new Entry(x,Float.valueOf(String.valueOf(md.trend)));
                        valsCompTrend.add(trend);
                    }

                    if (md.temperature != null) {
                        Entry temperature = new Entry(x,Float.valueOf(String.valueOf(md.temperature)));
                        valsCompTemperature.add(temperature);
                    }
                    lastTime = md.date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        LineDataSet setCompSpeed = new LineDataSet(valsCompSpeed, "Velocità");
        setCompSpeed.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompSpeed.setColor(Color.RED);
        setCompSpeed.setDrawCircles(true);
        setCompSpeed.setCircleColor(Color.RED);

        LineDataSet setCompAvSpeed = new LineDataSet(valsCompAvSpeed, "Velocità media");
        setCompAvSpeed.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompAvSpeed.setColor(Color.BLUE);
        setCompAvSpeed.setDrawCircles(true);
        setCompAvSpeed.setCircleColor(Color.BLUE);

        LineDataSet setCompDirection = new LineDataSet(valsCompDirection, "Direzione");
        setCompDirection.setAxisDependency(YAxis.AxisDependency.RIGHT);
        setCompDirection.setColor(Color.BLACK);
        setCompDirection.setDrawCircles(true);
        setCompDirection.setCircleColor(Color.BLACK);

        LineDataSet setCompTrend = new LineDataSet(valsCompTrend, "Trend");
        setCompTrend.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompTrend.setColor(Color.GREEN);
        setCompTrend.setDrawCircles(true);
        setCompTrend.setCircleColor(Color.GREEN);

        LineDataSet setCompTemperature = new LineDataSet(valsCompTemperature, "Temperatura");
        setCompTemperature.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompTemperature.setColor(Color.CYAN);
        setCompTemperature.setDrawCircles(true);
        setCompTemperature.setCircleColor(Color.CYAN);

        // use the interface ILineDataSet
        List<ILineDataSet> windDataSets = new ArrayList<ILineDataSet>();
        List<ILineDataSet> trendDataSets = new ArrayList<ILineDataSet>();
        List<ILineDataSet> temperatureDataSets = new ArrayList<ILineDataSet>();

        windDataSets.add(setCompSpeed);
        windDataSets.add(setCompAvSpeed);
        windDataSets.add(setCompDirection);
        trendDataSets.add(setCompTrend);
        temperatureDataSets.add(setCompTemperature);

        setCompDirection.setValueFormatter(new DirectionValueFormatter());


        mWindChart.setDescription("");
        //mWindChart.setDescriptionPosition(0,);

        XAxis xAxis = mWindChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(60000L * 5f); // one minute in millis
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long) value));
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        YAxis yLeftAxis = mWindChart.getAxisLeft();
        yLeftAxis.setDrawAxisLine(true);
        yLeftAxis.setDrawGridLines(true);
        yLeftAxis.setAxisMinValue(0f); // start at zero
        yLeftAxis.setAxisMaxValue(35f); // the axis maximum is 100
        yLeftAxis.setLabelCount(10);
        //yLeftAxis.setGranularity(22.5f); // one minute in millis
        //yLeftAxis.setInverted(true);


        YAxis yRightAxis = mWindChart.getAxisRight();
        yRightAxis.setDrawAxisLine(true);
        yRightAxis.setDrawGridLines(true);
        yRightAxis.setAxisMinValue(0f); // start at zero
        yRightAxis.setAxisMaxValue(337.50f); // the axis maximum is 100
        yRightAxis.setLabelCount(16);
        yRightAxis.setGranularity(22.5f); // one minute in millis
        yRightAxis.setInverted(true);
        yRightAxis.setGridColor(Color.GREEN);
        yRightAxis.setValueFormatter(new DirectionAxisValueFormatter());

        windData = new LineData(windDataSets);
        mWindChart.setData(windData);
        trendData = new LineData(trendDataSets);
        mTrendChart.setData(trendData);
        temperatureData = new LineData(temperatureDataSets);
        mTemperatureChart.setData(temperatureData);


        //mWindChart.getLegend().setEnabled(true);
        Legend legend = mWindChart.getLegend();
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        //l.setTypeface(...);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis
        legend.setEnabled(true);


        mWindChart.invalidate(); // refresh
        mTemperatureChart.invalidate(); // refresh
        mTrendChart.invalidate(); // refresh
    }

    private final String[] directionSymbols = {
            "E", "E-NE", "NE", "NE-N",
            "N", "N-NO", "NO", "NO-O",
            "O", "O-SO", "SO", "SO-S",
            "S", "S-SE", "SE", "SE-E"};

    private class DirectionAxisValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value > 22.5 * 15 || value < 0) return "" + value + "N/A";
            return ""+/*value+ " " +*/ directionSymbols[(int) (value / 22.5)];
        }

        @Override
        public int getDecimalDigits() {
            return 1;
        }
    }
    private class DirectionValueFormatter implements ValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value > 22.5 * 15 || value < 0) return "" + value + "N/A";
            return ""+/*value+ " " +*/ directionSymbols[(int) (value / 22.5)];
        }
    }

}
