package wind.newwindalarm.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.cardui.WebcamCard;
import wind.newwindalarm.cardui.WebcamCardItem;

public class SpotDetailsWebcamFragment extends Fragment implements WebcamCardItem.WebcamCardListener {

    private List<WebcamCardItem> webcamCards = new ArrayList<>();
    //private List<Bitmap> webcamBitmaps = new ArrayList<>();
    private MeteoStationData meteoData;
    private long spotID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public SpotDetailsWebcamFragment() {
        for(int i = 0; i < 3; i++) {
            WebcamCardItem wci = new WebcamCardItem(this);
            webcamCards.add(wci);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
        //refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_webcam, container, false);

        int[] ids = {R.id.webcamcard1,R.id.webcamcard2,R.id.webcamcard3};
        for (int i = 0; i < ids.length; i++) {

            webcamCards.get(i).card = (WebcamCard) v.findViewById(ids[i]);
            webcamCards.get(i).card.init();
            webcamCards.get(i).card.setVisibility(View.GONE);
        }

        refreshData();

        return v;
    }

    public void refreshData() {

        if (meteoData == null)
            return;

        for (WebcamCardItem wci : webcamCards) {
            wci.update();
        }



    }

    @Override
    public void cardSelected() {

    }

    public void setWebCamImage(int n, Bitmap bmp) {

        if (n < 1 || n > webcamCards.size())
            return;

        webcamCards.get(n-1).setWebCamImage(bmp);
        //webcamBitmaps[n] = bmp;
        //wc.card.setVisibility(View.VISIBLE);
        //    wc.hideProgressBar();


    }

    public void showWebCamProgressBar(int n) {

        if (n < 1 || n > webcamCards.size())
            return;

        //webcamCards.get(n-1).card.setVisibility(View.VISIBLE);
    }
}
