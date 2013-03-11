package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.bravelittlescientist.android_puzzle_view.ExampleJigsawConfigurations;
import com.bravelittlescientist.android_puzzle_view.JigsawPuzzle;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class RcatJigsawBotActivity extends Activity {

    private PuzzleCompactSurface puzzleSurface;

    private RcatJigsawConfig puzzleConfig;
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    private boolean running = false;

    private HashMap<String, PuzzlePieceView> jigsawPieces;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection mConnection = new WebSocketConnection();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        puzzleConfig = new RcatJigsawConfig(RcatJigsawBotActivity.this);
        Bundle config = ExampleJigsawConfigurations.getRcatKittenExample(R.drawable.happy_kitten);

        puzzleSurface = new PuzzleCompactSurface(this);
        JigsawPuzzle jigsawPuzzle = new JigsawPuzzle(this, config);
        puzzleSurface.setPuzzle(jigsawPuzzle);

        setContentView(R.layout.puzzle_login);
        startJigsawWebsocketConnection();
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

                    Log.d(TAG, "Got echo: " + payload);
                    // Initial state
                    if (!running) {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            // Configuration Message
                            if (msgContents.has("c")) {
                                running = true;
                                activePlayerLoginButton();

                                puzzleConfig.configure(msgContents.getJSONObject("c"));
                                jigsawPieces = new HashMap<String, PuzzlePieceView>();

                                //jigsawPrototypePieces = new HashMap<String, TextView>();
                                //drawJigsawPuzzle();
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
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("pm").toString(), Toast.LENGTH_SHORT).show();

                            } else if (msgContents.has("pd")) {
                                // Piece dropped message
                                //Toast.makeText(RcatJigsawBotActivity.this, msgContents.get("pd").toString(), Toast.LENGTH_SHORT).show();

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

    public void drawJigsawPuzzle() {

        /*RelativeLayout rL = (RelativeLayout) findViewById(R.id.relative);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT); */

        JSONObject pieces = puzzleConfig.getPieces();
        JSONObject board = puzzleConfig.getBoard();
        JSONObject grid = puzzleConfig.getGrid();

        // Create board + grid for puzzle


        // Get Jigsaw Pieces
        Iterator<?> pIter = pieces.keys();
        TextView prototypeT;
        int counter = 1;

        while(pIter.hasNext()) {
            String pO = (String) pIter.next();

            try {
                JSONObject piece = (JSONObject) pieces.get(pO);
                //Toast.makeText(RcatJigsawBotActivity.this, piece.toString(), Toast.LENGTH_SHORT).show();
                // Get piece information
                String pieceId = piece.getString("pid");
                Boolean piecePlaced = piece.getBoolean("b");
                Integer pieceTargetRow = piece.getInt("r");
                Integer pieceTargetCol = piece.getInt("c");
                Integer pieceRemoteXPos = piece.getInt("x");
                Integer pieceRemoteYPos = piece.getInt("y");

                // Create new custom ImageView from this puzzle piece
                jigsawPieces.put(pieceId,
                        new PuzzlePieceView(this,
                                pieceId, piecePlaced,
                                pieceTargetRow, pieceTargetCol,
                                pieceRemoteYPos, pieceRemoteXPos)
                );

                //Toast.makeText(RcatJigsawBotActivity.this, pieceId + ": " + pieceTargetRow + ", " + pieceTargetCol, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
               // Toast.makeText(RcatJigsawBotActivity.this, "Failed JSON", Toast.LENGTH_LONG);
                Log.d(TAG, "JSON Exception parsing pieces");
            }
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
                setContentView(puzzleSurface);

            } catch (JSONException e) {
                Log.d(TAG, "JSON Exception: Sending player login name");

                // TODO: Error handling in UI in case connection doesn't work
            }
        } else {
            Toast.makeText(RcatJigsawBotActivity.this, "Please enter a login name.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        puzzleSurface.getThread().pause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        puzzleSurface.getThread().saveState(outState);
    }

}