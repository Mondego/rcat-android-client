package com.bravelittlescientist.rcat;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/4/13
 * Time: 12:56 AM
 */
public class GameLoopThread extends Thread {

    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            // update game state
            // render state to the screen
        }
    }
}
