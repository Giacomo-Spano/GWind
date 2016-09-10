package wind.newwindalarm.cardui;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.controls.TouchImageView;


public class WebcamCardItem {

    public WebcamCard card;
    WebcamCardListener listener;

    public interface WebcamCardListener {
        public void cardSelected();
    }

    public WebcamCardItem(WebcamCardListener ml, final Activity activity, LinearLayout container) {

        listener = ml;
        card = (WebcamCard) activity.getLayoutInflater().inflate(R.layout.card_webcam, container, false);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.cardSelected();
            }
        });

        card.init();
    }

    public WebcamCardItem(WebcamCardListener ml) {

        listener = ml;
        //card.init();
    }

    public ImageView getImageView() {
        return card.getImageView();
    }

    public ProgressBar getProgressBar() {
        return card.getProgressBar();
    }

    public void setTitle(String title) {
        card.setTitle(title);
    }

    public void update(MeteoStationData data) {

   }
}