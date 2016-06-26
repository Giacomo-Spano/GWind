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
            return;;
        List<Spot> sl = mSpotList;
        SpotListArrayAdapter adapter = new SpotListArrayAdapter(getActivity(), sl, this);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        long spotId = mSpotList.get((int) id).id;
        showSpotDetail(spotId);


    }

    @Override
    public void onClickCheckBox(int position, boolean selected) {
        SpotListArrayAdapter adapter = (SpotListArrayAdapter) getListAdapter();
        adapter.getItem(position).enabled = selected;
        sendList();
    }

    public void sendList() {

        List<Long> list = new ArrayList<Long>();
        SpotListArrayAdapter adapter = (SpotListArrayAdapter) getListAdapter();
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