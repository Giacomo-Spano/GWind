package gwind.windalarm.controls;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TimePicker;

/**
 * Created by Giacomo on 28/12/2014.
 */
public class MyTimePickerDialog extends TimePickerDialog {

    public int m_hourOfDay;
    public int m_minute;

    public MyTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);

        m_hourOfDay = hourOfDay;
        m_minute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);

        m_hourOfDay = hourOfDay;
        m_minute = minute;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
    }
}
