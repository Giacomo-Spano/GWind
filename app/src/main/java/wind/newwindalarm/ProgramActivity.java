package wind.newwindalarm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import wind.newwindalarm.controls.DatePickerDialogFragment;
import wind.newwindalarm.controls.SpeedDialogFragment;
import wind.newwindalarm.controls.TimePickerDialogFragment;
import wind.newwindalarm.fragment.ProgramFragment;
import wind.newwindalarm.request.postprogramtask;

public class ProgramActivity extends AppCompatActivity {

    public static final int EDITPROGRAM_REQUEST = 1;
    public static final int CREATEPROGRAM_REQUEST = 2;
    public static final int REQUESTRESULT_SAVED = 1;
    public static final int REQUESTRESULT_DELETED = 2;
    public static final int REQUESTRESULT_ERROR = 3;
    public static final int REQUESTRESULT_ABORT = 4;

    protected ProgramFragment programFragment;
    protected String mServerUrl;
    FloatingActionButton saveProgramFab;
    FloatingActionButton deleteProgramFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_program);

        // Display the fragment as the main content.
        programFragment = new ProgramFragment();

        Bundle data2 = new Bundle();
        data2.putInt("position", 1);
        programFragment.setArguments(data2);
        getFragmentManager().beginTransaction()
                .replace(/*android.R.id.content*/R.id.content_frame, programFragment)
                .commit();

        String jsonMyObject;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("WindAlarmProgram");
            WindAlarmProgram program = new Gson().fromJson(jsonMyObject, WindAlarmProgram.class);

            if (getIntent().getStringExtra("spotlist") != null) {
                Gson gson = new Gson();
                SpotList list = gson.fromJson(getIntent().getStringExtra("spotlist"), SpotList.class);
                programFragment.setServerSpotList(list.spotList);
            } else {
                /*String spotId = pro//extras.getString("spotid");
                long id = Long.valueOf(spotId);
                programFragment.setSpotId(id);*/
            }

            //programFragment.setSpotId(program.spotId);
            programFragment.setProgram(program);
            //getActionBar().setTitle("Programma " + program.id);
            getSupportActionBar().setTitle("Programma " + program.id);


            //mServerUrl = getIntent().getStringExtra("serverurl");
            mServerUrl = AlarmPreferences.getServerUrl(this);
        }
        saveProgramFab = (FloatingActionButton) findViewById(R.id.saveProgramFab);
        saveProgramFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postProgram(false);
            }
        });
        saveProgramFab.setVisibility(View.VISIBLE);

        deleteProgramFab = (FloatingActionButton) findViewById(R.id.deleteProgramFab);
        deleteProgramFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postProgram(true);
            }
        });
        deleteProgramFab.setVisibility(View.VISIBLE);


        //saveProgramFab.setVisibility(View.VISIBLE);
        //deleteProgramFab.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.program, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                postProgram(false);
                return true;
            case R.id.action_delete:
                postProgram(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postProgram(final boolean deletekey) {

        final int postType;
        if (deletekey)
            postType = postprogramtask.POST_DELETEALARM;
        else
            postType = postprogramtask.POST_ALARM;

        WindAlarmProgram program = programFragment.saveProgram();
        if (program == null) // c'e stato un errore nel salvataggio
            return;
        postprogramtask task = (postprogramtask) new postprogramtask(this, new AsyncPostProgramResponse() {

            @Override
            public void processFinish(Object obj, boolean error, String errorMessage) {

                WindAlarmProgram program = (WindAlarmProgram) obj;
                if (error) {

                    setResult(ProgramActivity.REQUESTRESULT_ERROR, null);
                } else {
                    Intent output = new Intent();
                    output.putExtra("WindAlarmProgram", new Gson().toJson(program));

                    if (postType == postprogramtask.POST_DELETEALARM)
                        setResult(ProgramActivity.REQUESTRESULT_DELETED, output);
                    else if  (postType == postprogramtask.POST_ALARM)
                        setResult(ProgramActivity.REQUESTRESULT_SAVED, output);

                }
                finish();
            }
        }, postType).execute(program,mServerUrl);
    }

    public void onBackPressed() {

        setResult(ProgramActivity.REQUESTRESULT_ABORT, null);
        finish();
        // super.onBackPressed();
        // myFragment.onBackPressed();
    }

    public void showDatePickerDialog(int mDay, int mMonth, int mYear, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putInt("set_day", mDay);
        b.putInt("set_month", mMonth);
        b.putInt("set_year", mYear);
        b.putString("set_message", message);

        DatePickerDialogFragment datePicker = new DatePickerDialogFragment(
                mHandler);

        datePicker.setArguments(b);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(datePicker, message);
        ft.commit();
    }

    public void showTimePickerDialog(int mHour, int mMinute, int mBand, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putInt("set_hour", mHour);
        b.putInt("set_minute", mMinute);
        b.putInt("set_band", mBand);
        b.putString("set_message", message);

        TimePickerDialogFragment timePicker = new TimePickerDialogFragment(
                mHandler);
        timePicker.setArguments(b);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(timePicker, message/*"time_picker"*/);
        ft.commit();
    }

    public void showSpeedPickerDialog(double speed, String message, Handler mHandler) {

        Bundle b = new Bundle();
        b.putDouble("set_speed", speed);
        b.putString("set_message", message);
        SpeedDialogFragment numberPicker = new SpeedDialogFragment(mHandler);
        numberPicker.setArguments(b);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(numberPicker, message);
        ft.commit();
    }
}