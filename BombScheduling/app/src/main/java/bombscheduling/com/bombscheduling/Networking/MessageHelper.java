package bombscheduling.com.bombscheduling.Networking;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


/**
 * A collection of Message Helper Functions
 */

public class MessageHelper {

    public static final int NO_MESSAGE = -1;
    public static final int CONNECTED = 0;
    public static final int DISCONNECTED = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int RECIEVED_MODES = 3;

    public static final String K_RECIEVED_MODES = "RM";

    public static String messageToString(int what) {
        switch (what) {
            case CONNECTED:      return "CONNECTED";
            case DISCONNECTED:   return "DISCONNECTED";
            case NETWORK_ERROR:  return "NETWORK_ERROR";
            case RECIEVED_MODES: return "RECIEVED_MODES";
        }
        return "ERROR, UNKNOWN MESSAGE";
    }

    /**
     * Prints out a log of a message to debug log.
     * @param tag Tag to display in Log.d()
     * @param msg Message to log.
     */
    public static void logMessage(String tag, Message msg) {
        Log.d(tag, "Message Received: " + messageToString(msg.what)
                + ", canReply: " + (msg.replyTo != null)
                + ", args: " + msg.arg1 + ", " + msg.arg2
                + ", emptyBundle:" + (!msg.getData().hasFileDescriptors()));
    }

    /**
     * Sends a given message to a given Messenger.
     * @param sendTo The Messenger to send the message to
     * @param msg The Message to send.
     */
    public static void sendMessage(Messenger sendTo, Message msg) {
        if (sendTo != null) {
            try {
                sendTo.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Message builder. Self explanatory method names.
     */
    public static class Builder {
        private Message message;

        public Builder() {
            message = Message.obtain();
        }

        public Builder setWhat(int what) {
            message.what = what;
            return this;
        }

        public Builder setArg1(int arg1) {
            message.arg1 = arg1;
            return this;
        }

        public Builder setArg2(int arg2) {
            message.arg2 = arg2;
            return this;
        }

        public Builder setArgs(int arg1, int arg2) {
            message.arg1 = arg1;
            message.arg2 = arg2;
            return this;
        }

        public Builder setBundle(Bundle bundle) {
            message.setData(bundle);
            return this;
        }

        public Builder setReplyTo(Messenger replyTo) {
            message.replyTo = replyTo;
            return this;
        }

        public Message build() {
            return message;
        }
    }
}
