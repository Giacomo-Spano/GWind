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

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.cardui.WebcamCard;
import wind.newwindalarm.cardui.WebcamCardItem;

public class SpotDetailsWebcamFragment extends Fragment implements SpotDetailsFragmentInterface, WebcamCardItem.WebcamCardListener {

    private MeteoStationData meteoData;
    private long spotId;
    private WebcamCardItemList webcamCards;
    private int[] ids = {R.id.webcamcard1, R.id.webcamcard2, R.id.webcamcard3};

    private class WebcamCardItemList {
        public List<WebcamCardItem> list = new ArrayList<WebcamCardItem>();
    }

    public SpotDetailsWebcamFragment() {

        webcamCards = new WebcamCardItemList();

        for (int i = 0; i < ids.length; i++) {
            WebcamCardItem wci = new WebcamCardItem(this);
            webcamCards.list.add(wci);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        if (savedInstanceState != null) {
            meteoData = (MeteoStationData) savedInstanceState.getSerializable("meteoData");
            spotId = savedInstanceState.getLong("spotId");

            int count = 1;
            for (WebcamCardItem wci : webcamCards.list) {

                Bitmap bitmap = savedInstanceState.getParcelable("bitmap"+count);
                long lastWebCamWindId = savedInstanceState.getLong("lastWebcamWindId"+count);
                wci.setWebCamImage(bitmap,lastWebCamWindId);
                count++;
            }
        }

        View v;
        v = inflater.inflate(R.layout.fragment_webcam, container, false);
        int count = 0;
        for (WebcamCardItem wci : webcamCards.list) {

            wci.card = (WebcamCard) v.findViewById(ids[count]);
            wci.card.init();
            if (meteoData != null && meteoData.webcamurlList != null &&
                    count < (meteoData.webcamurlList.size()) &&
                    meteoData.webcamurlList.get(count) != null) {
                wci.card.setVisibility(View.VISIBLE);
            } else {
                wci.card.setVisibility(View.GONE);
            }
            count++;
        }
        refreshData();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("meteoData", meteoData);
        outState.putLong("spotId", spotId);

        int count = 1;
        for (WebcamCardItem wci : webcamCards.list) {
            Bitmap bitmap = wci.getWebCamImage();
            outState.putParcelable("bitmap"+count, bitmap);
            long lastWebCamWindId = wci.getlastWebcamImageWindId();
            outState.putLong("lastWebcamWindId"+count, lastWebCamWindId);
            count++;
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
    }

    public void setSpotId(long id) {
        spotId = id;
    }

    public void refreshData() {

        if (meteoData == null || webcamCards == null)
            return;

        for (WebcamCardItem wci : webcamCards.list) {
            wci.update();
        }
    }

    @Override
    public void cardSelected() {
    }

    public void setWebCamImage(int n, Bitmap bmp, long lastWebcamImageWindId) {

        if (n < 1 || n > webcamCards.list.size())
            return;

        webcamCards.list.get(n - 1).setWebCamImage(bmp,lastWebcamImageWindId);
    }

    public void showWebCamProgressBar(int n) {

        if (webcamCards == null)
            return;

        if (n < 1 || n > webcamCards.list.size())
            return;
    }
}
