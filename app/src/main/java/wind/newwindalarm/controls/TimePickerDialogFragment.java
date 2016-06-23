package wind.newwindalarm.controls;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TimePicker;


@SuppressLint("ValidFragment")
public class TimePickerDialogFragment extends DialogFragment{
    Handler mHandler ;
    int mHour;
    int mMinute;
    int mBand;
    String mMessage;
    boolean timeSet = true;
    
    @SuppressLint("ValidFragment")
	public TimePickerDialogFragment(Handler h){
        /** Getting the reference to the message handler instantiated in MainActivity class */
        mHandler = h;
    }

    /** DatePickerDialog's "Set" click listener */
    TimePickerDialog.OnTimeSetListener listener  = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            /// bohhh per qualche motivo non lo chiama quando si preme il  bottone ok o cancel

           /* if(timeSet == false)
            {
                return;
            }
            mHour = hour;
            mMinute = minute;
            Bundle b = new Bundle();
            b.putInt("set_hour", mHour);
            b.putInt("set_minute", mMinute);
            b.putInt("set_band", mBand);
            b.putString("set_time", "Set Time : " + Integer.toString(mHour) + " : " + Integer.toString(mMinute));

            Message m = new Message();
            m.setData(b);
            mHandler.sendMessage(m);*/
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
 
        Bundle b = getArguments();
        mHour = b.getInt("set_hour");
        mMinute = b.getInt("set_minute");
        mBand = b.getInt("set_band");
        mMessage = b.getString("set_message");



        final MyTimePickerDialog tp = new MyTimePickerDialog(getActivity(), listener, mHour, mMinute,true);
        tp.setTitle("ora");
        tp.setMessage(mMessage);
        tp.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    timeSet = true;


                    Bundle b = new Bundle();
                    b.putInt("set_hour", tp.m_hourOfDay);
                    b.putInt("set_minute", tp.m_minute);
                    b.putInt("set_band", mBand);
                    b.putString("set_time", "Set Time : " + Integer.toString(mHour) + " : " + Integer.toString(mMinute));

                    Message m = new Message();
                    m.setData(b);
                    mHandler.sendMessage(m);


                }
            }
        });
        tp.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    timeSet = false;
                }
            }
        });

        return tp;
    }
}
