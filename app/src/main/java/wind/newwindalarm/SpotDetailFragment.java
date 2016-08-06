package wind.newwindalarm;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SpotDetailFragment extends Fragment {

    private LineChart mLineChart;
    private long spotID;
    private ImageView mWebcamImageView;
    private MeteoStationData meteoData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem mm = menu.findItem(R.id.options_refresh);
        if (mm != null) {

            menu.findItem(R.id.options_refresh).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_refresh:
                // Do Fragment menu item stuff here
                refreshData();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        spotID = getArguments().getLong("spotID");
        String str = getArguments().getString("meteodata");
        try {
            JSONObject json = new JSONObject(str);
            meteoData = new MeteoStationData(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);

        mWebcamImageView = (ImageView) v.findViewById(R.id.imageView7);

        // Updating the action bar title
        String txt = MainActivity.getSpotName(spotID);// + */""+spotID;
        if (txt != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(txt);
            // in this example, a LineChart is initialized from xml
            mLineChart = (LineChart) v.findViewById(R.id.chart);

            refreshData();
        }
        return v;
    }

    private void refreshData() {

        new DownloadImageTask(mWebcamImageView).execute(meteoData.webcamurl);

        getHistoryData(spotID);


        //getLastData(spotID);

    }

    public void onBackPressed() {
        // super.onBackPressed();
        // myFragment.onBackPressed();
    }

    public void getHistoryData(final long spot) {

        HistoryChart hc = new HistoryChart(getActivity(),mLineChart);

        new requestMeteoDataTask(getActivity(),hc,requestMeteoDataTask.REQUEST_HISTORYMETEODATA).execute("" + spot);

    }


    public void getLastData(long spot) {


        new requestMeteoDataTask(getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {

                if (error) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    alertDialogBuilder.setTitle("Errore");
                    alertDialogBuilder
                            .setMessage(errorMessage)
                            .setCancelable(false);
                    alertDialogBuilder
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog;
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {

                    //mTitleTextView.setText(MainActivity.getSpotName(spotID));
                    MeteoStationData md = null;
                    for (int i = 0; i < list.size(); i++) {

                        md = (MeteoStationData) list.get(i);
                        if (md.spotID == spotID)
                            break;
                    }
                    if (md == null)
                        return;


                    DownloadImageTask downloadImageTask = (DownloadImageTask) new DownloadImageTask(mWebcamImageView)
                            .execute(md.webcamurl);


                }

            }

            @Override
            public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

            }
        },requestMeteoDataTask.REQUEST_LASTMETEODATA).execute("" + spot);
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

            View parent = (View)mWebcamImageView.getParent();
            int ivWidth = parent.getWidth();
            //int ivWidth = bmImage.getWidth();
            int new_width = ivWidth;

            if (ivWidth > 0 ) {
                int new_height = (int) Math.floor((double) bmHeight * ((double) new_width / (double) bmWidth));
                Bitmap newbitMap = Bitmap.createScaledBitmap(result, new_width, new_height, true);
                bmImage.setImageBitmap(newbitMap);
            }
        }
    }
}