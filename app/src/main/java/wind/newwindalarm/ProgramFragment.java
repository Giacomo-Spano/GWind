package wind.newwindalarm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import wind.newwindalarm.controls.baseOnClickAndFocusChangeListener;

public class ProgramFragment extends Fragment implements OnItemSelectedListener {

 WindAlarmProgram program = new WindAlarmProgram();
    private String[] arraySpinner;

    /**
     * This handles the message send from DatePickerDialogFragment on setting
     * date
     */
    @SuppressLint("HandlerLeak")
    Handler mstartDateHandler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            int mDay = b.getInt("set_day");
            int mMonth = b.getInt("set_month");
            int mYear = b.getInt("set_year");

            String formattedString = String.format("%02d/%02d/%04d", mDay, mMonth, mYear);
            mStartdate.setText(formattedString);
        }
    };
    Handler mendDateHandler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            int mDay = b.getInt("set_day");
            int mMonth = b.getInt("set_month");
            int mYear = b.getInt("set_year");
            String formattedString = String.format("%02d/%02d/%04d", mDay, mMonth, mYear);
            mEnddate.setText(formattedString);
        }
    };
    Handler mSpeedHandler = new Handler() {

        //public int band;

        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            Double speed = b.getDouble("set_temperature");
            mSpeed.setText(speed.toString());
        }
    };
    Handler mAvSpeedHandler = new Handler() {

        //public int band;

        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            Double speed = b.getDouble("set_temperature");
            mAvSpeed.setText(speed.toString());
        }
    };
    Handler mProgramStartTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            int mHour = b.getInt("set_hour");
            int mMinute = b.getInt("set_minute");
            //int mBand = b.getInt("set_band");

            String time = String.format("%02d:%02d", mHour, mMinute);
            mStartTime.setText(time);
        }
    };
    Handler mProgramEndTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            int mHour = b.getInt("set_hour");
            int mMinute = b.getInt("set_minute");
            //int mBand = b.getInt("set_band");

            String time = String.format("%02d:%02d", mHour, mMinute);
            mEndTime.setText(time);
        }
    };
    private TextView mId;
    private EditText mStartdate;
    private EditText mEnddate;
    private EditText mStartTime;
    private EditText mEndTime;
    private EditText mSpeed;
    private EditText mAvSpeed;
    private CheckBox mMonday;
    private CheckBox mTuesday;
    private CheckBox mWednesday;
    private CheckBox mThursday;
    private CheckBox mFriday;
    private CheckBox mSaturday;
    private CheckBox mSunday;
    private Spinner mSpot;

    private List<Spot> mSpotList;
    ArrayList<Long> spotIdList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = loadProgramFragment(inflater, container/*, items*/);

        return v;
    }

    public void setServerSpotList(List<Spot> list) {
        mSpotList = list;
    }

    private View loadProgramFragment(LayoutInflater inflater,
                                     ViewGroup container/*, String[] items*/) {

        if (mSpotList == null) {
            showError();
            return null;
        }


        View v;
        v = inflater.inflate(R.layout.fragment_program, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_content);

        mId = (TextView) v.findViewById(R.id.textViewID);
        mMonday = (CheckBox) v.findViewById(R.id.mondayCheckBox);
        mTuesday = (CheckBox) v.findViewById(R.id.tuesdayCheckBox);
        mWednesday = (CheckBox) v.findViewById(R.id.wednesdayCheckBox);
        mThursday = (CheckBox) v.findViewById(R.id.thursdayCheckBox);
        mFriday = (CheckBox) v.findViewById(R.id.fridayCheckBox);
        mSaturday = (CheckBox) v.findViewById(R.id.saturdayCheckBox);
        mSunday = (CheckBox) v.findViewById(R.id.sundayCheckBox);

        mStartdate = (EditText) v.findViewById(R.id.startdateEditText);
        mEnddate = (EditText) v.findViewById(R.id.enddateEditText);

        mSpeed = (EditText) v.findViewById(R.id.editTextSpeed);
        mAvSpeed = (EditText) v.findViewById(R.id.editTextAvSpeed);

        mStartdate.setInputType(InputType.TYPE_NULL);
        mStartdate.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMSTARTDATE, 0, "set program start date"));
        mStartdate.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMSTARTDATE, 0, "set program start date"));

        mEnddate.setInputType(InputType.TYPE_NULL);
        mEnddate.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMENDDATE, 0, "set program start date"));
        mEnddate.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMENDDATE, 0, "set program start date"));

        mStartTime = (EditText) v.findViewById(R.id.startTimeEditText);
        mStartTime.setInputType(InputType.TYPE_NULL);
        mStartTime.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMSTARTTIME, 0, "set program start time"));
        mStartTime.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMSTARTTIME, 0, "set program start time"));

        mEndTime = (EditText) v.findViewById(R.id.endTimeEditText);
        mEndTime.setInputType(InputType.TYPE_NULL);
        mEndTime.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMENDTIME, 0, "set program end time"));
        mEndTime.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.PROGRAMENDTIME, 0, "set program end time"));

        mSpeed.setInputType(InputType.TYPE_NULL);
        mSpeed.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.SPEED, 0, "set program min speed"));
        mSpeed.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.SPEED, 0, "set program min speed"));

        mAvSpeed.setInputType(InputType.TYPE_NULL);
        mAvSpeed.setOnClickListener(new programBandOnClickAndFocusChangeListener(pickerType.AVSPEED, 0, "set program min speed"));
        mAvSpeed.setOnFocusChangeListener(new programBandOnClickAndFocusChangeListener(pickerType.AVSPEED, 0, "set program min speed"));

        mSpot = (Spinner) v.findViewById(R.id.spinnerSpot) ;
        //List<Spot> sl = MainActivity.getSpotList();
        ArrayList<String> list = new ArrayList<String>();
        spotIdList = new ArrayList<Long>();

        for (int i= 0; i<mSpotList.size();i++) {

            list.add(mSpotList.get(i).name);
            spotIdList.add(i,mSpotList.get(i).id);

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this.getActivity(),android.R.layout.simple_dropdown_item_1line, list);
        mSpot.setAdapter(adapter);
        mSpot.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        tv.requestFocus();
        updateProgramFragment();

        return v;
    }

    private int getIndexFromSpotId(long spotid) {
        for (int i = 0; i < spotIdList.size(); i++) {
            if (spotIdList.get(i) == spotid)
                return i;
        }
        return -1;
    }

    private void showTimePicker(int hour, int minute, int band, String message, Handler handler) {
        ProgramActivity act = (ProgramActivity) getActivity();
        act.showTimePickerDialog(hour, minute, band, message, handler/*mTimeHandler*/);
    }

    private void showSpeedPicker(double speed, String message, Handler handler) {
        ProgramActivity act = (ProgramActivity) getActivity();
        act.showSpeedPickerDialog(speed, message, handler);
    }

    private void showDatePicker(int day, int month, int year, String message, Handler handler) {
        ProgramActivity act = (ProgramActivity) getActivity();
        act.showDatePickerDialog(day, month, year, message, handler);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        if (program != null) {

            updateProgramFragment();
        }
    }

    private void updateProgramFragment() {

        mId.setText("" + program.id);
        mSpeed.setText(program.speed.toString());
        mAvSpeed.setText(program.avspeed.toString());
        mStartdate.setText(program.startDate);
        mEnddate.setText(program.endDate);
        mStartTime.setText(program.startTime);
        mEndTime.setText(program.endTime);
        mMonday.setChecked(program.mo);
        mTuesday.setChecked(program.tu);
        mWednesday.setChecked(program.we);
        mThursday.setChecked(program.th);
        mFriday.setChecked(program.fr);
        mSaturday.setChecked(program.sa);
        mSunday.setChecked(program.su);
        mSpot.setSelection(getIndexFromSpotId(program.spotId));
    }

    @SuppressLint("HandlerLeak")
    public WindAlarmProgram saveProgram() {

        program.startTime = mStartTime.getText().toString();
        program.endTime = mEndTime.getText().toString();
        program.speed = Double.valueOf(mSpeed.getText().toString());
        program.avspeed = Double.valueOf(mAvSpeed.getText().toString());

        program.startDate = mStartdate.getText().toString();
        program.endDate = mEnddate.getText().toString();

        program.lastRingDate = "--/--/----"; // questa data viene azzrata dal server nella post

        program.direction = "N";
        program.enabled = true;
        program.id = Long.valueOf(mId.getText().toString());

        program.mo = mMonday.isChecked();
        program.tu = mTuesday.isChecked();
        program.we = mWednesday.isChecked();
        program.th = mThursday.isChecked();
        program.fr = mFriday.isChecked();
        program.sa = mSaturday.isChecked();
        program.su = mSunday.isChecked();

        program.spotId = spotIdList.get(mSpot.getSelectedItemPosition());

        Calendar startdate = Calendar.getInstance();
        Calendar enddate = Calendar.getInstance();
        Calendar starttime = Calendar.getInstance();
        Calendar endtime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        try {
            startdate.setTime(sdf.parse(program.startDate));
            enddate.setTime(sdf.parse(program.endDate));
            starttime.setTime(stf.parse(program.startTime));
            endtime.setTime(stf.parse(program.endTime));

            boolean error = false;
            String errorMessage = "";
            if (startdate.after(enddate)) {
                error = true;
                errorMessage = "Data inizio successiva data fine";
            }
            if (starttime.after(endtime)) {
                error = true;
                errorMessage = "Ora inizio successiva data fine";
            }
            if (error) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle("Errore");
                alertDialogBuilder
                        .setMessage(errorMessage)
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
                return null;
            } else {

                return program;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @SuppressLint("HandlerLeak")
    public void setWebduinoPrograms(WindAlarmProgram prgms) {

        program = prgms;

        //updateProgramFragment();
        //enableAllControls(true);
    }

    private enum pickerType {
        AVSPEED,
        SPEED,
        PROGRAMSTARTDATE,
        PROGRAMSTARTTIME,
        PROGRAMENDDATE,
        PROGRAMENDTIME;
    }

    private class programBandOnClickAndFocusChangeListener extends baseOnClickAndFocusChangeListener {

        public final String dateType = "date";
        public int band;
        public String message;
        private pickerType type = pickerType.SPEED;

        private programBandOnClickAndFocusChangeListener(pickerType type, int band, String message) {
            super();
            this.band = band;
            this.message = message;
            this.type = type;
        }

        @Override
        public void showPicker(View view) {

            if (type == pickerType.PROGRAMSTARTDATE) {

                String date = mStartdate.getText().toString();
                String[] split = date.split("/");
                showDatePicker(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), message, mstartDateHandler);

            } else if (type == pickerType.PROGRAMSTARTTIME) {

                String time = mStartTime.getText().toString();
                String[] split = time.split(":");
                showTimePicker(Integer.parseInt(split[0]), Integer.parseInt(split[1]), band, message, mProgramStartTimeHandler);

            } else if (type == pickerType.PROGRAMENDDATE) {

                String date = mEnddate.getText().toString();
                String[] split = date.split("/");
                showDatePicker(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), message, mendDateHandler);

            } else if (type == pickerType.PROGRAMENDTIME) {

                String time = mEndTime.getText().toString();
                String[] split = time.split(":");
                showTimePicker(Integer.parseInt(split[0]), Integer.parseInt(split[1]), band, message, mProgramEndTimeHandler);

            } else if (type == pickerType.SPEED) {

                String speed = mSpeed.getText().toString();
                showSpeedPicker(Double.parseDouble(speed), message, mSpeedHandler);

            } else if (type == pickerType.AVSPEED) {

                String speed = mAvSpeed.getText().toString();
                showSpeedPicker(Double.parseDouble(speed), message, mAvSpeedHandler);

            }

        }
    }
    private void showError() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Errore");
        alertDialogBuilder
                .setMessage("server offline")
                .setCancelable(false);
        alertDialogBuilder
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}