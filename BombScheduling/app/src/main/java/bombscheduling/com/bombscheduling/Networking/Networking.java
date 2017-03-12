package bombscheduling.com.bombscheduling.Networking;

import android.content.Context;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import bombscheduling.com.bombscheduling.Bomb;

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

public class Networking {

    public static final String PING          = "PNG";
    public static final String LOGIN         = "LGN";
    public static final String REQUEST_MODES = "REQ";
    public static final String REGISTER_USER = "USR";
    public static final String BOMB          = "BMB";
    public static final String BOMB_ALARM    = "ALR";
    public static final String LIST_BOMBS    = "LST";
    public static final String DELETE_BOMB   = "DEL";
    public static final String USER_INFO     = "INF";
    public static final String UPDATE_USER   = "UPD";

    private Context         context;
    private Messenger       sendTo;
    private WebSocketClient client;

    public Networking(Context context, Messenger replyTo) {
        this.context = context;
        this.sendTo  = replyTo;
    }

    public void connect() {
        initialiseClient();
        Log.d("Networking", "Connecting client...");
        client.connect();
    }

    public void sendMessage(String opCode, String data) {
        if (client.getConnection().isOpen()) {
            Log.d("Networking", "Message Sent, OP: " + opCode + ", DATA: " + data);
            client.send(opCode + data);
        } else {
            Log.d("Networking", "Message NOT SENT, OP: " + opCode + ", DATA: " + data);
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

                } else if (opcode.equals(REQUEST_MODES)) {

                    try {
                        JSONObject reader = new JSONObject(data);
                        Iterator keysToCopyIterator = reader.keys();
                        ArrayList<String> keysList = new ArrayList<String>();
                        while(keysToCopyIterator.hasNext()) {
                            String key = (String) keysToCopyIterator.next();
                            keysList.add(key);
                        }
                        Bundle b = new Bundle();
                        b.putStringArrayList(K_RECEIVED_MODES, keysList);
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                                                           .setWhat(RECEIVED_MODES)
                                                                           .setBundle(b)
                                                                           .build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (opcode.equals(REGISTER_USER)) {

                    // Assigned User ID
                    Boolean error = false;
                    Bundle b = new Bundle();
                    if (data.substring(0,1).equals("I")) {
                        b.putString(K_USER_ERROR, data);
                        error = true;
                    }
                    if (error) {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                     .setWhat(REGISTERED_USER)
                                     .setArg1(-1)
                                     .setBundle(b)
                                     .build());
                    } else {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                     .setWhat(REGISTERED_USER)
                                     .setArg1(new Integer(data))
                                     .build());
                    }

                } else if (opcode.equals(BOMB)) {

                    if (data.substring(0,1).equals("S")) {
                        data = data.substring(1,data.length());
                        data = data + " \uD83C\uDF89";
                    } else {
                        data = data.substring(1,data.length());
                        data = data + " \uD83D\uDE13";
                    }

                    Bundle b = new Bundle();
                    b.putString(K_BOMB_RESULT, data);
                    MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                            .setWhat(SET_BOMB)
                            .setBundle(b)
                            .build());

                } else if (opcode.equals(BOMB_ALARM)) {

                    Bundle b = new Bundle();
                    b.putString(K_BOMB_RESULT, data);
                    MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                            .setWhat(RECEIVED_ALERT)
                            .setBundle(b)
                            .build());
                    // Something related to me went BANG
                    Log.d("Networking", "OHSHITWADDAP");

                } else if (opcode.equals(LIST_BOMBS)) {

                    try {
                        JSONObject json = new JSONObject(data);
                        Log.d("Networking", "JSON DUMP: " + json.toString());
                        Iterator keysToCopyIterator = json.keys();
                        ArrayList<Bomb> bombList = new ArrayList<Bomb>();
                        while(keysToCopyIterator.hasNext()) {
                            int id = new Integer((String) keysToCopyIterator.next());
                            JSONObject nested = json.getJSONObject(String.valueOf(id));
                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date(nested.getInt("time")*1000));
                            Bomb b = new Bomb(id,
                                              nested.getString("title"),
                                              nested.getString("body"),
                                              c);
                            bombList.add(b);
                        }
                        Bundle b = new Bundle();
                        b.putParcelableArrayList(K_BOMB_LIST, bombList);
                        MessageHelper.sendMessage(sendTo,
                                new MessageHelper.Builder().setWhat(RECEIVED_BOMBS).setBundle(b).build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (opcode.equals(DELETE_BOMB)) {

                    if (data.substring(0,1).equals("S")) {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(DELETED_BOMB).setArg1(0).build());
                    } else {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(DELETED_BOMB).setArg1(1).build());
                    }

                } else if (opcode.equals(USER_INFO)) {

                    try {
                        JSONObject reader = new JSONObject(data);
                        Iterator keysToCopyIterator = reader.keys();
                        ArrayList<String> keysList = new ArrayList<String>();
                        while(keysToCopyIterator.hasNext()) {
                            String key = (String) keysToCopyIterator.next();
                            keysList.add(reader.getString(key));
                        }
                        Bundle b = new Bundle();
                        b.putStringArrayList(K_USER_INFO, keysList);
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                                .setWhat(RECEIVED_INFO)
                                .setBundle(b)
                                .build());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (opcode.equals(UPDATE_USER)) {

                    if (data.substring(0,1).equals("S")) {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(UPDATED_USER).setArg1(0).build());
                    } else {
                        MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(UPDATED_USER).setArg1(1).build());
                    }

                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("Networking", "WebSocket Closed " + reason);
                if (code == CloseFrame.NEVER_CONNECTED) {
                    MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                            .setWhat(DISCONNECTED)
                            .setArg1(1).build());
                } else {
                    MessageHelper.sendMessage(sendTo, new MessageHelper.Builder()
                            .setWhat(DISCONNECTED)
                            .setArg1(0).build());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("Networking", "WebSocket Error " + e.getMessage());
                MessageHelper.sendMessage(sendTo, new MessageHelper.Builder().setWhat(NETWORK_ERROR).build());
            }
        };
    }

}
