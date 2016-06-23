package wind.newwindalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Date;

import wind.newwindalarm.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class PlayAlarmActivity extends AppCompatActivity implements AlarmFragment.OnAlarmListener {

    private static AlarmFragment lastActiveAlarmFragment = null;
    AlarmFragment mAlarmFragment;
    int spotId;
    int alarmId;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_play_alarm);

        getSupportActionBar().hide();

        // Create a new Fragment to be placed in the activity layout
        mAlarmFragment = new AlarmFragment();
        mAlarmFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().add(R.id.content_frame, mAlarmFragment).commit();

        Bundle bundle = getIntent().getExtras();
        spotId = Integer.valueOf(bundle.getString("spotid"));
        alarmId = Integer.valueOf(bundle.getString("alarmid"));

        Bundle b = new Bundle();
        b.putInt("spotid", spotId);
        b.putInt("alarmid", alarmId);
        mAlarmFragment.setArguments(b);

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        if (lastActiveAlarmFragment != null)
            lastActiveAlarmFragment.stopRingtone();
        mAlarmFragment.playAlarm(spotId);
        lastActiveAlarmFragment = mAlarmFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mAlarmFragment.stopAlarm();

        showSpotDetail(0);
    }

    private void showSpotDetail(int snoozeMinutes) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.GO_DIRECTLY_TO_SPOT_DETAILS, true); //Optional parameters
        intent.putExtra("spotId", spotId); //Optional parameters
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("snoozeMinutes", snoozeMinutes);
        startActivity(intent);
    }



    @Override
    public void onStopAlarmClick() {
        showSpotDetail(0);
    }

    @Override
    public void onSnoozeAlarmClick(int snoozeMinutes) {

        snoozeAlarm(snoozeMinutes,alarmId);
        showSpotDetail(snoozeMinutes);
    }

    @Override
    public void onStartPlayAlarm() {

        Date date = new Date();
        //updateAlarmRingDate(date);
        updateAlarmRingDate(date,alarmId);
    }

    private void updateAlarmRingDate(final Date date, final int alarmid) {

        postprogramtask task = (postprogramtask) new postprogramtask(this, new AsyncPostProgramResponse() {
            @Override
            public void processFinish(Object obj, boolean error, String errorMessage) {
                // TODO
            }

        }, postprogramtask.POST_UPDATEALARMRINGDATE).execute(alarmid,date);
    }
    private void snoozeAlarm(final int snoozeMinutes, final int alarmid) {

        new postprogramtask(this, new AsyncPostProgramResponse() {
            @Override
            public void processFinish(Object obj, boolean error, String errorMessage) {
                // TODO
                //finish();
            }

        }, postprogramtask.POST_SNOOZEALARM).execute(alarmid,snoozeMinutes);

    }

}
