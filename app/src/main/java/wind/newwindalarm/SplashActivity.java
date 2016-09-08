package wind.newwindalarm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import wind.newwindalarm.fragment.LogoutFragment;

/**
 * Created by Giacomo Spanò on 29/07/2016.
 */
public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        LogoutFragment.OnSignInClickListener {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isReceiverRegistered;

    private static GoogleApiClient mGoogleApiClient;
    private UserProfile mProfile = null;
    private static final int RC_SIGN_IN = 9001;
    public static final int RC_SHOWMAINACTIVITY = 1;
    private static final int RC_SIGNOUT = 2;
    private static final int RC_DISCONNECT = 3;

    public static final int RESULT_SIGN_OUT = 1;
    public static final int RESULT_DISCONNECT = 2;
    public static final int RESULT_EXIT = 3;
    protected static GoogleSignInAccount acct;

    private static SplashActivity instance;


    public static SplashActivity getInstance() {
        return instance;
    }

    public static Context getContext() {

        if (instance != null)
            return instance.getApplicationContext();
        else
            return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        //initGoogleSignin();
        //silentSignIn();

        /*if (!sendLogToMail()) {

            //initGoogleSignin();
            //silentSignIn();

        } else {
            finish();
        }*/

        sendLogToMail();

        initGoogleSignin();
        silentSignIn();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {// TODO attivare le linee sotto per
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                    //mInformationTextView.setVisibility(ProgressBar.GONE);
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                }
                startMainActivity(mProfile);
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    void initGoogleSignin() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

        // Configure sign-in to request offline access to the user's ID, basic
        // profile, and Google Drive. The first time you request a code you will
        // be able to exchange it for an access token and refresh token, which
        // you should store. In subsequent calls, the code will only result in
        // an access token. By asking for profile access (through
        // DEFAULT_SIGN_IN) you will also get an ID Token as a result of the
        // code exchange.
        String serverClientId = "931700652688-vlqjc9s8klmjeti70p52ssnj4orgsdel.apps.googleusercontent.com";//getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId, false)
                .build();

        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        //SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
    }

    private void silentSignIn() {

        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            //updateButtonsAndStatusFromSignInResult(pendingResult.get());
            int i = 0;
            i++;
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            //showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    //updateButtonsAndStatusFromSignInResult(result);
                    //hideProgressIndicator();

                    handleSignInResult(result);
                }
            });
        }
    }

    private void startMainActivity(UserProfile profile) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userProfile", profile);
        startActivityForResult(intent, RC_SHOWMAINACTIVITY);
        //finish();
    }

    boolean sendLogToMail() {

        String line, trace = "";

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(SplashActivity.this
                            .openFileInput("stack.trace"))); // se non esiste Filenotfoundexception
            while ((line = reader.readLine()) != null) {
                trace += line + "\n";
            }


            String address = "giaggi70@gmail.com";
            String subject = "Error report";
            String body =
                    "Giacomo, l'applicazione si è bloccata in questo punto: \n" +
                            "Mail this to appdeveloper@gmail.com: " +
                            "\n" +
                            trace +
                            "\n";

            sendMail(address, subject, body);

            SplashActivity.this.deleteFile("stack.trace");


        } catch (FileNotFoundException fnfe) {

            // se il file non esiste non fare nulla
            return false;
            // ...
        } catch (IOException ioe) {
            // ...
        }

        return true;
    }

    public void sendMail(String address, String subject, String body) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{address});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        //getApplicationContext().startActivity(Intent.createChooser(sendIntent, "Title:"));

        SplashActivity.this.startActivity(
                Intent.createChooser(sendIntent, "Title:"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSignInClick() {
        signIn();
    }

    private void signIn() {

        //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == RC_SHOWMAINACTIVITY) {

            if (resultCode == RESULT_SIGN_OUT) {

                signOut();

            } else if (resultCode == RESULT_DISCONNECT) {

                revokeAccess();

            } else if (resultCode == RESULT_EXIT) {
                finish();
            } else {
                finish();
            }
        } else if (requestCode == RC_SIGNOUT) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            showLoginFragment();
        } else if (requestCode == RC_DISCONNECT) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            showLoginFragment();
        }
    }

    public static GoogleSignInAccount getAcct() {
        return acct;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        boolean signedIn;
        if (result.isSuccess()) {

            acct = result.getSignInAccount();

            mProfile = new UserProfile();
            mProfile.userName = acct.getDisplayName();
            mProfile.email = acct.getEmail();
            mProfile.personId = acct.getId();
            mProfile.photoUrl = acct.getPhotoUrl().toString();

            // Registering BroadcastReceiver
            registerReceiver();
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this/*MainActivity.getContext()*/, RegistrationIntentService.class);
                startService(intent);
            }
            //startMainActivity(mProfile);

        } else {

            mProfile = null;

            showLoginFragment();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("TAG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void showLoginFragment() {
        setContentView(R.layout.activity_splash);

        LogoutFragment logoutFragment = new LogoutFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        //ft.add(logoutFragment,"tag");
        ft.replace(R.id.content_frame, logoutFragment);
        //ft.addToBackStack(null);
        ft.commit();
    }

    private void revokeAccess() {
        /*Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        mProfile = null;

                    }
                });*/

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_DISCONNECT);

    }

    private void signOut() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGNOUT);
    }



    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }


}