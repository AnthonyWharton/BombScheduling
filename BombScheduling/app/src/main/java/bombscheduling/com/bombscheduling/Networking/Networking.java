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
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NETWORK_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NO_MESSAGE;

public class Networking {

    public static final String CONSOLE_LOG   = "   ";
    public static final String PING          = "PNG";
    public static final String REQUEST_MODES = "REQ";
    public static final String REGISTER_USER = "USR";

    private Context         context;
    private Messenger       sendTo;
    private WebSocketClient client;

    public Networking(Context context, Messenger replyTo) {
        this.context = context;
        this.sendTo  = replyTo;
    }

    public void connect() {
        initialiseClient();
        client.connect();
    }

    public void sendMessage(String opCode, String data) {
        if (client.getConnection().isOpen()) {
            Log.d("Networking", "Message Sent, OP: " + opCode + ", DATA: " + data);
            client.send(opCode + data);
        }
    }

    public void close() {
        client.close();
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
                Log.d("Networking", "WebSocket Message Received" + s);
                String opcode = s.substring(0,2);
                String data   = s.substring(3, s.length());
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Networking", "WebSocket Closed " + s);
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(DISCONNECTED).build());
            }

            @Override
            public void onError(Exception e) {
                Log.d("Networking", "WebSocket Error " + e.getMessage());
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(NETWORK_ERROR).build());
            }
        };
    }

}
