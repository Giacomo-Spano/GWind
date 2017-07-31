package gwind.windalarm.controls;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment{
    Handler mHandler ;
    int mDay;
    int mMonth;
    int mYear;
    String mMessage;
 
    @SuppressLint("ValidFragment")
	public DatePickerDialogFragment(Handler h){

        mHandler = h;
    }
 
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
 
        Bundle b = getArguments();
        mDay = b.getInt("set_day");
        mMonth = b.getInt("set_month");
        mYear = b.getInt("set_year");
        mMessage = b.getString("set_message");

        DatePickerDialog.OnDateSetListener listener  = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDay = dayOfMonth;
                mMonth = monthOfYear + 1;
                mYear = year;
 
                Bundle b = new Bundle();
                b.putInt("set_day", mDay);
                b.putInt("set_month", mMonth);
                b.putInt("set_year", mYear);
                b.putString("set_date", "Set Date : " + Integer.toString(mDay) + " / " + Integer.toString(mMonth+1) + " / " + Integer.toString(mYear));
                Message m = new Message();
                m.setData(b);
 
                mHandler.sendMessage(m);
            }
        };

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), listener, mYear, mMonth-1, mDay);
        dpd.setTitle(mMessage);
        return dpd;
 
        /** Opening the DatePickerDialog window */
        //return new DatePickerDialog(getActivity(), listener, mYear, mMonth, mDay);
    }
}
