package gwind.windalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gwind.windalarm.fragment.MeteoItem;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface MeteoItemListListener {

    public void onClickCheckBox(int position, boolean selected);

    public void onClick(long spotId);

    class MeteoItemListArrayAdapter extends ArrayAdapter<MeteoItem> {
        private final Context context;
        MeteoItemListListener mListener;

        public MeteoItemListArrayAdapter(Context context, List<MeteoItem> list, MeteoItemListListener listener) {

            super(context, R.layout.meteoitemrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public int getViewTypeCount() {
            return 2; //return 2, you have two types that the getView() method will return, normal(0) and for the last row(1)
        }

        @Override
        public int getItemViewType(int position) {
            MeteoItem item = getItem(position);
            return item.type;
            //return (position == 0) ? 0 : 1; //if we are at the last position then return 1, for any other position return 0
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            int type = getItemViewType(position);
            if (v == null) {
                MeteoItem item = getItem(position);
                // Inflate the layout according to the view type
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (type == 0) {
                    // Inflate the layout with image
                    v = inflater.inflate(R.layout.meteoitemrowlayout, parent, false);
                    TextView descriptioTextView = (TextView) v.findViewById(R.id.descriptionTextView);
                    descriptioTextView.setText(item.description);
                }
                else {
                    v = inflater.inflate(R.layout.meteoitemheaderrowlayout, parent, false);
                    TextView descriptioTextView = (TextView) v.findViewById(R.id.descriptionTextView);
                    descriptioTextView.setText(item.description);
                    TextView valueTextView = (TextView) v.findViewById(R.id.valueTextView);
                    valueTextView.setText(item.value);
                }
            }
            //




            return v;
        }
    }
}
