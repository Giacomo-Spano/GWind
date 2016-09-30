package wind.newwindalarm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by giacomo on 01/07/2015.
 */
public class requestMeteoDataTask extends
        AsyncTask<Object, Long, requestMeteoDataTask.Result> {

    public static final int REQUEST_LASTMETEODATA = 1;
    //public static final int REQUEST_HISTORYMETEODATA = 2;
    public static final int REQUEST_SPOTLIST = 3;
    public static final int REQUEST_SPOTLIST_FULLINFO = 4;
    public static final int REQUEST_LOGMETEODATA = 5;
    public static final int REQUEST_ADDFAVORITES = 6;
    public static final int REQUEST_REMOVEFAVORITE = 7;
    public static final int REQUEST_FAVORITESLASTMETEODATA = 8;

    public static String Spot_All = "all";
    public AsyncRequestMeteoDataResponse delegate = null;//Call back interface
    private ProgressDialog dialog;
    private Activity activity;
    private boolean error = false;
    private String errorMessage = "";
    int requestType;
    int contentSize;


    protected class Result {
        List<MeteoStationData> meteoList;
        List<Spot> spotList;
        List<Long> favorites;
        long spotId;
    }

    public requestMeteoDataTask(Activity activity, AsyncRequestMeteoDataResponse asyncResponse, int type) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
        requestType = type;
    }

    protected requestMeteoDataTask.Result doInBackground(Object... params) {

        requestMeteoDataTask.Result result = new requestMeteoDataTask.Result();
        URL url;

        if (requestType == REQUEST_ADDFAVORITES || requestType == REQUEST_REMOVEFAVORITE) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            String regId = AlarmPreferences.getRegId(activity.getApplicationContext());

            long spotId = (long) params[0];
            String userid = (String) params[1];
            String serverUrl = AlarmPreferences.getServerUrl(activity);
            httppost = new HttpPost(serverUrl + "/meteo");
            nameValuePairs.add(new BasicNameValuePair("spotid", "" + spotId));
            nameValuePairs.add(new BasicNameValuePair("userid", userid));
            if (requestType == REQUEST_REMOVEFAVORITE)
                nameValuePairs.add(new BasicNameValuePair("remove", "true"));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                error = true;
                e.printStackTrace();
                errorMessage = e.toString();
                return null;
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
                errorMessage = e.toString();
                return null;
            }

            result.spotId = spotId;
            return result;

        } else {

            try {
                String path = "/meteo?";
                if (requestType == REQUEST_LASTMETEODATA) {
                    String spotList = (String) params[0]; //lista di spot separata da virgola
                    String userid = (String) params[1];

                    path += "lastdata=true";
                    path += "&history=false";
                    path += "&requestspotlist=false";
                    path += "&fullinfo=false";
                    path += "&spot=" + spotList;
                    path += "&userid=" + userid;

                } else if (requestType == REQUEST_LOGMETEODATA) {
                    long spotId = (long) params[0]; // spotID
                    String userid = (String) params[1];
                    Date start = (Date) params[2];
                    Date end = (Date) params[3];
                    long lastWindId = (long) params[4];

                    path += "lastdata=false";
                    path += "&log=true";
                    path += "&requestspotlist=false";
                    path += "&fullinfo=false";
                    path += "&spot=" + spotId;
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
                    path += "&start=" + df.format(start);
                    path += "&end=" + df.format(end);
                    path += "&lastwindid=" + lastWindId;
                    path += "&userid=" + userid;

                    result.spotId = spotId;

                } else if (requestType == REQUEST_SPOTLIST) {
                    String userid = (String) params[0];
                    path += "lastdata=false";
                    path += "&history=false";
                    path += "&requestspotlist=true";
                    path += "&fullinfo=false";
                    path += "&spot=" + Spot_All;
                    path += "&userid=" + userid;

                } else if (requestType == REQUEST_FAVORITESLASTMETEODATA) {
                    String userid = (String) params[0];
                    Long windid = (Long) params[1];
                    path += "favoriteslastdata=true";
                    path += "&userid=" + userid;
                    path += "&lastwindid=" + windid;

                } else if (requestType == REQUEST_SPOTLIST_FULLINFO) {
                    String userid = (String) params[0];
                    path += "lastdata=false";
                    path += "&history=false";
                    path += "&requestspotlist=true";
                    path += "&fullinfo=true";
                    path += "&spot=" + Spot_All;
                    path += "&userid=" + userid;

                }

                String serverUrl = AlarmPreferences.getServerUrl(activity);
                url = new URL(serverUrl + path);
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (SplashActivity.getAcct() != null)
                    conn.setRequestProperty("user", SplashActivity.getAcct().getServerAuthCode());
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
                    contentSize = 1000;//Integer.valueOf(conn.getHeaderField("Length"));//conn.getContentLength();
                    String json = convertStreamToString(in);

                    if (requestType == REQUEST_SPOTLIST || requestType == REQUEST_SPOTLIST_FULLINFO) {
                        List<Spot> list = new ArrayList<Spot>();
                        JSONObject jObject = new JSONObject(json);
                        JSONArray jArray = jObject.getJSONArray("spotlist");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObject2 = jArray.getJSONObject(i);
                            Spot spt = new Spot(jObject2);
                            list.add(spt);
                        }
                        String favorites = jObject.getString("favorites");
                        result.spotList = list;
                        result.favorites = new ArrayList<Long>();
                        String[] split = favorites.split(",");
                        if (split != null) {
                            for (int i = 0; i < split.length; i++) {
                                if (!split[i].equals("")) {
                                    long id = Integer.valueOf(split[i]);
                                    if (id != 0)
                                        result.favorites.add(id);
                                }
                            }
                        }
                        // TODO add favorites list
                    } else if (requestType == REQUEST_FAVORITESLASTMETEODATA || requestType == REQUEST_LASTMETEODATA/*|| requestType == REQUEST_HISTORYMETEODATA*/) {
                        List<MeteoStationData> list = new ArrayList<MeteoStationData>();
                        JSONObject jObject = new JSONObject(json);
                        JSONArray jArray = jObject.getJSONArray("meteodata");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObject2 = jArray.getJSONObject(i);
                            MeteoStationData md = new MeteoStationData(jObject2);
                            list.add(md);
                        }
                        result.meteoList = list;
                    } else if (requestType == REQUEST_LOGMETEODATA) {
                        List<MeteoStationData> list = new ArrayList<MeteoStationData>();
                        JSONObject jObject = new JSONObject(json);
                        String date = jObject.getString("date");
                        String speed = jObject.getString("speed");
                        String avspeed = jObject.getString("avspeed");
                        String temperature = jObject.getString("temperature");
                        String direction = jObject.getString("direction");
                        String trend = jObject.getString("trend");
                        String id = jObject.getString("id");

                        String[] dates = date.split(";");
                        String[] speeds = speed.split(";");
                        String[] avspeeds = avspeed.split(";");
                        String[] temperatures = temperature.split(";");
                        String[] directions = direction.split(";");
                        String[] trends = trend.split(";");
                        String[] ids = id.split(";");
                        for (int i = 0; i < dates.length; i++) {
                            MeteoStationData md = new MeteoStationData();
                            if (dates[i] != null) {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
                                try {
                                    md.date = df.parse(dates[i]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                            } else {
                                continue;
                            }
                            if (speeds[i] != null && !speeds[i].equals(""))
                                md.speed = Double.valueOf(speeds[i]);
                            else
                                continue;
                            if (avspeeds[i] != null && !avspeeds[i].equals(""))
                                md.averagespeed = Double.valueOf(avspeeds[i]);
                            else
                                continue;
                            if (directions[i] != null && !directions[i].equals(""))
                                md.directionangle = Double.valueOf(directions[i]);
                            else
                                continue;
                            if (trends[i] != null && !trends[i].equals(""))
                                md.trend = Double.valueOf(trends[i]);
                            else
                                continue;
                            if (temperatures[i] != null && !temperatures[i].equals(""))
                                md.temperature = Double.valueOf(temperatures[i]);
                            else
                                continue;
                            if (ids[i] != null && !ids[i].equals(""))
                                md.id = Long.valueOf(ids[i]);
                            else
                                continue;
                            list.add(md);
                        }
                        result.meteoList = list;
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

            return result;
        }
    }

    protected void onPreExecute() {

        //progressBar.setProgress(10);
        String message = "attendere prego...";
        if (requestType == REQUEST_SPOTLIST)
            message = "Richiesta lista spot...";
        else if (requestType == REQUEST_SPOTLIST_FULLINFO)
            message = "Richiesta lista completa spot...";
            //else if (requestType == REQUEST_HISTORYMETEODATA)
            //message = "Richiesta dati storici...";
        else if (requestType == REQUEST_LASTMETEODATA)
            message = "Richiesta dati meteo...";

        //this.dialog.setMessage(message);
        //this.dialog.show();
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        //progressBar.setProgress(values[0].intValue());
    }

    protected void onPostExecute(Result result) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if (requestType == REQUEST_SPOTLIST || requestType == REQUEST_SPOTLIST_FULLINFO) {
            delegate.processFinishSpotList(result.spotList, result.favorites, error, errorMessage);
        } else if (requestType == REQUEST_LOGMETEODATA) {
            List<MeteoStationData> data = new ArrayList<>();
            if (result.meteoList != null) {
                for (Object obj : result.meteoList) {

                    data.add(new MeteoStationData((MeteoStationData) obj));
                }
            }
            delegate.processFinishHistory(result.spotId,data, error, errorMessage);
        } else if (requestType == REQUEST_LASTMETEODATA || requestType == REQUEST_FAVORITESLASTMETEODATA) {
            delegate.processFinish(result.meteoList, error, errorMessage);
        } else if (requestType == REQUEST_ADDFAVORITES) {
            delegate.processFinishAddFavorite(result.spotId, error, errorMessage);
        } else if (requestType == REQUEST_REMOVEFAVORITE) {
            delegate.processFinishRemoveFavorite(result.spotId, error, errorMessage);
        }


        //progressBar.setVisibility(View.GONE);
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
