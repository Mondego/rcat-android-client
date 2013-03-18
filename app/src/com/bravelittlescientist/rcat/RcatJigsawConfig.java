package com.bravelittlescientist.rcat;

import android.os.Bundle;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class RcatJigsawConfig {

    private static final String TAG = "RcatJigsawConfig";

    /** Game Configuration **/
    Bundle board;
    Bundle grid;
    Bundle frus;
    Bundle pieces;
    Bundle img;
    Bundle scores;

    public RcatJigsawConfig() {
        board = new Bundle();
        grid = new Bundle();
        frus = new Bundle();
        pieces = new Bundle();
        img = new Bundle();
        scores = new Bundle();
    }

    public void configure(JSONObject config) {

        /*try {
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
        }  */


    }

    /** Getters and Setters **/

    /**
     * getFullConfiguration returns all configuration info.
     * @return config, a Bundle containing all configuration subbundles
     */
    public Bundle getFullConfiguration() {
        Bundle config = new Bundle();
        config.putBundle("board", board);
        config.putBundle("grid", grid);
        config.putBundle("frus", frus);
        config.putBundle("pieces", pieces);
        config.putBundle("img", img);
        config.putBundle("scores", scores);

        // TODO config.putString("myId", "player1234-uid");

        return config;
    }

    public Bundle getBoard() {
        return board;
    }

    public Bundle getGrid() {
        return grid;
    }

    public Bundle getFrus() {
        return frus;
    }

    public Bundle getPieces() {
        return pieces;
    }

    public Bundle getImg() {
        return getImg();
    }

    public Bundle getScores() {
        return scores;
    }
}
