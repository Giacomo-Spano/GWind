package wind.newwindalarm.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.R;
import wind.newwindalarm.SearchMeteoForecastListener;
import wind.newwindalarm.SearchSpotListener;
import wind.newwindalarm.Spot;
import wind.newwindalarm.SpotList;
import wind.newwindalarm.data.Location;

/**
 * Created by Giacomo Spanò on 09/09/2016.
 */
public class SearchMeteoForecastFragment extends Fragment implements SearchMeteoForecastListener {

    private ListView lv;
    SearchMeteoForecastListener.SearchSpotArrayAdapter adapter;
    EditText inputSearch;
    Button searchButton;

    private List<Location> list = new ArrayList<>();

    OnSearchSpotClickListener mCallback;

    // Container Activity must implement this interface
    public interface OnSearchSpotClickListener {
        void onSearchLocationClick(String filter);
        void onSearchLocationSelectLocation(Location location);
    }

    public void setListener(OnSearchSpotClickListener listener) {
        mCallback = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
        outState.putSerializable("list", (Serializable) list);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null) {

            list = (List<Location>) savedInstanceState.getSerializable("list");

            MainActivity ma = (MainActivity) getActivity();
            mCallback = ma.getSearchMeteoForecastListener();
        }


        View v;
        v = inflater.inflate(R.layout.fragment_searchmeteoforecast, container, false);

        lv = (ListView) v.findViewById(R.id.list_view);

        searchButton = (Button) v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str = inputSearch.getText().toString();
                mCallback.onSearchLocationClick(str);
            }
        });


        inputSearch = (EditText) v.findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (cs.length() >= 3) {
                    searchButton.setEnabled(true);
                } else {
                    searchButton.setEnabled(false);
                }
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

        adapter = new SearchMeteoForecastListener.SearchSpotArrayAdapter(getActivity(), list, this);
        adapter.setList(list);

        lv.setAdapter(adapter);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setSpotList(List<Location> list) {

        this.list = list;
        adapter.setList(list);
    }

    @Override
    public void onClick(Location location) {

        // nascondi la tastiera
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);

        mCallback.onSearchLocationSelectLocation(location);
    }
}
