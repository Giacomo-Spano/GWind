package wind.newwindalarm;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import wind.newwindalarm.cardui.MeteoCardItem;
import wind.newwindalarm.cardui.MeteoCardListener;

import java.util.List;

public class AlarmFragment extends Fragment implements
        OnItemSelectedListener, MeteoCardListener {

    //private TextView fullscreenTextView;
    private LinearLayout mContainer;
    private Ringtone ringtone;
    public MeteoCardItem mCarditem;
    LineChart mLineChart;

    OnAlarmListener mCallback;

    // Container Activity must implement this interface
    public interface OnAlarmListener {
        public void onStopAlarmClick();

        public void onSnoozeAlarmClick(int snoozeMinutes);

        public void onStartPlayAlarm();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAlarmListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAlarmListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_alarm, container, false);

        mContainer = (LinearLayout) v.findViewById(R.id.container);

        final Button stopButton = (Button) v.findViewById(R.id.stopAlarmButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopAlarm();
            }
        });

        final Button snoozeButton = (Button) v.findViewById(R.id.snoozeAlarmButton);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                snoozeAlarm();
            }
        });


        mLineChart = (LineChart) v.findViewById(R.id.chart);

        return v;
    }

    public void playAlarm(int spot) {


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(this.getActivity(), alarmUri);
        ringtone.play();

        if (mCallback != null)
            mCallback.onStartPlayAlarm();

        getMeteoData(spot);

        HistoryChart hc = new HistoryChart(getActivity(),mLineChart);
        new requestMeteoDataTask(getActivity(), hc).execute(requestMeteoDataTask.REQUEST_HISTORYMETEODATA, "" + spot);
    }

    public void stopRingtone() {
        ringtone.stop();
        //fullscreenTextView.setText("Allarme disattivato");
    }

    public void stopAlarm() {
        stopRingtone();

        mCallback.onStopAlarmClick();
    }

    public void snoozeAlarm() {
        stopRingtone();

        mCallback.onSnoozeAlarmClick(15);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void getMeteoData(final int spotID) {

        final AlarmFragment fragment = this;

        new requestMeteoDataTask(this.getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, boolean error, String errorMessage) {

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

                } else {

                    mCarditem = new MeteoCardItem(fragment, fragment.getActivity(), mContainer);

                    MeteoStationData md = null;
                    for (int i = 0; i < list.size(); i++) {
                        md = (MeteoStationData) list.get(i);
                        if (md.spotID == spotID)
                            break;
                    }
                    mCarditem.spotID = spotID;
                    mCarditem.update(md);
                    mContainer.addView(mCarditem.card);
                    mContainer.invalidate();
                }
            }

            @Override
            public void processFinishHistory(List<Object> list, boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

            }
        }).execute(requestMeteoDataTask.REQUEST_LASTMETEODATA, "" + spotID);
    }

    @Override
    public void meteocardselected(long index) {


        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.GO_DIRECTLY_TO_SPOT_DETAILS, mCarditem.spotID); //Optional parameters
        intent.putExtra("spotID", mCarditem.spotID); //Optional parameters
        startActivity(intent);

    }
}
