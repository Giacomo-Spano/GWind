package gwind.windalarm;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SpotOrderFragment extends ListFragment implements SpotOrderListener {

    // Container Activity must implement this interface
    public interface OnSpotOrderListener {
        public void onSpotChangeOrder(List<Long> order);
        //public void onSpotListChangeSelection(int position, boolean selected);
    }

    OnSpotOrderListener mListener;
    List<Spot> mSpotList;

    public void setListener(OnSpotOrderListener listener) {
        mListener = listener;
    }
    public void setSpotList(List<Spot> list) {

        mSpotList = list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Spot> sl = mSpotList;//MainActivity.getSpotListFavorites();

        List<Spot> list = new ArrayList<Spot>();
        for (int i = 0; i < sl.size(); i++) {
            if (sl.get(i).enabled)
                list.add(sl.get(i));
        }
        SpotListArrayAdapter adapter = new SpotListArrayAdapter(getActivity(), list, this);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void onClickMoveUp(int position) {

        SpotListArrayAdapter adapter = (SpotListArrayAdapter) getListAdapter();

        if (position < 1)
            return;

        Spot current = adapter.getItem(position);
        adapter.remove(current);
        adapter.insert(current, position - 1);

        sendListOrder();
    }

    @Override
    public void onClickMoveDown(int position) {

        SpotListArrayAdapter adapter = (SpotListArrayAdapter) getListAdapter();

        if (position >= adapter.getCount()-1)
            return;

        Spot current = adapter.getItem(position);
        adapter.remove(current);
        adapter.insert(current,position+1);

        sendListOrder();

    }


    public void sendListOrder() {

        List<Long> list = new ArrayList<Long>();
        SpotListArrayAdapter adapter = (SpotListArrayAdapter) getListAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {

            //if (adapter.getItem(i).enabled)
                list.add(adapter.getItem(i).id);
        }

        mListener.onSpotChangeOrder(list);
    }

}