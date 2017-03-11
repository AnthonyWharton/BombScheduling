package bombscheduling.com.bombscheduling;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import bombscheduling.com.bombscheduling.Fragments.NewUser;
import bombscheduling.com.bombscheduling.Networking.Networking;

import static bombscheduling.com.bombscheduling.Networking.MessageHelper.CONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DISCONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.MESSAGE_RECEIVED;

public class ActivityMain extends AppCompatActivity {

    protected Messenger replyTo = new Messenger(new IncomingHandler());
    protected Messenger sendTo;
    protected Networking connection;

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED:
                    Toast.makeText(getBaseContext(), "A CONNECTED!", Toast.LENGTH_SHORT);
                    break;
                case DISCONNECTED:
                    Toast.makeText(getBaseContext(), "A DISCONNECTED!", Toast.LENGTH_SHORT);
                    break;
                case MESSAGE_RECEIVED:
                    Toast.makeText(getBaseContext(), "A MESSAGE RECEIVED!", Toast.LENGTH_SHORT);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void initialiseNetworking() {
        Log.d("ActivityMain", "initialiseNetworking()");
        connection =  new Networking(getBaseContext(), replyTo);
        sendTo = connection.getReplyTo();
        connection.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                .add(R.id.fragment_container, firstFragment).commit();
    }

    @Override
    protected void onStop() {
        connection.close();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        connection.connect();
        super.onRestart();
    }

}
