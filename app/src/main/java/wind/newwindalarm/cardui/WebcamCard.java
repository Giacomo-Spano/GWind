package wind.newwindalarm.cardui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
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
    private ProgressBar progressBar;

    public WebcamCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        imageView = (TouchImageView) findViewById(R.id.imageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void setTitle(String title) {

        if (title == null) return;
            titleTextView.setText(title);
    }

    public void setImage(Bitmap bmp) {
        imageView.setImageBitmap(bmp);
    }
}
