package gwind.windalarm.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import gwind.windalarm.MainActivity;
import gwind.windalarm.Spot;
import gwind.windalarm.fragment.OnStartDragListener;

public class RecyclerSpotListOrderFragment extends Fragment implements OnStartDragListener {


    private ItemTouchHelper mItemTouchHelper;
    private RecyclerSpotOrderListAdapter adapter;
    OnListener mCallback;

    public RecyclerSpotListOrderFragment() {
    }

    public interface OnListener {
        void onAddSpot(long spotId);
        void onRemoveSpot(long spotId);
        void onEnableAddFavoriteSpotButtonRequest();
    }

    public void setListener(OnListener listener) {
        mCallback = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("prova", "prova");
        //outState.putLong("spotId", spotId);
        //outState.putInt("position", mPager.getCurrentItem());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            /*setMeteoData((MeteoStationData) savedInstanceState.getSerializable("meteoData"));
            setSpotId(savedInstanceState.getLong("spotId"));

            MainActivity ma = (MainActivity) getActivity();
            ma.setSpotDetailsFragment(this);
            mCallback = ma.getSpotDetailsListener();
            int position = savedInstanceState.getInt("position");
            mCallback.onChangeDetailView(position, spotId, meteoData);*/
        }

        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (adapter == null)
            adapter = new RecyclerSpotOrderListAdapter(getActivity(), this/*, mSpotList.spotList*/);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mCallback.onEnableAddFavoriteSpotButtonRequest();

        MainActivity a = (MainActivity) getActivity();
        //((MainActivity) getActivity()).getSpotListFromServer();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onRemove(long spotid) {
        mCallback.onRemoveSpot(spotid);
    }

    public void setSpotList(List<Spot> spotList) {
        //adapter.mItems.
        adapter.setSpotList(spotList);
        //adapter.notifyDataSetChanged();
    }


}
