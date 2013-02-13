package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 1/20/13
 * Time: 8:36 PM
 */
public class RcatJigsawConfig {

    /**
     * This class is designed to manage the input configuration of an RCAT Jigsaw puzzle.
     */
    private static final String TAG = "RcatJigsawConfig";

    /** Game Configuration **/
    private JSONObject grid;
    private JSONObject imgSet;
    private JSONObject board;
    private JSONObject dfrus;
    private JSONObject pieces;
    private String myId;
    private String imgUrl;

    private Context cfgContext;

    private boolean running = false;

    public RcatJigsawConfig(Context c) {
        cfgContext = c;
    }

    public void configure(JSONObject config) {

        try {
            imgSet = config.getJSONObject("img");
            board = config.getJSONObject("board");
            grid = config.getJSONObject("grid");
            dfrus = config.getJSONObject("frus");
            pieces = config.getJSONObject("pieces");
            myId = config.getString("myid");
            imgUrl = config.getJSONObject("img").getString("img_url");
        }

        catch (JSONException je) {
            Log.d(TAG, je.toString());
        }
    }

    /** Getters **/
    /**
     * getGrid
     * @return grid, JSON Object
     * grid["nrows"]    number of puzzle rows
     * grid["ncols"]    number of puzzle columns
     * grid["cellw"]    cell dimensions, width
     * grid["cellh"]    cell dimensions, height
     * grid["y"]        ???
     * grid["x"]        ???
     */
    public JSONObject getGrid() {
        return grid;
    }

    /**
     * getBoard
     * @return board, JSON Object
     * board["w"]           width of gameplay board
     * board["h"]           height of gameplay board
     * board["maxScale"]    maximum scale of board
     * board["minscale"]    minimum scale of board
     */
    public JSONObject getBoard() {
        return board;
    }

    /**
     * getImgSet
     * @return imgSet, JSON Object
     * imgSet["img_url"]    URL of puzzle image
     * imgSet["img_w"]      Height of puzzle image
     * imgset["img_h"]      Width of puzzle image
     */
    public JSONObject getImgSet () {
        return imgSet;
    }

    /**
     * getMyId
     * @return myId, String
     */
    public String getMyId () {
        return myId;
    }

    /**
     * getImgUrl
     * @return imgUrl, String
     */
    public String getImgUrl () {
        return imgUrl;
    }

    /**
     * getPieces
     * @return pieces, JSON Object
     *
     */
    public JSONObject getPieces () {
        return pieces;
    }


    public void automateBot () {
        int x = 0;
        int y = 0;
        Log.d(TAG, "[Jigsaw Bot: Starting bot...");
        Random r = new Random();
        while (running) {
            /*v = random of pieces.values
            if (!v['l'] || v['l'] == "None"]) {
                while (running) {
                    while (y < board.getInt("h")) {
                        while (x < board.getInt("w")) {
                            // Send piece     via move_piece(v, x, y)
                            // Thread sleeps
                        }

                        x = 0;
                        y += 5;
                    }

                    y = 0;
                    x = 0;
                }
            }


            */
        }

    }

    public void movePiece (JSONObject p, int x, int y) {

        try {
            // Generate message contents
            JSONObject payload = new JSONObject();
            payload.put("id", p.get("pid"));
            payload.put("x", x);
            payload.put("y", y);

            JSONObject msg = new JSONObject();
            msg.put("pm", payload);

            // SEND
        }
        catch (JSONException je) {
            Log.d(TAG, "Error: JSON Error");
        }

    }
}
