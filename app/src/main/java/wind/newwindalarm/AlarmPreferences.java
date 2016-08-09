package wind.newwindalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    public static Set<String> getSpotListFavorites(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //String regId = sharedPreferences.getString(QuickstartPreferences.KEY_SPOTLISTFAVORITES, "");
        Set<String> stringSet = sharedPreferences.getStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, new HashSet<String>(Arrays.asList("0", "12","2","1")));

        return stringSet;
    }

    public static void setSpotListFavorites(Context context, Set<String> stringSet) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, stringSet);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void addToSpotListFavorites(Context context, long spotId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = getSpotListFavorites(context);
        if (!favorites.contains("" + spotId))
            favorites.add("" + spotId);
        editor.clear();
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, favorites);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deleteFromSpotListFavorites(Context context, long spotId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = getSpotListFavorites(context);
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.toArray()[i].equals("" + spotId)) {
                favorites.remove("" + spotId);
            }
        }
        editor.clear();
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, favorites);  // Saving string
        editor.commit(); // commit changes*/
    }

}
