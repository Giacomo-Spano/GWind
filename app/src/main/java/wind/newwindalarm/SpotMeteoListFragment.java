package wind.newwindalarm;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SpotMeteoListFragment extends ListFragment implements SpotMeteoListListener {

    // Container Activity must implement this interface
    public interface OnSpotMeteoListListener {
        public void onSpotListChangeSelection(List<Long> list);
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

        if (mSpotList == null)
            return;


        getSpotListFromServer(this);
        /*List<Spot> sl = mSpotList;
        SpotMeteoListArrayAdapter adapter = new SpotMeteoListArrayAdapter(getActivity(), sl, this);
        setListAdapter(adapter);*/
    }

    private void getSpotListFromServer(final SpotMeteoListListener listener) {

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
                    sl.add(spot);
                }
                SpotMeteoListArrayAdapter adapter = new SpotMeteoListArrayAdapter(getActivity(), sl, listener);
                setListAdapter(adapter);
                //spotMeteoListFragment.setSpotList(spotList);
            }
        }).execute(requestMeteoDataTask.REQUEST_SPOTLIST);
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
        sendList();
    }

    public void sendList() {

        List<Long> list = new ArrayList<Long>();
        SpotMeteoListArrayAdapter adapter = (SpotMeteoListArrayAdapter) getListAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            if (adapter.getItem(i).enabled)
                list.add(adapter.getItem(i).id);
        }

        mListener.onSpotListChangeSelection(list);
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