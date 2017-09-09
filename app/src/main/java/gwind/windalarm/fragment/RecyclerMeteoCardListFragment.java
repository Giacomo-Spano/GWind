package gwind.windalarm.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import gwind.windalarm.MainActivity;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.fragment.RecyclerMeteoCardListAdapter;

public class RecyclerMeteoCardListFragment extends Fragment implements RecyclerMeteoCardListAdapter.OnListener {
    RecyclerMeteoCardListAdapter adapter;

    @Override
    public void onSpotClick(long spotId) {
        mCallback.onSpotClick(spotId);
    }

    private class MeteoDataList {
        public List<MeteoStationData> list = new ArrayList<MeteoStationData>();
    }
    private MeteoDataList meteoDataList = new MeteoDataList();

    OnListener mCallback;
    public interface OnListener {
        void onSpotClick(long spotId);
        void onEnableMeteoCardListRefreshButtonRequest();
    }

    public void setListener(OnListener listener) {
        mCallback = listener;
    }

    public RecyclerMeteoCardListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            Gson gson = new Gson();
            String gsonString = savedInstanceState.getString("meteoDataList");
            meteoDataList = gson.fromJson(gsonString, MeteoDataList.class);
            MainActivity ma = (MainActivity) getActivity();
            //mCallback = ma.getRecyclerMeteoCardListListener();
            mCallback.onEnableMeteoCardListRefreshButtonRequest();
        }

        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            Gson gson = new Gson();
            String gsonString = savedInstanceState.getString("meteoDataList");
            meteoDataList = gson.fromJson(gsonString, MeteoDataList.class);
            MainActivity ma = (MainActivity) getActivity();
            mCallback = ma.getRecyclerMeteoCardListListener();
            mCallback.onEnableMeteoCardListRefreshButtonRequest();
        }

        if (adapter == null) {
            adapter = new RecyclerMeteoCardListAdapter(getActivity());
            adapter.setListener(this);
        }

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Preferiti");

        refreshMeteoData();
        mCallback.onEnableMeteoCardListRefreshButtonRequest();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setMeteoDataList(List<MeteoStationData> list) {
        meteoDataList.list = list;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new Gson();
        String myJson = gson.toJson(meteoDataList);
        outState.putString("meteoDataList", myJson);
    }

    public void refreshMeteoData() { /// chi kla chiama
        if (adapter != null)
            adapter.setMeteoDataList(meteoDataList.list);
    }
}
