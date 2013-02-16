package com.bravelittlescientist.rcat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/4/13
 * Time: 12:56 AM
 */
public class GameLoopThread extends Thread {

    /** Game state / play variables **/
    private boolean running;

    private SurfaceHolder surfaceHolder;
    private Context gtContext;
    private Handler gtHandler;

    private Drawable jigsawImage;

    private int gameState;
    public static final int STATE_RUNNING = 10;
    public static final int STATE_PAUSED = 11;
    public static final int STATE_READY = 12;

    private int gCanvasWidth;
    private int gCanvasHeight;

    private Paint paint;

    private static final String TAG = GameLoopThread.class.getSimpleName();

    /** Thread methods **/

    public GameLoopThread (SurfaceHolder sH, Context c, Handler h) {

        surfaceHolder = sH;
        gtContext = c;
        gtHandler = h;

        Resources res = gtContext.getResources();
        jigsawImage = gtContext.getResources().getDrawable(R.drawable.diablo_1mb);
        paint = new Paint();
    }

    public void doStart() {
        synchronized(surfaceHolder) {

            // TODO: Initialize game time variables here

            // TODO Most configuration variables go here

            setState(STATE_RUNNING);
        }
    }

    public void pause() {
        synchronized (surfaceHolder) {
            if (gameState == STATE_RUNNING) setState(STATE_PAUSED);
        }
    }

    public synchronized void restoreState(Bundle savedState) {
        synchronized (surfaceHolder) {
            setState(STATE_PAUSED);

            // TODO Puzzle management variables go here
        }
    }

    @Override
    public void run() {
        while (running) {

            Canvas c = null;
            try {
                c = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    if (gameState == STATE_RUNNING) {
                        // TODO Update puzzle state
                        doDraw(c); // TODO draw puzzle stuff;
                    }
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }

        }
    }

    public void setRunning(boolean r) {
        this.running = running;
    }

    public Bundle saveState(Bundle map) {
        synchronized (surfaceHolder) {
            if (map != null) {

                // TODO Puzzle state variables here from config
            }
        }
        return map;
    }

    public void setState(int state) {
        synchronized (surfaceHolder) {
            setState(state, null);
        }
    }

    public void setState(int state, CharSequence message) {

        synchronized (surfaceHolder) {
            gameState = state;

            if (gameState == STATE_RUNNING) {
                Message msg = gtHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                msg.setData(b);
                gtHandler.sendMessage(msg);
            } else {

                Resources res = gtContext.getResources();
                CharSequence str = "";
                if (message != null) {
                    str = message + "\n" + str;
                }

                Message msg = gtHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", str.toString());
                b.putInt("viz", View.VISIBLE);
                msg.setData(b);
                gtHandler.sendMessage(msg);
            }
        }
    }

    public void setSurfaceSize(int width, int height) {
        // synchronized to make sure these all change atomically
        synchronized (surfaceHolder) {
            gCanvasWidth = width;
            gCanvasHeight = height;
        }
    }

    public void unpause() {
        // Move the real time clock up to now
        synchronized (surfaceHolder) {
            // Clock if needed
        }
        setState(STATE_RUNNING);
    }

    private void doDraw(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.

        //canvas.drawBitmap(mBackgroundImage, 0, 0, null);

        jigsawImage.draw(canvas);
        canvas.restore();
    }

}
