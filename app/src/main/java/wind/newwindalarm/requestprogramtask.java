package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
public class requestprogramtask extends
        AsyncTask<String, Integer, List<WindAlarmProgram>> {

    public AsyncRequestProgramResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    private String mServerURL;
    private long deviceId;
    private long spotId;

    public requestprogramtask(Activity activity, AsyncRequestProgramResponse asyncResponse, long deviceId, long spotId) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
        this.deviceId = deviceId;
        this.spotId = spotId;
    }

    protected List<WindAlarmProgram> doInBackground(String... params) {

        URL url;
        List<WindAlarmProgram> list = new ArrayList<WindAlarmProgram>();
        WindAlarmProgram wp = null;
        try {
            mServerURL = AlarmPreferences.getServerUrl(activity);

            String strpath = "/alarm?deviceId="+deviceId;
            if (spotId != -1)
                strpath += "&spotId="+spotId;
            url = new URL(mServerURL + strpath);

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

                JSONObject jObject = new JSONObject(json);

                JSONArray jArray = jObject.getJSONArray("alarms");

                for (int i=0; i< jArray.length();i++) {
                    JSONObject jObject2 = jArray.getJSONObject(i);
                    wp = new WindAlarmProgram(jObject2);
                    list.add(wp);
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

        //this.dialog.setMessage("Reading program...");
        //this.dialog.show();
    }

    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(List<WindAlarmProgram> list) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

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

