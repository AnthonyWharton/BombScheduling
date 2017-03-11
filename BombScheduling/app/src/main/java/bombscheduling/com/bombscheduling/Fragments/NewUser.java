package bombscheduling.com.bombscheduling.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bombscheduling.com.bombscheduling.ActivityMain;
import bombscheduling.com.bombscheduling.Networking.Networking;
import bombscheduling.com.bombscheduling.R;

/**
 * Created by anthony on 11/03/17.
 */

public class NewUser extends Fragment {

    public interface NewUserToActivityListener {
        void sendMessage(String opCode, String data);
    }

    private NewUserToActivityListener listener;
    private Button yesButton;
    private Button noButton;

    private void captureAndInitialise() {
        yesButton = (Button) getView().findViewById(R.id.newUser_yesButton);
        noButton  = (Button) getView().findViewById(R.id.newUser_noButton);

        // Login
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login newFragment = new Login();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_LOGIN);
                transaction.addToBackStack(null);
                transaction.commit();
                listener.sendMessage(Networking.PING, "you have no friends and you suck lol");
            }
        });

        // Register
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register newFragment = new Register();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_REGISTER);
                transaction.addToBackStack(null);
                transaction.setTransition(android.R.style.Animation_Translucent);
                transaction.commit();
                listener.sendMessage(Networking.REQUEST_MODES, "");
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
    }

}