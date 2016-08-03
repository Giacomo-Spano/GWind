package wind.newwindalarm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.Time;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Giacomo Spanò on 26/06/2016.
 */
public class HistoryChart implements AsyncRequestMeteoDataResponse {

    Context mContext;
    private LineChart mLineChart;

    public HistoryChart(Context context, LineChart lineChart) {
        mContext = context;
        mLineChart = lineChart;
    }

    @Override
    public void processFinish(List<Object> list, boolean error, String errorMessage) {
    }

    @Override
    public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {
        if (error) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

            alertDialogBuilder.setTitle("Errore");
            alertDialogBuilder
                    .setMessage(errorMessage)
                    .setCancelable(false);
            alertDialogBuilder
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else {

            //mTitleTextView.setText(MainActivity.getSpotName(spot));

            ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
            ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
            ArrayList<String> xVals = new ArrayList<String>();

            String lastTime = "";
            int index = 0;
            for (int i = 0; i < list.size(); i++) {

                MeteoStationData md = (MeteoStationData) list.get(i);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date = null;
                    date = sdf.parse(md.date/* + " " + md.time*/);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(date.getTime());
                    Time time = new Time();
                    //time.set( int second, int minute, int hourOfDay, int monthDay, int month, int year);

                    if (!md.date.equals(lastTime)) {
                        Entry speedEntry = new Entry(Float.valueOf(String.valueOf(md.speed)), index); // 0 == quarter 1
                        Entry avspeedEntry = new Entry(Float.valueOf(String.valueOf(md.averagespeed)), index); // 0 == quarter 1
                        valsComp1.add(speedEntry);
                        valsComp2.add(avspeedEntry);
                        xVals.add(md.date);
                        lastTime = md.date;
                        index++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
            LineDataSet setComp1 = new LineDataSet(valsComp1, "Speed");
            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setColor(Color.RED);
            setComp1.setDrawCircles(true);
            setComp1.setCircleColor(Color.RED);

            LineDataSet setComp2 = new LineDataSet(valsComp2, "Average Speed");
            setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp2.setColor(Color.BLUE);
            setComp2.setDrawCircles(true);
            setComp2.setCircleColor(Color.BLUE);


            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(setComp1);
            dataSets.add(setComp2);


            mLineChart.setDescription("Km/h");

            LineData data = new LineData(xVals, dataSets);
            mLineChart.setData(data);

            mLineChart.setVisibleXRange(30); // allow 20 values to be displayed at once on the x-axis, not more
            mLineChart.moveViewToX(xVals.size()-30); // set the left edge of the chart to x-index 10

            //mLineChart.invalidate(); // refresh
        }
    }

    @Override
    public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

    }
}
