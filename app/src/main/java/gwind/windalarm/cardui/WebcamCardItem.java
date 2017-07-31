package gwind.windalarm.cardui;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.Serializable;


public class WebcamCardItem implements Serializable {

    public WebcamCard card;
    WebcamCardListener listener;
    private Bitmap image;
    private long lastWebcamImageWindId;

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

    public void setWebCamImage(Bitmap bmp, long lastWebcamImageWindId) {

        image = bmp;
        this.lastWebcamImageWindId = lastWebcamImageWindId;
    }

    public Bitmap getWebCamImage() {
        return image;
    }

    public long getlastWebcamImageWindId() {
        return lastWebcamImageWindId;
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

            card.setImage(image);
            card.hideProgressBar();
            card.setVisibility(View.VISIBLE);
        }

   }
}