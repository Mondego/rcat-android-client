package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 1/20/13
 * Time: 10:22 PM
 */
public class RcatJigsawBotActivity extends Activity {

    private RcatJigsawConfig puzzleConfig;
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    private GameLoopThread mGameThread;
    private JigsawView mJigsawView;
    private boolean running;

    private HashMap<String, PuzzlePieceView> jigsawPieces;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection botConnection = new WebSocketConnection();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_layout);

        mJigsawView = (JigsawView) findViewById(R.id.jigsaw);
        mGameThread = mJigsawView.getThread();
        mJigsawView.setTextView((TextView) findViewById(R.id.gameText));

        puzzleConfig = new RcatJigsawConfig(RcatJigsawBotActivity.this);
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
                                drawJigsawPuzzle();
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


                    // Game is now running.
                    } else {
                        try {
                            JSONObject msgContents = new JSONObject(payload);

                            Toast.makeText(RcatJigsawBotActivity.this, payload, Toast.LENGTH_LONG);

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

        RelativeLayout rL = (RelativeLayout) findViewById(R.id.relative);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);

        JSONObject pieces = puzzleConfig.getPieces();
        JSONObject board = puzzleConfig.getBoard();
        JSONObject grid = puzzleConfig.getGrid();

        Iterator<?> pIter = pieces.keys();
        while(pIter.hasNext()) {
            String pO = (String) pIter.next();

            try {
                JSONObject piece = (JSONObject) pieces.get(pO);
                // Get piece information
                String pieceId = piece.getString("pid");
                Boolean piecePlaced = piece.getBoolean("b");
                Integer pieceTargetRow = pieces.getInt("r");
                Integer pieceTargetCol = pieces.getInt("c");
                Integer pieceRemoteXPos = pieces.getInt("x");
                Integer pieceRemoteYPos = pieces.getInt("y");

                // Create new custom ImageView from this puzzle piece
                jigsawPieces.put(pieceId,
                        new PuzzlePieceView(this,
                                pieceId, piecePlaced,
                                pieceTargetRow, pieceTargetCol,
                                pieceRemoteYPos, pieceRemoteXPos)
                );

            } catch (JSONException e) {
                Log.d(TAG, "JSON Exception parsing pieces");
            }
        }

        /*
        ImageView i = new ImageView(RcatJigsawBotActivity.this);
        i.setImageResource(R.drawable.diablo_1mb);
        i.setId(?);
        //rL.addView(i, relativeParams);

        //Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.diablo_1mb);
        //Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 240, 240, true);

                                /*RelativeLayout.LayoutParams rParams =
                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                                //
                                //i.setImageBitmap(bMapScaled);
                                //
                                //


                                rL.addView(t, rParams);
        bitmapsArray[0] = Bitmap.createBitmap(bMapScaled, 0, 0, 80, 80);
        bitmapsArray[1] = Bitmap.createBitmap(bMapScaled, 80, 0, 80, 80);
        bitmapsArray[2] = Bitmap.createBitmap(bMapScaled, 160, 0, 80, 80);
        bitmapsArray[3] = Bitmap.createBitmap(bMapScaled, 0, 80, 80, 80);
        bitmapsArray[4] = Bitmap.createBitmap(bMapScaled, 80, 80, 80, 80);
        bitmapsArray[5] = Bitmap.createBitmap(bMapScaled, 160, 80, 80, 80);
        bitmapsArray[6] = Bitmap.createBitmap(bMapScaled, 0, 160, 80, 80);
        bitmapsArray[7] = Bitmap.createBitmap(bMapScaled, 80, 160, 80, 80);
        bitmapsArray[8] = Bitmap.createBitmap(bMapScaled, 160, 160, 80, 80);             */
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