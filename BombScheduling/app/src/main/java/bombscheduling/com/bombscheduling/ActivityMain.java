package bombscheduling.com.bombscheduling;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bombscheduling.com.bombscheduling.Fragments.BombSchedule;
import bombscheduling.com.bombscheduling.Fragments.ListBombs;
import bombscheduling.com.bombscheduling.Fragments.NewUser;
import bombscheduling.com.bombscheduling.Fragments.Register;
import bombscheduling.com.bombscheduling.Networking.Networking;

import static bombscheduling.com.bombscheduling.Networking.MessageHelper.CONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DELETED_BOMB;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DISCONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_BOMB_LIST;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_BOMB_RESULT;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_RECEIVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_USER_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_USER_INFO;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NETWORK_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECEIVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECEIVED_ALERT;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECEIVED_BOMBS;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECEIVED_INFO;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.REGISTERED_USER;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.SET_BOMB;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.UPDATED_USER;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.logMessage;

public class ActivityMain extends AppCompatActivity
                          implements NewUser.NewUserToActivityListener,
                                     Register.RegisterToActivityListener,
                                     BombSchedule.BombScheduleActivityListener,
                                     ListBombs.ListBombsToActivityListener {

    public static final String STORE_USER_ID          = "uid";
    public static final String FRAGMENT_NEW_USER      = "nus";
    public static final String FRAGMENT_REGISTER      = "reg";
    public static final String FRAGMENT_BOMB_SCHEDULE = "bsc";
    public static final String FRAGMENT_LIST_BOMBS    = "lsb";

    protected Messenger replyTo = new Messenger(new IncomingHandler());
    protected Networking connection;

    private DrawerLayout drawerLayout;
    private ListView     drawerList;
    private FrameLayout  fragmentContainer;
    private ProgressBar  connectionWheel;

    private Boolean      errorShown    = false;
    private long         reconnectTime = 500;
    private ArrayList<String> tabNames = new ArrayList<>(Arrays.asList("Schedule Bomb", "List Bombs", "Change Details"));

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            logMessage("ActivityMain", msg);
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            Snackbar snackbar = Snackbar.make(fragmentContainer, "", Snackbar.LENGTH_SHORT);;
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
            switch (msg.what) {
                case CONNECTED:
                    hideLoadingWheel();
                    snackbar.setText("Good to go, all connected! \uD83D\uDC4C");
                    snackbar.show();
                    reconnectTime = 500;
                    errorShown = false;
                    // Try Load
                    int id = sharedPref.getInt(ActivityMain.STORE_USER_ID, -1);
                    if (id != -1) connection.sendMessage(Networking.LOGIN, String.valueOf(id));
                    break;

                case DISCONNECTED:
                    showLoadingWheel();
                    if (msg.arg1 != 0) {
                        reconnectTime *= 1.5; // Recursive doubling on reconnect time.
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ActivityMain", "Attempting to reconnect (" + reconnectTime + ")");
                                connection.connect();
                            }
                        }, reconnectTime);
                    }
                    break;

                case NETWORK_ERROR:
                    showLoadingWheel();
                    if (!errorShown) {
                        snackbar.setText("We're having some trouble with your network.. \uD83D\uDE13");
                        snackbar.setDuration(Snackbar.LENGTH_LONG);
                        snackbar.show();
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
                    Register r = (Register) getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER);
                    r.updateFieldTypes(msg.getData().getStringArrayList(K_RECEIVED_MODES));
                    break;

                case REGISTERED_USER:
                    int uid = msg.arg1;
                    Register r2 = (Register) getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER);
                    if (uid < 0) {
                        r2.unsuccessfulRegister(msg.getData().getString(K_USER_ERROR));
                    } else {
                        r2.successfulRegister();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(STORE_USER_ID, uid);
                        editor.apply();
                        connection.sendMessage(Networking.LOGIN, String.valueOf(uid));
                        snackbar.setText("Wahoo! Time to start scheduling \uD83C\uDF89");
                        snackbar.show();
                    }
                    break;

                case SET_BOMB:
                    snackbar.setText(msg.getData().getString(K_BOMB_RESULT));
                    snackbar.setDuration(Snackbar.LENGTH_LONG);
                    snackbar.show();
                    BombSchedule b = (BombSchedule) getSupportFragmentManager().findFragmentByTag(FRAGMENT_BOMB_SCHEDULE);
                    b.clearFields();
                    break;

                case RECEIVED_ALERT:
                    showNotification(msg.getData().getString(K_BOMB_RESULT));
                    ring();
                    vibrate();
                    break;

                case RECEIVED_BOMBS:
                    ArrayList<Bomb> bs = msg.getData().getParcelableArrayList(K_BOMB_LIST);
                    ListBombs fr = (ListBombs) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_BOMBS);
                    fr.updateFields(bs);
                    break;

                case DELETED_BOMB:
                    if (msg.arg1 == 0) {
                        snackbar.setText("Poof and it's gone! \uD83C\uDF87");
                    } else {
                        snackbar.setText("Uh oh, something went wrong.. \uD83D\uDE33");
                    }
                    snackbar.show();
                    break;

                case RECEIVED_INFO:
                    Register r3 = (Register) getSupportFragmentManager().findFragmentByTag(FRAGMENT_REGISTER);
                    r3.updateFieldText(msg.getData().getStringArrayList(K_USER_INFO));
                    break;

                case UPDATED_USER:
                    if (msg.arg1 == 0) {
                        snackbar.setText("All set! Back to scheduling \uD83C\uDF89");
                    } else {
                        snackbar.setText("Uh oh, something went wrong.. \uD83D\uDE33");
                    }
                    hideLoadingWheel();
                    snackbar.show();

                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void showNotification(String json) {
        String body = "";
        String title = "Incoming Bomb Schedule!";
        try {

            JSONObject obj = new JSONObject(json);
            body = obj.getString("body");
            title = obj.getString("title");

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent resultIntent = new Intent(this, ActivityMain.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ActivityMain.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void vibrate(){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(3000);
    }

    private void ring(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    public void hideLoadingWheel() {
        connectionWheel.setVisibility(View.INVISIBLE);
    }

    public void showLoadingWheel() {
        connectionWheel.setVisibility(View.VISIBLE);
    }

    private void initialiseNetworking() {
        Log.d("ActivityMain", "initialiseNetworking()");
        Snackbar snackbar = Snackbar.make(fragmentContainer,
                "Hold on, We're just connecting you to the server.. \uD83D\uDE0E",
                Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
        snackbar.show();
        connection = new Networking(getBaseContext(), replyTo);
        connection.connect();
    }

    private void initialiseUI() {
        drawerLayout      = (DrawerLayout) findViewById(R.id.drawer);
        drawerList        = (ListView)     findViewById(R.id.left_drawer);
        fragmentContainer = (FrameLayout)  findViewById(R.id.fragment_container);
        connectionWheel   = (ProgressBar)  findViewById(R.id.main_connectionWheel);

        drawerList.setAdapter(new DrawerAdapter(getBaseContext(), R.layout.list_drawer_item, tabNames));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                switch (position) {
                    case 0: // Schedule Bomb
                        BombSchedule n = new BombSchedule();
                        transaction.replace(R.id.fragment_container, n, ActivityMain.FRAGMENT_BOMB_SCHEDULE);
                        break;
                    case 1: // List Bombs
                        ListBombs newFragment = new ListBombs();
                        transaction.replace(R.id.fragment_container, newFragment, ActivityMain.FRAGMENT_LIST_BOMBS);
                        break;
                    case 2: // Change Details
                        Register p = new Register();
                        transaction.replace(R.id.fragment_container, p, ActivityMain.FRAGMENT_REGISTER);
                        break;
                }
                transaction.commit();
                drawerLayout.closeDrawers();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseUI();
        initialiseNetworking();

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
            Snackbar snackbar = Snackbar.make(fragmentContainer,
                    "Hold on, We're just connecting you to the server.. \uD83D\uDE0E",
                    Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
            snackbar.show();
        }
        super.onRestart();
    }

    public void sendMessage(String opcode, String data) {
        connection.sendMessage(opcode, data);
    }

    public Boolean isConnected() {
        return connection.isOpen();
    }

    public class DrawerAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> list;

        public DrawerAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);

            this.context = context;
            this.list = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_drawer_item, null);
            ImageView iv = (ImageView) view.findViewById(R.id.drawer_icon);
            TextView tv = (TextView) view.findViewById(R.id.drawer_itemName);
            tv.setText(list.get(position));
            switch (position) {
                case 0:
                    iv.setImageDrawable(getDrawable(R.drawable.ic_schedule_black_24dp));
                    break;
                case 1:
                    iv.setImageDrawable(getDrawable(R.drawable.ic_view_list_black_24dp));
                    break;
                case 2:
                    iv.setImageDrawable(getDrawable(R.drawable.ic_settings_black_24dp));
                    break;
            }
            return view;
        }
    }
}
