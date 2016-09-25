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

    /*public static Set<String> getSpotListFavorites(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //String regId = sharedPreferences.getString(QuickstartPreferences.KEY_SPOTLISTFAVORITES, "");
        Set<String> stringSet = sharedPreferences.getStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, new HashSet<String>(Arrays.asList("0", "12","2","1")));

        return stringSet;
    }*/

    /*public static void setSpotListFavorites(Context context, Set<String> stringSet) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, stringSet);  // Saving string
        editor.commit();
    }*/

    /*public static void addToSpotListFavorites(Context context, long spotId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = getSpotListFavorites(context);
        if (!favorites.contains("" + spotId))
            favorites.add("" + spotId);
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, favorites);  // Saving string

    }*/

    /*public static void deleteFromSpotListFavorites(Context context, long spotId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = getSpotListFavorites(context);
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.toArray()[i].equals("" + spotId)) {
                favorites.remove("" + spotId);
            }
        }
        //editor.clear();
        editor.putStringSet(QuickstartPreferences.KEY_SPOTLISTFAVORITES, favorites);  // Saving string
        editor.commit(); // commit changes
    }*/

    /*public static boolean isSpotFavorite(Context context, long spotId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = getSpotListFavorites(context);
        if (favorites.contains("" + spotId))
            return true;
        return false;
    }*/

    public static String getPersonId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String personId = sharedPreferences.getString(QuickstartPreferences.PERSON_ID,null);
        return personId;
    }

    public static void setPersonId(Context context, String personId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.PERSON_ID, personId);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deletePersonId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.PERSON_ID);
        editor.commit();

    }

    public static long getUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long userId = sharedPreferences.getLong(QuickstartPreferences.USER_ID,-1l);
        return userId;
    }

    public static void setUserId(Context context, long userId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(QuickstartPreferences.USER_ID, userId);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deleteUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.USER_ID);
        editor.commit();

    }

    public static String getPhotoURL(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String photoURL = sharedPreferences.getString(QuickstartPreferences.PHOTOURL_ID,null);
        return photoURL;
    }

    public static void setPhotoURL(Context context, String photoURL) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.PHOTOURL_ID, photoURL);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deletePhotoURL(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.PHOTOURL_ID);
        editor.commit();

    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = sharedPreferences.getString(QuickstartPreferences.EMAIL_ID,null);
        return email;
    }

    public static void setEmail(Context context, String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.EMAIL_ID, email);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deleteEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.EMAIL_ID);
        editor.commit();

    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(QuickstartPreferences.USERNAME_ID,null);
        return userName;
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.USERNAME_ID, userName);  // Saving string
        editor.commit(); // commit changes*/
    }

    public static void deleteUserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.USERNAME_ID);
        editor.commit();

    }

}
