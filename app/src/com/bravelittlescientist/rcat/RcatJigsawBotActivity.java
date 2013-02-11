package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

    private RcatJigsawConfig rcatBot = new RcatJigsawConfig();
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    private GameLoopThread mGameThread;
    private JigsawView mJigsawView;
    private boolean running;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection botConnection = new WebSocketConnection();

    private void startJigsawWebsocketConnection() {

        try {
            botConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                }

                @Override
                public void onTextMessage(String payload) {

                    Log.d(TAG, "Got echo: " + payload);
                    // Initial state
                    if (!running) {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            if (msgContents.has("c")) {
                                //rcatBot.configure(msgContents.getJSONObject("c"));
                                TextView t = (TextView) findViewById(R.id.gameText);
                                t.setText("Found C");
                            }
                            else {
                                Log.d(TAG, "Error: No jigsaw configuration received");
                                TextView t = (TextView) findViewById(R.id.gameText);
                                t.setText("No C Found");
                            }
                        }
                        catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }



                    } else {

                    }

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

        setContentView(R.layout.puzzle_layout);

        mJigsawView = (JigsawView) findViewById(R.id.jigsaw);
        mGameThread = mJigsawView.getThread();
        mJigsawView.setTextView((TextView) findViewById(R.id.gameText));
        running = false;

        // Connection to RCAT Server
        startJigsawWebsocketConnection();

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mGameThread.setState(GameLoopThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mGameThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }

        // Full screen Jigsaw Surface View
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(new JigsawView(this));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
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

    @Override
    protected void onPause() {
        super.onPause();
        mJigsawView.getThread().pause(); // pause game when Activity pauses
    }

    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mGameThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }


}