package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giacomo on 01/07/2015.
 */
public class requestMeteoDataTask extends
        AsyncTask<Object, Long, List<Object/*MeteoStationData*/>> {

    private boolean requestSpotList;
    private boolean requestLastdata;
    private boolean requestHistory;
    //private long spot;
    public AsyncRequestMeteoDataResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    private String mSpot;
    private String mServerURL;

    public requestMeteoDataTask(Activity activity, AsyncRequestMeteoDataResponse asyncResponse) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    protected List<Object> doInBackground(Object... params) {

        URL url;
        List<Object> list = new ArrayList<Object>();
        requestSpotList = (boolean) params[0];
        requestLastdata = (boolean) params[1];
        requestHistory = (boolean) params[2];
        mSpot = (String) params[3];
        mServerURL = (String) params[4];

        try {
            //String regId = MainActivity.preferences.getRegId();

            String path = "/meteo?";


            if (requestSpotList) {
                path += "requestspotlist=true";
            } else {
                path += "requestspotlist=false";
            }
            if (requestHistory) {
                path += "&history=true";
            } else {
                path += "&history=false";
            }
            if (requestLastdata) {
                path += "&lastdata=true";
            } else {
                path += "&lastdata=false";
            }

            //if (mSpot.equals(MeteoStationData.Spot_All)) {
            path += "&spot="+mSpot;
            //}
            url = new URL(mServerURL + path);

            Log.d("url=", url.toString());
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            try {
                BufferedInputStream in = new BufferedInputStream(
                        conn.getInputStream());
                // Convert the stream to a String
                // There are various approaches, I'll leave it up to you
                String json = convertStreamToString(in);


                if (requestSpotList) {
                    JSONObject jObject = new JSONObject(json);
                    JSONArray jArray = jObject.getJSONArray("spotlist");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject2 = jArray.getJSONObject(i);
                        Spot spt = new Spot(jObject2);
                        list.add(spt);
                    }
                } else {
                    JSONObject jObject = new JSONObject(json);
                    JSONArray jArray = jObject.getJSONArray("meteodata");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject2 = jArray.getJSONObject(i);
                        MeteoStationData md = new MeteoStationData(jObject2);
                        list.add(md);
                    }
                }

                if (conn != null)
                    conn.disconnect();


            } catch (JSONException e) {
                error = true;
                e.printStackTrace();
                errorMessage = e.toString();
            }
        } catch (MalformedURLException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        return list;
    }

    protected void onPreExecute() {

        //android.os.Debug.waitForDebugger();

        //String[] planets = getMaiy().getResources().getStringArray(R.string.dialog_readingmeteo);
        this.dialog.setMessage("Lettura dati..."/*getString(R.string.dialog_readingmeteo)*/);
        this.dialog.show();
    }

    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(List<Object> list) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (requestSpotList)
            delegate.processFinishSpotList(list, false, errorMessage);
        else if (requestHistory)
            delegate.processFinishHistory(list, error, errorMessage);
        else
            delegate.processFinish(list, error, errorMessage);
    }
    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

