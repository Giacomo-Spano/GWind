package wind.newwindalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Giacomo on 16/06/2015.
 */
public class AlarmPreferences {

    public AlarmPreferences(){

    }
    public static String getServerUrl(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(QuickstartPreferences.KEY_PREF_SERVERURL, context.getResources().getString(R.string.pref_serverURL_default));
        return url;
    }
    public static String getRegId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String regId = sharedPreferences.getString(QuickstartPreferences.REGISTRATION_ID, "");
        return regId;
    }
    public void setRegId(Context context, String regId)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QuickstartPreferences.REGISTRATION_ID, regId);  // Saving string
        editor.commit(); // commit changes*/
    }
    public void deleteRegId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(QuickstartPreferences.REGISTRATION_ID); // will delete key key_name4
        editor.commit(); // commit changes*/
    }

}
