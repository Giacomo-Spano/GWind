package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by giacomo on 04/07/2015.
 */
public class registertask extends AsyncTask<Object, Boolean, Boolean> {


    public static int POST_REGISTERDEVICE = 1;
    public static int POST_DELETEALARM = 2;
    public static int POST_UPDATEALARMRINGDATE = 3;
    public static int POST_SNOOZEALARM = 4;
    public static final int POST_TESTALARM = 5;
    public static int POST_NOTIFICATIONSETTING = 100;
    private int postType;

    public AsyncRegisterResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    int regId;
    String response = "";

    public registertask(Activity activity, AsyncRegisterResponse asyncResponse, int postType) {

        this.postType = postType;
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor

    }

    @Override
    protected Boolean doInBackground(Object... params) {

        URL myUrl = null;
        HttpURLConnection conn = null;

        String regId = (String) params[0];
        String name = (String) params[1];

        String serverURL = AlarmPreferences.getServerUrl(activity);//getServerURL();

        if (postType == POST_REGISTERDEVICE) {
            try {
                myUrl = new URL(serverURL + "/register");
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //one long string, first encode is the key to get the  data on your web
                //page, second encode is the value, keep concatenating key and value.
                //theres another ways which easier then this long string in case you are
                //posting a lot of info, look it up.
                String postData = URLEncoder.encode("registerdevice", "UTF-8") + "=" +
                        URLEncoder.encode("true", "UTF-8") + "&" +
                        URLEncoder.encode("regId", "UTF-8") + "=" +
                        URLEncoder.encode(regId, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" +
                        URLEncoder.encode(name, "UTF-8") + "&";
                OutputStream os = conn.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected void onPreExecute() {

        String message = "attendere prego...";
        if (postType == POST_REGISTERDEVICE)
            message = "Registrazione in corso...";

        this.dialog.setMessage(message);
        this.dialog.show();


        /*this.dialog.show(this.activity,
                "Title",
                "Message");*/
        this.dialog.setCancelable(true);
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // Do something...
            }
        });
    }

    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Boolean res) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        delegate.processFinish(response, error, errorMessage);
    }

}
