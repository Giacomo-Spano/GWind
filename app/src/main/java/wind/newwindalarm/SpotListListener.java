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
public interface SpotListListener {
    // you can define any parameter as per your requirement
    //public void onClickMoveUp(int position);
    //public void onClickMoveDown(int position);
    public void onClickCheckBox(int position, boolean selected);

    class SpotListArrayAdapter extends ArrayAdapter<Spot> {
        private final Context context;
        SpotListListener mListener;

        public SpotListArrayAdapter(Context context, List<Spot> list, SpotListListener listener) {

            super(context, R.layout.spotlistrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.spotlistrowlayout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(getItem(position).name);
            TextView idView = (TextView) rowView.findViewById(R.id.spotIdTextView);
            idView.setText("" + getItem(position).id);

            CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.checkBox);
            boolean checked = getItem(position).enabled;
            checkbox.setChecked(checked);
            //checkbox.setChecked(true);


            CheckBox checkBox = (CheckBox) rowView.findViewById((R.id.checkBox));
            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int position = (int) buttonView.getTag();
                    boolean selected = ((CheckBox) buttonView).isSelected();
                    mListener.onClickCheckBox(position, isChecked);
                }
            });



            return rowView;
        }
    }
}
