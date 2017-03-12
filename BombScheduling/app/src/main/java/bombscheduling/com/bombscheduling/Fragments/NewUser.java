package bombscheduling.com.bombscheduling.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bombscheduling.com.bombscheduling.ActivityMain;
import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

public class NewUser extends Fragment {

    public interface NewUserToActivityListener {
        void sendMessage(String opCode, String data);
    }

    private NewUserToActivityListener listener;
    private Button submit;
    private int uid;

    private void captureAndInitialise() {
        submit = (Button) getView().findViewById(R.id.newUser_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                Register newFragment = new Register();
                transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_REGISTER);
                transaction.addToBackStack(null);
                transaction.commit();
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
        return inflater.inflate(R.layout.fragment_new_user, container, false);
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
            listener = (NewUserToActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement THINGY");
        }

        // Try Load
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        uid = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);

        // If there was a thing to load, skip setup and login
        if (uid != -1) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            BombSchedule newFragment = new BombSchedule();
            transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_BOMB_SCHEDULE);
            transaction.commit();
        }
    }

}
