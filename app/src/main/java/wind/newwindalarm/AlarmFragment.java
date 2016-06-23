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

import wind.newwindalarm.cardui.MeteoCardItem;
import wind.newwindalarm.cardui.MeteoCardListener;

import java.util.List;

public class AlarmFragment extends Fragment implements
        OnItemSelectedListener, MeteoCardListener {

    //static boolean ringActive = false;
    private TextView fullscreenTextView;
    private LinearLayout mContainer;
    private Ringtone ringtone;
    public MeteoCardItem mCarditem;

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
        fullscreenTextView = (TextView) v.findViewById(R.id.textView17);

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

        Bundle data=getArguments();

        int spotID = data.getInt("spotid");
        TextView tv = (TextView) v.findViewById(R.id.spotName);
        tv.setText(MainActivity.getSpotName(spotID));

        int alarmID = data.getInt("alarmid");
        tv = (TextView) v.findViewById(R.id.alarmIdTextView);
        tv.setText(MainActivity.getSpotName(alarmID));

        String startDate = data.getString("startDate");
        String startTime = data.getString("startTime");
        String lastRingTime = data.getString("lastRingTime");
        String endDate = data.getString("endDate");
        String endTime = data.getString("endTime");
        Double curspotId = data.getDouble("curspotId");

        return v;
    }

    public void playAlarm(int spot) {

        /// va in crash!!!!
        ///getMeteoData(spot);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(this.getActivity(), alarmUri);
        ringtone.play();
        fullscreenTextView.setText("Allarme attivo");

        if (mCallback != null)
            mCallback.onStartPlayAlarm();    }

    public void stopRingtone() {
        ringtone.stop();
        fullscreenTextView.setText("Allarme disattivato");
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

    public void getMeteoData(final int spot) {

        final AlarmFragment fragment = this;

        requestMeteoDataTask task = (requestMeteoDataTask) new requestMeteoDataTask(this.getActivity(), new AsyncRequestMeteoDataResponse() {

            @Override
            public void processFinish(List<Object> list, /*long spotID,  */boolean error, String errorMessage) {

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


                    //final MeteoCardItem mCarditem;
                    mCarditem = new MeteoCardItem(fragment, fragment.getActivity(), mContainer);

                    //MeteoCard meteocarditem = (MeteoCard) mContainer.findViewById(R.id.meteocarditem);
                    //meteocarditem

                    MeteoStationData md = null;
                    for (int i = 0; i < list.size(); i++) {
                        md = (MeteoStationData) list.get(i);
                        if (md.spotID == spot)
                            break;
                    }
                    mCarditem.spotID = spot;

                    mCarditem.update(md);
                    mContainer.addView(mCarditem.card);
                    mContainer.invalidate();

                }
            }

            @Override
            public void processFinishHistory(List<Object> list, /*long spotID, */boolean error, String errorMessage) {

            }

            @Override
            public void processFinishSpotList(List<Object> list, boolean error, String errorMessage) {

            }
        }).execute(false, true, false, spot);
    }

    @Override
    public void meteocardselected(long index) {

        //Fragment spotDetail = new SpotDetailFragment();

        /*Bundle data = new Bundle();
        data.putLong("spotID", mCarditem.spotID);
        spotDetail.setArguments(data);*/

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.GO_DIRECTLY_TO_SPOT_DETAILS, mCarditem.spotID); //Optional parameters
        intent.putExtra("spotID", mCarditem.spotID); //Optional parameters
        startActivity(intent);

        //FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        //transaction.replace(R.id.content_frame, spotDetail);
        //transaction.addToBackStack(null);

        // Commit the transaction
        //transaction.commit();
    }
}
