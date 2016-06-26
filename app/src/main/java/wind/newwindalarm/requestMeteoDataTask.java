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


    public static int REQUEST_LASTMETEODATA = 1;
    public static final int REQUEST_HISTORYMETEODATA = 2;
    public static final int REQUEST_SPOTLIST = 3;

    static public String Spot_All = "all";

    public AsyncRequestMeteoDataResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    private String mSpot;
    int requestType;

    public requestMeteoDataTask(Activity activity, AsyncRequestMeteoDataResponse asyncResponse) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }

    protected List<Object> doInBackground(Object... params) {

        URL url;
        List<Object> list = new ArrayList<Object>();
        requestType = (int) params[0];

        try {
            String path = "/meteo?";

            if (requestType == REQUEST_LASTMETEODATA) {
                mSpot = (String) params[1]; //lista di spot separata da virgola

                path += "lastdata=true";
                path += "&history=false";
                path += "&requestspotlist=false";
                path += "&spot="+mSpot;

            } else if (requestType == REQUEST_HISTORYMETEODATA) {
                mSpot = (String) params[1]; //lista di spot separata da virgola

                path += "lastdata=false";
                path += "&history=true";
                path += "&requestspotlist=false";
                path += "&spot="+mSpot;

            } else if (requestType == REQUEST_SPOTLIST) {
                path += "lastdata=false";
                path += "&history=false";
                path += "&requestspotlist=true";
                path += "&spot="+Spot_All;

            }

            String serverUrl = AlarmPreferences.getServerUrl(activity);
            url = new URL(serverUrl + path);

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


                if (requestType == REQUEST_SPOTLIST) {
                    JSONObject jObject = new JSONObject(json);
                    JSONArray jArray = jObject.getJSONArray("spotlist");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject2 = jArray.getJSONObject(i);
                        Spot spt = new Spot(jObject2);
                        list.add(spt);
                    }
                } else if (requestType == REQUEST_LASTMETEODATA || requestType == REQUEST_HISTORYMETEODATA) {
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

        if (requestType == REQUEST_SPOTLIST)
            delegate.processFinishSpotList(list, false, errorMessage);
        else if (requestType == REQUEST_HISTORYMETEODATA)
            delegate.processFinishHistory(list, error, errorMessage);
        else if (requestType == REQUEST_LASTMETEODATA)
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

