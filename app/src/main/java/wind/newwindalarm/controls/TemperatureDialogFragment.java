package wind.newwindalarm.controls;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import wind.newwindalarm.R;



@SuppressLint("ValidFragment")
public class TemperatureDialogFragment extends DialogFragment {
    Handler mHandler;
    Double temperature = 0.0;
    String message = "";
    int mBand = 0;


    public TemperatureDialogFragment(Handler h) {
        /**
         * Getting the reference to the message handler instantiated in
         * MainActivity class
         */
        mHandler = h;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //int title = getArguments().getInt("title");

        /** Creating a bundle object to pass currently set date to the fragment */
        Bundle b = getArguments();

        /** Getting the day of month from bundle */
        temperature = b.getDouble("set_temperature");
        message = b.getString("set_message");
        mBand = b.getInt("set_band");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.numberpicker_layout, null);
        final NumberPicker np = (NumberPicker) DialogView.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(30);
        np.setWrapSelectorWheel(false);
        np.setValue(temperature.intValue());

        final NumberPicker np2 = (NumberPicker) DialogView.findViewById(R.id.numberPicker2);
        np2.setMinValue(0);
        np2.setMaxValue(9);
        np2.setWrapSelectorWheel(false);
        np2.setValue((int) (temperature*10.0 - temperature.intValue()*10));

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setView(DialogView)
                .setTitle("Temperatura")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        temperature = np.getValue() + np2.getValue() / 10.0;
                        Bundle b = new Bundle();
                        b.putDouble("set_temperature", temperature);
                        b.putInt("set_band", mBand);
                        b.putString("set_temperature_text", "Set temperature : " + temperature.toString());
                        Message m = new Message();
                        m.setData(b);
                        mHandler.sendMessage(m);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();
    }
}