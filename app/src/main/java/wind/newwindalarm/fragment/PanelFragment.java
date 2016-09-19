package wind.newwindalarm.fragment;


import android.app.Activity;
//import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import wind.newwindalarm.AlarmPreferences;
import wind.newwindalarm.MainActivity;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.Spot;
import wind.newwindalarm.cardui.MeteoCardItem;
import wind.newwindalarm.cardui.MeteoCardListener;

public class PanelFragment extends Fragment implements OnItemSelectedListener, MeteoCardListener {

    private boolean viewCreated = false;
    OnSpotClickListener mCallback;
    private LinearLayout mcontainer;
    private Menu mMenu;
    private List<MeteoStationData> meteoDataList;
    private FloatingActionButton refreshFab;


    // Container Activity must implement this interface
    public interface OnSpotClickListener {
        void onSpotClick(long spotId);
        void onRefreshPanelRequest();
    }

    @Override
    public void onResume() {
        super.onResume();

        /*refreshFab.setImageResource(R.drawable.refreshbutton);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onRefreshPanelRequest();
            }
        });*/
    }

    public void setMeteoDataList(List<MeteoStationData> list) {
        meteoDataList = list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSpotClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAlarmListener");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (OnSpotClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnAlarmListener");
            }
        }
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_controlpanel, container, false);

        mcontainer = (LinearLayout) v.findViewById(R.id.meteolist);

        //refreshFab = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);


        //mErrorLayout = (LinearLayout) v.findViewById(R.id.errorLayout);
        //mProgress = (ProgressBar) v.findViewById(R.id.progressBar);

        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Stazioni meteo"/*items[position]*/);

        viewCreated = true;
        refreshMeteoData();

        return v;
    }

    @Override
    public void meteocardselected(long spotID, MeteoStationData meteoStationData) {

        mCallback.onSpotClick(spotID);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    MeteoStationData getMeteoDataFromId(long id) {
        if (meteoDataList == null)
            return null;
        Iterator iterator = meteoDataList.iterator();
        while (iterator.hasNext()) {
            MeteoStationData md = (MeteoStationData) iterator.next();
            if (md.spotID == id)
                return md;
        }
        return null;
    }

    public void refreshMeteoData() {

        if (viewCreated == false)
            return;

        mcontainer.removeAllViews();

        if (meteoDataList == null)
            return;

        for (MeteoStationData md : meteoDataList) {
            MeteoCardItem carditem = new MeteoCardItem(this, getActivity(), mcontainer);
            mcontainer.addView(carditem.getCard());
            carditem.setSourceUrl(md.source);
            carditem.setTitle(md.spotName);
            /*if (md.offline) {
                 carditem.card.setTitle("offline");
            }*/
            carditem.update(md);
            carditem.setSpotId(md.spotID);
        }
        mcontainer.invalidate();



        /*MainActivity a = (MainActivity) getActivity();
        List<Spot> favorites = a.getFavorites();

        for (Spot spot : favorites) {

            MeteoCardItem carditem = new MeteoCardItem(this, getActivity(), mcontainer);
            mcontainer.addView(carditem.getCard());
            carditem.setSourceUrl(spot.sourceUrl);
            carditem.setTitle(spot.spotName);
            MeteoStationData md = getMeteoDataFromId(spot.id);
            if (md != null)
                carditem.update(md);
            carditem.setSpotId(spot.id);
        }
        mcontainer.invalidate();
        */
    }
}