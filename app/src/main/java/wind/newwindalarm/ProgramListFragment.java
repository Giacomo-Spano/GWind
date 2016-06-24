package wind.newwindalarm;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import wind.newwindalarm.cardui.AlarmCard;
import wind.newwindalarm.cardui.AlarmCardSubitem;

public class ProgramListFragment extends Fragment implements
        OnItemSelectedListener {

    public static final int SHOWPROGRAM_REQUEST = 1;
    public static final int CREATEPROGRAM_REQUEST = 2;
    public static final int REQUESTRESULT_SAVED = 1;
    public static final int REQUESTRESULT_DELETED = 2;
    public static final int REQUESTRESULT_ERROR = 3;
    public static final int REQUESTRESULT_ABORT = 4;

    private boolean offline = false;

    //int position;
    View programListView;
    LinearLayout mcontainer;
    LinearLayout mErrorLayout;
    private List<Spot> mSpotList;

    List<AlarmCardItem> alarmList = new ArrayList<AlarmCardItem>();

    public void setServerSpotList(List<Spot> list) {

        mSpotList = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //position = getArguments().getInt("position");
        String[] items = getResources().getStringArray(R.array.opzioni);
        programListView = inflater.inflate(R.layout.fragment_programlist, container, false);

        mcontainer = (LinearLayout) programListView.findViewById(R.id.programlist);
        mErrorLayout = (LinearLayout) programListView.findViewById(R.id.errorLayout);

        getAlarmListFromServer();

        final Button button = (Button) programListView.findViewById(R.id.retryButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                getAlarmListFromServer();
            }
        });

        // Updating the action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(/*items[position]*/"Sveglie");






        return programListView;
    }

    private void getAlarmListFromServer() {

        alarmList.clear();

        requestprogramtask task = (requestprogramtask) new requestprogramtask(getActivity(), new AsyncRequestProgramResponse() {

            @Override
            public void processFinish(List<WindAlarmProgram> list, boolean error, String errorMessage) {

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

                    mErrorLayout.setVisibility(View.VISIBLE);
                    offline = true;


                } else {

                    mErrorLayout.setVisibility(View.GONE);
                    for (int i = 0; i < list.size(); i++) {
                        addProgram(list.get(i));
                    }
                    offline = false;
                }
            }
        }).execute(((MainActivity) getActivity()).getServerURL());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void createProgram() {

        if (mSpotList == null)
            return;

        WindAlarmProgram program = new WindAlarmProgram();
        if (alarmList.size() == 0)
            program.id = 1L;
        else
            program.id = alarmList.get(alarmList.size() - 1).alarm.id + 1;

        //addProgram(program);
        startProgramActivity(program, CREATEPROGRAM_REQUEST);
    }

    private void addProgram(WindAlarmProgram program) {

        final AlarmCardItem carditem = new AlarmCardItem();
        carditem.alarm = program;

        //LinearLayout container = (LinearLayout) programListView.findViewById(R.id.programlist);
        mcontainer.addView(carditem.card);
        carditem.initSwitchListener(program.enabled);
        carditem.update(program);

        alarmList.add(carditem);

        carditem.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startProgramActivity(carditem.alarm, SHOWPROGRAM_REQUEST);
            }
        });
    }

    private void startProgramActivity(WindAlarmProgram alarm, int request) {
        Intent resultIntent = new Intent(getActivity(), ProgramActivity.class);
        resultIntent.putExtra("WindAlarmProgram", new Gson().toJson(alarm));

        Gson gson = new Gson();
        SpotList sl = new SpotList();
        sl.list = mSpotList;
        String myJson = gson.toJson(sl);
        resultIntent.putExtra("spotlist", myJson);
        resultIntent.putExtra("serverurl", ((MainActivity) getActivity()).getServerURL());


        startActivityForResult(resultIntent, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == REQUESTRESULT_ERROR) {
            // non faccio niemnte
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setTitle("Errore");
            alertDialogBuilder
                    .setMessage("Impossibile salvare")
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
            return;
        }
        if (resultCode == REQUESTRESULT_ABORT) {
            // non faccio niemnte
            return;
        }

        String jsonMyObject;
        jsonMyObject = data.getStringExtra("WindAlarmProgram");
        WindAlarmProgram program = new Gson().fromJson(jsonMyObject, WindAlarmProgram.class);
        if (requestCode == CREATEPROGRAM_REQUEST) {


            if (resultCode == REQUESTRESULT_SAVED) {

                addProgram(program);

            } else if (resultCode == REQUESTRESULT_DELETED) {
                // non faccio niemnte
            }
        }

        if (requestCode == SHOWPROGRAM_REQUEST) {

            if (resultCode == REQUESTRESULT_SAVED) {


                AlarmCardItem card = getCardFromId(program.id);
                card.update(program);

            } else if (resultCode == REQUESTRESULT_DELETED) {


                AlarmCardItem card = getCardFromId(program.id);
                alarmList.remove(card);
                card.remove();
            }
        }

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

    public AlarmCardItem getCardFromId(long id) {
        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).alarm.id == id)
                return alarmList.get(i);
        }
        return null;
    }

    private class AlarmCardItem {
        WindAlarmProgram alarm;
        AlarmCard card;

        AlarmCardSubitem id;
        AlarmCardSubitem minSpeedSubitem;
        AlarmCardSubitem minAvSpeedSubitem;
        AlarmCardSubitem directionSubitem;
        AlarmCardSubitem lastRingSubitem;
        Switch alarmSwitch;

        AlarmCardItem() {
            card = (AlarmCard) getActivity().getLayoutInflater().inflate(R.layout.card_alarm, mcontainer, false);



            card.addSeparator();
            minSpeedSubitem = card.addSubItem("Velocita minima vento: ");
            minAvSpeedSubitem = card.addSubItem("Velocita media minima:");
            directionSubitem = card.addSubItem("Direzione: ");
            id = card.addSubItem("Id: ");
            lastRingSubitem = card.addSubItem("lastring:");
        }

        public void initSwitchListener(boolean enabled) {
            alarmSwitch = (Switch) card.findViewById(R.id.alarmSwitch);
            alarmSwitch.setChecked(enabled);
            alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Log.v("Switch State=", ""+isChecked);
                    if (isChecked) {
                        alarm.enabled = true;
                        enableProgram(alarm,true);
                        alarmSwitch.setText("Attivo");

                    } else {
                        alarm.enabled = false;
                        enableProgram(alarm,false);
                        alarmSwitch.setText("Non attivo");
                    }
                }
            });
        }

        public void remove() {
            mcontainer.removeView(card);
        }

        public void update(WindAlarmProgram program) {

            alarm = program;

            if (program.enabled) {
                alarmSwitch.setText("Attivo");
                alarmSwitch.setChecked(true);
            } else {
                alarmSwitch.setText("Non Attivo");
                alarmSwitch.setChecked(false);
            }
            String days = "";
            if (alarm.mo)
                days += "L";
            else
                days += " ";
            if (alarm.tu)
                days += "M";
            else
                days += " ";
            if (alarm.we)
                days += "M";
            else
                days += " ";
            if (alarm.th)
                days += "G";
            else
                days += " ";
            if (alarm.fr)
                days += "V";
            else
                days += " ";
            if (alarm.sa)
                days += "S";
            else
                days += " ";
            if (alarm.su)
                days += "D";
            else
                days += " ";
            String spotname = MainActivity.getSpotName(alarm.spotId);
            if (spotname == null)
                spotname = "<empty>";
            card.setSpotName(spotname);
            card.setTitle(alarm.startTime + " - " + alarm.endTime + " " + days);
            card.setDescription(alarm.startDate + " - " + alarm.endDate);

            minSpeedSubitem.setDescription("Velocita minima vento: " + alarm.speed);
            minAvSpeedSubitem.setDescription("Velocita media minima: " + alarm.avspeed);
            directionSubitem.setDescription("Direzione: " + alarm.direction);
            id.setDescription("id: " + alarm.id);
            lastRingSubitem.setDescription("lastring: " + alarm.lastRingDate + alarm.lastRingTime);
        }
    }

    private void enableProgram(final WindAlarmProgram _program, boolean enabled) {

        postprogramtask task = (postprogramtask) new postprogramtask(this.getActivity(), new AsyncPostProgramResponse() {
            @Override
            public void processFinish(Object obj, boolean error, String errorMessage) {
                WindAlarmProgram program = (WindAlarmProgram) obj;
                if (error) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Errore");
                    alertDialogBuilder
                            .setMessage("Impossibile salvare")
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

                } else {
                    //
                }
            }

        }, postprogramtask.POST_ALARM).execute(_program, ((MainActivity) getActivity()).getServerURL());
    }

}
