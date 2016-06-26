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

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SpotMeteoListListener {

    public void onClickCheckBox(int position, boolean selected);

    class SpotListArrayAdapter extends ArrayAdapter<Spot> {
        private final Context context;
        SpotMeteoListListener mListener;

        public SpotListArrayAdapter(Context context, List<Spot> list, SpotMeteoListListener listener) {

            super(context, R.layout.spotmeteolistrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.spotmeteolistrowlayout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(getItem(position).name);
            TextView idView = (TextView) rowView.findViewById(R.id.spotIdTextView);
            idView.setText("" + getItem(position).id);

            return rowView;
        }
    }
}
