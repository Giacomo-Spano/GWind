package wind.newwindalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import wind.newwindalarm.controls.WindControl;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SpotMeteoListListener {

    public void onClickCheckBox(int position, boolean selected);

    public void onClick(long spotId);

    class SpotMeteoListArrayAdapter extends ArrayAdapter<Spot> {
        private final Context context;
        SpotMeteoListListener mListener;

        public SpotMeteoListArrayAdapter(Context context, List<Spot> list, SpotMeteoListListener listener) {

            super(context, R.layout.spotmeteolistrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.spotmeteolistrowlayout, parent, false);
            rowView.setTag(getItem(position).id);

            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(getItem(position).name);
            TextView idView = (TextView) rowView.findViewById(R.id.spotIdTextView);
            idView.setText("" + getItem(position).id);

            TextView speedView = (TextView) rowView.findViewById(R.id.speedTextView);
            speedView.setText("" + getItem(position).speed);

            WindControl windView = (WindControl) rowView.findViewById(R.id.windcontrol);
            windView.setDirection(getItem(position).directionAngle, getItem(position).direction);
            windView.setPower(getItem(position).speed);

            TextView dateView = (TextView) rowView.findViewById(R.id.dateTextView);
            dateView.setText("" + getItem(position).date);

            CheckBox checkBox = (CheckBox) rowView.findViewById((R.id.favoritecheckBox));
            checkBox.setTag(position);
            checkBox.setChecked(getItem(position).favorites);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int position = (int) buttonView.getTag();
                    boolean selected = ((CheckBox) buttonView).isSelected();
                    mListener.onClickCheckBox(position, isChecked);
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    long spotId = (long) v.getTag();
                    mListener.onClick(spotId);

                }
            });
            return rowView;
        }
    }
}
