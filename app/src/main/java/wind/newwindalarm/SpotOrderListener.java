package wind.newwindalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SpotOrderListener {
    // you can define any parameter as per your requirement
    public void onClickMoveUp(int position);
    public void onClickMoveDown(int position);

    class SpotListArrayAdapter extends ArrayAdapter<Spot> {
        private final Context context;
        SpotOrderListener mListener;

        public SpotListArrayAdapter(Context context, List<Spot> list, SpotOrderListener listener) {

            super(context, R.layout.spotorderrowlayout, list);
            mListener = listener;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.spotorderrowlayout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(getItem(position).spotName);
            TextView idView = (TextView) rowView.findViewById(R.id.spotIdTextView);
            idView.setText("" + getItem(position).id);

            ImageButton btnUP = (ImageButton) rowView.findViewById((R.id.imageButtonUp));
            btnUP.setTag(position);
            btnUP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = (int) view.getTag();
                    mListener.onClickMoveUp(position);
                }
            });

            ImageButton btnDown = (ImageButton) rowView.findViewById((R.id.imageButtonDown));
            btnDown.setTag(position);
            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = (int) view.getTag();
                    mListener.onClickMoveDown(position);
                }
            });

            return rowView;
        }
    }
}
