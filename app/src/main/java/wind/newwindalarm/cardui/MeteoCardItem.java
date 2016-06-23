package wind.newwindalarm.cardui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;



public class MeteoCardItem {

    public long spotID;
    public String mWecamUrl = "";
    MeteoStationData md;
    public MeteoCard card;
    MeteoCardListener listener;



    public MeteoCardItem(MeteoCardListener ml, final Activity activity, LinearLayout container) {


        listener = ml;
        card = (MeteoCard) activity.getLayoutInflater().inflate(R.layout.card_meteo, container, false);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.meteocardselected(spotID);
                // Create new fragment and transaction
            }
        });

        card.init();
    }

    public void update(MeteoStationData data) {

        md = data;

        card.setTitle(data.spotName);
        card.setSpeed("" + data.speed/* + " km/h"*/ /*+ data.direction + data.directionangle*/);
        card.setSpeed(data.speed);
        card.setDirection(data.directionangle,data.direction);
        card.setTrend(data.trend);
        card.setAvSpeed("" + data.averagespeed/* + " km/h"*/);
        card.setTemperature("" + data.temperature/* + " C"*/);
        card.setHumidity("" +data.humidity/* + " %"*/);
        card.setPressure("" + data.pressure/* + " hPa"*/);
        card.setRainRate("" + data.rainrate /*+ " mm"*/);
        card.setDate(data.date/* + " " + data.time*/);
        //card.setDate(data.date + " " + data.time);

        //mWindControl.setPower(data.speed);
        //mWindControl.setDirection(data.directionangle);

    }
}