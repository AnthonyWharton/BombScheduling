package bombscheduling.com.bombscheduling.Networking;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static bombscheduling.com.bombscheduling.Networking.MessageHelper.CONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DISCONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.MESSAGE_RECEIVED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NO_MESSAGE;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.sendMessage;

public class Networking {

    private Context         context;
    private Messenger       sendTo;
    private Messenger       replyTo = new Messenger(new IncomingHandler());
    private WebSocketClient client;

    public Networking(Context context, Messenger replyTo) {
        this.context = context;
        this.sendTo  = replyTo;
    }

    public void connect() {
        initialiseClient();
        client.connect();
    }

    public void sendMessage() {
        client.send("you have no friends and you suck lol");
    }

    public void close() {
        client.close();
    }

    public Messenger getReplyTo() {
        return replyTo;
    }

    public class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MessageHelper.Builder b = new MessageHelper.Builder().setWhat(NO_MESSAGE);

            switch (msg.what) {
                case CONNECTED:
                    Toast.makeText(context, "N CONNECTED!", Toast.LENGTH_SHORT);
                    b.setWhat(CONNECTED);
                    break;
                case DISCONNECTED:
                    Toast.makeText(context, "N DISCONNECTED!", Toast.LENGTH_SHORT);
                    b.setWhat(DISCONNECTED);
                    break;
                case MESSAGE_RECEIVED:
                    Toast.makeText(context, "N MESSAGE RECEIVED!", Toast.LENGTH_SHORT);
                    b.setWhat(MESSAGE_RECEIVED);
                    break;
                default:
                    super.handleMessage(msg);
            }

            Message m = b.build();
            if (m.what != NO_MESSAGE) MessageHelper.sendMessage(sendTo, m);
        }
    }

    private void initialiseClient() {
        URI uri;
        try {
            uri = new URI("ws://139.59.162.84:40111");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Networking", "WebSocket Opened");
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(CONNECTED).build());
            }

            @Override
            public void onMessage(String s) {
                Log.d("Networking", "WebSocket Message Received");
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(MESSAGE_RECEIVED).build());
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Networking", "WebSocket Closed " + s);
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(DISCONNECTED).build());
            }

            @Override
            public void onError(Exception e) {
                Log.d("Networking", "WebSocket Error " + e.getMessage());
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(DISCONNECTED).build());
            }
        };
    }

}
