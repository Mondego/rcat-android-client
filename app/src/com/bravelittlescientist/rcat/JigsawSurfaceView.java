package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/15/13
 * Time: 7:55 PM
 */
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

                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(3);

                int w = c.getWidth();
                int h = c.getHeight();
                int x = random.nextInt(w-1);
                int y = random.nextInt(h-1);
                int r = random.nextInt(255);
                int g = random.nextInt(255);
                int b = random.nextInt(255);
                p.setColor(0xff000000 + (r << 16) + (g << 8) + b);
                c.drawPoint(x, y, p);

                sH.unlockCanvasAndPost(c);
            }
        }
    }
}
