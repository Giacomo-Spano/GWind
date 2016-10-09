package wind.newwindalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.data.Location;

/**
 * Created by giacomo on 19/07/2015.
 */
public interface SearchMeteoForecastListener {

    //public void onClickCheckBox(Spot spot, boolean selected);
    void onClick(Location location);

    //void setListener(MainActivity mainActivity);



    class SearchSpotArrayAdapter extends ArrayAdapter<Spot> implements Filterable {
        private final Context context;
        private List<Location> list;
        private List<Location> filteredList;
        SearchMeteoForecastListener mListener;

        public SearchSpotArrayAdapter(Context context, List<Location> list, SearchMeteoForecastListener listener) {

            super(context, R.layout.searchspotlistrowlayout/*, list*/);
            mListener = listener;
            this.context = context;
            this.list = new ArrayList<>();
            filteredList = list;
        }

        @Override
        public int getCount() {

            return filteredList.size();
        }

        public void setList(List<Location> list) {

            this.list = list;
            filteredList = list;

            if (list.size() == 0)
                notifyDataSetInvalidated();
            else {
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (position >= filteredList.size())
                return null;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.searchspotlistrowlayout, parent, false);
            //rowView.setTag(getItem(position).id);
            rowView.setTag(filteredList.get(position).id);

            TextView textView = (TextView) rowView.findViewById(R.id.spotNameTextView);
            //textView.setText(getItem(position).spotName);
            textView.setText(filteredList.get(position).name);

            CheckBox checkBox = (CheckBox) rowView.findViewById((R.id.favoritecheckBox));
            checkBox.setTag(position);
            checkBox.setChecked(false/*filteredList.get(position).favorites*/);
            checkBox.setTag(filteredList.get(position).id);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Location l = filteredList.get(position);
                    mListener.onClick(l);
                }
            });
            return rowView;
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

                        for (Location s : list) {
                            if (s.name.toUpperCase().contains(constraint.toString().toUpperCase()))
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
                        filteredList = (List<Location>) results.values;
                        notifyDataSetChanged();
                    }
                }

            };
        }
    }
}
