package gwind.windalarm.fragment;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import java.util.ArrayList;
import java.util.List;
import gwind.windalarm.MeteoItemListListener;
import gwind.windalarm.data.MeteoStationData;

public class MeteoItemListFragment extends ListFragment implements /*MeteoItemListListener,*/ SpotDetailsFragmentInterface {

    private long spotID;
    private MeteoStationData meteoData;

    /*@Override
    public void onClickCheckBox(int position, boolean selected) {

    }

    @Override
    public void onClick(long spotId) {

    }*/

    /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setDivider(new ColorDrawable(Color.YELLOW));
        listView.setDividerHeight(3); // 3 pixels height
    }*/

    public boolean adaptercreated = false;

    public interface OnMeteoItemListListener {
        void onSpotListChangeSelection(List<Long> list);
    }

    MeteoItemListListener listener;

    public void setListener(MeteoItemListListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        createAdapter();
        refreshData();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        /*refreshFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.refreshbutton));
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onRefreshMeteoDataRequest();
            }
        });*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void createAdapter() {
        adaptercreated = true;
    }

    public void destroyAdapter() {
        adaptercreated = false;
    }


    public void refreshData() {

        if (!adaptercreated)
            return;

        List<MeteoItem> list = new ArrayList<>();
        int count = 0;

        // vento
        MeteoItem mi = new MeteoItem();
        mi.type = 0;
        mi.description = "Vento";
        list.add(mi);
        String unit = "Km/h";
        // wind speed
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Velocità vento";
        mi.value = "" + meteoData.speed + " " + unit;
        list.add(mi);
        // wind av speed
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Velocità media";
        mi.value = "" + meteoData.speed + " " + unit;
        list.add(mi);
        // wind direction
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Direzione";
        mi.value = "" + meteoData.direction;
        list.add(mi);
        // wind max
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Raffica max odierna";
        if (meteoData.maxTodaySpeed != null && meteoData.maxTodaySpeedDatetime != null) {
            mi.value = "" + meteoData.maxTodaySpeed + " " + unit;;
            mi.date = meteoData.maxTodaySpeedDatetime;
        }
        list.add(mi);
        // wind max
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Raffica max mese";
        //mi.date
        mi.value = "";
        list.add(mi);

        // temperature
        mi = new MeteoItem();
        mi.type = 0;
        mi.description = "Temperatura";
        list.add(mi);
        unit = "°C";
        // temperatura
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Temperatura";
        mi.value = "" + meteoData.temperature + " " + unit;
        list.add(mi);
        // temperatura massima
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Temperatura max";
        mi.value = "" + 0 + " " + unit;
        list.add(mi);
        // temperatura minima
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Temperatura min.";
        mi.value = "" + 0 + " " + unit;
        list.add(mi);
        // Humidity and pressure
        mi = new MeteoItem();
        mi.type = 0;
        mi.description = "Umidità e pressione";
        list.add(mi);
        // Humidity
        unit = "%";
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Umidità";
        mi.value = "" + meteoData.humidity + "" + unit;
        list.add(mi);
        // pressure
        mi = new MeteoItem();
        mi.type = 1;
        unit = "hPa";
        mi.description = "Pressione";
        mi.value = "" + meteoData.pressure + " " + unit;
        list.add(mi);
        // Rainrate
        mi = new MeteoItem();
        mi.type = 0;
        mi.description = "Precipitazioni";
        list.add(mi);
        // rainrate
        unit = "mm";
        mi = new MeteoItem();
        mi.type = 1;
        mi.description = "Rainrate";
        mi.value = "" + meteoData.rainrate + "" + unit;
        list.add(mi);
        // daily rainrate
        mi = new MeteoItem();
        mi.type = 1;
        unit = "mm";
        mi.description = "Pioggia odierna";
        mi.value = "" + 0 + " " + unit;
        list.add(mi);


        MeteoItemListListener.MeteoItemListArrayAdapter adapter = new MeteoItemListListener.MeteoItemListArrayAdapter(getActivity(), list, listener);
        setListAdapter(adapter);

    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
        refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }
}
