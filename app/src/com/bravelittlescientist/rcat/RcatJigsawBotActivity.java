package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class RcatJigsawBotActivity extends Activity {

    private RcatExtendedPuzzleSurface puzzleSurface;
    private RcatJigsawConfig puzzleConfig;

    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private String wsuri = "ws://10.0.2.2:8888/client";

    private boolean running = false;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection mConnection = new WebSocketConnection();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Notification noti = new Notification.Builder(RcatJigsawBotActivity.this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);

        setContentView(R.layout.enter_host_view);
        puzzleConfig = new RcatJigsawConfig();
    }

    private void startJigsawWebsocketConnection() {

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                }

                @Override
                public void onTextMessage(String payload) {

                    Log.d(TAG, "Got message: " + payload);

                    // Initial state
                    if (!running) {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            // Configuration Message
                            if (msgContents.has("c")) {

                                running = true;
                                activePlayerLoginButton();

                                /** Puzzle Surface Configuration **/
                                puzzleConfig.configure(msgContents.getJSONObject("c"), R.drawable.diablo_1mb);
                                puzzleSurface = new RcatExtendedPuzzleSurface(RcatJigsawBotActivity.this, mConnection);
                                RcatExtendedJigsawPuzzle jigsawPuzzle = new RcatExtendedJigsawPuzzle(RcatJigsawBotActivity.this, puzzleConfig.getFullConfiguration());
                                puzzleSurface.setPuzzle(jigsawPuzzle);

                            } else {
                                Log.d(TAG, "Error: No jigsaw configuration received");
                                Toast.makeText(RcatJigsawBotActivity.this, "No Configuration Found", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        // Game is now running.
                    } else {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            if (msgContents.has("pm")) {
                                // Piece moved message
                                puzzleSurface.onMovePieceFromMessage(msgContents.getJSONObject("pm"));

                            } else if (msgContents.has("pd")) {
                                // Piece dropped message
                                puzzleSurface.onDropPieceFromMessage(msgContents.getJSONObject("pd"));

                            } else if (msgContents.has("NU")) {
                                // New user message
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("NU").toString(), Toast.LENGTH_SHORT).show();

                            } else if (msgContents.has("scu")) {
                                // Score update message
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("scu").toString(), Toast.LENGTH_SHORT).show();

                            } else if (msgContents.has("UD")) {
                                // User left message
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("UD").toString(), Toast.LENGTH_SHORT).show();

                            } else if (msgContents.has("go")) {
                                // Game over message
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("go").toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Log.d(TAG, "Error: No jigsaw configuration received");
                                Toast.makeText(RcatJigsawBotActivity.this, "No Configuration Found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }

                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            Toast.makeText(RcatJigsawBotActivity.this, "FAILURE", Toast.LENGTH_LONG).show();
            Log.d(TAG, e.toString());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void activePlayerLoginButton() {
        Button loginButton = (Button)findViewById(R.id.playerLoginbutton);
        loginButton.setText(R.string.loginButtonText);
        loginButton.setEnabled(true);
    }

    public void onPlayerLogin(View view) {
        // Send text of player login to socket
        EditText playerLoginName = (EditText)findViewById(R.id.player_name_entry_field);
        String playerName = playerLoginName.getText().toString();

        if (playerName.length() > 0) {
            try {
                JSONObject userLoginMessage = new JSONObject();
                userLoginMessage.put("usr", playerName);

                mConnection.sendTextMessage(userLoginMessage.toString());

                // Now we open our surface
                setContentView(puzzleSurface);

            } catch (JSONException e) {
                Log.d(TAG, "JSON Exception: Sending player login name");

                // TODO: Error handling in UI in case connection doesn't work
            }
        } else {
            Toast.makeText(RcatJigsawBotActivity.this, "Please enter a login name.", Toast.LENGTH_LONG).show();
        }
    }

    public void onWebsocketInfoAdded(View view) {

        EditText hostAddress = (EditText)findViewById(R.id.enterIPText);
        String host = hostAddress.getText().toString();
        wsuri = "ws://" + host + ":8888/client";

        setContentView(R.layout.puzzle_login);
        startJigsawWebsocketConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (puzzleSurface != null) {
            puzzleSurface.getThread().pause();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (puzzleSurface != null) {
            puzzleSurface.getThread().saveState(outState);
        }
    }

}