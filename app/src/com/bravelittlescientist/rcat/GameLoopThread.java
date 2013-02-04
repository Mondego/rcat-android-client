package com.bravelittlescientist.rcat;

import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/4/13
 * Time: 12:56 AM
 */
public class GameLoopThread extends Thread {

    private boolean running;
    private SurfaceHolder surfaceHolder;
    private JigsawView jigsaw;
    private static final String TAG = GameLoopThread.class.getSimpleName();

    public GameLoopThread (SurfaceHolder sH, JigsawView jV) {
        this.surfaceHolder = sH;
        this.jigsaw = jV;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {

            long tick = 0L;
            Log.d(TAG, "Begin game loop.");
            while (running) {
                tick++;

                // update game state
                // render state to the screen

            }

            Log.d(TAG, "Game loop executed " + tick + " times");

        }
    }
}
