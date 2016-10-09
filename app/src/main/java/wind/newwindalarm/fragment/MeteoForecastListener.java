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
import java.util.Locale;

import wind.newwindalarm.R;
import wind.newwindalarm.SplashActivity;
import wind.newwindalarm.data.Forecast;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface MeteoForecastListener {

    //void onClickCheckBox(Spot spot, boolean selected);
    void onClick();

    class MeteoForecastArrayAdapter extends ArrayAdapter<String> implements Filterable {
        private final Context context;
        private List<Item> list = new ArrayList<>();
        Forecast forecast;
        MeteoForecastListener mListener;

        private class Item {

            public boolean header;
            public int index;
            public Date datetime = null;

        }

        public MeteoForecastArrayAdapter(Context context, /*List<String> list*/Forecast forecast, MeteoForecastListener listener) {

            super(context, R.layout.forecastrowitemlayout);//, forecast.weathers);
            mListener = listener;
            this.context = context;
            this.forecast = forecast;

            if (forecast == null) return;

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

                if (i == 0 || day != oldday || month != oldmonth || year != oldyear) {

                    Item item = new Item();
                    item.header = true;
                    item.index = -1;
                    item.datetime = date;
                    list.add(item);

                }
                oldday = day;
                oldmonth = month;
                oldyear = year;

                Item item = new Item();
                item.header = false;
                item.index = i;

                list.add(item);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            Item item = list.get(position);
            if (item.header) {
                rowView = inflater.inflate(R.layout.forecastrowheaderlayout, parent, false);
                TextView tv = (TextView) rowView.findViewById(R.id.dayTextView);
                SimpleDateFormat df = new SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.ITALY);
                String strDate = df.format(item.datetime);
                tv.setText(strDate);

            } else {

                rowView = inflater.inflate(R.layout.forecastrowitemlayout, parent, false);

                TextView tv = (TextView) rowView.findViewById(R.id.weatherTextView);
                if (forecast.weatherdescriptions.size() > item.index) {
                    tv.setText(forecast.weatherdescriptions.get(item.index));
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.hourTextView);
                if (forecast.datetimes.size() > item.index) {
                    SimpleDateFormat timef = new SimpleDateFormat("HH:mm");
                    String hour = timef.format(forecast.datetimes.get(item.index));
                    tv.setText(hour);
                } else {
                    tv.setText("--:---");
                }

                tv = (TextView) rowView.findViewById(R.id.temperatureTextView);
                if (forecast.temperatures.size() > item.index) {
                    tv.setText(forecast.temperatures.get(item.index).toString() + "°C");
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.maxTemperature);
                if (forecast.maxtemperatures.size() > item.index) {
                    tv.setText(forecast.maxtemperatures.get(item.index).toString() + "°C");
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.minTemperature);
                if (forecast.mintemperatures.size() > item.index) {
                    tv.setText(forecast.mintemperatures.get(item.index).toString() + "°C");
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.humidityTextView);
                if (forecast.humidities.size() > item.index) {
                    tv.setText(forecast.humidities.get(item.index).toString() + "%");
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.pressureTextView);
                if (forecast.pressures.size() > item.index) {
                    tv.setText(forecast.pressures.get(item.index).toString() + "hPa");
                } else {
                    tv.setText("----");
                }

                tv = (TextView) rowView.findViewById(R.id.windTextView);
                if (forecast.speeds.size() > item.index) {
                    tv.setText(forecast.speeds.get(item.index).toString() + "km/h");
                } else {
                    tv.setText("----");
                }


                ImageView iv = (ImageView) rowView.findViewById(R.id.weatherImageView);

                if (forecast.speeds.size() > item.index) {
                    String uri = "@drawable/m" + forecast.icons.get(item.index);
                    int imageResource = parent.getResources().getIdentifier(uri, null, SplashActivity.getContext().getPackageName());
                    Drawable res = parent.getResources().getDrawable(imageResource);
                    iv.setImageDrawable(res);
                }

                rowView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mListener.onClick();
                    }
                });

            }
            return rowView;
        }

            }


}
