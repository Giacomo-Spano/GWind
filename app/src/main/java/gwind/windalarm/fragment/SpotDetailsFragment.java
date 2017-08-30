package gwind.windalarm.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.List;

import gwind.windalarm.MainActivity;
import gwind.windalarm.MeteoItemListListener;
import gwind.windalarm.Spot;
import gwind.windalarm.data.Forecast;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.R;
import gwind.windalarm.WindAlarmProgram;

public class SpotDetailsFragment extends Fragment implements /*SpotDetailsMeteodataFragment.OnClickListener,*/ ProgramListFragment.OnProgramListListener, ForecastFragment.OnMeteoForecastClickListener, MeteoItemListListener/*, OnMapReadyCallback*/ {


    public static final int Pager_MeteodataPage = 0;
    public static final int Pager_ChartPage = 1;
    public static final int Pager_WebcamPage = 2;
    public static final int Pager_ProgramListPage = 3;
    public static final int Pager_ForecastPage = 4;

    private MeteoStationData meteoData;
    private long spotId;
    double lat, lon;

    private ForecastFragment forecastFragment;
    //private SpotDetailsMeteodataFragment meteodataFragment;
    private MeteoItemListFragment meteoItemListFragment;
    private SpotDetailsWebcamFragment webcamFragment;
    private SpotDetailsChartFragment chartFragment;
    private ProgramListFragment programListFragment;
    private ViewPager mPager;
    private SpotDetailPagerAdapter mPagerAdapter;
    private TextView spotNameTextView;
    private TextView speedTextView;
    private TextView lastUpateTextView;

    //private GoogleMap mMap;
    public SupportMapFragment mapFragment;

    OnClickListener mCallback;

    @Override
    public void onMeteoForecastClick(Spot spot) {

    }

    public void setForecast(Forecast forecast) {
        forecastFragment.setForecast(forecast);
    }

    @Override
    public void onClickCheckBox(int position, boolean selected) {

    }

    @Override
    public void onClick(long spotId) {

    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney, Australia, and move the camera.
        if (meteoData != null && meteoData.lat != null && meteoData.lat > 0 && meteoData.lon != null && meteoData.lon > 0) {
            // Move the camera instantly to Sydney with a zoom of 15.
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
            LatLng spotLatLng = new LatLng(meteoData.lat, meteoData.lon);
            mMap.addMarker(new MarkerOptions().position(spotLatLng).title("Marker in " + meteoData.spotName));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotLatLng, 12));
        }
    }*/

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
            webcamFragment.setWebCamImage(n, bmp, lastWebcamWindId);
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
            mCallback = ma.getSpotDetailsListener();
            int position = savedInstanceState.getInt("position");
            mCallback.onChangeDetailView(position, spotId, meteoData);
        }

        View v;
        v = inflater.inflate(R.layout.fragment_spotdetail, container, false);

        ////////////
        mContainerView = (LinearLayout) v;

        v.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                /*if(event.getAction() == MotionEvent.ACTION_MOVE){
                    //do something
                }
                return true;*/

                boolean retVal = mGestureDetector.onTouchEvent(event);
                return retVal;
            }
        });

        mGestureDetector = new GestureDetectorCompat(getActivity(), mGestureListener);
        mScroller = new OverScroller(getActivity());
///////////////////

        spotNameTextView = (TextView) v.findViewById(R.id.spotNameTextView);
        speedTextView = (TextView) v.findViewById(R.id.speedTextView);
        lastUpateTextView = (TextView) v.findViewById(R.id.lastUpdateTextView);


        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayout);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /*@Override
            public void onTabSelected(TabLayout.Tab tab) {
                //viewPager.setCurrentItem(tab.getPosition());
                //Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }*/

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) return;
                //super.onTabSelected(tab);
                //int tabIconColor = ContextCompat.getColor(getContext(), Color.parseColor("#ff0000"));
                Drawable icon = tab.getIcon();
                if (icon != null)
                    icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == null) return;
                //super.onTabUnselected(tab);
                //int tabIconColor = ContextCompat.getColor(getContext(), Color.parseColor("#00ff00"));
                Drawable icon = tab.getIcon();
                if (icon != null)
                    icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextPrimaryDark), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //super.onTabReselected(tab);
            }


        });



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
                    case Pager_ForecastPage:
                        forecastFragment.refreshData();
                        break;

                    case Pager_MeteodataPage:
                        meteoItemListFragment.refreshData();
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

        /*ImageView image = new ImageView(this.getContext());
        image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null));
        image.setColorFilter(new LightingColorFilter(Color.parseColor("#ff0000"), Color.parseColor("#ff0000")));
        image,getI*/

        tabLayout.setupWithViewPager(mPager);



        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);;
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Pager_MeteodataPage).setIcon(icon);

        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.graphicon, null);
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Pager_ChartPage).setIcon(icon);

        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.webcamicon, null);;
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Pager_WebcamPage).setIcon(icon);

        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.bell, null);;
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Pager_ProgramListPage).setIcon(icon);

        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.m02n, null);;
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextPrimaryDark), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Pager_ForecastPage).setIcon(icon);

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

        // Add the fragment to the 'fragment_container' FrameLayout
        /*mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // RIMOSSO
        /*if (mMap == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }*/
    }

    /*private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }*/

    public void setListener(OnClickListener listener) {
        mCallback = listener;
    }

    public SpotDetailsFragment() {
        forecastFragment = new ForecastFragment();
        forecastFragment.setListener(this);
        meteoItemListFragment = new MeteoItemListFragment();
        meteoItemListFragment.setListener(this);
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
        forecastFragment.setMeteoData(data);
        meteoItemListFragment.setMeteoData(data);
        webcamFragment.setMeteoData(data);
        chartFragment.setMeteoData(data);
    }

    public void setSpotId(long spotId) {
        this.spotId = spotId;
        forecastFragment.setSpotId(spotId);
        meteoItemListFragment.setSpotId(spotId);
        webcamFragment.setSpotId(spotId);
        chartFragment.setSpotId(spotId);
        programListFragment.setSpotId(spotId);
    }

    public void refreshData() {

        if (meteoData == null)
            return;

        spotNameTextView.setText(meteoData.spotName);
        speedTextView.setText("" + meteoData.speed);
        SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy HH:mm");
        lastUpateTextView.setText(df.format(meteoData.date));

        meteoItemListFragment.refreshData();
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

    /*@Override
    public void onRefreshMeteoDataRequest() {

        int position = mPager.getCurrentItem();
        mCallback.onRefreshDetailViewRequest(position);
    }*/

    private class SpotDetailPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 5;
        SparseArray<Fragment> registeredFragments;

        public SpotDetailPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);

            registeredFragments = new SparseArray<>();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            SpotDetailsFragmentInterface fragment;

            switch (position) {
                case Pager_ForecastPage:
                    fragment = forecastFragment;
                    break;
                case Pager_MeteodataPage:
                    fragment = meteoItemListFragment;
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

            String title = "";

            /*switch (position) {
                case Pager_ForecastPage:
                    return "Previsioni";
                case Pager_MeteodataPage:
                    return "Dati meteo";
                case Pager_WebcamPage:
                    return "Webcam";
                case Pager_ChartPage:
                    return "Grafico";
                case Pager_ProgramListPage:
                    return "Sveglie";
            }*/
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
                case Pager_ForecastPage:
                    forecastFragment = (ForecastFragment) fragment;
                    break;
                case Pager_MeteodataPage:
                    meteoItemListFragment = (MeteoItemListFragment) fragment;
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




    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    LinearLayout mContainerView;
    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {

            // Initiates the decay phase of any active edge effects.
            //releaseEdgeEffects();
            //mScrollerStartViewport.set(mCurrentViewport);
            // Aborts any active scroll animations and invalidates.
            mScroller.forceFinished(true);

            ViewCompat.postInvalidateOnAnimation(mContainerView);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            /*mZoomer.forceFinished(true);
            if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
                mZoomer.startZoom(ZOOM_AMOUNT);
            }
            ViewCompat.postInvalidateOnAnimation(InteractiveLineGraphView.this);*/
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            /**
             * Pixel offset is the offset in screen pixels, while viewport offset is the
             * offset within the current viewport. For additional information on surface sizes
             * and pixel offsets, see the docs for {@link computeScrollSurfaceSize()}. For
             * additional information about the viewport, see the comments for
             * {@link mCurrentViewport}.
             */

            /*yy += distanceY;
            MainActivity ma = (MainActivity) getActivity();
            resizeFragment(ma.spotDetailsFragment, xx,yy);
            */

            MainActivity ma = (MainActivity) getActivity();
            //ma.spotDetailsFragment.bringToFront();

            resizeFragment(ma.spotDetailsFragment, distanceX,distanceY);


            return true;
        }

        @Override
        public boolean onFling (MotionEvent e1,
                                         MotionEvent e2,
                                float velocityX,
                                float velocityY) {




            return true;

        }

        private int xx = 100, yy = 100;


    };

    private void resizeFragment(Fragment f, float newWidth, float newHeight) {
        if (f != null) {
            View view = f.getView();


            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(view.getWidth(), view.getHeight());

            int delta = -(int)newHeight;
            if (newHeight < 0) {
                p.topMargin = view.getTop() + delta;
                p.height += delta;
            } else {
                p.topMargin = view.getTop() + delta;
                p.height += delta;
            }


            /*LayoutParams layoutParams=new LayoutParams(width, height);
            layoutParams.leftMargin = newLeft;
            layoutParams.topMargin = newTop;
            imageView.setLayoutParams(layoutParams);*/



            view.setLayoutParams(p);
            view.requestLayout();


        }
    }


}
