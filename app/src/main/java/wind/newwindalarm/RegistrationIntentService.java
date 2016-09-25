package wind.newwindalarm;

/**
 * Created by Giacomo Spanò on 11/06/2016.
 * <p/>
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]


            AlarmPreferences.setRegId(SplashActivity.getContext(), token);
            Log.i(TAG, "GCM Registration Token: " + token);
            boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            Log.i(TAG, "sentToken=" + sentToken);

            long deviceId = AlarmPreferences.getDeviceId(SplashActivity.getContext());
            Log.i(TAG, "deviceid=" + deviceId);
            //String str = MainActivity.getContext().getResources().getString(R.string.pref_serverURL_default);
            //String serverURL = sharedPreferences.getString(QuickstartPreferences.KEY_PREF_SERVERURL, str);
            //String serverURL = AlarmPreferences.getServerUrl(MainActivity.getContext());

            //Log.i(TAG, "serverURL="+serverURL);

            //TelephonyManager mngr = (TelephonyManager) MainActivity.getContext().getSystemService(MainActivity.getContext().TELEPHONY_SERVICE);
            //int deviceId = -1; //getDeviceIdFromServer(token,serverURL);


            //if (!sentToken || deviceId == -1 || deviceId == 0) {

            String personId = SplashActivity.getAcct().getId();
            String personName = SplashActivity.getAcct().getDisplayName();
            String personEmail = SplashActivity.getAcct().getEmail();
            Uri personPhoto = SplashActivity.getAcct().getPhotoUrl();
            String authCode = SplashActivity.getAcct().getServerAuthCode();

            sendRegistrationToServer(personId, personName, personEmail, personPhoto, authCode);
            //}

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            /*AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Do you want to logout?");
            alert.setMessage("Message: " + e.toString());
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                }
            });

            alert.show();*/

            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String personId, final String personName, final String personEmail, Uri personPhoto, String authCode) {
        // Add custom implementation, as needed.
        //ServerUtilities.register(token, serverURL);
        String model = Build.MODEL;

        new registertask(SplashActivity.getInstance(), new AsyncRegisterResponse() {

            @Override
            public void processFinish(String jsonStr, boolean error, String errorMessage) {


                try {
                    JSONObject json = new JSONObject(jsonStr);
                    int deviceId = -1;
                    int userId = -1;
                    if (json.has("deviceid")) {
                        deviceId = json.getInt("deviceid");
                        long old_deviceId = AlarmPreferences.getDeviceId(SplashActivity.getContext());
                        AlarmPreferences.setDeviceId(SplashActivity.getContext(), deviceId);
                    }
                    if (json.has("userid")) {
                        userId = json.getInt("userid");
                        AlarmPreferences.setUserId(SplashActivity.getContext(), userId);
                    }
                    AlarmPreferences.setPersonId(SplashActivity.getContext(), personId);
                    AlarmPreferences.setEmail(SplashActivity.getContext(), personEmail);
                    AlarmPreferences.setUserName(SplashActivity.getContext(), personName);
                    CommonUtilities.sendMessageToMainActivity(SplashActivity.getContext(), "title", "Registrazione utente completata: deviceid=" + deviceId + "userId=" + userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, registertask.POST_REGISTERDEVICE).execute(AlarmPreferences.getRegId(this), model, personId, personName, personEmail, personPhoto, authCode);

    }

    private int getDeviceIdFromServer(String token, String serverURL) {
        // Add custom implementation, as needed.
        return ServerUtilities.getDeviceIdFromRegId(token, serverURL);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}