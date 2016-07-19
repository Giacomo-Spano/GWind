package wind.newwindalarm;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SpotMeteoListFragment extends ListFragment implements SpotMeteoListListener {

    // Container Activity must implement this interface
    public interface OnSpotMeteoListListener {
        /*public */void onSpotListChangeSelection(List<Long> list);
    }

    OnSpotMeteoListListener mListener;
    List<Spot> mSpotList;

    public void setListener(OnSpotMeteoListListener listener) {
        mListener = listener;
    }

    public void setSpotList(List<Spot> list) {

        mSpotList = list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*if (mSpotList == null)
            return;*/


        getSpotListFromServer(this);
        /*List<Spot> sl = mSpotList;
        SpotMeteoListArrayAdapter adapter = new SpotMeteoListArrayAdapter(getActivity(), sl, this);
        setListAdapter(adapter);*/
    }

    private void getSpotListFromServer(final SpotMeteoListListener listener) {

        final Set<String> favorites = AlarmPreferences.getSpotListFavorites(getActivity());

        new requestMeteoDataTask(getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

                ArrayList<Spot> spotList = new ArrayList<Spot>();

                if (error) {
                    ((MainActivity)getActivity()).showError(errorMessage);
                    return;
                }

                List<Spot> sl = new ArrayList<Spot>();
                for (int i = 0; i < list.size(); i++) {
                    Spot spot = (Spot) list.get(i);

                    Iterator iter = favorites.iterator();
                    spot.favorites = false;
                    while (iter.hasNext()) {
                        long id = Long.valueOf((String)iter.next());
                        if (id == spot.id)
                            spot.favorites = true;
                    }
                    sl.add(spot);
                }
                SpotMeteoListArrayAdapter adapter = new SpotMeteoListArrayAdapter(getActivity(), sl, listener);
                setListAdapter(adapter);
            }
        }).execute(requestMeteoDataTask.REQUEST_SPOTLIST_FULLINFO);
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

        if (selected)
            AlarmPreferences.addToSpotListFavorites(getActivity(),spotId);
        else
            AlarmPreferences.deleteFromSpotListFavorites(getActivity(),spotId);
    }

    @Override
    public void onClick(long spotId) {
        showSpotDetail(spotId);
    }

    public void showSpotDetail(long spotID) {
        Fragment spotDetail = new SpotDetailFragment();

        Bundle data = new Bundle();
        data.putLong("spotID", spotID);
        spotDetail.setArguments(data);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, spotDetail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}