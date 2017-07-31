package gwind.windalarm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;

/**
 * Created by Giacomo Spanò on 20/09/2016.
 */
public class DownloadImageTask extends AsyncTask<Object, Void, Bitmap> {
    private int index;
    private long windId;
    private AsyncDownloadImageResponse delegate = null; //Call back interface
    private int width = 0;

    public interface AsyncDownloadImageResponse {
        void processFinishDownloadImage(int index, Bitmap bmp, long windId);
    }

    public DownloadImageTask(AsyncDownloadImageResponse asyncResponse, int index, long windId, int width) {

        delegate = asyncResponse;
        this.index = index;
        this.windId = windId;
        this.width = width;
    }

    protected Bitmap doInBackground(Object... params) {
        String urldisplay = (String) params[0];

        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {

        if (result == null) {
            delegate.processFinishDownloadImage(index,null,-1);
            return;
        }
        int bmWidth = result.getWidth();
        int bmHeight = result.getHeight();

        //View parent = (View) mWebcamCard1.card.getParent();
        int ivWidth = width;//parent.getWidth();
        //int ivWidth = bmImage.getWidth();
        int new_width = ivWidth;

        Bitmap newbitMap = null;
        if (ivWidth > 0) {
            int new_height = (int) Math.floor((double) bmHeight * ((double) new_width / (double) bmWidth));
            newbitMap = Bitmap.createScaledBitmap(result, new_width, new_height, true);
        }
        delegate.processFinishDownloadImage(index,newbitMap,windId);

    }

}