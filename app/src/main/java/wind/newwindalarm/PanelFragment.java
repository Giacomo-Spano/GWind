package wind.newwindalarm;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import wind.newwindalarm.cardui.MeteoCardItem;
import wind.newwindalarm.cardui.MeteoCardListener;

public class PanelFragment extends Fragment implements OnItemSelectedListener, MeteoCardListener {

    private ProgressBar mProgress;
    private LinearLayout mcontainer;
    private LinearLayout mErrorLayout;
    private List<MeteoCardItem> meteoList = new ArrayList<MeteoCardItem>();
    private List<Long> spotOrder = new ArrayList<Long>();
    ;
    private Menu mMenu;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;//.getItem(R.id.options_refresh);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.options_refresh).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options_refresh:
                // Do Fragment menu item stuff here
                getMeteoData();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v;
        v = inflater.inflate(R.layout.fragment_controlpanel, container, false);


        mcontainer = (LinearLayout) v.findViewById(R.id.meteolist);
        mErrorLayout = (LinearLayout) v.findViewById(R.id.errorLayout);

        mProgress = (ProgressBar) v.findViewById(R.id.progressBar);

        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Stazioni meteo"/*items[position]*/);

        getMeteoData();

        return v;
    }


    @Override
    public void meteocardselected(long index) {

    }

    public void showSpotDetail(long spotID) {
        Fragment spotDetail = new SpotDetailFragment();

        Bundle data = new Bundle();
        data.putLong("spotID", spotID);
        spotDetail.setArguments(data);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        // TODO commentato
        transaction.replace(R.id.content_frame, spotDetail);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private class requestDataResponse implements AsyncRequestMeteoDataResponse, MeteoCardListener {

        FragmentManager mFragmentManager;

        public requestDataResponse(FragmentManager fm) {

            mFragmentManager = fm;
        }

        @Override
        public void processFinish(List<Object> list, /*long spotID,*/ boolean error, String errorMessage) {

            if (error) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle("Errore");
                alertDialogBuilder
                        .setMessage(errorMessage)
                        .setCancelable(false);
                alertDialogBuilder
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog;
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            } else {
                // rimuovi tutte le card
                for (int i = 0; i < meteoList.size(); i++) {
                    mcontainer.removeView(meteoList.get(i).card);
                }
                meteoList.clear();


                Set<String> favorites = AlarmPreferences.getSpotListFavorites(getActivity());
                Iterator iter = favorites.iterator();
                while (iter.hasNext()) {
                    long id = Long.valueOf((String) iter.next());
                    Iterator resultIterator = list.iterator();
                    while (resultIterator.hasNext()) {
                        MeteoStationData md = (MeteoStationData) resultIterator.next();
                        if(md.spotID == id) {
                            final MeteoCardItem carditem = new MeteoCardItem(this, getActivity(), mcontainer);
                            carditem.spotID = md.spotID;
                            Spot spot = MainActivity.getSpotFromId(md.spotID);
                            if (spot != null) {
                                carditem.card.setTitle(spot.name);
                                carditem.card.setSourceUrl(spot.sourceUrl);
                                if (md.offline) {
                                    carditem.card.setTitle("offline");
                                }
                            }
                            carditem.update(md);
                            mcontainer.addView(carditem.card);
                            meteoList.add(carditem);
                        }
                    }

                }

                mcontainer.invalidate();
            }
        }




        @Override
        public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {
        }

        @Override
        public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {
        }

        @Override
        public void meteocardselected(long spotID) {

            showSpotDetail(spotID);
        }
    }

    public void getMeteoData() {

        /*String spotList = "";
        for (int i = 0; i < spotOrder.size(); i++) {

            if (i > 0)
                spotList += ",";

            spotList += spotOrder.get(i);
        }
        if (spotOrder == null || spotOrder.size() == 0)
            return;*/

        Set<String> favorites = AlarmPreferences.getSpotListFavorites(getActivity());
        if (favorites.size() == 0)
            return;

        String spotList = "";
        Iterator iter = favorites.iterator();
        while (iter.hasNext()) {
            long id = Long.valueOf((String)iter.next());
            spotList += id;
            if (iter.hasNext())
                spotList += ",";
        }

        new requestMeteoDataTask(getActivity(), new requestDataResponse(getFragmentManager()),requestMeteoDataTask.REQUEST_LASTMETEODATA).execute(spotList);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void updateProgressbar(int n) {

        mProgress.setProgress(n);
    }

    public void setSpotOrder(List<Long> list) {
        spotOrder = list;
        if (mMenu != null)
            mMenu.findItem(R.id.options_refresh).setVisible(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {

    }
}