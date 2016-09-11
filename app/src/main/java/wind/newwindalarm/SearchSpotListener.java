package wind.newwindalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SearchSpotListener {

    public void onClickCheckBox(long spotId, boolean selected);

    public void onClick(long spotId);

    class SearchSpotArrayAdapter extends ArrayAdapter<Spot> implements Filterable {
        private final Context context;
        SearchSpotListener mListener;

        public SearchSpotArrayAdapter(Context context, List<Spot> list, SearchSpotListener listener) {

            super(context, R.layout.searchspotlistrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.searchspotlistrowlayout, parent, false);
            rowView.setTag(getItem(position).id);

            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(getItem(position).spotName);

            CheckBox checkBox = (CheckBox) rowView.findViewById((R.id.favoritecheckBox));
            checkBox.setTag(position);
            checkBox.setChecked(getItem(position).favorites);
            checkBox.setTag(getItem(position).id);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    long spotId = (long) buttonView.getTag();
                    boolean selected = ((CheckBox) buttonView).isSelected();
                    mListener.onClickCheckBox(spotId, isChecked);
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
