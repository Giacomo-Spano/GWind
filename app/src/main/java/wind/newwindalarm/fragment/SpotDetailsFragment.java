package wind.newwindalarm.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import wind.newwindalarm.MainActivity;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.ScreenSlidePageFragment;
import wind.newwindalarm.WebcamFragment;

public class SpotDetailsFragment extends Fragment {

    private LineChart mWindChart;
    private LineChart mTrendChart;
    private LineChart mTemperatureChart;
    private ImageView mWebcamImageView;
    private MeteoStationData meteoData;

    private SpotDetailsMeteodataFragment meteodataFragment;
    private WebcamFragment webcamFragment;
    private SpotDetailsChartFragment chartFragment;

    public SpotDetailsFragment() {
        meteodataFragment = new SpotDetailsMeteodataFragment();
        webcamFragment = new WebcamFragment();
        chartFragment = new SpotDetailsChartFragment();
    }

    /**

     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;

        /*chartFragment.setMeteoData(data);
        webcamFragment.setMeteoData(data);
        meteodataFragment.setMeteoData(data);*/
    }

    public void updateChartData(List<Object> meteoDataList) {

        chartFragment.setMeteoData(meteoDataList);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem mm = menu.findItem(R.id.options_refresh);
        if (mm != null) {

            menu.findItem(R.id.options_refresh).setVisible(true);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);
        mWebcamImageView = (ImageView) v.findViewById(R.id.imageView7);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) v.findViewById(R.id.pager);

        MainActivity ma = (MainActivity) getActivity();

        mPagerAdapter = new SpotDetailPagerAdapter(ma.getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        tabLayout.setupWithViewPager(mPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.webcamicon);
        tabLayout.getTabAt(2).setIcon(R.drawable.graphicon);

        // Updating the action bar title
        if (meteoData != null) {
            String txt = ((MainActivity)getActivity()).getSpotName(meteoData.spotID);
            if (txt != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(txt);
            }
        }
        mWindChart = (LineChart) v.findViewById(R.id.chart);

        //spotID = getArguments().getLong("spotID"); // TODO a cosa serve? E' già inizializzato
        /*String str = getArguments().getString("meteodata");
        if (str != null) { // TODO non serve a nulla. Servirebbe solo per aggiornare il titolo forse????
            try {
                JSONObject json = new JSONObject(str);
                meteoData = new MeteoStationData(json);
                //refreshData();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //getLastData(spotID);
        }*/
        return v;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class SpotDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 3;

        public SpotDetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if (position == 0 ) {

                Bundle data = new Bundle();
                data.putLong("spotID", meteoData.spotID);
                if (meteoData != null)
                    data.putString("meteodata", meteoData.toJson());
                meteodataFragment.setArguments(data);
                return  meteodataFragment;

            }  else if (position == 1 ) {


                webcamFragment.setMeteoData(meteoData);
                Bundle data = new Bundle();
                data.putLong("spotID", meteoData.spotID);
                if (meteoData != null)
                    data.putString("meteodata", meteoData.toJson());
                webcamFragment.setArguments(data);
                return  webcamFragment;

            }  else if (position == 2 ) {


                /*Bundle data = new Bundle();
                data.putLong("spotID", meteoData.spotID);
                if (meteoData != null)
                    data.putString("meteodata", meteoData.toJson());
                chartFragment.setArguments(data);*/
                chartFragment.refreshData();
                return chartFragment;

            }  else{
                return new ScreenSlidePageFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String titolo = "titolo " + position;
            if (position == 0 ) {
                return  "Dati meteo";
            }  else if (position == 1 ) {
                return  "Webcam";
            } else if (position == 2 ) {
                return  "Grafico";
            }
            return titolo;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
