package wind.newwindalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.R;
import wind.newwindalarm.SearchSpotListener;
import wind.newwindalarm.Spot;
import wind.newwindalarm.SpotList;

/**
 * Created by Giacomo Spanò on 09/09/2016.
 */
public class SearchMeteoForecastFragment extends Fragment implements SearchSpotListener {

    // List view
    private ListView lv;
    // Listview Adapter
    SearchSpotArrayAdapter adapter;
    //SearchSpotListener mListener;
    SpotList mSpotList;
    // Search EditText
    EditText inputSearch;

    OnSearchSpotClickListener mCallback;

    // Container Activity must implement this interface
    public interface OnSearchSpotClickListener {
        void onSearchSpotClick(Spot spot);
    }

    public void setListener(OnSearchSpotClickListener listener) {
        mCallback = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);




        View v;
        v = inflater.inflate(R.layout.fragment_searchmeteoforecast, container, false);

        lv = (ListView) v.findViewById(R.id.list_view);

        inputSearch = (EditText) v.findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });



        //SpotList spotList = new SpotList();
        //setSpotList(spotList);
        mSpotList = new SpotList();;
        adapter = new SearchSpotArrayAdapter(getActivity(), mSpotList.spotList, this);
        lv.setAdapter(adapter);





        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setSpotList(SpotList spotList) {

/*        mSpotList = spotList;
        adapter = new SearchSpotArrayAdapter(getActivity(), mSpotList.spotList, this);
        lv.setAdapter(adapter);*/
    }

    @Override
    public void onClickCheckBox(Spot spot, boolean selected) {
        MainActivity a = (MainActivity) getActivity();

        if (selected)
            a.addToFavorites(spot.id);
        else
            a.removeFromFavorites(spot.id);
    }

    @Override
    public void onClick(Spot spot) {
        mCallback.onSearchSpotClick(spot);
    }


}
