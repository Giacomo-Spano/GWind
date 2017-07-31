package gwind.windalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import gwind.windalarm.R;

/**
 * Created by giacomo on 28/02/14.
 */
public class ChartCard extends LinearLayout {

    private TextView mTitle;
    private LineChart chart;
    private ProgressBar progressBar;

    public ChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        chart = (LineChart) findViewById(R.id.chart);
        //chart.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public LineChart getChart() {
        return chart;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setTitle(String name) {

        if (name == null) return;
        mTitle.setText(name);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        //chart.setVisibility(View.VISIBLE);
    }
}
