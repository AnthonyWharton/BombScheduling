package bombscheduling.com.bombscheduling.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bombscheduling.com.bombscheduling.R;

public class BombSchedule extends Fragment {

    public interface BombScheduleActivityListener {
        void sendMessage(String opCode, String data);
    }

    private BombScheduleActivityListener listener;
    private EditText title;
    private EditText description;
    private TextView dateText;
    private TextView timeText;
    private Button   dateSet;
    private Button   timeSet;
    private Button   submit;

    private Date dateTime;

    private void captureAndInitialise() {
        title       = (EditText) getView().findViewById(R.id.bs_title);
        description = (EditText) getView().findViewById(R.id.bs_description);
        dateText    = (TextView) getView().findViewById(R.id.bs_dateText);
        timeText    = (TextView) getView().findViewById(R.id.bs_timeText);
        dateSet     = (Button)   getView().findViewById(R.id.bs_date);
        timeSet     = (Button)   getView().findViewById(R.id.bs_time);
        submit      = (Button)   getView().findViewById(R.id.bs_submit);

        dateTime = new Date();
        dateTime.setTime(dateTime.getTime() + 300000);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateText.setText(df.format(dateTime));
        df = new SimpleDateFormat("HH:mm");
        timeText.setText(df.format(dateTime));

        dateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        timeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
