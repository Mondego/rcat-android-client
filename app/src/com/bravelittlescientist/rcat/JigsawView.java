package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/3/13
 * Time: 8:48 PM
 */
public class JigsawView extends SurfaceView implements
        SurfaceHolder.Callback {

    private Context gContext;

    private TextView gameStatusText;

    private GameLoopThread thread;

    private static final String TAG = JigsawView.class.getSimpleName();

    public JigsawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new GameLoopThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                gameStatusText.setVisibility(m.getData().getInt("viz"));
                gameStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true);
    }

    public GameLoopThread getThread() {
        return thread;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    public void setTextView(TextView textView) {
        gameStatusText = textView;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        thread.setRunning(false);
        while (retry) {

            try {

                thread.join();
                retry = false;

            } catch (InterruptedException e) {

                // try again shutting down the thread

            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > getHeight() - 50) {
                gameThread.setRunning(false);
                ((Activity)getContext()).finish();
            } else {
                Log.d(TAG, "Coordinates: x=" + event.getX() + ",y=" + event.getY());
            }
        }*/
        return super.onTouchEvent(event);
    }
}