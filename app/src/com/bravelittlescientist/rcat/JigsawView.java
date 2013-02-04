package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/3/13
 * Time: 8:48 PM
 */
public class JigsawView extends SurfaceView implements
        SurfaceHolder.Callback {

    private GameLoopThread gameThread;
    private static final String TAG = JigsawView.class.getSimpleName();


    public JigsawView(Context context) {
        super(context);
        getHolder().addCallback(this);

        gameThread = new GameLoopThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        while (retry) {

            try {

                gameThread.join();
                retry = false;

            } catch (InterruptedException e) {

                // try again shutting down the thread

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > getHeight() - 50) {
                gameThread.setRunning(false);
                ((Activity)getContext()).finish();
            } else {
                Log.d(TAG, "Coordinates: x=" + event.getX() + ",y=" + event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }
}