package team_310.x_chair;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static team_310.x_chair.mainactivity.act;

////////////WEBSOCKET//////////////
/////////////////////////////////////

/**
 * Created by Dirk Vanbeveren on 12/04/2017.
 */

class WebSocket {
    String host;          //host of the websocket
    boolean running;
    int port;
    private WebSocketClient mWebSocketClient;
    boolean connected;
    void connectWebSocket() {
        //Wifi.connect();
        URI uri;
        try {
            uri = new URI("ws://3.1.0.1:81/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("going to connect to: "+uri);



        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                //mWebSocketClient.send("request");
                connected = true;
                //Notification.toast("Connected");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("websocket", "Received message: " + message);
                Transcoder.transcode(message);
                /*act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Transcoder.transcode(message);
                    }



                });*/
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                connected = false;
                //Notification.toast("connection closed");
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };





        Notification.toast("connecting to websocket");
        mWebSocketClient.connect();

        if(!connected) {
            Notification.toast("unable to connect");
        } else if(connected) {
            Notification.toast("Connected");
        }
    }

   void sendMessage(String message) {
        if(connected) {
            System.out.println("Sending message: " + message);
            mWebSocketClient.send(message);
        } else {
            Notification.toast("Websocket not connected");
        }
    }
    void sendMessage(String message, boolean override) {
        if(connected) {
            System.out.println("Sending message: " + message);
            mWebSocketClient.send(message);
        }
        //TODO: remove this
        System.out.print(" Val " + message);
    }
}
