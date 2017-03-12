package bombscheduling.com.bombscheduling.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import bombscheduling.com.bombscheduling.ActivityMain;
import bombscheduling.com.bombscheduling.Bomb;
import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

public class ListBombs extends Fragment {

    public interface ListBombsToActivityListener {
        void sendMessage(String opCode, String data);
        Boolean isConnected();
        void showLoadingWheel();
        void hideLoadingWheel();
    }

    private ListBombsToActivityListener listener;
    private ListView listView;
    private TextView error;
    private ListBombFieldItemsAdapter adapter;

    public ListBombs() {

    }

    private void captureAndInitialise() {
        listView = (ListView) getView().findViewById(R.id.list_listView);
        error = (TextView) getView().findViewById(R.id.list_error);

        if (!listener.isConnected()) {
            listView.setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.register_error).setVisibility(View.VISIBLE);
        }

        adapter = new ListBombFieldItemsAdapter(getContext(), 0, new ArrayList<Bomb>());
        listView.setAdapter(adapter);
    }

    public void updateFields(List<Bomb> fields) {
        adapter.clear();
        adapter.addAll(fields);
        if (!listener.isConnected()) {
            listView.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);
        }
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
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    /**
     * Called when Fragments activity is created. Used for initialization.
     * @param savedInstanceState If re-creating from a previous saved state, this is it.
     */
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        listener.sendMessage(Networking.LIST_BOMBS, String.valueOf(sharedPref.getInt(ActivityMain.STORE_USER_ID, -1)));
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
            listener = (ListBombsToActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement THINGY");
        }
    }

    public class ListBombFieldItemsAdapter extends ArrayAdapter<Bomb> {

        private Context context;
        private List<Bomb> list;

        public ListBombFieldItemsAdapter(Context context, int resource, List<Bomb> objects) {
            super(context, resource, objects);

            this.context = context;
            this.list = objects;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_bomb_item, null);

            final Bomb b = list.get(position);

            TextView title = (TextView) view.findViewById(R.id.list_title);
            TextView body  = (TextView) view.findViewById(R.id.list_body);
            TextView time  = (TextView) view.findViewById(R.id.list_time);
            Button   done  = (Button)   view.findViewById(R.id.list_done);

            if (b.getTitle().equals("")) title.setText("Incoming Bomb Schedule!");
            else                         title.setText(b.getTitle());
            body.setText(b.getBody());
            String dateTime = "";
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            dateTime = df.format(b.getTime().getTime());
            df = new SimpleDateFormat("dd/MM/yyyy");
            dateTime = dateTime + " " + df.format(b.getTime().getTime());
            time.setText(dateTime);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Hang on...")
                            .setMessage("Are you sure you've done this?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    listener.sendMessage(Networking.DELETE_BOMB, String.valueOf(b.getId()));
                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                    listener.sendMessage(Networking.LIST_BOMBS, String.valueOf(sharedPref.getInt(ActivityMain.STORE_USER_ID, -1)));
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            return view;
        }
    }
}
