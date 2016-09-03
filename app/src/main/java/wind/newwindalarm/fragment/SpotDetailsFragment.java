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

public class SpotDetailsFragment extends Fragment {

    private MeteoStationData meteoData;
    private SpotDetailsMeteodataFragment meteodataFragment;
    private SpotDetailsWebcamFragment webcamFragment;
    private SpotDetailsChartFragment chartFragment;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public SpotDetailsFragment() {
        meteodataFragment = new SpotDetailsMeteodataFragment();
        webcamFragment = new SpotDetailsWebcamFragment();
        chartFragment = new SpotDetailsChartFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;

        /*chartFragment.setMeteoData(data);
        webcamFragment.setMeteoData(data);*/
        meteodataFragment.setMeteoData(data);
    }

    public void setHistoryMeteoData(List<MeteoStationData> meteoDataList) {

        chartFragment.setHistoryMeteoData(meteoDataList);
    }

    public MeteoStationData getLastHistoryMeteoData() {
        return chartFragment.getLastHistoryMeteoData();
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
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        mPager = (ViewPager) v.findViewById(R.id.pager);

        mPagerAdapter = new SpotDetailPagerAdapter(((MainActivity)getActivity()).getSupportFragmentManager());
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
        return v;
    }

    private class SpotDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 3;

        public SpotDetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if (position == 0 ) {

                meteodataFragment.setSpotId(meteoData.spotID);
                meteodataFragment.setMeteoData(meteoData);
                meteodataFragment.refreshData();
                return  meteodataFragment;

            }  else if (position == 1 ) {

                webcamFragment.setSpotId(meteoData.spotID);
                webcamFragment.setMeteoData(meteoData);
                webcamFragment.refreshData();
                return  webcamFragment;

            }  else if (position == 2 ) {

                chartFragment.setSpotId(meteoData.spotID);
                chartFragment.setMeteoData(meteoData);
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
