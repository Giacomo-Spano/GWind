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

package wind.newwindalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;

import static wind.newwindalarm.CommonUtilities.sendMessageToMainActivity;

public class WindAlarmGcmListenerService extends GcmListenerService {

    private static final String TAG = "WindAlarmGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = "";// = data.getString("message");
        //Log.d(TAG, "From: " + from);
        //Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        String notificationType = data.getString("notificationtype");
        // notifies user
        if (notificationType.equals("Alarm")) {

            playAlarm(getApplicationContext(), data);

            return;

        } else {

            String spotId = data.getString("spotID");
            if (spotId != null) {
                if (!AlarmPreferences.isSpotFavorite(MainActivity.getContext(),Integer.valueOf(spotId)))
                    return;
            }

            String title = data.getString("title");
            message = data.getString("message");
            //CommonUtilities.sendMessageToMainActivity(getApplicationContext(), title, "messagetext", notificationType); // questto fa in modo che venga mandato un messaggio alla main actrivitik che poi puo fare qualcosa in base al tipo
            generateUINotification(getApplicationContext(), message, title); // questo genera la notifica nella barra notifica
        }

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(message);
        //generateUINotification(getApplicationContext(), message, "GWindAlarm"); // questo genera la notifica nella barra notifica
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("GWindAlarm")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void playAlarm(Context context, Bundle alarmData) {
        Intent resultIntent = new Intent(context, PlayAlarmActivity.class);

        String spotId = alarmData.getString("spotID");
        String alarmId = alarmData.getString("alarmId");
        String curDate = alarmData.getString("curDate");
        String curspeed = alarmData.getString("curspeed");
        String curavspeed = alarmData.getString("curavspeed");

        Bundle b = new Bundle();
        b.putString("spotid", spotId);
        b.putString("alarmid", alarmId);
        b.putString("curspeed",curspeed);
        b.putString("curavspeed",curavspeed);
        b.putString("curdate",curDate);
        resultIntent.putExtras(b); //Put your id to your next Intent

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(resultIntent);

        generateUINotification(getApplicationContext(), curDate.toString()
                                + "\nSveglia vento attivata (id:" + alarmId+ ")"
                                + "\nIntensità vento " + curspeed
                                + "\nIntensità media " + curavspeed, MainActivity.getSpotName(Integer.valueOf(spotId)));
        //sendMessageToMainActivity(getApplicationContext(), "title", "messagetext", notificationType); // questto fa in modo che venga mandato un messaggio alla main actrivitik che poi puo fare qualcosa in base al tipo



    }

    /**
     * Issues a notification to inform the user that server has sent a message. // cioe mostra un messaggio nella barra notifiche e fa in modo
     * che venga aperta una activiti se l'utente clicca sulla notifica
     */
    private void generateUINotification(Context context, String message, String title) {

        int icon = R.drawable.logo;

        Intent resultIntent = new Intent(context, MainActivity.class);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.setContentIntent(resultIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(icon)
                .setContentText(message).build();
        // Creates an explicit intent for an Activity in your app


        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);  // questo fa in modo che venga aperta l'activiti quando l'user clicca sulla notifica
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, notification/*mBuilder.build()*/);


    }

}
