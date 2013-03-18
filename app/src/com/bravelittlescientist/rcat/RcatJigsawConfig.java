package com.bravelittlescientist.rcat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RcatJigsawConfig {

    private static final String TAG = "RcatJigsawConfig";

    /** Game Configuration **/
    private Bundle board;
    private Bundle grid;
    private Bundle frus;
    private Bundle pieces;
    private Bundle img;
    private Bundle scores;
    private String myId;

    public RcatJigsawConfig() {
        board = new Bundle();
        grid = new Bundle();
        frus = new Bundle();
        pieces = new Bundle();
        img = new Bundle();
        scores = new Bundle();
    }

    public void configure(JSONObject config, int local) {

        try {
            // Frus
            JSONObject c_frus = config.getJSONObject("frus");
            frus.putInt("x", c_frus.getInt("x"));
            frus.putInt("y", c_frus.getInt("y"));
            frus.putInt("scale", c_frus.getInt("scale"));
            frus.putString("w", c_frus.getString("w"));
            frus.putString("h", c_frus.getString("h"));

            // Image
            JSONObject c_img = config.getJSONObject("img");
            img.putString("img_url", c_img.getString("img_url"));
            img.putInt("img_local", local); // TEMPORARY
            img.putInt("img_w", c_img.getInt("img_w"));
            img.putInt("img_h", c_img.getInt("img_h"));

            // Grid
            JSONObject c_grid = config.getJSONObject("grid");
            grid.putInt("x", c_grid.getInt("x"));
            grid.putInt("y", c_grid.getInt("y"));
            grid.putInt("ncols", c_grid.getInt("ncols"));
            grid.putInt("nrows", c_grid.getInt("nrows"));
            grid.putInt("cellw", c_grid.getInt("cellw"));
            grid.putInt("cellh", c_grid.getInt("cellh"));

            // Pieces
            Bundle p;
            String key;
            for (int h = 0; h < grid.getInt("nrows"); h++) {
                for (int w = 0; w < grid.getInt("ncols"); w++) {
                    key = "piece_" + String.valueOf(w) + String.valueOf(h);
                    p = new Bundle();
                    p.putString("l", "-1");
                    p.putString("pid", key);
                    p.putBoolean("b", false);
                    p.putInt("x", w*(img.getInt("img_w")/grid.getInt("ncols")));
                    p.putInt("y", h*(img.getInt("img_h")/grid.getInt("nrows")));
                    p.putInt("r", h);
                    p.putInt("c", w);
                    pieces.putBundle(key, p);
                }
            }

            // TODO: Scores

            // Board
            JSONObject c_board = config.getJSONObject("board");
            board.putInt("w", c_board.getInt("w"));
            board.putInt("h", c_board.getInt("h"));
            board.putInt("minScale", c_board.getInt("minScale"));
            board.putInt("maxScale", c_board.getInt("maxScale"));

            // Get player ID
            myId = config.getString("myid");
        }

        catch (JSONException je) {
            Log.d(TAG, je.toString());
        }


    }

    /** Getters and Setters **/

    /**
     * getFullConfiguration returns all configuration info.
     * @return config, a Bundle containing all configuration subbundles
     */
    public Bundle getFullConfiguration() {
        Bundle config = new Bundle();

        config.putBundle("frus", frus);
        config.putBundle("img", img);
        config.putBundle("grid", grid);
        config.putBundle("pieces", pieces);
        config.putBundle("scores", scores);
        config.putBundle("board", board);
        config.putString("myId", myId);

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
        return img;
    }

    public Bundle getScores() {
        return scores;
    }

    public String getMyId() {
        return myId;
    }
}
