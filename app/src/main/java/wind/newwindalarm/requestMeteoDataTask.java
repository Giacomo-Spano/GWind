package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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



    public static final int REQUEST_LASTMETEODATA = 1;
    public static final int REQUEST_HISTORYMETEODATA = 2;
    public static final int REQUEST_SPOTLIST = 3;
    public static final int REQUEST_SPOTLIST_FULLINFO = 4;

    static public String Spot_All = "all";

    public AsyncRequestMeteoDataResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    private String mSpot;
    int requestType;
    ProgressBar progressBar;
    int contentSize;

    public requestMeteoDataTask(Activity activity, AsyncRequestMeteoDataResponse asyncResponse, int type) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
        requestType = type;

        progressBar = ((MainActivity)activity).getProgressBar();
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected List<Object> doInBackground(Object... params) {

        URL url;
        List<Object> list = new ArrayList<Object>();


        try {
            String path = "/meteo?";

            if (requestType == REQUEST_LASTMETEODATA) {
                mSpot = (String) params[0]; //lista di spot separata da virgola

                path += "lastdata=true";
                path += "&history=false";
                path += "&requestspotlist=false";
                path += "&fullinfo=false";
                path += "&spot="+mSpot;

            } else if (requestType == REQUEST_HISTORYMETEODATA) {
                mSpot = (String) params[0]; //lista di spot separata da virgola

                path += "lastdata=false";
                path += "&history=true";
                path += "&requestspotlist=false";
                path += "&fullinfo=false";
                path += "&spot="+mSpot;

            } else if (requestType == REQUEST_SPOTLIST) {
                path += "lastdata=false";
                path += "&history=false";
                path += "&requestspotlist=true";
                path += "&fullinfo=false";
                path += "&spot="+Spot_All;

            } else if (requestType == REQUEST_SPOTLIST_FULLINFO) {
                path += "lastdata=false";
                path += "&history=false";
                path += "&requestspotlist=true";
                path += "&fullinfo=true";
                path += "&spot="+Spot_All;

            }

            String serverUrl = AlarmPreferences.getServerUrl(activity);
            url = new URL(serverUrl + path);

            Log.d("url=", url.toString());



            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("user",MainActivity.authCode);
            conn.setConnectTimeout(5000); //set timeout to 5 seconds
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");



            try {
                BufferedInputStream in = new BufferedInputStream(
                        conn.getInputStream());
                // Convert the stream to a String
                // There are various approaches, I'll leave it up to you
                contentSize = Integer.valueOf(conn.getHeaderField("Length"));//conn.getContentLength();
                String json = convertStreamToString(in);

                if (requestType == REQUEST_SPOTLIST || requestType == REQUEST_SPOTLIST_FULLINFO) {
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
        } catch (java.net.SocketTimeoutException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
        }

        /*if (dialog.isShowing()) {
            dialog.dismiss();
        }*/
        return list;
    }

    protected void onPreExecute() {

        //progressBar.setProgress(10);

        String message = "attendere prego...";
        if (requestType == REQUEST_SPOTLIST)
            message = "Richiesta lista spot...";
        else if (requestType == REQUEST_SPOTLIST_FULLINFO)
            message = "Richiesta lista completa spot...";
        else if (requestType == REQUEST_HISTORYMETEODATA)
            message = "Richiesta dati storici...";
        else if (requestType == REQUEST_LASTMETEODATA)
            message = "Richiesta dati meteo...";

        //this.dialog.setMessage(message);
        //this.dialog.show();

    }


    @Override
    protected void onProgressUpdate(Long... values) {
        progressBar.setProgress(values[0].intValue());
    }

    protected void onPostExecute(List<Object> list) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (requestType == REQUEST_SPOTLIST || requestType == REQUEST_SPOTLIST_FULLINFO)
            delegate.processFinishSpotList(list, error, errorMessage);
        else if (requestType == REQUEST_HISTORYMETEODATA)
            delegate.processFinishHistory(list, error, errorMessage);
        else if (requestType == REQUEST_LASTMETEODATA)
            delegate.processFinish(list, error, errorMessage);

        progressBar.setVisibility(View.GONE);
    }
    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

                long l = 100 * sb.length() / contentSize;

                publishProgress(l);
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

