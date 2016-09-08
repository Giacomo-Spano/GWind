package wind.newwindalarm;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.text.format.Time;
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import wind.newwindalarm.fragment.PanelFragment;
import wind.newwindalarm.fragment.ProgramFragment;
import wind.newwindalarm.fragment.SpotDetailsFragment;

public class MainActivity extends AppCompatActivity implements
        //GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnSignInClickListener,
        PanelFragment.OnSpotClickListener {

    private List<MeteoStationData> meteoDataList = new ArrayList<>();

    CountDownTimer countDownTimer;
    ProgressBar progressBar;

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

    private TextView mInformationTextView;
    private static final String TAG = "SignInActivity";
    public List<Spot> spotList;
    private Settings mSettings;

    PanelFragment panelFragment;
    ProgramFragment programFragment;
    ProgramListFragment programListFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    SpotMeteoListFragment spotMeteoListFragment;
    SpotDetailsFragment spotDetailsFragment;
    HistoricalMetoData historicalMeteoData = new HistoricalMetoData();
    // google properties
    TextView mUserNameTextView;
    TextView memailTextView;
    ImageView mUserImageImageView;

    FloatingActionButton addFab;
    FloatingActionButton refreshFab;

    private UserProfile mProfile = null;
    static boolean signedIn = false;
    static int nextFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        //instance = this;

        Intent intent = getIntent();
        mProfile = (UserProfile) intent.getSerializableExtra("userProfile");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFab = (FloatingActionButton) findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                programListFragment.createProgram();
            }
        });

        refreshFab = (FloatingActionButton) findViewById(R.id.refreshFab);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastMeteoData();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
        getSpotListFromServer();

        programFragment = new ProgramFragment();
        programListFragment = new ProgramListFragment();
        settingsFragment = new SettingsFragment();
        settingsFragment.setSettings(mSettings);
        profileFragment = new ProfileFragment();
        spotMeteoListFragment = new SpotMeteoListFragment();

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver();
    }

    // TODO ma questo a cosa serve?????
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE/*EXTRA_MESSAGE*/);
            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
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
		 * unregister the device (you will also need to uncomment the equivalent
		 * options on options_menu.xml).*/

            case R.id.options_diagfile:
                startSendLogActivity();
                return false;
            /*case R.id.action_settings:
                openSettings();
                return true;*/
            /*case R.id.action_add:

                programListFragment.createProgram();
                ;
                return true;
            case R.id.action_unregister:
                //ServerUtilities.unregister(AlarmPreferences.getRegId(this), AlarmPreferences.getServerUrl(this));
                unregister();
                return true;*/
            /*case R.id.action_clear:
                deleteFile(messageFragment.messageFileName);
                //new File(messageFragment.messageFileName).delete();
                messageFragment.clearAll();
                return true;*/
            /*case R.id.options_refreshspotdetail:
                // D// Not implemented here, implemented in fragment
                return false;*/
            default:
                return super.onOptionsItemSelected(item);
        }

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

            refreshFab.setVisibility(View.GONE);
            addFab.setVisibility(View.GONE);


            if (!signedIn && mPosition != R.id.nav_settings) {
                nextFragment = mPosition;
                ft.replace(R.id.content_frame, profileFragment);
                profileFragment.setProfile(mProfile);
            } else {

                if (mPosition == R.id.nav_favorites && panelFragment != null) {
                    ft.replace(R.id.content_frame, panelFragment);
                    refreshFab.setVisibility(View.VISIBLE);
                } else if (mPosition == R.id.nav_program) {
                    ft.replace(R.id.content_frame, programListFragment);
                    addFab.setVisibility(View.VISIBLE);
                } else if (mPosition == R.id.nav_meteostation) {
                    ft.replace(R.id.content_frame, spotMeteoListFragment);
                } else if (mPosition == R.id.nav_settings) {
                    ft.replace(R.id.content_frame, settingsFragment);
                } else if (mPosition == R.id.nav_profile) {
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

    String getServerURL() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String syncConnPref = sharedPref.getString(QuickstartPreferences.KEY_PREF_SERVERURL, this.getResources().getString(R.string.pref_serverURL_default));
        return syncConnPref;
    }

    public List<Spot> getServerSpotList() { // TODO Eliminare . usata soloda programFragmentspotlist

        return spotList;
    }

    private void getSpotListFromServer() {

        new requestMeteoDataTask(this, new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishHistory(List<MeteoStationData> list, boolean error, String errorMessage) {
            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

                if (error) {
                    showError(errorMessage);
                    return;
                }

                spotList = new ArrayList<Spot>();
                for (int i = 0; i < list.size(); i++) {
                    spotList.add((Spot) list.get(i));
                }

                //settingsFragment.setServerSpotList(spotList);
                programListFragment.setServerSpotList(spotList);
                //spotMeteoListFragment.setSpotList(spotList);
            }
        }, requestMeteoDataTask.REQUEST_SPOTLIST).execute();

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

    public String getSpotName(long id) {

        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id)
                return spotList.get(i).spotName;
        }
        return null;
    }

    public Spot getSpotFromId(long id) {

        if (spotList == null)
            return null;

        for (int i = 0; i < spotList.size(); i++) {
            if (spotList.get(i).id == id) {

                return (Spot) spotList.get(i);
            }
        }
        return null;
    }

    public MeteoStationData getMeteodataFromId(long spotId) {

        if (meteoDataList == null)
            return null;

        Iterator iterator = meteoDataList.iterator();
        while (iterator.hasNext()) {
            MeteoStationData md = (MeteoStationData) iterator.next();
            if (md.spotID == spotId)
                return md;
        }
        return null;
    }

    /*public static GoogleSignInAccount getAcct() {
        return acct;
    }*/

    private void init(/*GoogleSignInAccount acc*/) {

        signedIn = true;

        new LoadImagefromUrl().execute();
        //authCode = acct.getServerAuthCode();

        //mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
            /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        mInformationTextView.setText(getString(R.string.gcm_send_message));
                        mInformationTextView.setVisibility(ProgressBar.GONE);
                    } else {
                        mInformationTextView.setText(getString(R.string.token_error_message));
                    }
                }
            };*/
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);


        // Registering BroadcastReceiver
            /*registerReceiver();
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(MainActivity.getContext(), RegistrationIntentService.class);
                startService(intent);
            }*/

        getLastMeteoData();
        //showFragment(R.id.nav_favorites);
        //panelFragment.refreshMeteoData(meteoDataList);


        //unregisterReceiver();

    }


    private void unregister() {
        AlarmPreferences.deleteRegId(this);
        AlarmPreferences.deletePersonId(this);
    }

    private void showNoUser() {
        mUserNameTextView.setText("no user");
        memailTextView.setText("no email");
        mUserImageImageView.setImageResource(R.drawable.user_white);

        if (profileFragment != null) {
            profileFragment.setProfile(null);

        }
    }

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/


    @Override
    public void onSpotClick(long spotId) {

        spotDetailsFragment = new SpotDetailsFragment();
        spotDetailsFragment.setMeteoData(getMeteodataFromId(spotId));
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

        List<MeteoStationData> list = historicalMeteoData.getFromId(spotId);
        if (list != null) {
            spotDetailsFragment.setHistoryMeteoData(list);
        } else {

            getHistoryMeteoData(spotId);
        }
    }

    @Override
    public void onClick(View view) {

    }


    public void getLastMeteoData() {


        startProgressBar();

        Set<String> favorites = AlarmPreferences.getSpotListFavorites();
        if (favorites.size() == 0)
            return;

        String spotList = "";
        Iterator iter = favorites.iterator();
        while (iter.hasNext()) {
            long id = Long.valueOf((String) iter.next());
            spotList += id;
            if (iter.hasNext())
                spotList += ",";
        }

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_LASTMETEODATA).execute(spotList);


        /*countDownTimer = new CountDownTimer(50000, 3000){

            private int progress = 10;
            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                progressBar.setProgress(progress);
                progress += 10;
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }

        }.start();*/

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

        Date end = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.add(Calendar.HOUR_OF_DAY, -10); //minus number would decrement the hours
        Date start = cal.getTime();

        startProgressBar();

        new requestMeteoDataTask(this, new requestDataResponse(), requestMeteoDataTask.REQUEST_LOGMETEODATA).execute(spotId, start, end);
    }

    @Override
    public void onSignInClick() {

    }

    @Override
    public void onSignOutClick() {
        unregister();
        //unregisterReceiver();
        setResult(SplashActivity.RESULT_SIGN_OUT);
        finish();
    }

    @Override
    public void onDisconnectClick() {
        unregister();
        //unregisterReceiver();
        setResult(SplashActivity.RESULT_DISCONNECT);
        finish();
    }

    private class requestDataResponse implements AsyncRequestMeteoDataResponse {

        @Override
        public void processFinish(List<Object> list, boolean error, String errorMessage) {

            if (panelFragment == null) {
                panelFragment = new PanelFragment();
                showFragment(R.id.nav_favorites, false);
            }
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                MeteoStationData md = (MeteoStationData) iterator.next();
                meteoDataList.add(md);
            }
            panelFragment.setMeteoDataList(meteoDataList);
            panelFragment.refreshMeteoData();

            //countDownTimer.cancel();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void processFinishHistory(List<MeteoStationData> list, boolean error, String errorMessage) {

            if (spotDetailsFragment != null)
                spotDetailsFragment.setHistoryMeteoData(list);

            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

            progressBar.setVisibility(View.GONE);

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
                Bitmap newbitMap = Bitmap.createScaledBitmap(resultBitmap, newWidth, newHeight, true);

                mUserImageImageView.setImageBitmap(getCircleBitmap(newbitMap));
                mProfile.userImage = ((BitmapDrawable) mUserImageImageView.getDrawable()).getBitmap();
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
}
