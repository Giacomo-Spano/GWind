package wind.newwindalarm;

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
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wind.newwindalarm.data.Location;
import wind.newwindalarm.data.MeteoStationData;
import wind.newwindalarm.data.Forecast;
import wind.newwindalarm.fragment.ForecastFragment;
import wind.newwindalarm.fragment.PanelFragment;
import wind.newwindalarm.fragment.ProfileFragment;
import wind.newwindalarm.fragment.ProgramFragment;
import wind.newwindalarm.fragment.ProgramListFragment;
import wind.newwindalarm.fragment.SearchMeteoForecastFragment;
import wind.newwindalarm.fragment.SearchSpotFragment;
import wind.newwindalarm.fragment.SpotDetailsFragment;
import wind.newwindalarm.fragment.SpotMeteoListFragment;
import wind.newwindalarm.request.requestMeteoDataTask;

import static wind.newwindalarm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static wind.newwindalarm.request.requestMeteoDataTask.FORECAST_OPENWEATHERMAP;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnSignInClickListener,
        //PanelFragment.OnListener,
        //SpotDetailsFragment.OnClickListener,
        ProgramListFragment.OnProgramListListener,
        SearchSpotFragment.OnSearchSpotClickListener,
        DownloadImageTask.AsyncDownloadImageResponse//,
        /*SearchMeteoForecastFragment.OnSearchSpotClickListener,*/
        /*ForecastFragment.OnMeteoForecastClickListener*/ {

    private static final int FORECAST_REQUESTID_METEOSTATION = 99;
    private static final int FORECAST_REQUESTID_LOCATION = 88;


    private List<MeteoStationData> meteoDataList = new ArrayList<>();

    CountDownTimer countDownTimer;
    ProgressBar progressBar;
    private long spotId;

    public MainActivity() {
        searchMeteoForecastListener = new SearchMeteoForecastListener(this);
        panelListener = new PanelListener();
        spotDetailsListener = new SpotDetailsListener();
        forecastListener = new ForecastListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startSendLogActivity() {
        Intent resultIntent = new Intent(this, SendLogActivity.class);
        //startActivityForResult(resultIntent, 1);
        startActivity(resultIntent);
        finish();
    }

    public void SendLoagcatMail() {


        DateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
        String date = df.format(Calendar.getInstance().getTime());

        // save logcat in file
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "logcat" /*-+ date*/ + ".txt");
        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //send file using email
        /*Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"giaggi70@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, outputFile.getAbsolutePath());
        // the mail subject



        String str = outputFile.getAbsolutePath();

        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject " + str);
        startActivity(Intent.createChooser(emailIntent , "Send email..."));*/
    }

    public SearchMeteoForecastListener getSearchMeteoForecastListener() {
        return searchMeteoForecastListener;
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

            new requestMeteoDataTask(/*getParent()*/activity, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, location.id, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_LOCATION);
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
    private String mUsername;
    private String mPhotoUrl;
    public static final String ANONYMOUS = "anonymous";

    private TextView mInformationTextView;
    private static final String TAG = "SignInActivity";
    //public List<Spot> spotList;
    private SpotList mSpotList = new SpotList();
    private Settings mSettings;

    // questi listener devono essere dichiasrati final altrimento quando c'è una rotazione si perde il riferimento
    private final SearchMeteoForecastListener searchMeteoForecastListener;
    private final PanelListener panelListener;
    private final SpotDetailsListener spotDetailsListener;
    private final ForecastListener forecastListener;

    private PanelFragment panelFragment;
    private SpotDetailsFragment spotDetailsFragment;
    private SearchMeteoForecastFragment searchMeteoForecastFragment;
    private ProgramFragment programFragment;
    private ProgramListFragment programListFragment;
    private SettingsFragment settingsFragment;
    private ProfileFragment profileFragment;
    private SpotMeteoListFragment spotMeteoListFragment;
    private SearchSpotFragment searchSpotFragment;
    private SpotMeteoDataList spotMeteoDataList = new SpotMeteoDataList();

    private IntentFilter filter = new IntentFilter(DISPLAY_MESSAGE_ACTION);
    // google properties
    TextView mUserNameTextView;
    TextView memailTextView;
    ImageView mUserImageImageView;

    FloatingActionButton fabButton;

    private UserProfile mProfile = null;
    static boolean signedIn = false;
    static int nextFragment = -1;

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
        mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            //startActivity(new Intent(this, SignInActivity.class));
            //finish();
            //return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();

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


        // carica la spot list per la combo dei programmmi
        //getSpotListFromServer();
        getLastMeteoData();

        panelFragment = new PanelFragment();
        panelFragment.setListener(panelListener);

        programFragment = new ProgramFragment();
        programListFragment = new ProgramListFragment();
        programListFragment.setListener(this);
        settingsFragment = new SettingsFragment();
        settingsFragment.setSettings(mSettings);
        profileFragment = new ProfileFragment();
        spotMeteoListFragment = new SpotMeteoListFragment();
        //searchSpotFragment = new SearchSpotFragment();

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        init();

        if (savedInstanceState != null) {

            //spotDetailsFragment.setListener(this);
            spotId = savedInstanceState.getLong("spotId");

        } else {
            showFragment(R.id.nav_favorites, false);
        }
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

            case R.id.options_diagfile:
                startSendLogActivity();
                return true;
            case R.id.options_searchspot:
                showSearchSpot();
                return true;
            case R.id.options_searchmeteoforecast:
                showSearchMeteoForecast();
                return true;


            /*case R.id.action_settings:
                openSettings();
                return true;*/
            case R.id.action_add:

                //programListFragment.createProgram();
                ;
                return true;
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

        ft.replace(R.id.content_frame, searchMeteoForecastFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    private void showSearchSpot() {

        getSpotListFromServer();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        searchSpotFragment = new SearchSpotFragment();
        searchSpotFragment.setListener(this);

        ft.replace(R.id.content_frame, searchSpotFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

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

                if (mPosition == R.id.nav_favorites && panelFragment != null) {
                    ft.replace(R.id.content_frame, panelFragment);
                    enableRefreshFavoritesMeteoDataButton();
                } else if (mPosition == R.id.nav_program) {
                    ft.replace(R.id.content_frame, programListFragment);
                    enableAddProgramButton();
                } else if (mPosition == R.id.nav_meteostation) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, spotMeteoListFragment);
                } else if (mPosition == R.id.nav_settings) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, settingsFragment);
                } else if (mPosition == R.id.nav_profile) {
                    disableAllButton();
                    ft.replace(R.id.content_frame, profileFragment);
                    profileFragment.setProfile(mProfile);
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

    private void getSpotListFromServer() {

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


    public PanelFragment.OnListener getPanelListener() {
        return panelListener;
    }

    private class PanelListener implements PanelFragment.OnListener {
        /*public PanelListener() {
            panelListener = this;
        }*/

        @Override
        public void onEnablePanelRefreshButtonRequest() {
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

        //Forecast f = spotMeteoDataList.getForecast(spotId);

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, "" + spotId, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_METEOSTATION);
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
        cal.add(Calendar.HOUR_OF_DAY, -10); //minus number would decrement the hours
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
        showSpotDetailsFragment(spot.id);
        //searchSpotFragment.
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(searchSpotFragment);
        ft.commit();
        searchSpotFragment = null;
    }

    private void showLocationForecast(Forecast forecast) {


        //new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_FORECAST).execute(mProfile.personId, spotId, FORECAST_OPENWEATHERMAP, FORECAST_REQUESTID_LOCATION);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        ForecastFragment forecastFragment = new ForecastFragment();
        forecastFragment.setListener(forecastListener);
        forecastFragment.setForecast(forecast);

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

            // aggiorna i dati archiviati
            for (MeteoStationData md : list)
                spotMeteoDataList.setLastMeteoData(md.spotID, md);
            // aggiorna tutte le schede dei favorites
            panelFragment.setMeteoDataList(list);
            panelFragment.refreshMeteoData();
            // aggiorna la spotdetailview
            if (spotDetailsFragment != null) {
                MeteoStationData md = spotMeteoDataList.getLastMeteoData(spotId);
                spotDetailsFragment.setMeteoData(md);
                spotDetailsFragment.refreshData();
            }
            //disattiva la progress bar
            progressBar.setVisibility(View.GONE);
            mInformationTextView.setVisibility(View.GONE);
        }

        @Override
        public void processFinishHistory(long spotId, List<MeteoStationData> list, boolean error, String errorMessage) {

            if (error)
                showError(errorMessage);

            if (spotDetailsFragment != null) {
                spotMeteoDataList.setHistory(spotId, list);
                spotDetailsFragment.setHistoryMeteoData(spotMeteoDataList.getHistory(spotId));
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
                spotDetailsFragment.setForecast(forecast);
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

    private class SpotMeteoData {
        private MeteoStationData md;
        private List<MeteoStationData> history = new ArrayList<MeteoStationData>();
        private Bitmap[] webcamBitmap = new Bitmap[3];
        private long[] webcamWindId = new long[3];
        private long[] webcamImageRequestInprogress = new long[3]; // windid dell'immagine richiesta
        private Forecast forecast;
    }

    private class SpotMeteoDataList {
        List<SpotMeteoData> lastMeteoData = new ArrayList<SpotMeteoData>();


        private SpotMeteoData getFromId(long spotId) {
            if (lastMeteoData == null) return null;
            for (SpotMeteoData smd : lastMeteoData) {
                if (smd.md.spotID == spotId)
                    return smd;
            }
            return null;
        }

        public MeteoStationData getLastMeteoData(long spotId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null)
                return null;
            else
                return smd.md;
        }

        public void setLastMeteoData(long spotId, MeteoStationData md) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
                lastMeteoData.add(smd);
            }
            smd.md = md;
        }

        public List<MeteoStationData> getHistory(long spotId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null || smd.history == null || smd.history.size() == 0)
                return null;
            return smd.history;
        }

        public long getLastHistoryId(long spotId) {
            List<MeteoStationData> list = getHistory(spotId);
            if (list == null) return -1;
            return list.get(list.size() - 1).id;
        }

        public void setHistory(long spotId, List<MeteoStationData> history) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }

            for (MeteoStationData md : history) {
                smd.history.add(md);
            }
        }

        private long getLastWindId() {

            long windId = 0;
            for (SpotMeteoData smd : lastMeteoData) {
                if (smd.md.id > windId)
                    windId = smd.md.id;
            }
            return windId;
        }

        public Bitmap getWebCamImage(long spotId, int index) {

            SpotMeteoData smd = getFromId(spotId);
            if (index < 1 || index > smd.webcamBitmap.length)
                return null;
            return smd.webcamBitmap[index - 1];
        }

        public long getWebCamWindId(long spotId, int index) {

            SpotMeteoData smd = getFromId(spotId);
            if (index < 1 || index > smd.webcamBitmap.length)
                return -1;
            return smd.webcamWindId[index - 1];
        }

        public void setWebCamImage(long spotId, int index, Bitmap bmp, long windId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }
            if (index < 1 || index > smd.webcamBitmap.length)
                return;
            smd.webcamBitmap[index - 1] = bmp;
            smd.webcamWindId[index - 1] = windId;
            smd.webcamImageRequestInprogress[index - 1] = 0;
        }

        public void setWebCamImageRequestInProgress(long spotId, int index, long requestedWindId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }
            if (index < 1 || index > smd.webcamBitmap.length)
                return;
            smd.webcamImageRequestInprogress[index - 1] = requestedWindId;
        }

        public long getWebCamImageRequestInProgressId(long spotId, int index) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }
            if (index < 1 || index > smd.webcamBitmap.length)
                return 0;
            return smd.webcamImageRequestInprogress[index - 1];
        }

        /*public long getLastForecastId(long spotId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                return -1;
            }
            return smd.forecast.id;
        }*/
        public Forecast getForecast(long spotId) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null)
                return null;
            else
                return smd.forecast;
        }

        public void setForecast(long spotId, Forecast f) {
            SpotMeteoData smd = getFromId(spotId);
            if (smd == null) {
                smd = new SpotMeteoData();
            }
            smd.forecast = f;
        }
    }
}
