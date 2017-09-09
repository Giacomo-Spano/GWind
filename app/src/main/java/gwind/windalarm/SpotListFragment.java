package gwind.windalarm;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SpotListFragment extends ListFragment implements SpotListListener {

    // Container Activity must implement this interface
    public interface OnSpotListListener {
        //public void onSpotListChangeOrder(List<Long> order);
        /*public */void onSpotListChangeSelection(List<Long> list);
    }

    OnSpotListListener mListener;
    List<Spot> mSpotList;

    public void setListener(OnSpotListListener listener) {
        mListener = listener;
    }

    public void setSpotList(List<Spot> list) {

        mSpotList = list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Spot> sl = mSpotList;//MainActivity.getSpotListFavorites();
        SpotListArrayAdapter adapter = new SpotListArrayAdapter(getActivity(), sl, this);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }



    @Override
    public void onClickCheckBox(int position, boolean selected) {
        //mListener.onSpotListChangeSelection(position, selected);
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

        mListener.onSpotListChangeSelection(list);/////
    }

}