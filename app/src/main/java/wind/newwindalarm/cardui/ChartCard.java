package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import wind.newwindalarm.R;
import wind.newwindalarm.controls.WindControl;

/**
 * Created by giacomo on 28/02/14.
 */
public class ChartCard extends LinearLayout {

    private TextView mTitle;
    private LineChart chart;
    private ProgressBar progress;

    public ChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        chart = (LineChart) findViewById(R.id.chart);
        progress = (ProgressBar) findViewById(R.id.progressBar);
    }

    public LineChart getChart() {
        return chart;
    }

    public ProgressBar getProgressBar() {
        return progress;
    }

    public void setTitle(String name) {

        if (name == null) return;
        mTitle.setText(name);
    }
}
