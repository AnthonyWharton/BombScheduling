package bombscheduling.com.bombscheduling;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import bombscheduling.com.bombscheduling.Fragments.BombSchedule;
import bombscheduling.com.bombscheduling.Fragments.NewUser;
import bombscheduling.com.bombscheduling.Fragments.Register;
import bombscheduling.com.bombscheduling.Networking.Networking;

import static bombscheduling.com.bombscheduling.Networking.MessageHelper.CONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DISCONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_RECIEVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_USER_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NETWORK_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECIEVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.REGISTERED_USER;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.logMessage;

public class ActivityMain extends AppCompatActivity
                          implements NewUser.NewUserToActivityListener,
                                     Register.RegisterToActivityListener,
                                     BombSchedule.BombScheduleActivityListener {

    public static final String FRAGMENT_NEW_USER      = "nu";
    public static final String FRAGMENT_REGISTER      = "rs";
    public static final String FRAGMENT_BOMB_SCHEDULE = "bs";

    protected Messenger replyTo = new Messenger(new IncomingHandler());
    protected Networking connection;

    private FrameLayout fragmentContainer;
    private ProgressBar connectionWheel;
    private Boolean     connectedAlert = true;
    private Boolean     errorShown     = false;
    private long        reconnectTime  = 500;

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            logMessage("ActivityMain", msg);
            switch (msg.what) {
                case CONNECTED:
                    hideLoadingWheel();
                    Snackbar.make(fragmentContainer, "Good to go, all connected! \uD83D\uDC4C", Snackbar.LENGTH_LONG).show();
                    reconnectTime = 500;
                    errorShown = false;
                    break;
                case DISCONNECTED:
                    showLoadingWheel();
                    break;
                case NETWORK_ERROR:
                    showLoadingWheel();
                    if (!errorShown) {
                        Snackbar.make(fragmentContainer,
                                "We're having some trouble with your network.. \uD83D\uDE13",
                                Snackbar.LENGTH_LONG)
                                .show();
                        errorShown = true;
                    }
                    connection.close();
                    reconnectTime *= 1.5; // Recursive doubling on reconnect time.
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connection.connect();
                        }
                    }, reconnectTime);
                    break;
                case RECIEVED_MODES:
                    Register f = (Register) getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER);
                    f.updateFields(msg.getData().getStringArrayList(K_RECIEVED_MODES));
                    break;
                case REGISTERED_USER:
                    int uid = msg.arg1;
                    Register r = (Register) getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER);
                    if (uid < 0) {
                        r.unsuccessfulRegister(msg.getData().getString(K_USER_ERROR));
                    } else {
                        r.successfulRegister();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void initialiseNetworking() {
        Log.d("ActivityMain", "initialiseNetworking()");
        connection = new Networking(getBaseContext(), replyTo);
        connection.connect();
    }

    public void hideLoadingWheel() {
        connectionWheel.setVisibility(View.INVISIBLE);
        if (connectedAlert) {
            connectedAlert = false;
        }
    }

    public void showLoadingWheel() {
        connectionWheel.setVisibility(View.VISIBLE);
        connectedAlert = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseNetworking();

        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        connectionWheel   = (ProgressBar) findViewById(R.id.main_connectionWheel);
        Snackbar.make(fragmentContainer,
                "Hold on, We're just connecting you to the server.. \uD83D\uDE0E",
                Snackbar.LENGTH_LONG)
                .show();

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) return;

        // Create a new Fragment to be placed in the activity layout
        NewUser firstFragment = new NewUser();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstFragment, FRAGMENT_NEW_USER).commit();
    }

    @Override
    protected void onDestroy() {
        connection.close();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        if (!isConnected()) {
            connection.connect();
            Snackbar.make(fragmentContainer,
                    "Hold on, We're just connecting you to the server.. \uD83D\uDE0E",
                    Snackbar.LENGTH_LONG)
                    .show();
        }
        super.onRestart();
    }

    public void sendMessage(String opcode, String data) {
        connection.sendMessage(opcode, data);
    }

    public Boolean isConnected() {
        return connection.isOpen();
    }
}
