package com.bravelittlescientist.rcat;

import android.util.Log;
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
     * This class is designed to play the RCAT jigsaw puzzle as a bot.
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

    private boolean running = false;

    public RcatJigsawConfig() {
        // TODO possibly initialize configuration here instead TBD error checking
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
