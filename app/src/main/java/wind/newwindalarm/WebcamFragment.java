package wind.newwindalarm;


import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class WebcamFragment extends Fragment {

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

        View v;
        v = inflater.inflate(R.layout.fragment_webcam, container, false);
        mWebcamImageView = (ImageView) v.findViewById(R.id.imageView7);

        //spotID = getArguments().getLong("spotID");
        refreshData();
        return v;
    }

    private void refreshData() {

        new DownloadImageTask(mWebcamImageView).execute(meteoData.webcamurl);
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

            View parent = (View) mWebcamImageView.getParent();
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
