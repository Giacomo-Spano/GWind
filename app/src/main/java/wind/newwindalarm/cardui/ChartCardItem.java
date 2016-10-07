package wind.newwindalarm.cardui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;

import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.R;


public class ChartCardItem {

    public ChartCard card;
    ChartCardListener listener;
    private int id;

    public ChartCardItem(ChartCardListener ml, final Activity activity, LinearLayout container) {

        listener = ml;
        card = (ChartCard) activity.getLayoutInflater().inflate(R.layout.card_chart, container, false);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.chartCardSelected(id);
            }
        });

        card.init();
    }

    public ChartCardItem(int id, ChartCardListener ml) {

        this.id = id;
        listener = ml;
        //card.init();
    }


    public LineChart getChart() {
        return card.getChart();
    }

    public ProgressBar getProgressBar() {
        return card.getProgressBar();
    }

    public void hideProgressBar() {

        card.hideProgressBar();
    }

    public void setTitle(String title) {
        card.setTitle(title);
    }

    public void update(MeteoStationData data) {
        card.invalidate();
   }
}