package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class RcatClientActivity extends Activity
{

    private static final String TAG = "RcatClientActivity";

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection mConnection = new WebSocketConnection();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    private void startWebSocketConnection() {

        final String wsuri = "ws://10.0.2.2:9998/echo";

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage("Hello, world!");
                    TextView t = (TextView)findViewById(R.id.ws_status_text);
                    t.append("\nStatus: Connected to " + wsuri + "\n");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    TextView t = (TextView)findViewById(R.id.ws_status_text);
                    t.append("Got echo: " + payload + "\n");
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                    TextView t = (TextView)findViewById(R.id.ws_status_text);
                    t.append("Connection lost.\n");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    public void onConnectToRcatServer(View view) {
        startWebSocketConnection();
    }

}
