package gwind.windalarm.fragment;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
//import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import gwind.windalarm.AsyncRequestMeteoDataResponse;
import gwind.windalarm.MainActivity;
import gwind.windalarm.data.Location;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;
import gwind.windalarm.Spot;
import gwind.windalarm.SpotMeteoListListener;
import gwind.windalarm.data.Forecast;
import gwind.windalarm.request.requestMeteoDataTask;

public class SpotMeteoListFragment extends ListFragment implements SpotMeteoListListener {

    private String userid;
    // Container Activity must implement this interface
    public interface OnSpotMeteoListListener {
        void onSpotListChangeSelection(List<Long> list);
    }

    OnSpotMeteoListListener mListener;
    List<Spot> mSpotList;

    public void setUserId(String userid) {
        this.userid = userid;
    }

    public void setListener(OnSpotMeteoListListener listener) {
        mListener = listener;
    }

    public void setSpotList(List<Spot> list) {

        mSpotList = list;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSpotListFromServer(this);
    }

    private void getSpotListFromServer(final SpotMeteoListListener listener) {



        new requestMeteoDataTask(getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<MeteoStationData> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage) {

                ArrayList<Spot> spotList = new ArrayList<Spot>();

                if (error) {
                    ((MainActivity)getActivity()).showError(errorMessage);
                    return;
                }

                List<Spot> sl = new ArrayList<Spot>();
                for (Spot spot : list) {

                    if (favorites != null) {
                        spot.favorites = false;
                        for (Long spotId : favorites) {
                            if (spotId == spot.id)
                                spot.favorites = true;
                        }
                    }
                    sl.add(spot);
                }
                SpotMeteoListArrayAdapter adapter = new SpotMeteoListArrayAdapter(getActivity(), sl, listener);
                setListAdapter(adapter);
            }

            @Override
            public void processFinishAddFavorite(long spotId, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishRemoveFavorite(long spotId, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishForecast(int requestId, Forecast forecast, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishForecastLocation(List<Location> locations, boolean error, String errorMessage) {

            }

        },requestMeteoDataTask.REQUEST_SPOTLIST_FULLINFO).execute(userid);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        long spotId = mSpotList.get((int) id).id;
        showSpotDetail(spotId);
    }

    @Override
    public void onClickCheckBox(int position, boolean selected) {
        SpotMeteoListArrayAdapter adapter = (SpotMeteoListArrayAdapter) getListAdapter();
        adapter.getItem(position).enabled = selected;
        long spotId = adapter.getItem(position).id;


        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String url = sharedPreferences.getString(QuickstartPreferences.KEY_PREF_SERVERURL, getActivity().getResources().getString(R.string.pref_serverURL_default));

        MainActivity a = (MainActivity) getActivity();
        if (selected) {
            a.addToFavorites(spotId);
            //AlarmPreferences.addToSpotListFavorites(getActivity(), spotId);
        } else {
            a.removeFromFavorites(spotId);
            //AlarmPreferences.deleteFromSpotListFavorites(getActivity(), spotId);
        }

        /*Set<String> favorites = AlarmPreferences.getSpotListFavorites(getActivity());
        String list = "";
        int count = 0;
        for (String str : favorites) {

            if (count++ != 0)
                list += ",";
            list += str;
        }
        updateFavorites(list);*/
    }

    /*public void updateFavorites(String favorites) {

        MainActivity a = (MainActivity) getActivity();
        a.updateFavorites(favorites);
    }*/

    @Override
    public void onClick(long spotId) {
        showSpotDetail(spotId);
    }

    public void showSpotDetail(long spotID) {
        Fragment spotDetail = new SpotDetailsFragment();

        Bundle data = new Bundle();
        data.putLong("spotID", spotID);
        //data.putString("meteodata", meteoStationData.toJson());
        spotDetail.setArguments(data);

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        android.support.v4.app.FragmentManager fragmentManager = ((MainActivity)getActivity()).getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();


        transaction.replace(R.id.content_frame, spotDetail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}