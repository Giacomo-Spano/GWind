package wind.newwindalarm.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import wind.newwindalarm.AsyncRequestMeteoDataResponse;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.cardui.ChartCard;
import wind.newwindalarm.cardui.WebcamCard;
import wind.newwindalarm.controls.TouchImageView;
import wind.newwindalarm.requestMeteoDataTask;

public class SpotDetailsWebcamFragment extends Fragment {

    private WebcamCard mWebcamCard1;
    private ImageView mImageView1;
    private WebcamCard mWebcamCard2;
    private ImageView mImageView2;
    private WebcamCard mWebcamCard3;
    private ImageView mImageView3;

    private MeteoStationData meteoData;
    private long spotID;

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
        refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_webcam, container, false);

        mWebcamCard1 = (WebcamCard) v.findViewById(R.id.webcamcard1);
        mWebcamCard1.init();
        mImageView1 = mWebcamCard1.getImageView();

        mWebcamCard2 = (WebcamCard) v.findViewById(R.id.webcamcard2);
        mWebcamCard2.init();
        mImageView2 = mWebcamCard2.getImageView();

        mWebcamCard3 = (WebcamCard) v.findViewById(R.id.webcamcard3);
        mWebcamCard3.init();
        mImageView3 = mWebcamCard3.getImageView();

        refreshData();

        return v;
    }

    public void refreshData() {

        if (meteoData != null && mWebcamCard1 != null && mWebcamCard2 != null && mWebcamCard2 != null ) {
            //if (mWebcamImageView != null && meteoData.webcamurl != null)
            //    new DownloadImageTask(mWebcamImageView).execute(meteoData.webcamurl);

            if (mImageView1 != null && meteoData.webcamurl != null) {
                new DownloadImageTask(mImageView1).execute(meteoData.webcamurl);
                mWebcamCard1.setVisibility(View.VISIBLE);
            } else {
                mWebcamCard1.setVisibility(View.GONE);
            }

            if (mImageView2 != null && meteoData.webcamurl2 != null) {
                new DownloadImageTask(mImageView2).execute(meteoData.webcamurl2);
                mWebcamCard2.setVisibility(View.VISIBLE);
            } else {
                mWebcamCard2.setVisibility(View.GONE);
            }

            if (mImageView3 != null && meteoData.webcamurl3 != null) {
                new DownloadImageTask(mImageView3).execute(meteoData.webcamurl3);
                mWebcamCard3.setVisibility(View.VISIBLE);
            } else {
                mWebcamCard3.setVisibility(View.GONE);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result == null)
                return;
            int bmWidth = result.getWidth();
            int bmHeight = result.getHeight();

            View parent = (View) /*mWebcamImageView*/mWebcamCard1.getParent();
            int ivWidth = parent.getWidth();
            //int ivWidth = bmImage.getWidth();
            int new_width = ivWidth;

            if (ivWidth > 0) {
                int new_height = (int) Math.floor((double) bmHeight * ((double) new_width / (double) bmWidth));
                Bitmap newbitMap = Bitmap.createScaledBitmap(result, new_width, new_height, true);
                bmImage.setImageBitmap(newbitMap);
            }
        }

    }
}
