package wind.newwindalarm.request;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wind.newwindalarm.AlarmPreferences;
import wind.newwindalarm.AsyncPostProgramResponse;
import wind.newwindalarm.NotificationSettings;
import wind.newwindalarm.WindAlarmProgram;

/**
 * Created by giacomo on 04/07/2015.
 */
public class postprogramtask extends AsyncTask<Object, Boolean, Boolean> {


    public static int POST_ALARM = 1;
    public static int POST_DELETEALARM = 2;
    public static int POST_UPDATEALARMRINGDATE = 3;
    public static int POST_SNOOZEALARM = 4;
    public static final int POST_TESTALARM = 5;
    public static int POST_NOTIFICATIONSETTING = 100;
    private int postType;

    public AsyncPostProgramResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    WindAlarmProgram alarm;
    NotificationSettings notificationSettings;
    private String mServerURL;


    public postprogramtask(Activity activity, AsyncPostProgramResponse asyncResponse, int postType) {

        this.postType = postType;
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
        mServerURL = AlarmPreferences.getServerUrl(activity);//getServerURL();
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost;
        Gson gson = new Gson();
        String jsonText;
        boolean deletekey = false;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

        String regId = AlarmPreferences.getRegId(activity.getApplicationContext());
        //String IMEI = MainActivity.getIMEI();


        if (postType == POST_ALARM || postType == POST_DELETEALARM) {

            alarm = (WindAlarmProgram) params[0];
            //mServerURL = (String) params[1];
            httppost = new HttpPost(mServerURL + "/alarm");
            nameValuePairs.add(new BasicNameValuePair("alarmId", "" + alarm.id));
            //deletekey = (boolean) params[1];
            if (postType == POST_DELETEALARM)
                nameValuePairs.add(new BasicNameValuePair("delete", "true"));
            jsonText = gson.toJson(alarm);
            nameValuePairs.add(new BasicNameValuePair("json", jsonText));
            nameValuePairs.add(new BasicNameValuePair("regId", regId));
            //nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId.toString()));

        } else if (postType == POST_UPDATEALARMRINGDATE) {
            int alarmId = (int) params[0];
            Date date = (Date) params[1];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            SimpleDateFormat stf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            httppost = new HttpPost(mServerURL + "/alarm");
            nameValuePairs.add(new BasicNameValuePair("alarmId", "" + alarmId));
            nameValuePairs.add(new BasicNameValuePair("ring", "true"));
            String strDate = sdf.format(date);
            String strTime = stf.format(date);
            nameValuePairs.add(new BasicNameValuePair("date", "" + strDate));
            nameValuePairs.add(new BasicNameValuePair("time", "" + strTime));
            nameValuePairs.add(new BasicNameValuePair("snooze", "" + strTime));
            nameValuePairs.add(new BasicNameValuePair("regId", regId));
            //nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId.toString()));

        } else if (postType == POST_SNOOZEALARM) {
            int alarmId = (int) params[0];
            int snoozeMinutes = (int) params[1];
            httppost = new HttpPost(mServerURL + "/alarm");
            nameValuePairs.add(new BasicNameValuePair("alarmId", "" + alarmId));
            nameValuePairs.add(new BasicNameValuePair("snooze", "true"));
            nameValuePairs.add(new BasicNameValuePair("minutes", "" + snoozeMinutes));
            nameValuePairs.add(new BasicNameValuePair("regId", regId));
            //nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId.toString()));

        } else if (postType == POST_TESTALARM) {
            long alarmId = (long) params[1];
            httppost = new HttpPost(mServerURL + "/alarm");
            nameValuePairs.add(new BasicNameValuePair("alarmId", "" + alarmId));
            //nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId.toString()));
            nameValuePairs.add(new BasicNameValuePair("test", "true"));
            ;

        } else if (postType == POST_NOTIFICATIONSETTING) {
            notificationSettings = (NotificationSettings) params[0];
            //mServerURL = (String) params[1];
            httppost = new HttpPost(mServerURL + "/notification");
            jsonText = gson.toJson(notificationSettings);
        } else {
            return false;
        }

        //nameValuePairs.add(new BasicNameValuePair("regId", regId));
        //nameValuePairs.add(new BasicNameValuePair("json", jsonText));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
            return false;
        } catch (IOException e) {
            error = true;
            e.printStackTrace();
            errorMessage = e.toString();
            return false;
        }
        return true;
    }

    protected void onPreExecute() {

        String message = "Attendere prego...";
        if (postType == POST_TESTALARM)
            message = "Test sveglia i corso...";
        else if (postType == POST_ALARM)
            message = "Salvataggio in corso...";
        else if (postType == POST_DELETEALARM)
            message = "Cancellazione in corso...";

        this.dialog.setMessage(message);
        //this.dialog.show();

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

        /*if (dialog.isShowing()) {
            dialog.dismiss();
        }*/
        delegate.processFinish(alarm, error, errorMessage);
    }

}
