package gwind.windalarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.transition.Scene;
import android.transition.Transition;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gwind.windalarm.data.Location;
import gwind.windalarm.data.MeteoStationData;
import gwind.windalarm.data.Forecast;
import gwind.windalarm.fragment.ForecastFragment;
//import gwind.windalarm.fragment.PanelFragment;
//import gwind.windalarm.fragment.PanelFragment;
import gwind.windalarm.fragment.ProfileFragment;
import gwind.windalarm.fragment.ProgramFragment;
import gwind.windalarm.fragment.ProgramListFragment;
//import gwind.windalarm.fragment.RecyclerMeteoCardListFragment;
//import gwind.windalarm.fragment.RecyclerSpotListOrderFragment;
//import gwind.windalarm.fragment.RecyclerMeteoCardListFragment;
//import gwind.windalarm.fragment.RecyclerMeteoCardListFragment;
//import gwind.windalarm.fragment.RecyclerMeteoCardListFragment;
import gwind.windalarm.fragment.RecyclerMeteoCardListFragment;
import gwind.windalarm.fragment.RecyclerSpotListOrderFragment;
import gwind.windalarm.fragment.SearchMeteoForecastFragment;
import gwind.windalarm.fragment.SearchSpotFragment;
import gwind.windalarm.fragment.SpotDetailsFragment;
import gwind.windalarm.fragment.SpotMeteoListFragment;
import gwind.windalarm.request.requestMeteoDataTask;
//import gwind.windalarm.request.requestMeteoDataTask;
//import gwind.windalarm.request.requestMeteoDataTask;

import static gwind.windalarm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static gwind.windalarm.fragment.SearchSpotFragment.*;
import static gwind.windalarm.request.requestMeteoDataTask.FORECAST_OPENWEATHERMAP;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnSignInClickListener,
        ProgramListFragment.OnProgramListListener,
        OnSearchSpotClickListener,
        DownloadImageTask.AsyncDownloadImageResponse, SpotListFragment.OnSpotListListener, RecyclerSpotListOrderFragment.OnListener//,//,
        /*OnMapReadyCallback*/ {

    private static final int FORECAST_REQUESTID_METEOSTATION = 99;
    private static final int FORECAST_REQUESTID_LOCATION = 88;

    private final static String TAG_FRAGMENT_SEARCHSPOT = "TAG_FRAGMENT_SEARCHSPOT";
    private final static String TAG_FRAGMENT_ORDERSPOT = "TAG_FRAGMENT_ORDERSPOT";

    private Fragment currentFragment = null;
    /*private GoogleMap mMap;
    public SupportMapFragment mapFragment;*/

    CountDownTimer countDownTimer;
    ProgressBar progressBar;
    private long spotId;
    private RecyclerMeteoCardListFragment recyclerMeteoCardListFragment;
    private SpotListFragment spotListFragment;


    public MainActivity() {
        searchMeteoForecastListener = new SearchMeteoForecastListener(this);
        searchSpotListener = new SearchSpotListener(this);
        recyclerMeteoCardListListener = new RecyclerMeteoCardListListener();
        spotDetailsListener = new SpotDetailsListener();
        forecastListener = new ForecastListener();
        recyclerSpotOrderListListener = new RecyclerSpotOrderListListener();
        ///recyclerMeteocardListener = new RecyclerMeteoCardListListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public SearchMeteoForecastListener getSearchMeteoForecastListener() {
        return searchMeteoForecastListener;
    }

    @Override
    public void onSpotListChangeSelection(List<Long> list) {
    }

    @Override
    public void onAddSpot(long spotId) {
    }

    @Override
    public void onRemoveSpot(long spotId) {
    }

    @Override
    public void onEnableAddFavoriteSpotButtonRequest() {
    }

    private class SearchMeteoForecastListener implements SearchMeteoForecastFragment.OnSearchSpotClickListener {

        private final Activity activity;
        public SearchMeteoForecastListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onSearchLocationClick(String filter) {
            getForecastLocationFromServer("openweathermap", filter);
        }

        @Override
        public void onSearchLocationSelectLocation(Location location) {

            new requestMeteoDataTask(activity, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, location.id, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_LOCATION);
        }
    }

    private class SearchSpotListener implements OnSearchSpotClickListener {

        public SearchSpotListener(MainActivity activity) {

        }

        @Override
        public void onSearchSpotClick(Spot spot) {
            addToFavorites(spot.id);
            showSetSpotOrder();
        }
    }

    public ForecastListener getForecastListener() {
        return forecastListener;
    }

    private class ForecastListener implements ForecastFragment.OnMeteoForecastClickListener {
        @Override
        public void onMeteoForecastClick(Spot spot) {
        }
    }

    @Override
    public void onEditProgram(WindAlarmProgram program) { /// TODO questa è duplicata in due listener

        startProgramActivity(program, ProgramActivity.EDITPROGRAM_REQUEST);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    public static final String ANONYMOUS = "anonymous";

    private TextView mInformationTextView;
    private static final String TAG = "SignInActivity";
    //public List<Spot> spotList;
    private SpotList mSpotList = new SpotList();
    private Settings mSettings;

    // questi listener devono essere dichiasrati final altrimento quando c'è una rotazione si perde il riferimento
    private final SearchMeteoForecastListener searchMeteoForecastListener;
    private final SearchSpotListener searchSpotListener;
    private final RecyclerMeteoCardListListener recyclerMeteoCardListListener;
    private final RecyclerSpotOrderListListener recyclerSpotOrderListListener;
    private final SpotDetailsListener spotDetailsListener;
    private final ForecastListener forecastListener;

    //private PanelFragment panelFragment;
    public/*private*/ SpotDetailsFragment spotDetailsFragment;
    private SearchMeteoForecastFragment searchMeteoForecastFragment;
    private ProgramFragment programFragment;
    private ProgramListFragment programListFragment;
    private SettingsFragment settingsFragment;
    private ProfileFragment profileFragment;
    private SpotMeteoListFragment spotMeteoListFragment;
    private SearchSpotFragment searchSpotFragment;
    private SpotMeteoDataList spotMeteoDataList;
    private RecyclerSpotListOrderFragment spotOrderFragment;

    private IntentFilter filter = new IntentFilter(DISPLAY_MESSAGE_ACTION);
    // google properties
    TextView mUserNameTextView;
    TextView memailTextView;
    ImageView mUserImageImageView;

    FloatingActionButton fabButton;

    private static UserProfile mProfile = null;

    public static UserProfile getUserProfile() {
        return mProfile;
    }

    static boolean signedIn = false;
    static int nextFragment = -1;

    //scenes to transition
    private Scene scene1, scene2;
    //transition to move between scenes
    private Transition transition;
    //flag to swap between scenes
    private boolean start;

    public List<Spot> getFavorites() {
        return mSpotList.getSpotFavorites();
    }

    public void addToFavorites(long id) {
        mSpotList.addToFavorites(this, id, mProfile.personId);
    }

    public void removeFromFavorites(long id) {
        mSpotList.removeFromFavorites(this, id, mProfile.personId);
    }

    public Spot getSpotFromId(long id) {
        return mSpotList.getSpotFromId(id);
    }

    public String getSpotName(long id) {
        return mSpotList.getSpotName(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            spotId = savedInstanceState.getLong("spotId");
            mProfile = (UserProfile) savedInstanceState.getSerializable("userProfile");

        } else {
            Intent intent = getIntent();
            String str = (String) intent.getStringExtra("spotId");
            mProfile = (UserProfile) intent.getSerializableExtra("userProfile");
            if (str != null)
                spotId = Long.valueOf(str);
        }

        // Set default username is anonymous.
        //mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            //startActivity(new Intent(this, SignInActivity.class));
            //finish();
            //return;
        } else {
            //mUsername = mFirebaseUser.getDisplayName();

            /*if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }*/

            mProfile.userName = mFirebaseUser.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            mProfile.email = mFirebaseUser.getEmail();
            mProfile.personId = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mProfile.photoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabButton = (FloatingActionButton) findViewById(R.id.addFab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //getSupportActionBar().setElevation(0);
        //drawer.setScrimColor(Color.TRANSPARENT);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUserNameTextView = (TextView) header.findViewById(R.id.UserNameTextView);
        memailTextView = (TextView) header.findViewById(R.id.UserEmailTextView);
        mUserImageImageView = (ImageView) header.findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mSettings = new Settings(this);
        mSettings.setListener(new Settings.SettingsListener() {
            @Override
            public void onChangeOrder(List<Long> order) {
                //panelFragment.setSpotOrder(order);
            }

            @Override
            public void onChangeList(List<Long> list) {

            }
        });

        //panelFragment = new PanelFragment();
        //panelFragment.setListener(panelListener);
        recyclerMeteoCardListFragment = new RecyclerMeteoCardListFragment();
        recyclerMeteoCardListFragment.setListener(recyclerMeteoCardListListener);
        programFragment = new ProgramFragment();
        programListFragment = new ProgramListFragment();
        programListFragment.setListener(this);
        settingsFragment = new SettingsFragment();
        settingsFragment.setSettings(mSettings);
        profileFragment = new ProfileFragment();
        spotMeteoListFragment = new SpotMeteoListFragment();
        spotMeteoListFragment.setUserId(mProfile.personId);

        //recyclerMeteoCardListFragment.setListener(recyclerSpotOrderListListener);


        //searchSpotFragment = new SearchSpotFragment();

        // non serve perchè già chiamata nella onResume
        //getLastMeteoData();
        spotMeteoDataList = new SpotMeteoDataList();
        SpotMeteoDataList s =  spotMeteoDataList.loadFromFile(getApplicationContext());
        if (s != null) {
            spotMeteoDataList = s;
            updateFavoritesMeteoData(s.getMeteoDataList());
        }

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        init();

        if (savedInstanceState != null) {

            //spotDetailsFragment.setListener(this);
            spotId = savedInstanceState.getLong("spotId");

        } else {
            showFragment(R.id.nav_favorites, false);
        }

        // Add the fragment to the 'fragment_container' FrameLayout
        /*mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //spotDetailsFragment.setListener(this);
        }
    }

    private void init() {

        signedIn = true;
        mInformationTextView.setText("Loading profile");
        new LoadImagefromUrl().execute();
        mInformationTextView.setText("Loading meteodata");
    }

    @Override
    public void onResume() {
        super.onResume();
        getLastMeteoData();
        // Register the broadcast receiver.
        registerReceiver(mHandleMessageReceiver, filter);
    }

    @Override
    public void onPause() {
        // Unregister the receiver
        unregisterReceiver(mHandleMessageReceiver);
        super.onPause();
    }

    // Questo receiver serve a ricevere dei messaggi dall'esterno o dall'activity Splash
    // inviati trami te CommonUtilities.sendMessageToMainActivity
    // per riceverli deve esserci la chiamata a registerReceiver nella onResume
    // altrimenti non funziona
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE/*EXTRA_MESSAGE*/);
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            Toast.makeText(getApplicationContext(), "" + newMessage, Toast.LENGTH_LONG).show();
            String title = intent.getExtras().getString("title");
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
        }
    };

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SEARCHSPOT);
            if (fragment != null) // blocca bottone back
            {
                showSetSpotOrder();
                return;
            }
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_ORDERSPOT);
            if (fragment != null) // blocca bottone back
            {
                showFragment(R.id.nav_favorites,false);
                getLastMeteoData();
                return;
            }

            super.onBackPressed();
        }
    }



    /**
     * Handling the touch event of app icon
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // return super.onOptionsItemSelected(item);
        int id = item.getItemId();

        switch (item.getItemId()) {
        /*
         * Typically, an application registers automatically, so options below
		 * are disabled. Uncomment them if you want to manually register or
		 * unregisterUserData the device (you will also need to uncomment the equivalent
		 * options on options_menu.xml).*/

            /*case R.id.action_favorite:
                showSetSpotOrder();
                return true;*/
            case R.id.options_favorites:
                showSetSpotOrder();
                return true;
            /*case R.id.options_searchspot:
                showSearchSpot();
                return true;*/
            case R.id.options_searchmeteoforecast:
                showSearchMeteoForecast();
                return true;


            /*case R.id.action_settings:
                openSettings();
                return true;*/
            /*case R.id.action_add:

                showSetSpotOrder2();
                ;
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showSearchMeteoForecast() {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        searchMeteoForecastFragment = new SearchMeteoForecastFragment();
        //searchMeteoForecastListener = new SearchMeteoForecastListener();
        searchMeteoForecastFragment.setListener(searchMeteoForecastListener);

        currentFragment = searchMeteoForecastFragment;
        ft.replace(R.id.content_frame, searchMeteoForecastFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    private void showSearchSpot() {

        getSpotListFromServer();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        searchSpotFragment = new SearchSpotFragment();
        searchSpotFragment.setListener(searchSpotListener);

        currentFragment = searchSpotFragment;
        ft.replace(R.id.content_frame, searchSpotFragment, TAG_FRAGMENT_SEARCHSPOT);
        //ft.replace(R.id.content_frame, searchSpotFragment);
        //ft.addToBackStack(null);
        ft.commit();
    }

    private void showSetSpotOrder() {

        getSpotListFromServer();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();


        //getSupportFragmentManager().findFragmentById(R.id.spo);

        spotOrderFragment = new RecyclerSpotListOrderFragment();
        spotOrderFragment.setListener(recyclerSpotOrderListListener);

        currentFragment = spotOrderFragment;
        ft.replace(R.id.content_frame, spotOrderFragment, TAG_FRAGMENT_ORDERSPOT);
        //ft.replace(R.id.content_frame, spotOrderFragment);
        //ft.addToBackStack(null);
        ft.commit();
    }

    /*private void showSetSpotOrder2() {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        spotListFragment = new SpotListFragment();
        spotListFragment.setListener(this);

        ft.replace(R.id.content_frame, spotListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        showFragment(id, true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(int mPosition, boolean addToBackStack) {

        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

            if (!signedIn && mPosition != R.id.nav_settings) {
                nextFragment = mPosition;
                ft.replace(R.id.content_frame, profileFragment);
                profileFragment.setProfile(mProfile);
            } else {

                if (mPosition == R.id.nav_favorites && recyclerMeteoCardListFragment != null) {
                    ft.replace(R.id.content_frame, recyclerMeteoCardListFragment);
                    enableRefreshFavoritesMeteoDataButton();
                    currentFragment = recyclerMeteoCardListFragment;
                } else if (mPosition == R.id.nav_program) {
                    ft.replace(R.id.content_frame, programListFragment);
                    enableAddProgramButton();
                    currentFragment = programListFragment;
                } /*else if (mPosition == R.id.nav_meteostation) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, recyclerMeteoCardListFragment);
                    currentFragment = recyclerMeteoCardListFragment;
                } */else if (mPosition == R.id.nav_settings) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, settingsFragment);
                    currentFragment = settingsFragment;
                } else if (mPosition == R.id.nav_profile) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, profileFragment);
                    profileFragment.setProfile(mProfile);
                    currentFragment = profileFragment;
                }
            }
            if (addToBackStack)
                ft.addToBackStack(null);
            ft.commit();

        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
    }

    private void getForecastLocationFromServer(String source, String filter) {

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECASTLOCATIONS).execute(mProfile.personId, source, filter);


    }

    public void getSpotListFromServer() {

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_SPOTLIST).execute(mProfile.personId);
    }

    public void showError(String errorMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Errore");
        alertDialogBuilder
                .setMessage(errorMessage)
                .

                        setCancelable(false);

        alertDialogBuilder
                .setNegativeButton("Ok", new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        }

                );
        AlertDialog alertDialog;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void unregisterUserData() {
        AlarmPreferences.deleteRegId(this);

    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/



    private void showSpotDetailsFragment(long spotId) {
        this.spotId = spotId;

        spotDetailsFragment = new SpotDetailsFragment(); // questo fragment deve essere creato qui altrimenti in certi casi non lo ridisegna
        spotDetailsFragment.setListener(spotDetailsListener);


        MeteoStationData md = spotMeteoDataList.getLastMeteoData(spotId);
        spotDetailsFragment.setMeteoData(spotMeteoDataList.getLastMeteoData(spotId));

        spotDetailsFragment.setSpotId(spotId);
        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

            currentFragment = spotDetailsFragment;
            ft.replace(R.id.content_frame, spotDetailsFragment);
            ft.addToBackStack(null);
            ft.commit();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }

        getForecast();

        getWebCamImages(spotId, md);

        getHistoryMeteoData(spotId);
    }

    public RecyclerMeteoCardListFragment.OnListener getRecyclerMeteoCardListListener() {
        return recyclerMeteoCardListListener;
    }

    private class RecyclerSpotOrderListListener implements RecyclerSpotListOrderFragment.OnListener {

        @Override
        public void onEnableAddFavoriteSpotButtonRequest() {
            enableAddFavoriteSpotButton();
        }

        @Override
        public void onAddSpot(long spotId) {

        }

        @Override
        public void onRemoveSpot(long spotId) {
            removeFromFavorites(spotId);
            showSetSpotOrder();
        }
    }

    private class RecyclerMeteoCardListListener implements RecyclerMeteoCardListFragment.OnListener {

        @Override
        public void onEnableMeteoCardListRefreshButtonRequest() {
            //getLastMeteoData();
            enableRefreshFavoritesMeteoDataButton();
        }

        @Override
        public void onSpotClick(long spotId) {

            showSpotDetailsFragment(spotId);
        }
    }

    public void getLastMeteoData() {

        startProgressBar();

        long windId = spotMeteoDataList.getLastWindId();
        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FAVORITESLASTMETEODATA).execute(mProfile.personId, windId);
    }

    public void getForecast() {

        Forecast f = spotMeteoDataList.getForecast(spotId);
        long forecastid = 0;
        if (f != null) {
            forecastid = f.forecastId;
            spotDetailsFragment.setForecast(f);
        }

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, "" + spotId, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_METEOSTATION,forecastid);
    }

    private void startProgressBar() {
        progressBar.setProgress(10);
        progressBar.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(50000, 1000) {

            private int progress = 10;

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                progress += 10;
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();
    }

    public void getHistoryMeteoData(long spotId) {

        long lastWindId = spotMeteoDataList.getLastHistoryId(spotId);

        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR_OF_DAY, -3); //minus number would decrement the hours
        Date start = cal.getTime();

        startProgressBar();

        // richiedi dati storici a partire da lastWindId e vai in append
        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_LOGMETEODATA).execute(spotId, mProfile.personId, start, end, lastWindId);
    }

    public void getWebCamImages(long spotId, MeteoStationData md) {

        if (spotId == -1 || md == null)
            return;

        long lastWindId = spotMeteoDataList.getWebCamWindId(spotId, 1);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int count = 1;
        for (String webcamurl : md.webcamurlList) {
            if (lastWindId < md.id) {

                if (md.id <= spotMeteoDataList.getWebCamImageRequestInProgressId(spotId, count)) {
                    // c'è già una richiesta in corso, non fare nulla
                } else {
                    spotDetailsFragment.showWebCamProgressBar(count);
                    spotMeteoDataList.setWebCamImageRequestInProgress(spotId, count, md.id);
                    new DownloadImageTask(this, count, md.id, width).execute(webcamurl);
                }

            } else {
                spotDetailsFragment.setWebCamImage(count, spotMeteoDataList.getWebCamImage(spotId, count), lastWindId);
            }
            count++;
        }
    }

    @Override
    public void onSignInClick() {

    }

    @Override
    public void onSignOutClick() {
        unregisterUserData();
        setResult(SplashActivity.RESULT_SIGN_OUT);
        finish();
    }

    @Override
    public void onDisconnectClick() {
        unregisterUserData();
        setResult(SplashActivity.RESULT_DISCONNECT);
        finish();
    }

    public SpotDetailsListener getSpotDetailsListener() {
        return spotDetailsListener;
    }

    private class SpotDetailsListener implements SpotDetailsFragment.OnClickListener {

        @Override
        public void onRefreshDetailViewRequest(int position) {
            getLastMeteoData();
        }

        @Override
        public void onEditProgram(WindAlarmProgram program) {

            startProgramActivity(program, ProgramActivity.EDITPROGRAM_REQUEST);
        }

        @Override
        public void onChangeDetailView(int page, long spotId, MeteoStationData md) {

            switch (page) {

                case SpotDetailsFragment.Pager_MeteodataPage:
                    enableRefreshMeteoDataButton();
                    break;
                case SpotDetailsFragment.Pager_WebcamPage:
                    enableRefreshWebcamImagesButton(spotId, md);
                    break;
                case SpotDetailsFragment.Pager_ChartPage:
                    enableRefreshHistoryButton();
                    break;
                case SpotDetailsFragment.Pager_ProgramListPage:
                    enableAddProgramButton();
                    break;
                default:
                    break;
            }
        }
    }

    // Questo è il bottone attivo quando è visibile il panelFragment
    // è diverso da enableRefreshMeteoDataButton() perchè
    // non aggiorna la history e le webcam
    private void enableRefreshFavoritesMeteoDataButton() {
        fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastMeteoData();
            }
        });
    }

    private void enableAddFavoriteSpotButton() {
        fabButton.setImageResource(R.drawable.add);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchSpot();
            }
        });
    }

    private void enableRefreshMeteoDataButton() {
        fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastMeteoData();
                //getWebCamImages(spotId.);
                //getHistoryMeteoData(spotId);
            }
        });
    }

    private void enableRefreshHistoryButton() {
        fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastMeteoData();
                getHistoryMeteoData(spotId);
            }
        });
    }

    // abilita bottone che aggiorna solo le webcam
    private void enableRefreshWebcamImagesButton(final long spotId, final MeteoStationData md) {
        fabButton.setImageResource(R.drawable.refreshbutton);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWebCamImages(spotId, md);
            }
        });
    }

    private void enableAddProgramButton() {

        fabButton.setImageResource(R.drawable.add);
        fabButton.setVisibility(View.VISIBLE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //programListFragment.createProgram();
                WindAlarmProgram program = new WindAlarmProgram();
                program.spotId = spotId;
                /*if (alarmList.size() == 0)
                    program.id = 1L;
                else
                    program.id = alarmList.get(alarmList.size() - 1).alarm.id + 1;*/
                startProgramActivity(program, ProgramActivity.CREATEPROGRAM_REQUEST);
            }
        });
    }

    private void disableAllButton() {

        fabButton.setVisibility(View.GONE);
    }


    private void startProgramActivity(WindAlarmProgram alarm, int request) {
        //Intent resultIntent = new Intent(getActivity(), ProgramActivity.class);
        Intent resultIntent = new Intent(SplashActivity.getContext(), ProgramActivity.class);
        resultIntent.putExtra("WindAlarmProgram", new Gson().toJson(alarm));

        //resultIntent.putExtra("spotid",spotId);

        /*Gson gson = new Gson();
        MainActivity a = (MainActivity) getActivity();
        SpotList sl = a.getServerSpotList();
        String myJson = gson.toJson(sl);
        resultIntent.putExtra("spotlist", myJson);*/
        //resultIntent.putExtra("serverurl", (AlarmPreferences.getServerUrl(getActivity())));


        startActivityForResult(resultIntent, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == ProgramActivity.REQUESTRESULT_ERROR) {
            // non faccio niemnte
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("Errore");
            alertDialogBuilder
                    .setMessage("Impossibile salvare")
                    .setCancelable(false);
            alertDialogBuilder
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }
        if (resultCode == ProgramActivity.REQUESTRESULT_ABORT) {
            // non faccio niemnte
            return;
        }

        String jsonMyObject;
        jsonMyObject = data.getStringExtra("WindAlarmProgram");
        WindAlarmProgram program = new Gson().fromJson(jsonMyObject, WindAlarmProgram.class);
        if (requestCode == ProgramActivity.CREATEPROGRAM_REQUEST) {
            if (resultCode == ProgramActivity.REQUESTRESULT_SAVED) {
                //programListFragment.addProgram(program);
                //programListFragment.
                //programListFragment = new ProgramListFragment();
            } else if (resultCode == ProgramActivity.REQUESTRESULT_DELETED) {
                // non faccio niemnte
            }
        } else if (requestCode == ProgramActivity.EDITPROGRAM_REQUEST) {
            if (resultCode == ProgramActivity.REQUESTRESULT_SAVED) {
                /*AlarmCardItem card = getCardFromId(program.id);
                card.update(program);*/
                //programListFragment.updateProgram(program);
            } else if (resultCode == ProgramActivity.REQUESTRESULT_DELETED) {
                /*AlarmCardItem card = getCardFromId(program.id);
                alarmList.remove(card);
                card.remove();*/
                //programListFragment.deleteProgram(program);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("spotId", spotId);
        outState.putSerializable("userProfile", mProfile);
    }

    public void setSpotDetailsFragment(SpotDetailsFragment fragment) {
        spotDetailsFragment = fragment;
    }


    @Override
    public void processFinishDownloadImage(int index, Bitmap bmp, long windId) {
        //if (bmp != null) {
        //
        spotMeteoDataList.setWebCamImage(spotId, index, bmp, windId);
        spotDetailsFragment.setWebCamImage(index, bmp, windId); /// TODO qui c'è un errore. lo spot del fragment nel frattempo potrebbe cambiare

        //}

    }

    @Override
    public void onSearchSpotClick(Spot spot) {
        addToFavorites(spot.id);
        showSetSpotOrder();
        //searchSpotFragment.
        /*android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(searchSpotFragment);
        ft.commit();
        searchSpotFragment = null;*/
    }

    private void showLocationForecast(Forecast forecast) {


        //new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, spotId, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_LOCATION);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        ForecastFragment forecastFragment = new ForecastFragment();
        forecastFragment.setListener(forecastListener);
        forecastFragment.setForecast(forecast);

        currentFragment = forecastFragment;
        ft.replace(R.id.content_frame, forecastFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    private class requestDataResponse implements AsyncRequestMeteoDataResponse {

        @Override
        public void processFinish(List<MeteoStationData> list, boolean error, String errorMessage) {

            if (error)
                showError(errorMessage);

            if (list == null)
                return;

            // rimuovi spot non più tra i favoriti
            spotMeteoDataList.update(list);

            updateFavoritesMeteoData(list);

            spotMeteoDataList.saveToFile(getApplicationContext());

            //disattiva la progress bar
            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);
        }

        @Override
        public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {

            if (error)
                showError(errorMessage);

            if (spotDetailsFragment != null) {
                // aggiorna la history in memoria con i dati aggiornati
                spotMeteoDataList.setHistory(spotId, list);
                //spotDetailsFragment.setHistoryMeteoData(spotMeteoDataList.getHistory(spotId));
                spotDetailsFragment.setHistoryMeteoData(list);
            }

            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);
        }

        @Override
        public void processFinishSpotList(List<Spot> list, List<Long> favorites, boolean error, String errorMessage) {

            if (error) {
                showError(errorMessage);
                return;
            }

            mSpotList.spotList.clear();
            for (Spot spot : list) {
                for (Long id : favorites) {
                    if (id == spot.id) {
                        spot.favorites = true;
                        break;
                    }
                }
                mSpotList.add(spot);
            }

            if (searchSpotFragment != null) {
                searchSpotFragment.setSpotList(mSpotList);
            }

            if (spotOrderFragment != null) {

                List<Spot> orderedFavoritesSpotList = new ArrayList<>();
                if (favorites != null && favorites.size() > 0) {
                    for (Long id : favorites) {
                        Spot spot = mSpotList.getSpotFromId(id);
                        if (spot != null)
                            orderedFavoritesSpotList.add(spot);
                    }
                }

                spotOrderFragment.setSpotList(orderedFavoritesSpotList);
            }
        }

        @Override
        public void processFinishAddFavorite(long spotId, boolean error, String errorMessage) {

        }

        @Override
        public void processFinishRemoveFavorite(long spotId, boolean error, String errorMessage) {

        }

        @Override
        public void processFinishForecast(int requestId, Forecast forecast, boolean error, String errorMessage) {

            if (requestId == FORECAST_REQUESTID_METEOSTATION) {
                if (forecast == null) {
                    return;
                }
                spotDetailsFragment.setForecast(forecast);
                spotMeteoDataList.setForecast(forecast.spotId, forecast);
            } else if (requestId == FORECAST_REQUESTID_LOCATION) {
                showLocationForecast(forecast);
            }
        }

        @Override
        public void processFinishForecastLocation(List<Location> locations, boolean error, String errorMessage) {

            if (error) {
                showError(errorMessage);
                return;
            }

            if (searchMeteoForecastFragment != null) {
                searchMeteoForecastFragment.setSpotList(locations);
            }
        }

    }

    private void updateFavoritesMeteoData(List<MeteoStationData> list) {

        if (recyclerMeteoCardListFragment == null || list == null || list.size() == 0) return;

        // aggiorna tutte le schede dei favorites
        //panelFragment.setMeteoDataList(list);
        //panelFragment.refreshMeteoData();

        // aggiorna tutte le schede dei favorites
        recyclerMeteoCardListFragment.setMeteoDataList(list);
        recyclerMeteoCardListFragment.refreshMeteoData();

        // aggiorna i dati di spotDetailsFragment
        if (spotDetailsFragment != null) {
            MeteoStationData md = spotMeteoDataList.getLastMeteoData(spotId);
            spotDetailsFragment.setMeteoData(md);
            spotDetailsFragment.refreshData();
        }
    }

    private class LoadImagefromUrl extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... params) {

            if (mProfile.photoUrl != null) {

                String url = mProfile.photoUrl.toString();
                return loadBitmapFromUrl(url);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            super.onPostExecute(resultBitmap);

            //mProfile = new UserProfile();
            /*mProfile.userName = mProfile.userName;
            mProfile.email = mProfile.email;
            mProfile.personId = acct.getId();*/
            mUserNameTextView.setText(mProfile.userName);
            memailTextView.setText(mProfile.email);

            if (resultBitmap != null) {
                int currentBitmapWidth = resultBitmap.getWidth();
                int currentBitmapHeight = resultBitmap.getHeight();
                int ivWidth = mUserImageImageView.getWidth();
                int ivHeight = mUserImageImageView.getHeight();
                int newWidth = ivWidth;
                int newHeight = (int) Math.floor((double) currentBitmapHeight * ((double) newWidth / (double) currentBitmapWidth));

                if (ivWidth > 0) {
                    Bitmap newbitMap = Bitmap.createScaledBitmap(resultBitmap, newWidth, newHeight, true);

                    mUserImageImageView.setImageBitmap(getCircleBitmap(newbitMap));
                    //mProfile.userImage = ((BitmapDrawable) mUserImageImageView.getDrawable()).getBitmap();
                }
            }

            if (profileFragment != null)
                profileFragment.setProfile(mProfile);
        }
    }

    public Bitmap loadBitmapFromUrl(String url) {
        URL newurl = null;
        Bitmap bitmap = null;
        try {
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap circuleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(circuleBitmap);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return circuleBitmap;
    }



    public void saveToFile(Context context) {
        String fileName = "SpotMeteoDataListClass";
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(spotMeteoDataList/*.get(0).md*/);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



