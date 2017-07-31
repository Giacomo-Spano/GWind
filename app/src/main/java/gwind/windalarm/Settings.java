package gwind.windalarm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giacomo on 06/09/2015.
 */
public class Settings {

    public static final String DEF_SPOTLIST = "0";
    private SharedPreferences sharedPreferences;

    public interface SettingsListener {
        public void onChangeOrder(List<Long> order);

        public void onChangeList(List<Long> order);
    }

    SettingsListener mListener = null;


    public Settings(Activity activity) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void setListener(SettingsListener listener) {
        mListener = listener;
    }

    public List<Long> readSpotOrder() {

        List<Long> list = new ArrayList<Long>();
        String key = "spotOrder";
        String value = sharedPreferences.getString(key, DEF_SPOTLIST);
        if (value != "") {

            String[] values = value.split(",");
            for (int i = 0; i < values.length; i++) {
                list.add(Long.parseLong(values[i]));
            }
        }

        return list;
    }

    public void writeSpotOrder(List<Long> list) {

        String newValue = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0)
                newValue += ",";
            newValue += list.get(i);
        }

        String key = "spotOrder";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, newValue);
        editor.commit();

        mListener.onChangeOrder(list);
    }

    public List<Long> readSpotList() {

        List<Long> list = new ArrayList<Long>();

        String key = "spotList";
        String value = sharedPreferences.getString(key, DEF_SPOTLIST);
        if (value != "") {


            String[] values = value.split(",");

            for (int i = 0; i < values.length; i++) {
                list.add(Long.parseLong(values[i]));
            }
        }
        return list;
    }

    public void writeSpotList(List<Long> list) {

        String newValue = "";
        for (int i = 0; i < list.size(); i++) {
            if (i != 0)
                newValue += ",";
            newValue += list.get(i);
        }

        String key = "spotList";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, newValue);
        editor.commit();

        if (mListener != null)
            mListener.onChangeList(list);
    }

    public String getListString() {
        String newValue = "";
        List<Long> l = readSpotList();
        for (int i = 0; i < l.size(); i++) {
            if (i != 0) newValue += ",";
            newValue += l.get(i);
        }
        return newValue;
    }

    public String getOrderString() {
        String newValue = "";
        List<Long> l = readSpotOrder();
        for (int i = 0; i < l.size(); i++) {
            if (i != 0) newValue += ",";
            newValue += l.get(i);
        }
        return newValue;
    }

    public List<Long> getNewOrderList(List<Long> list) {
        List<Long> newlist = new ArrayList<Long>();

        List<Long> currentlist = readSpotOrder();

        for (int i = 0; i < currentlist.size(); i++) {

            for (int k = 0; k < list.size(); k++) {
                if (currentlist.get(i) == list.get(k)) {
                    newlist.add(currentlist.get(i));
                    break;
                }
            }
        }
        // aggiungi in fondo quelli che non c'erano
        for (int i = 0; i < list.size(); i++) {

            boolean found = false;
            for (int k = 0; k < currentlist.size(); k++) {
                if (currentlist.get(k) == list.get(i)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                newlist.add(list.get(i));
        }
        return newlist;
    }
}
