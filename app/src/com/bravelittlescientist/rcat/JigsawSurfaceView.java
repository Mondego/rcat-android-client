package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class JigsawSurfaceView extends SurfaceView implements Runnable {

    Thread gameThread = null;
    SurfaceHolder sH;
    volatile boolean running = false;

    private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    Random random;

    public JigsawSurfaceView(Context context) {
        super(context);
        sH = getHolder();
        random = new Random();
    }

    public void onResumeJigsawSurfaceView () {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void onPauseJigsawSurfaceView() {
        boolean retry = true;
        running = false;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException iE) {
                iE.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            if (sH.getSurface().isValid()) {
                Canvas c = sH.lockCanvas();

                // TODO draw jigsaw here

                sH.unlockCanvasAndPost(c);
            }
        }
    }
}
