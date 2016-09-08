package wind.newwindalarm;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.preference.CheckBoxPreference;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//import android.support.v4.app.p;
//import android.preference.PreferenceFragment;
import android.preference.PreferenceFragment;
//import android.support.v7.;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {


    public static final String KEY_PREF_SPOTLIST = "spotList";
    public static final String KEY_PREF_SPOTORDER = "spotOrder";

    private Settings mSettings;
    private List<Spot> mSpotList;

    public SettingsFragment() {
    }

    String serverurl = "prova";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Updating the action bar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Impostazioni");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //String key = getResources().getString(R.string.pref_serverURL);
        android.support.v7.preference.Preference pref = findPreference(QuickstartPreferences.KEY_PREF_SERVERURL);
        String str = ((ListPreference) pref).getValue();
        pref.setSummary(str/*mSettings.getServerURL()*/);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                serverurl = newValue.toString();
                return true;
            }
        });


        CheckBoxPreference pLight = (CheckBoxPreference)getPreferenceManager().findPreference(QuickstartPreferences.KEY_PREF_NOTIFICATIONWINDINCREASE);
        pLight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean myValue = (Boolean) newValue;

                if(myValue){
                }
                return true;
            }
        });

        pLight = (CheckBoxPreference)getPreferenceManager().findPreference(QuickstartPreferences.KEY_PREF_NOTIFICATIONHIGHWIND);
        pLight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean myValue = (Boolean) newValue;

                if(myValue){
                }
                return true;
            }
        });


        /*pref = findPreference(KEY_PREF_SPOTLIST);
        String newValue = mSettings.getListString();
        pref.setSummary(newValue);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {

                //showSpotList();
                return true;
            }
        });

        pref = findPreference(KEY_PREF_SPOTORDER);
        newValue = mSettings.getOrderString();
        pref.setSummary(newValue);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {

                //showSpotOrder();
                return true;
            }
        });*/
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }


    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    /*public void setServerSpotList(List<Spot> list) {

        mSpotList = list;
    }*/

    /*public void showSpotList() {

        SpotListFragment spotList = new SpotListFragment();
        if (mSpotList == null) {
            showError();
            return;
        }
        spotList.setSpotList(mSpotList);
        spotList.setListener(new SpotListFragment.OnSpotListListener() {
            @Override
            public void onSpotListChangeSelection(List<Long> list) {
                List<Long> newlist = mSettings.getNewOrderList(list);
                setSpotOrder(newlist);
                setSpotList(list);
            }
        });

        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        // TODO commentato
        transaction.replace(R.id.content_frame, spotList); //

        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    /*public void showSpotOrder() {

        SpotOrderFragment spotList = new SpotOrderFragment();
        if (mSpotList == null) {
            showError();
            return;
        }
        List<Long> list = mSettings.readSpotOrder();
        List<Spot> sl = new ArrayList<Spot>();
        for (int i = 0; i < list.size(); i++) {
            for (int k = 0; k < mSpotList.size(); k++) {
                Spot s = mSpotList.get(k);
                if (s.id == list.get(i)) {
                    sl.add(s);
                    break;
                }
            }
        }

        spotList.setSpotList(sl);
        spotList.setListener(new SpotOrderFragment.OnSpotOrderListener() {
            @Override
            public void onSpotChangeOrder(List<Long> order) {
                setSpotOrder(order);
            }
        });
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        // TODO commentato
        transaction.replace(R.id.content_frame, spotList);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    private void showError() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Errore");
        alertDialogBuilder
                .setMessage("server offline")
                .setCancelable(false);
        alertDialogBuilder
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*public void setSpotOrder(List<Long> list) {

        mSettings.writeSpotOrder(list);
        Preference pref = findPreference(KEY_PREF_SPOTORDER);
        String newValue = mSettings.getOrderString();
        pref.setSummary(newValue);
    }

    public void setSpotList(List<Long> list) {

        mSettings.writeSpotList(list);
        Preference pref = findPreference(KEY_PREF_SPOTLIST);
        String newValue = mSettings.getListString();
        pref.setSummary(newValue);
    }*/


}
