package wind.newwindalarm.fragment;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import wind.newwindalarm.MainActivity;
import wind.newwindalarm.MeteoStationData;
import wind.newwindalarm.R;
import wind.newwindalarm.WindAlarmProgram;

public class SpotDetailsFragment extends Fragment implements SpotDetailsMeteodataFragment.OnClickListener, ProgramListFragment.OnProgramListListener {

    public static final int Pager_MeteodataPage = 0;
    public static final int Pager_WebcamPage = 1;
    public static final int Pager_ChartPage = 2;
    public static final int Pager_ProgramListPage = 3;

    private MeteoStationData meteoData;
    private long spotId;
    private SpotDetailsMeteodataFragment meteodataFragment;
    private SpotDetailsWebcamFragment webcamFragment;
    private SpotDetailsChartFragment chartFragment;
    private ProgramListFragment programListFragment;
    private ViewPager mPager;
    private SpotDetailPagerAdapter mPagerAdapter;
    private TextView spotNameTextView;
    private TextView speedTextView;
    private TextView lastUpateTextView;

    OnClickListener mCallback;

    // Container Activity must implement this interface
    public interface OnClickListener {
        void onRefreshDetailViewRequest(int position); // chiamta per sesempio quand si cambia view nella tabview
        void onChangeDetailView(int position, long spotId, MeteoStationData md); // cambiamento Tab pager attivo. chiamata per cambiare ilfab button
        void onEditProgram(WindAlarmProgram program);
    }

    @Override
    public void onEditProgram(WindAlarmProgram program) {
        mCallback.onEditProgram(program);
    }

    public void setWebCamImage(int n, Bitmap bmp, long lastWebcamWindId) {

        // TODO è inutile chiamare getpager fragment. I fragment ora dovrebbero essere
        // aggiornati autromaticamente dalla instantiate quando c'è una roitazione
        //SpotDetailsWebcamFragment webcamFragment = (SpotDetailsWebcamFragment) getPagerFragment(Pager_WebcamPage);

        if (webcamFragment != null) {
            webcamFragment.setWebCamImage(n,bmp,lastWebcamWindId);
            webcamFragment.refreshData();
        }
    }

    private SpotDetailsFragmentInterface getPagerFragment(int position) {
        if (mPager == null)
            return null;
        SpotDetailPagerAdapter adapter = ((SpotDetailPagerAdapter) mPager.getAdapter());
        return (SpotDetailsFragmentInterface) adapter.getRegisteredFragment(position);
    }

    public void showWebCamProgressBar(int n) {
        SpotDetailsWebcamFragment fragment = (SpotDetailsWebcamFragment) getPagerFragment(Pager_WebcamPage);
        if (fragment != null) {
            fragment.showWebCamProgressBar(n);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("meteoData", meteoData);
        outState.putLong("spotId", spotId);
        outState.putInt("position", mPager.getCurrentItem());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            setMeteoData((MeteoStationData) savedInstanceState.getSerializable("meteoData"));
            setSpotId(savedInstanceState.getLong("spotId"));

            MainActivity ma = (MainActivity) getActivity();
            ma.setSpotDetailsFragment(this);
            mCallback = ma;
            int position = savedInstanceState.getInt("position");
            mCallback.onChangeDetailView(position, spotId, meteoData);
        }

        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);

        spotNameTextView = (TextView) v.findViewById(R.id.spotNameTextView);
        speedTextView = (TextView) v.findViewById(R.id.speedTextView);
        lastUpateTextView = (TextView) v.findViewById(R.id.lastUpdateTextView);


        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);
        mPager = (ViewPager) v.findViewById(R.id.pager);
        mPagerAdapter = new SpotDetailPagerAdapter(((MainActivity) getActivity()).getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            // optional
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // optional
            @Override
            public void onPageSelected(int position) {
                if (mCallback != null) // aggiorna il fab button nella mainactivity
                    mCallback.onChangeDetailView(position, spotId, meteoData);

                switch (position) {
                    case Pager_MeteodataPage:
                        meteodataFragment.refreshData();
                        break;

                    case Pager_WebcamPage:
                        webcamFragment.refreshData();
                        break;

                    case Pager_ChartPage:
                        chartFragment.refreshData();
                        break;

                    case Pager_ProgramListPage:
                        //programListFragment.refreshData();
                        break;
                }
            }

            // optional
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout.setupWithViewPager(mPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.logo);
        tabLayout.getTabAt(1).setIcon(R.drawable.webcamicon);
        tabLayout.getTabAt(2).setIcon(R.drawable.graphicon);
        tabLayout.getTabAt(3).setIcon(R.drawable.graphicon);

        // Updating the action bar title
        if (meteoData != null) {
            String spotName = ((MainActivity) getActivity()).getSpotName(meteoData.spotID);
            if (spotName != null) {
                //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(spotName);
                TextView tv = (TextView) v.findViewById(R.id.spotNameTextView);
                tv.setText(spotName);
            }
        }

        refreshData();

        return v;
    }

    public void setListener(OnClickListener listener) {
        mCallback = listener;
    }

    public SpotDetailsFragment() {
        meteodataFragment = new SpotDetailsMeteodataFragment();
        meteodataFragment.setListener(this);
        webcamFragment = new SpotDetailsWebcamFragment();
        chartFragment = new SpotDetailsChartFragment();
        programListFragment = new ProgramListFragment();
        programListFragment.setListener(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setMeteoData(MeteoStationData data) {
        meteoData = data;
        meteodataFragment.setMeteoData(data);
        webcamFragment.setMeteoData(data);
        chartFragment.setMeteoData(data);
    }

    public void setSpotId(long spotId) {
        this.spotId = spotId;
        meteodataFragment.setSpotId(spotId);
        webcamFragment.setSpotId(spotId);
        chartFragment.setSpotId(spotId);
        programListFragment.setSpotId(spotId);
    }

    public void refreshData() {

        if (meteoData == null)
            return;

        spotNameTextView.setText(meteoData.spotName);
        speedTextView.setText(""+meteoData.speed);
        SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy HH:mm");
        lastUpateTextView.setText(df.format(meteoData.date));

        meteodataFragment.refreshData();
        webcamFragment.refreshData();
        //chartFragment.refreshData();
        //programListFragment.refreshData();
    }

    public void setHistoryMeteoData(List<MeteoStationData> meteoDataList) {

        /*SpotDetailsChartFragment fragment = (SpotDetailsChartFragment) getPagerFragment(Pager_ChartPage);
        if (fragment != null) {
            fragment.setHistoryMeteoData(meteoDataList);
        }*/

        chartFragment.setHistoryMeteoData(meteoDataList);
    }

    /*public MeteoStationData getLastHistoryMeteoData() {
        return chartFragment.getLastHistoryMeteoData();
    }*/

    @Override
    public void onRefreshMeteoDataRequest() {

        int position = mPager.getCurrentItem();
        mCallback.onRefreshDetailViewRequest(position);
    }

    private class SpotDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 4;
        SparseArray<Fragment> registeredFragments;

        public SpotDetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);

            registeredFragments = new SparseArray<>();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            SpotDetailsFragmentInterface fragment;

            switch (position) {
                case Pager_MeteodataPage:
                    fragment = meteodataFragment;
                    break;
                case Pager_WebcamPage:
                    fragment = webcamFragment;
                    break;
                case Pager_ChartPage:
                    fragment = chartFragment;
                    break;
                case Pager_ProgramListPage:
                    fragment = programListFragment;
                    break;
                default:
                    return null;
            }
            fragment.setSpotId(spotId);
            fragment.setMeteoData(meteoData);
            return (Fragment) fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "titolo " + position;
            if (position == 0) {
                return "Dati meteo";
            } else if (position == 1) {
                return "Webcam";
            } else if (position == 2) {
                return "Grafico";
            } else if (position == 3) {
                return "Sveglie";
            }
            return title;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // l'override di questo metodo serve perchè i fragmnent creati all'inizio
            // potrebbero essere null perchè creati nuovamente per esempio quando
            // si ruota lo schermo.
            // Non è detto che la getitem sia sempre chiamata quando vengono creati i fragment
            // quindi è necessario questo metodo che riallinea
            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            switch (position) {
                case Pager_MeteodataPage:
                    meteodataFragment = (SpotDetailsMeteodataFragment) fragment;
                    break;
                case Pager_WebcamPage:
                    webcamFragment = (SpotDetailsWebcamFragment) fragment;
                    break;
                case Pager_ChartPage:
                    chartFragment = (SpotDetailsChartFragment) fragment;
                    break;
                case Pager_ProgramListPage:
                    programListFragment = (ProgramListFragment) fragment;
                    break;
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
