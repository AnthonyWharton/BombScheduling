package bombscheduling.com.bombscheduling.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bombscheduling.com.bombscheduling.ActivityMain;
import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

public class Register extends Fragment {

    public interface RegisterToActivityListener {
        void sendMessage(String opCode, String data);
        Boolean isConnected();
        void showLoadingWheel();
        void hideLoadingWheel();
    }

    private Button goButton;
    private ListView listView;

    private RegisterToActivityListener listener;
    private RegisterFieldItemsAdapter adapter;
    private ArrayList<String> testFields = new ArrayList<>(
            Arrays.asList("Username", "Password", "Phone Number", "Email"));

    private void captureAndInitialise() {
        goButton = (Button) getView().findViewById(R.id.register_submit);
        listView = (ListView) getView().findViewById(R.id.register_listView);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int id = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
                if (listener.isConnected()) {
                    try {
                        JSONObject json = new JSONObject();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            EditText e = (EditText) adapter.getView(i, null, null).findViewById(R.id.list_register_item);
                            json.put(adapter.getItem(i), e.getText());
                        }

                        if (id == -1) {
                            Log.d("Registering", "");
                            listener.sendMessage(Networking.REGISTER_USER, json.toString());
                            listener.showLoadingWheel();
                            snackbar.setText("Registering... \uD83E\uDD14");
                        } else {
                            Log.d("Updating", "");
                            JSONObject outer = new JSONObject();
                            outer.put("id", id);
                            outer.put("data", json.toString());

                            listener.sendMessage(Networking.UPDATE_USER, outer.toString());
                            listener.showLoadingWheel();
                            snackbar.setText("Updating... \uD83E\uDD14");
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    snackbar.setText("Boohoo! You're not connected \uD83D\uDE2D");
                    snackbar.setDuration(Snackbar.LENGTH_LONG);
                }
                snackbar.show();
            }
        });

        if (!listener.isConnected()) {
            listView.setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.register_error).setVisibility(View.VISIBLE);
        }

        adapter = new RegisterFieldItemsAdapter(getContext(), 0, testFields);
        listView.setAdapter(adapter);
    }

    public void updateFieldTypes(List<String> fields) {
        adapter.clear();
        adapter.addAll(fields);
        if (!listener.isConnected()) {
            listView.setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.register_error).setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.register_error).setVisibility(View.INVISIBLE);
        }
    }

    public void updateFieldText(List<String> fields) {
        adapter.setListText(new ArrayList<String>(fields));
    }

    public void successfulRegister() {
        listener.hideLoadingWheel();
        getFragmentManager().popBackStack();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        BombSchedule newFragment = new BombSchedule();
        transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_BOMB_SCHEDULE);
        transaction.commit();
    }

    public void unsuccessfulRegister(String msg) {
        listener.hideLoadingWheel();
        Snackbar snackbar = Snackbar.make(getView(), "Boohoo! " + msg + " \uD83D\uDE2D", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        snackbar.show();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    /**
     * Called when Fragments activity is created. Used for initialization.
     * @param savedInstanceState If re-creating from a previous saved state, this is it.
     */
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener.sendMessage(Networking.REQUEST_MODES, "");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int id = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
        if (id != -1) {
            listener.sendMessage(Networking.USER_INFO, String.valueOf(id));
        }
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
            listener = (RegisterToActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement THINGY");
        }
    }

    public class RegisterFieldItemsAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> list;
        private ArrayList<String> listText;

        public void setListText(ArrayList<String> listText) {
            this.listText = listText;
            notifyDataSetChanged();
        }

        public RegisterFieldItemsAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);

            this.context = context;
            this.list = objects;
            this.listText = new ArrayList<String>();
            for (int i = 0; i < objects.size(); i++) {
                listText.add("");
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_register_item, null);
            final EditText editText = (EditText) view.findViewById(R.id.list_register_item);

            String current = list.get(position);
            current = current.replaceAll("_", " ");
            current = WordUtils.capitalize(current);

            editText.setHint(current);
            editText.setText(listText.get(position));

            if (current.contains("Password")) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            if (current.contains("Phone")) {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    listText.remove(position);
                    listText.add(position, s.toString());
                }
            });

            return view;
        }
    }
}
