package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bravelittlescientist.android_puzzle_view.ExampleJigsawConfigurations;
import com.bravelittlescientist.android_puzzle_view.JigsawPuzzle;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class RcatJigsawBotActivity extends Activity {

    private PuzzleCompactSurface puzzleSurface;

    private RcatJigsawConfig puzzleConfig;
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    private boolean running;

    private HashMap<String, PuzzlePieceView> jigsawPieces;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection botConnection = new WebSocketConnection();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        puzzleConfig = new RcatJigsawConfig(RcatJigsawBotActivity.this);
        Bundle config = ExampleJigsawConfigurations.getRcatKittenExample(R.drawable.diablo_1mb);

        puzzleSurface = new PuzzleCompactSurface(this);
        JigsawPuzzle jigsawPuzzle = new JigsawPuzzle(this, config);
        puzzleSurface.setPuzzle(jigsawPuzzle);

        running = false;
        setContentView(puzzleSurface);
        // Connection to RCAT Server
        startJigsawWebsocketConnection();
    }

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
                                running = true;
                                puzzleConfig.configure(msgContents.getJSONObject("c"));
                                jigsawPieces = new HashMap<String, PuzzlePieceView>();
                                //jigsawPrototypePieces = new HashMap<String, TextView>();
                                //drawJigsawPuzzle();
                            }
                            else {
                                Log.d(TAG, "Error: No jigsaw configuration received");
                                //Toast.makeText(RcatJigsawBotActivity.this, "No Configuration Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }


                    // Game is now running.
                    } else {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            //Toast.makeText(RcatJigsawBotActivity.this, payload, Toast.LENGTH_LONG);

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