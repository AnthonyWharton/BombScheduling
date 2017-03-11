package bombscheduling.com.bombscheduling;

import android.content.Context;
import android.content.SharedPreferences;
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
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECEIVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.REGISTERED_USER;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.SET_BOMB;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.logMessage;

public class ActivityMain extends AppCompatActivity
                          implements NewUser.NewUserToActivityListener,
                                     Register.RegisterToActivityListener,
                                     BombSchedule.BombScheduleActivityListener {

    public static final String STORE_USER_ID          = "uid";
    public static final String FRAGMENT_NEW_USER      = "nus";
    public static final String FRAGMENT_REGISTER      = "reg";
    public static final String FRAGMENT_BOMB_SCHEDULE = "bsc";

    protected Messenger replyTo = new Messenger(new IncomingHandler());
    protected Networking connection;

    private FrameLayout fragmentContainer;
    private ProgressBar connectionWheel;
    private Boolean     errorShown     = false;
    private long        reconnectTime  = 500;

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            logMessage("ActivityMain", msg);
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            switch (msg.what) {
                case CONNECTED:
                    hideLoadingWheel();
                    Snackbar.make(fragmentContainer, "Good to go, all connected! \uD83D\uDC4C", Snackbar.LENGTH_SHORT).show();
                    reconnectTime = 500;
                    errorShown = false;
                    // Try Load
                    int id = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
                    if (id != -1) connection.sendMessage(Networking.LOGIN, String.valueOf(id));
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
                            Log.d("ActivityMain", "Attempting to reconnect (" + reconnectTime + ")");
                            connection.connect();
                        }
                    }, reconnectTime);
                    break;

                case RECEIVED_MODES:
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
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(STORE_USER_ID, uid);
                        editor.apply();
                        connection.sendMessage(Networking.LOGIN, String.valueOf(uid));
                    }
                    break;

                case SET_BOMB:
                    if (msg.arg1 == 0) {
                        Snackbar.make(fragmentContainer,
                                "Tic Toc.. Bomb set! \uD83C\uDF89\uD83D\uDCA3\uD83C\uDF89\uD83D\uDCA3",
                                Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Snackbar.make(fragmentContainer,
                                "Something went wrong.. \uD83D\uDE13",
                                Snackbar.LENGTH_LONG)
                                .show();
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
    }

    public void showLoadingWheel() {
        connectionWheel.setVisibility(View.VISIBLE);
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
