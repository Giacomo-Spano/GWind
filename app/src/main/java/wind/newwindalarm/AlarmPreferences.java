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
    public static String getRegId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*MainActivity.getContext()*/);
        String regId = sharedPreferences.getString(QuickstartPreferences.REGISTRATION_ID, "");
        return regId;

        /*SharedPreferences pref = MainActivity.getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        String id = pref.getString(QuickstartPreferences.REGISTRATION_ID, null);
        return id;*/
    }
    public void setRegId(Context context, String regId)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*MainActivity.getContext()*/);
        //String regId = sharedPreferences.getString(QuickstartPreferences.REGISTRATION_ID, "");

        //SharedPreferences pref = MainActivity.getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(QuickstartPreferences.REGISTRATION_ID, regId);  // Saving string
        // Save the changes in SharedPreferences
        editor.commit(); // commit changes*/
    }
    public void deleteRegId(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context/*MainActivity.getContext()*/);
        //SharedPreferences pref = MainActivity.getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(QuickstartPreferences.REGISTRATION_ID); // will delete key key_name4
        // Save the changes in SharedPreferences
        editor.commit(); // commit changes*/
    }

}
