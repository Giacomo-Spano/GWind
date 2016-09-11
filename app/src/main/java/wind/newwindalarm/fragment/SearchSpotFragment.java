package wind.newwindalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.R;
import wind.newwindalarm.SearchSpotListener;
import wind.newwindalarm.Spot;
import wind.newwindalarm.SpotList;

/**
 * Created by Giacomo Spanò on 09/09/2016.
 */
public class SearchSpotFragment extends Fragment implements SearchSpotListener {

    // List view
    private ListView lv;

    // Listview Adapter
    SearchSpotListener.SearchSpotArrayAdapter adapter;
    //SearchSpotListener mListener;

    SpotList mSpotList;

    // Search EditText
    EditText inputSearch;


    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    // Listview Data
    String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
            "iPhone 4S", "Samsung Galaxy Note 800",
            "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        v = inflater.inflate(R.layout.fragment_searchspot, container, false);



        lv = (ListView) v.findViewById(R.id.list_view);
        inputSearch = (EditText) v.findViewById(R.id.inputSearch);

        // Adding items to listview
        //adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item, R.id.product_name, products);

        //lv.setAdapter(adapter);

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
    return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SearchSpotListener.SearchSpotArrayAdapter(getActivity(), mSpotList.spotList, this);
        lv.setAdapter(adapter);
    }




    public void setSpotList(SpotList spotList) {

        mSpotList = spotList;

        products = new String[mSpotList.spotList.size()];
        int count = 0;
        for (Spot spot : mSpotList.spotList) {
            products[count] = mSpotList.spotList.get(count).spotName;
            count++;
        }
    }

    @Override
    public void onClickCheckBox(long spotId, boolean selected) {
        MainActivity a = (MainActivity) getActivity();

        if (selected)
            a.addToFavorites(spotId);
        else
            a.removeFromFavorites(spotId);
    }

    @Override
    public void onClick(long spotId) {

    }
}
