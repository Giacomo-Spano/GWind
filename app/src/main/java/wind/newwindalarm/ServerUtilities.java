/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wind.newwindalarm;

import android.os.Build;
import android.util.Log;

//import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static wind.newwindalarm.CommonUtilities.TAG;
import static wind.newwindalarm.CommonUtilities.sendMessageToMainActivity;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();



    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final String regId, String serverUrl) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");

        serverUrl += "//unregister";

        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("unregister", "true");

        //post(serverUrl, params);
        AlarmPreferences.setRegId(SplashActivity.getContext(), regId);
        AlarmPreferences.deleteRegId(SplashActivity.getContext());

    }

    static boolean sendAuthCode(final String authCode, String serverUrl) {
        Log.i(TAG, "send auth code (authCode = " + authCode + ")");

        serverUrl += "//register";

        Map<String, String> params = new HashMap<String, String>();
        params.put("registeruser", "true");
        params.put("authcode", authCode);
        //rams.put("deviceId", "" + AlarmPreferences.getDeviceId(gt)/*MainActivity.getDeviceId()*/);

        try {
            post(serverUrl, params);

            return true;
        } catch (IOException e) {

            Log.e(TAG, "Failed to register on attempt ", e);
        }

        return false;
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    public static int getDeviceIdFromRegId(String token, String serverURL) {



        int deviceId = -1;

        try {

            URL url = new URL(serverURL + "/register?regid="+token);

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
                if (jObject.has("id")) {
                    deviceId = jObject.getInt("id");
                }

                if (conn != null)
                    conn.disconnect();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return deviceId;
    }
    private static String convertStreamToString(InputStream is) {

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
