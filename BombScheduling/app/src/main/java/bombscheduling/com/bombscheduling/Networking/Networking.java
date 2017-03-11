package bombscheduling.com.bombscheduling.Networking;

import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import static bombscheduling.com.bombscheduling.Networking.MessageHelper.CONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.DISCONNECTED;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_RECIEVED_MODES;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_USER_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.K_USER_ID;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.NETWORK_ERROR;
import static bombscheduling.com.bombscheduling.Networking.MessageHelper.RECIEVED_MODES;

public class Networking {

    public static final String PING          = "PNG";
    public static final String REQUEST_MODES = "REQ";
    public static final String REGISTER_USER = "USR";
    public static final String BOMB          = "BMB";

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
        } else {
            MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(NETWORK_ERROR).build());
        }
    }

    public Boolean isOpen() {
        return client.getConnection().isOpen();
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
                String opcode = s.substring(0,3);
                String data   = s.substring(3, s.length());
                Log.d("Networking", "WebSocket Message Received " + opcode + " " + data);

                if (opcode.equals(PING)) {
                    // PONG
                } else if (opcode.equals(REQUEST_MODES)) { // List of stuff to register
                    try {
                        JSONObject reader = new JSONObject(data);
                        Iterator keysToCopyIterator = reader.keys();
                        ArrayList<String> keysList = new ArrayList<String>();
                        while(keysToCopyIterator.hasNext()) {
                            String key = (String) keysToCopyIterator.next();
                            keysList.add(key);
                        }
                        Bundle b = new Bundle();
                        b.putStringArrayList(K_RECIEVED_MODES, keysList);
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                                                           .setWhat(RECIEVED_MODES)
                                                                           .setBundle(b)
                                                                           .build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (opcode.equals(REGISTER_USER)) {
                    // Assigned User ID
                    try {
                        JSONObject reader = new JSONObject(data);
                        int uid = (int) reader.get("user_id");
                        Bundle b = new Bundle();
                        if (uid < 0) b.putString(K_USER_ERROR, reader.getString("error"));
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                .setWhat(RECIEVED_MODES)
                                .setArg1(uid)
                                .setBundle(b)
                                .build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (opcode.equals(BOMB)) {
                    // Success/Failure
                }
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
