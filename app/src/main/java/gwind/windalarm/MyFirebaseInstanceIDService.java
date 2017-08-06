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

package gwind.windalarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import gwind.windalarm.request.registertask;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
    * the previous token had been compromised. This call is initiated by the
    * InstanceID provider.
            */
    // [START refresh_token]
    /*@Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }*/
    // [END refresh_token]

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        /*InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);*/
        // [END get_token]

       /* AlarmPreferences.setRegId(SplashActivity.getContext(), refreshedToken);
        Log.i(TAG, "GCM Registration Token: " + refreshedToken);
        //boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        //Log.i(TAG, "sentToken=" + sentToken);

        long deviceId = AlarmPreferences.getDeviceId(SplashActivity.getContext());
        Log.i(TAG, "deviceid=" + deviceId);

        String personId, personName, personEmail, authCode;
        Uri personPhoto = null;

        UserProfile profile = (UserProfile) intent.getSerializableExtra("userProfile");*/

            /*if (SplashActivity.getAcct() == null) {
                personId = AlarmPreferences.getPersonId(SplashActivity.getContext());;
                personName = null;
                personEmail = null;
                personPhoto = null;
                authCode = null;
            } else {
                personId = SplashActivity.getAcct().getId();
                personName = SplashActivity.getAcct().getDisplayName();
                personEmail = SplashActivity.getAcct().getEmail();
                personPhoto = SplashActivity.getAcct().getPhotoUrl();
                authCode = SplashActivity.getAcct().getServerAuthCode();
            }*/

        //sendRegistrationToServer(profile.personId, profile.userName, profile.email, profile.photoUrl, null);



        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String refreshedToken) {
        // Add custom implementation, as needed.
        //ServerUtilities.register(token, serverURL);


        String model = Build.MODEL;

        new registertask(SplashActivity.getInstance(), new AsyncRegisterResponse() {

            @Override
            public void processFinish(String jsonStr, boolean error, String errorMessage) {

                if (error)
                    CommonUtilities.sendMessageToMainActivity(SplashActivity.getContext(), "errore registrazione", errorMessage);


                try {
                    JSONObject json = new JSONObject(jsonStr);
                    int deviceId = -1;
                    int userId = -1;
                    if (json.has("deviceid")) {
                        deviceId = json.getInt("deviceid");
                        //long old_deviceId = AlarmPreferences.getDeviceId(SplashActivity.getContext());
                        AlarmPreferences.setDeviceId(SplashActivity.getContext(), deviceId);
                    }
                    /*if (json.has("userid")) {
                        userId = json.getInt("userid");
                        //AlarmPreferences.setUserId(SplashActivity.getContext(), userId);
                    }*/
                    /*AlarmPreferences.setPersonId(SplashActivity.getContext(), personId);
                    if (personEmail != null)
                        AlarmPreferences.setEmail(SplashActivity.getContext(), personEmail);
                    if (personName != null)
                        AlarmPreferences.setUserName(SplashActivity.getContext(), personName);*/
                    CommonUtilities.sendMessageToMainActivity(SplashActivity.getContext(), "title", "Registrazione utente completata: deviceid=" + deviceId + "userId=" + userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonUtilities.sendMessageToMainActivity(SplashActivity.getContext(), "title", "impossibile registrare device");
                }
            }
        }, registertask.POST_REGISTERDEVICE).execute(refreshedToken,model);

    }
}
