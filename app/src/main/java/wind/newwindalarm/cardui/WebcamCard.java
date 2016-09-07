package wind.newwindalarm.cardui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import wind.newwindalarm.R;
import wind.newwindalarm.controls.TouchImageView;

/**
 * Created by giacomo on 28/02/14.
 */
public class WebcamCard extends LinearLayout {

    private TextView titleTextView;
    private TouchImageView imageView;

    public WebcamCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        imageView = (TouchImageView) findViewById(R.id.imageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setTitle(String title) {

        if (title == null) return;
            titleTextView.setText(title);
    }
}
