package gwind.windalarm.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;

import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;
import gwind.windalarm.cardui.InfoCard;

public class SpotDetailsMeteodataFragment extends Fragment implements SpotDetailsFragmentInterface {

    private long spotID;
    private MeteoStationData meteoData;

    private InfoCard speedInfoCard;
    private InfoCard avspeedInfoCard;
    private InfoCard temperatureInfoCard;
    private InfoCard pressureInfoCard;
    private InfoCard humidityInfoCard;
    private InfoCard rainrateInfoCard;
    private InfoCard lastupdateInfoCard;
    private InfoCard sourceInfoCard;
    private InfoCard windIdInfoCard;

    OnClickListener mCallback;
    //private FloatingActionButton refreshFab;

    // Container Activity must implement this interface
    public interface OnClickListener {
        void onRefreshMeteoDataRequest();
    }

    public void setListener(OnClickListener listener) {
        mCallback = listener;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_spotdetails_meteodata, container, false);

        //refreshFab = (FloatingActionButton) getActivity().findViewById(R.id.fabButton);

        speedInfoCard = (InfoCard) v.findViewById(R.id.speedInfoCard);
        speedInfoCard.init();
        speedInfoCard.setImageView(R.drawable.wind);
        speedInfoCard.setTitle("Velocità vento");
        speedInfoCard.setDescription ("");

        avspeedInfoCard = (InfoCard) v.findViewById(R.id.avspeedInfoCard);
        avspeedInfoCard.init();
        avspeedInfoCard.setImageView(R.drawable.wind);
        avspeedInfoCard.setTitle("Velocità media vento");
        avspeedInfoCard.setDescription ("");

        temperatureInfoCard = (InfoCard) v.findViewById(R.id.temperatureInfoCard);
        temperatureInfoCard.init();
        temperatureInfoCard.setImageView(R.drawable.temperature);
        temperatureInfoCard.setTitle("Temperatura");
        temperatureInfoCard.setDescription ("");

        pressureInfoCard = (InfoCard) v.findViewById(R.id.pressureInfoCard);
        pressureInfoCard.init();
        pressureInfoCard.setImageView(R.drawable.pressione);
        pressureInfoCard.setTitle("Pressione atm.");
        pressureInfoCard.setDescription ("");

        humidityInfoCard = (InfoCard) v.findViewById(R.id.humidityInfoCard);
        humidityInfoCard.init();
        humidityInfoCard.setImageView(R.drawable.humidity);
        humidityInfoCard.setTitle("Umidità");
        humidityInfoCard.setDescription ("");

        rainrateInfoCard = (InfoCard) v.findViewById(R.id.rainrateInfoCard);
        rainrateInfoCard.init();
        rainrateInfoCard.setImageView(R.drawable.rainrate2);
        rainrateInfoCard.setTitle("Rain rate")        ;
        rainrateInfoCard.setDescription ("");

        lastupdateInfoCard = (InfoCard) v.findViewById(R.id.lastupdateInfoCard);
        lastupdateInfoCard.init();
        lastupdateInfoCard.setImageView(R.drawable.bell);
        lastupdateInfoCard.setTitle("Ultimo aggiornamento");
        lastupdateInfoCard.setDescription ("");

        sourceInfoCard = (InfoCard) v.findViewById(R.id.sourceInfoCard);
        sourceInfoCard.init();
        sourceInfoCard.setImageView(R.drawable.bell);
        sourceInfoCard.setTitle("Fonte dati");
        sourceInfoCard.setDescription ("");

        windIdInfoCard = (InfoCard) v.findViewById(R.id.windIdInfoCard);
        windIdInfoCard.init();
        windIdInfoCard.setImageView(R.drawable.windlogo);
        windIdInfoCard.setTitle("windid");
        windIdInfoCard.setDescription ("");

        refreshData();
        return v;
    }

    public void refreshData() {

        if (meteoData != null) {

            if (speedInfoCard != null && meteoData.speed != null)
                speedInfoCard.setValue(meteoData.speed + "Km/h");
            if (avspeedInfoCard != null && meteoData.averagespeed != null)
                avspeedInfoCard.setValue(meteoData.averagespeed + "Km/h");

            if (temperatureInfoCard != null && meteoData.temperature != null)
                temperatureInfoCard.setValue(meteoData.temperature + "°C");
            if (pressureInfoCard != null && meteoData.pressure != null)
                pressureInfoCard.setValue(meteoData.pressure + "hPa");
            if (humidityInfoCard != null && meteoData.humidity != null)
                humidityInfoCard.setValue(meteoData.humidity + "%");
            if (rainrateInfoCard != null && meteoData.rainrate != null)
                rainrateInfoCard.setValue(meteoData.rainrate + "mm");
            if (lastupdateInfoCard != null && meteoData.sampledatetime != null) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                lastupdateInfoCard.setDescription(df.format(meteoData.sampledatetime));
                lastupdateInfoCard.setValue("");
            }
            if (sourceInfoCard != null) {
                sourceInfoCard.setDescription("-----");
                sourceInfoCard.setValue("");
            }
            if (windIdInfoCard != null /*&& meteoData.id != null*/) {
                windIdInfoCard.setValue("" + meteoData.id);
            }

        }
        //if (sourceTextView != null)
        //sourceTextView.setText(meteoData. + "Km/h");
    }

    public void setMeteoData(MeteoStationData meteoData) {
        this.meteoData = meteoData;
        refreshData();
    }

    public void setSpotId(long id) {
        spotID = id;
    }
}
