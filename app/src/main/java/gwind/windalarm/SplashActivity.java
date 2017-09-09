package gwind.windalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import gwind.windalarm.fragment.LogoutFragment;

//import gwind.windalarm.fragment.LogoutFragment;

/**
 * Created by Giacomo Spanò on 29/07/2016.
 */
public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        LogoutFragment.OnSignInClickListener {

    private static final String TAG = "SplashActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isReceiverRegistered;

    public static GoogleApiClient mGoogleApiClient;
    private UserProfile mProfile = null;
    private static final int RC_SIGN_IN = 9001;
    public static final int RC_SHOWMAINACTIVITY = 1;
    //private static final int RC_SIGNOUT = 2;
    //private static final int RC_DISCONNECT = 3;

    public static final int RESULT_SIGN_OUT = 1;
    public static final int RESULT_DISCONNECT = 2;
    public static final int RESULT_EXIT = 3;
    protected static GoogleSignInAccount acct;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    private static SplashActivity instance;

    private long spotId = -1;

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

        String spotIdStr = getIntent().getStringExtra("spotId");
        if (spotIdStr != null) {
            spotId = Integer.valueOf(spotIdStr);
        }

        if (checkPlayServices()) {
            Log.i("TAG", "checkPlayServices ok");
        }







        initGoogleSignin();

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {

            mProfile = new UserProfile();
            mProfile.userName = mFirebaseUser.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            mProfile.email = mFirebaseUser.getEmail();
            mProfile.personId = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mProfile.photoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            if (checkPlayServices()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("userProfile", mProfile); //
                startService(intent);
                startMainActivity(mProfile);
            }


        } else {
            showLoginFragment();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {// TODO attivare le linee sotto per
                                // servono per indicare che la registrazione non è arrivata e potrebbero non suonare le sveglie
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                    //mInformationTextView.setVisibility(ProgressBar.GONE);
                    CommonUtilities.sendMessageToMainActivity(context, "title", "Registrazione completata");
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                    CommonUtilities.sendMessageToMainActivity(context, "title", "Errore: registrazione non completata");
                }
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
        // unregisterReceiver è commentato perchè altrimenti se il messaggio
        // QuickstartPreferences.REGISTRATION_COMPLETE arriva quando l'activity
        // MainActivity è già partita allora non arriva mai
        //registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver();
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
        //String serverClientId = "931700652688-vlqjc9s8klmjeti70p52ssnj4orgsdel.apps.googleusercontent.com";
        //String serverClientId = "931700652688-abt1att0a5f102v2o7fgqm37m62ik79f.apps.googleusercontent.com";//getString(R.string.server_client_id);
        //String serverClientId = "967167304447-5s89prfr3fll088m6135rk7g02jf8pvp.apps.googleusercontent.com";
        String serverClientId = "546111443616-6k2ubm9q79k44oee8ihidnmjrvlptpuo.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId, false)
                .requestIdToken(serverClientId)
                .requestEmail()
                .requestId()
                .build();
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();*/

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

    private void startMainActivity(UserProfile profile) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userProfile", profile);
        intent.putExtra("spotId", ""+spotId);
        startActivityForResult(intent, RC_SHOWMAINACTIVITY);
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
                unregisterReceiver();
                finish();
            } else {
                unregisterReceiver();
                finish();
            }
        } /*else if (requestCode == RC_SIGNOUT) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            showLoginFragment();
        } else if (requestCode == RC_DISCONNECT) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            showLoginFragment();
        }*/
    }

    public static GoogleSignInAccount getAcct() {
        return acct;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("TAG", "handleSignInResult:" + result.isSuccess());
        boolean signedIn;
        if (result.isSuccess()) {

            // Se il signin è ok controlla che siano installati
            // i playservices di google e avvia l'intent RegistrationIntentService
            // Se la registration ok viuene chiamato in modo async mRegistrationBroadcastReceiver
            // che poi avvia la MainActivity

            acct = result.getSignInAccount();

            firebaseAuthWithGoogle(acct);

            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            /*mProfile = new UserProfile();
            mProfile.userName = mFirebaseUser.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            mProfile.email = mFirebaseUser.getEmail();
            mProfile.personId = mFirebaseUser.getUid();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mProfile.photoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            // Registering BroadcastReceiver
            registerReceiver();
            if (checkPlayServices()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("userProfile", mProfile);
                startService(intent);
                startMainActivity(mProfile);
            }*/

        } else {

            Log.i("TAG", "handleSignInResult: error" + result.toString());
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
                //finish();
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
        ft.replace(R.id.content_frame, logoutFragment);
        //ft.addToBackStack(null);
        ft.commit();
    }

    private void revokeAccess() {

        //mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseAuth.signOut();

        //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //startActivityForResult(signInIntent, RC_DISCONNECT);

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
                    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);

                    //handleSignInResult(result);
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.signOut();

                    showLoginFragment();

                }
            });
        }
    }

    private void signOut() {

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
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                    //handleSignInResult(result);
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.signOut();

                    showLoginFragment();

                }
            });
        }
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            //Toast.makeText(SignInActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                        } else {
                            //startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            //finish();
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                            mProfile = new UserProfile();
                            mProfile.userName = mFirebaseUser.getDisplayName();
                            //String personGivenName = acct.getGivenName();
                            //String personFamilyName = acct.getFamilyName();
                            mProfile.email = mFirebaseUser.getEmail();
                            mProfile.personId = mFirebaseUser.getUid();
                            if (mFirebaseUser.getPhotoUrl() != null) {
                                mProfile.photoUrl = mFirebaseUser.getPhotoUrl().toString();
                            }



                            startMainActivity(mProfile);
                        }
                    }
                });
    }
}