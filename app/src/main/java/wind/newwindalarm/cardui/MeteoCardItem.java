package wind.newwindalarm.cardui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;



public class MeteoCardItem {

    public long spotID;
    public MeteoStationData meteoStationData;
    public MeteoCard card;
    MeteoCardListener listener;



    public MeteoCardItem(MeteoCardListener ml, final Activity activity, LinearLayout container) {


        listener = ml;
        card = (MeteoCard) activity.getLayoutInflater().inflate(R.layout.card_meteo, container, false);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.meteocardselected(spotID,meteoStationData);
                // Create new fragment and transaction
            }
        });

        card.init();
    }

    public void update(MeteoStationData data) {

        meteoStationData = new MeteoStationData(data);

        if (data.speed != null) {
            card.setSpeed("" + data.speed);
        } else {
            card.setSpeed("--");
        }
        card.setSpeed(data.speed);

        if (data.directionangle != null) {
            card.setDirection(data.directionangle,data.direction);
        } else {
            card.setDirection(0.0,"--");
        }
        card.setTrend(data.trend);

        if (data.averagespeed != null) {
            card.setAvSpeed("" + data.averagespeed);
        } else {
            card.setAvSpeed("--");
        }
        if (data.temperature != null) {
            card.setTemperature("" + data.temperature);
        } else {
            card.setTemperature("--");
        }
        if (data.humidity != null) {
            card.setHumidity("" + data.humidity);
        } else {
            card.setHumidity("--");
        }
        if (data.pressure != null) {
            card.setPressure("" + data.pressure);
        } else {
            card.setPressure("--");
        }
        if (data.rainrate != null) {
            card.setRainRate("" + data.rainrate);
        } else {
            card.setRainRate("--");
        }
        card.setDate(data.date/* + " " + data.time*/);
    }
}