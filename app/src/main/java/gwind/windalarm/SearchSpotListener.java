package gwind.windalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SearchSpotListener {

    public void onClickCheckBox(Spot spot, boolean selected);
    public void onClick(Spot spot);


    class SearchSpotArrayAdapter extends ArrayAdapter<Spot> implements Filterable {
        private final Context context;
        private List<Spot> list;
        private List<Spot> filteredList;
        SearchSpotListener mListener;

        public SearchSpotArrayAdapter(Context context, List<Spot> list, SearchSpotListener listener) {

            super(context, R.layout.searchspotlistrowlayout);
            mListener = listener;
            this.context = context;
            this.list = list;
            filteredList = list;
        }

        @Override
        public int getCount() {

            return filteredList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (position >= filteredList.size())
                return null;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.searchspotlistrowlayout, parent, false);
            rowView.setTag(filteredList.get(position).id);

            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            textView.setText(filteredList.get(position).spotName);


            CheckBox checkBox = (CheckBox) rowView.findViewById((R.id.favoritecheckBox));
            //checkBox.setTag(position);
            checkBox.setChecked(filteredList.get(position).favorites);
            //checkBox.setTag(filteredList.get(position).id);
            /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    long spotId = (long) buttonView.getTag();
                    Spot s = getSpotFromId(spotId);
                    if (s != null)
                        mListener.onClickCheckBox(s, isChecked);
                }
            });*/

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    long spotId = (long) v.getTag();
                    Spot s = getSpotFromId(spotId);
                    if (s != null)
                        mListener.onClick(s);
                }
            });
            return rowView;
        }

        private Spot getSpotFromId(long spotId) {
            for (Spot s : filteredList) {
                if (s.id == spotId)
                    return s;
            }
            return null;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                /* (non-Javadoc)
                 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
                 */
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    /*
                    * Here, you take the constraint and let it run against the array
                    * You return the result in the object of FilterResults in a form
                    * you can read later in publichResults.
                    */
                    FilterResults results = new FilterResults();
                    // We implement here the filter logic
                    if (constraint == null || constraint.length() == 0) {
                        // No filter implemented we return all the list
                        results.values = list;
                        results.count = list.size();
                    } else {
                        // We perform filtering operation
                        /*List<Spot> */filteredList = new ArrayList<>();

                        for (Spot s : list) {
                            if (s.spotName.toUpperCase().contains(constraint.toString().toUpperCase()))
                                filteredList.add(s);
                        }

                        results.values = filteredList;
                        results.count = filteredList.size();

                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // TODO Auto-generated method stub
                    /*
                     * Here, you take the result, put it into Adapters array
                     * and inform about the the change in data.
                     */
                    // Now we have to inform the adapter about the new list filtered
                    if (results.count == 0)
                        notifyDataSetInvalidated();
                    else {
                        filteredList = (List<Spot>) results.values;
                        notifyDataSetChanged();
                    }
                }

            };
        }
    }
}
