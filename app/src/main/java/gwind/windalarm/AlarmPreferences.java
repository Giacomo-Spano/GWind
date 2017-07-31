package gwind.windalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Giacomo on 16/06/2015.
 */
public class AlarmPreferences {

    public AlarmPreferences() {

    }

    public static String getServerUrl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(QuickstartPreferences.KEY_PREF_SERVERURL, context.getResources().getString(R.string.pref_serverURL_default));
        return url;
    }

    public static Boolean getHighWindNotification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean option = sharedPreferences.getBoolean(QuickstartPreferences.KEY_PREF_NOTIFICATIONHIGHWIND, true);
        return option;
    }

    public static Boolean getWindIncreaseNotification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean option = sharedPreferences.getBoolean(QuickstartPreferences.KEY_PREF_NOTIFICATIONWINDINCREASE, true);
        return option;
    }

    public static String getRegId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String regId = sharedPreferences.getString(QuickstartPreferences.REGISTRATION_ID, "");
        return regId;
    }

    public static void setRegId(Context context, String regId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.REGISTRATION_ID, regId);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static int getDeviceId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int deviceId = sharedPreferences.getInt(QuickstartPreferences.DEVICE_ID, -1);
        return deviceId;
    }

    public static void setDeviceId(Context context, int deviceId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(QuickstartPreferences.DEVICE_ID, deviceId);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deleteRegId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.REGISTRATION_ID);
        editor.commit();

        editor.remove(QuickstartPreferences.SENT_TOKEN_TO_SERVER);
        editor.commit();

    }

}
