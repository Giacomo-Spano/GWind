package wind.newwindalarm.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wind.newwindalarm.R;
import wind.newwindalarm.SplashActivity;
import wind.newwindalarm.Spot;
import wind.newwindalarm.data.WindForecast;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface MeteoForecastListener {

    public void onClickCheckBox(Spot spot, boolean selected);
    public void onClick(Spot spot);


    class MeteoForecastArrayAdapter extends ArrayAdapter<String> implements Filterable {
        private final Context context;
        private List<Item> list = new ArrayList<>();
        WindForecast forecast;
        MeteoForecastListener mListener;

        private class Item {

            public boolean header;
            public int index;

            public Date datetime;
            public Double speed;
            public Double maxSpeed;
            public Double speedDir;
            public Double temperature;
            public Double maxtemperature;
            public Double mintemperature;
            public Integer humidity;
            public String weather;
            public String weatherdescription;
            public String icon;
            public Integer cloudPercentage;

        }


        public MeteoForecastArrayAdapter(Context context, /*List<String> list*/WindForecast forecast, MeteoForecastListener listener) {

            super(context, R.layout.forecastrowitemlayout);//, forecast.weathers);
            mListener = listener;
            this.context = context;

            this.forecast = forecast;
            int oldday = -1;
            int oldmonth = -1;
            int oldyear = -1;

            for (int i = 0; i < forecast.datetimes.size(); i++) {



                Date date = forecast.datetimes.get(i); // your date
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                if (i == 0 || day != oldday || month != month || year != oldyear) {

                    Item item = new Item();
                    item.header = true;
                    list.add(item);

                }

                oldday = day;
                oldmonth = month;
                oldyear = year;

                Item item = new Item();
                item.header = false;
                item.index = i;
                list.add(item);


                    /*if (forecast.datetimes != null && forecast.datetimes.size() > 0)
                        item.datetime = forecast.datetimes.get(i);

                    if (forecast.speeds != null && forecast.speeds.size() > 0)
                        item.speed = forecast.speeds.get(i);

                    if (forecast.maxSpeeds != null && forecast.maxSpeeds.size() > 0)
                        item.maxSpeed = forecast.maxSpeeds.get(i);

                    if (forecast.speedDirs != null && forecast.speedDirs.size() > 0)
                        item.speedDir = forecast.speedDirs.get(i);

                    if (forecast.temperatures != null && forecast.temperatures.size() > 0)
                        item.temperature = forecast.temperatures.get(i);

                    if (forecast.maxtemperatures != null && forecast.maxtemperatures.size() > 0)
                        item.maxtemperature = forecast.maxtemperatures.get(i);

                    if (forecast.mintemperatures != null && forecast.mintemperatures.size() > 0)
                        item.mintemperature = forecast.mintemperatures.get(i);

                    if (forecast.humidities != null && forecast.humidities.size() > 0)
                        item.humidity = forecast.humidities.get(i);

                    if (forecast.weathers != null && forecast.weathers.size() > 0)
                        item.weather = forecast.weathers.get(i);

                    if (forecast.weatherdescriptions != null && forecast.weatherdescriptions.size() > 0)
                        item.weatherdescription = forecast.weatherdescriptions.get(i);

                    if (forecast.icons != null && forecast.icons.size() > 0)
                        item.icon = forecast.icons.get(i);

                    if (forecast.cloudPercentages != null && forecast.cloudPercentages.size() > 0)
                        item.cloudPercentage = forecast.cloudPercentages.get(i);*/

                }


        }

        @Override
        public int getCount() {

            //return forecast.weathers.size();
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            /*if (position >= forecast.weathers.size())
                return null;*/

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            Item item = list.get(position);
            if (item.header) {

                rowView = inflater.inflate(R.layout.forecastrowheaderlayout, parent, false);

            } else {


                rowView = inflater.inflate(R.layout.forecastrowitemlayout, parent, false);

                TextView tv = (TextView) rowView.findViewById(R.id.weatherTextView);
                tv.setText(forecast.weathers.get(position));


                SimpleDateFormat timef = new SimpleDateFormat("HH:mm");
                SimpleDateFormat datef = new SimpleDateFormat("dd-MM-yyyy");
                String hour = timef.format(forecast.datetimes.get(position));
                String day = datef.format(forecast.datetimes.get(position));
                tv = (TextView) rowView.findViewById(R.id.dayTextView);
                tv.setText(day);
                tv = (TextView) rowView.findViewById(R.id.hourTextView);
                tv.setText(hour);

                ImageView iv = (ImageView) rowView.findViewById(R.id.weatherImageView);

                //String uri = "@drawable/m01d";
                String uri = "@drawable/m" + forecast.icons.get(position);
                int imageResource = parent.getResources().getIdentifier(uri, null, SplashActivity.getContext().getPackageName());
                Drawable res = parent.getResources().getDrawable(imageResource);
                iv.setImageDrawable(res);

                rowView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        long spotId = (long) v.getTag();
                        Spot s = getSpotFromId(spotId);
                        if (s != null)
                            mListener.onClick(s);
                    }
                });

            }
            return rowView;
        }

        private Spot getSpotFromId(long spotId) {
            /*for (Spot s : filteredList) {
                if (s.id == spotId)
                    return s;
            }*/
            return null;
        }

        private int getImage(String id) {
            switch (id) {
                case "01d":
                    return R.drawable.m01d;
                case "02d":
                    return R.drawable.m02d;
                case "03d":
                    return R.drawable.m03d;
                case "04d":
                    return R.drawable.m04d;
                case "09d":
                    return R.drawable.m09d;
                case "10d":
                    return R.drawable.m10d;
                case "11d":
                    return R.drawable.m11d;
                case "13d":
                    return R.drawable.m13d;
                case "50d":
                    return R.drawable.m50d;
                case "01n":
                    return R.drawable.m01n;
                case "02n":
                    return R.drawable.m02n;
                case "03n":
                    return R.drawable.m03n;
                case "04n":
                    return R.drawable.m04n;
                case "09n":
                    return R.drawable.m09n;
                case "10n":
                    return R.drawable.m10n;
                case "11n":
                    return R.drawable.m11n;
                case "13n":
                    return R.drawable.m13n;
                case "50n":
                    return R.drawable.m50n;

                default:
                    return -1;
            }

        }
    }


}
