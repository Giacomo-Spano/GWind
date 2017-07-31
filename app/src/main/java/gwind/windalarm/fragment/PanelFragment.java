package gwind.windalarm.fragment;


//import android.app.Fragment;
import android.support.v4.app.Fragment;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import gwind.windalarm.MainActivity;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;
import gwind.windalarm.cardui.MeteoCardItem;
import gwind.windalarm.cardui.MeteoCardListener;

public class PanelFragment extends Fragment implements OnItemSelectedListener, MeteoCardListener {

    private boolean viewCreated = false;
    OnListener mCallback;
    private LinearLayout mcontainer;
    private MeteoDataList meteoDataList = new MeteoDataList();

    private class MeteoDataList {
        public List<MeteoStationData> list = new ArrayList<MeteoStationData>();
    }

    // Container Activity must implement this interface
    public interface OnListener {
        void onSpotClick(long spotId);
        void onEnablePanelRefreshButtonRequest();
    }

    public void setListener(OnListener listener) {
        mCallback = listener;
    }

    public void setMeteoDataList(List<MeteoStationData> list) {
        meteoDataList.list = list;
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnListener) context;
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
                mCallback = (OnListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnAlarmListener");
            }
        }
    }*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Gson gson = new Gson();
            String gsonString = savedInstanceState.getString("meteoDataList");
            meteoDataList = gson.fromJson(gsonString, MeteoDataList.class);
            MainActivity ma = (MainActivity) getActivity();
            mCallback = ma.getPanelListener();
            mCallback.onEnablePanelRefreshButtonRequest();
        }

        View v;
        v = inflater.inflate(R.layout.fragment_controlpanel, container, false);

        mcontainer = (LinearLayout) v.findViewById(R.id.meteolist);

        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Stazioni meteo"/*items[position]*/);

        viewCreated = true;
        refreshMeteoData();
        mCallback.onEnablePanelRefreshButtonRequest();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new Gson();
        String myJson = gson.toJson(meteoDataList);
        outState.putString("meteoDataList", myJson);
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

    /*MeteoStationData getMeteoDataFromId(long id) {
        if (meteoDataList == null)
            return null;
        Iterator iterator = meteoDataList.iterator();
        while (iterator.hasNext()) {
            MeteoStationData md = (MeteoStationData) iterator.next();
            if (md.spotID == id)
                return md;
        }
        return null;
    }*/

    public void refreshMeteoData() {

        if (viewCreated == false)
            return;

        mcontainer.removeAllViews();

        synchronized (meteoDataList) {
            if (meteoDataList == null || meteoDataList.list == null || meteoDataList.list.size() == 0)
                return;

            for (MeteoStationData md : meteoDataList.list) {
                if (md == null) {
                    // qui c'è un bug. Per qualche motivo quanlche volta al riavvio ci può essere un elemento null
                    continue;
                }
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
        }
    }
}