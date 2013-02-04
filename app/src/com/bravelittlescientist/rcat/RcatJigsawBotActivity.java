package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 1/20/13
 * Time: 10:22 PM
 */
public class RcatJigsawBotActivity extends Activity {

    private JigsawBot rcatBot = new JigsawBot();
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection botConnection = new WebSocketConnection();

    private void startJigsawWebsocketConnection() {

        try {
            botConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    botConnection.sendTextMessage("Hello, world!");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    parseJigsawConfigurationPayload(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen Jigsaw Surface View
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new JigsawView(this));

        // Connection to RCAT Server
        startJigsawWebsocketConnection();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopping...");
        super.onStop();
    }


    public void parseJigsawConfigurationPayload(String message) {

        try {
            JSONObject msgContents = new JSONObject(message);

            if (msgContents.has("c")) {
                rcatBot.configure(msgContents.getJSONObject("c"));
                launchBot();
            }
            else {
                Log.d(TAG, "Error: No jigsaw configuration received"); // TODO: Determine appropriate fail action.
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    public void launchBot() {
        // TODO: Replace image initialization with bot config call
        //ImageView img = new ImageView(RcatJigsawBotActivity.this);
        //img.setImageResource(R.drawable.diablo_1mb);
        //img.setTag("puzzleContainer");
        //LinearLayout layout = (LinearLayout)findViewById(R.id.jigsaw_bot_layout);
        //layout.addView(img);
    }
}