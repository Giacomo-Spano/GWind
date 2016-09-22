package wind.newwindalarm.cardui;

import android.app.Activity;
import android.graphics.Bitmap;
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
    private Bitmap image;

    public void show() {

    }

    public void hide() {

    }

    public interface WebcamCardListener {
        public void cardSelected();
    }

    public WebcamCardItem(WebcamCardListener ml) {

        listener = ml;
    }

    public ImageView getImageView() {
        return card.getImageView();
    }

    public void setWebCamImage(Bitmap bmp) {
        image = bmp;
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

    public void update() {
        if (image != null && card != null) {
            card.setVisibility(View.VISIBLE);
            card.setImage(image);
            card.hideProgressBar();
        }

   }
}