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
public class RcatJigsawBotActivity extends Activity
        implements View.OnTouchListener, View.OnClickListener {

    private RcatJigsawConfig puzzleConfig;
    private static final String TAG = RcatJigsawBotActivity.class.getSimpleName();
    private final String wsuri = "ws://10.0.2.2:8888/client";

    private GameLoopThread mGameThread;
    private JigsawView mJigsawView;
    private boolean running;

    private HashMap<String, PuzzlePieceView> jigsawPieces;
    private HashMap<String, TextView> jigsawPrototypePieces;

    private SandboxView sandbox = null;
    private boolean isFlagHidden = false;

    private JigsawSurfaceView jSurfaceView;

    /** Autobahn WebSocket initializations **/
    private final WebSocketConnection botConnection = new WebSocketConnection();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jSurfaceView = new JigsawSurfaceView(this);
        setContentView(jSurfaceView);

        //setContentView(R.layout.sandbox_game_layout);
        //sandbox = (SandboxView) findViewById(R.id.gameCanvas);
        //sandbox.setOnTouchListener(this);

        //Button b = (Button) findViewById(R.id.sandboxButton);
        //b.setOnClickListener(this);

        /*setContentView(R.layout.puzzle_layout);
        mJigsawView = (JigsawView) findViewById(R.id.jigsaw);
        mGameThread = mJigsawView.getThread();
        mJigsawView.setTextView((TextView) findViewById(R.id.gameText));*/

        puzzleConfig = new RcatJigsawConfig(RcatJigsawBotActivity.this);
        running = false;

        // Connection to RCAT Server
        startJigsawWebsocketConnection();

        /*if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mGameThread.setState(GameLoopThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mGameThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }*/
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
                                jigsawPrototypePieces = new HashMap<String, TextView>();
                                drawJigsawPuzzle();
                            }
                            else {
                                Log.d(TAG, "Error: No jigsaw configuration received");
                                Toast.makeText(RcatJigsawBotActivity.this, "No Configuration Found", Toast.LENGTH_SHORT).show();
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

        // Create board + grid for puzzle


        // Get Jigsaw Pieces
        Iterator<?> pIter = pieces.keys();
        TextView prototypeT;
        int counter = 1;

        while(pIter.hasNext()) {
            String pO = (String) pIter.next();

            try {
                JSONObject piece = (JSONObject) pieces.get(pO);
                Toast.makeText(RcatJigsawBotActivity.this, piece.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RcatJigsawBotActivity.this, "Failed JSON", Toast.LENGTH_LONG);
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
        //mJigsawView.getThread().pause(); // pause game when Activity pauses
        jSurfaceView.onPauseJigsawSurfaceView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jSurfaceView.onResumeJigsawSurfaceView();
    }

    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        //mGameThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }

    /** Touch/Click Events **/
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sandboxButton) {

            TextView tv = (TextView) findViewById(R.id.sandboxTextView);
            tv.setText("");
            Button b = (Button) findViewById(R.id.sandboxButton);
            isFlagHidden = !isFlagHidden;

            if (isFlagHidden) {
                b.setText("Give up!");
                sandbox.hideTheFlag();
            } else {
                b.setText("Hide Flag");
                sandbox.giveUp();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.gameCanvas) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isFlagHidden) {
                    TextView tv = (TextView) findViewById(R.id.sandboxTextView);
                    switch (sandbox.takeAGuess(event.getX(), event.getY())) {
                        case BULLSEYE:
                            Button b = (Button) findViewById(R.id.sandboxButton);
                            isFlagHidden = false;
                            b.setText("Hide Flag");
                            tv.setText("Found it");
                            tv.setTextColor(Color.GREEN);
                            break;

                        case HOT:
                            tv.setText("Hot");
                            tv.setTextColor(Color.RED);
                            break;

                        case WARM:
                            tv.setText("Warmer");
                            tv.setTextColor(Color.YELLOW);
                            break;

                        case COLD:
                            tv.setText("Cold");
                            tv.setTextColor(Color.BLUE);
                            break;
                    }
                }
            }
            return true;
        }
        return false;
    }
}