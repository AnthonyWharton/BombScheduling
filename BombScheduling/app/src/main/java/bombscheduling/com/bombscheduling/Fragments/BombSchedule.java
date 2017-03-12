package bombscheduling.com.bombscheduling.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import bombscheduling.com.bombscheduling.ActivityMain;
import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

public class BombSchedule extends Fragment {

    public interface BombScheduleActivityListener {
        void sendMessage(String opCode, String data);
    }

    private BombScheduleActivityListener listener;
    private int      uid;
    private EditText title;
    private EditText message;
    private TextView dateText;
    private TextView timeText;
    private TextView userID;
    private Button   dateSet;
    private Button   timeSet;
    private Button   submit;

    private Calendar dateTime;

    private void captureAndInitialise() {
        title    = (EditText) getView().findViewById(R.id.bs_title);
        message  = (EditText) getView().findViewById(R.id.bs_message);
        dateText = (TextView) getView().findViewById(R.id.bs_dateText);
        timeText = (TextView) getView().findViewById(R.id.bs_timeText);
        dateSet  = (Button)   getView().findViewById(R.id.bs_date);
        timeSet  = (Button)   getView().findViewById(R.id.bs_time);
        submit   = (Button)   getView().findViewById(R.id.bs_submit);
        userID   = (TextView) getView().findViewById(R.id.bs_uid);

        dateTime = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateText.setText(df.format(dateTime.getTime()));
        df = new SimpleDateFormat("HH:mm");
        timeText.setText(df.format(dateTime.getTime()));

        dateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpg = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateTime.set(year, month, dayOfMonth);
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        dateText.setText(df.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                dpg.show();
            }
        });

        timeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpg = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);
                        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                        timeText.setText(df.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true);
                tpg.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("title",   title.getText());
                    json.put("message", message.getText());
                    json.put("time",    dateTime.getTime().getTime()/1000L);
                    json.put("uid",     uid);
                    listener.sendMessage(Networking.BOMB, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        uid = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
        if (uid == -1) {
            userID.setVisibility(View.INVISIBLE);
        } else {
            userID.setText("UID: " + uid);
        }
    }

    public void clearFields() {
        title.setText("");
        message.setText("");
        dateTime = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateText.setText(df.format(dateTime.getTime()));
        df = new SimpleDateFormat("HH:mm");
        timeText.setText(df.format(dateTime.getTime()));
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container This is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Re-construction from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bomb_schedule, container, false);
    }

    /**
     * Called when Fragments activity is created. Used for initialization.
     * @param savedInstanceState If re-creating from a previous saved state, this is it.
     */
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        captureAndInitialise();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        uid = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
    }

    /**
     * Overridden onAttach method that catches the implementations of the methods in the interface
     * defined in this fragment from the host Activity.
     * @param context The context that we are attaching to.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Connect to interface implemented within host Activity
        try {
            listener = (BombScheduleActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement THINGY");
        }
    }
}
