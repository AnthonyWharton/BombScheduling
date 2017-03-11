package bombscheduling.com.bombscheduling.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import java.util.regex.Pattern;

import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

/**
 * Created by anthony on 11/03/17.
 */

public class Register extends Fragment {

    public interface RegisterToActivityListener {
        void sendMessage(String opCode, String data);
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
                JSONObject json = new JSONObject();
                for (int i = 0; i < adapter.getCount(); i++) {
                    EditText e = (EditText) adapter.getView(i, null, null).findViewById(R.id.list_register_item);
                    try {
                        json.put(adapter.getItem(i), e.getText());
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                listener.sendMessage(Networking.REGISTER_USER, json.toString());
            }
        });

        adapter = new RegisterFieldItemsAdapter(getContext(), 0, testFields);
        listView.setAdapter(adapter);
    }

    public void updateFields(List<String> fields) {
        adapter.clear();
        adapter.addAll(fields);
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
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_register_item, null);
            EditText editText = (EditText) view.findViewById(R.id.list_register_item);
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