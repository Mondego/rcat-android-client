package com.bravelittlescientist.rcat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 1/20/13
 * Time: 10:22 PM
 */
public class RcatJigsawBotActivity extends Activity {

    private JigsawBot rcatBot;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jigsaw_layout);

        ImageView img = new ImageView(RcatJigsawBotActivity.this);
        img.setImageResource(R.drawable.diablo_1mb);
        LinearLayout layout = (LinearLayout)findViewById(R.id.jigsaw_bot_layout);
        layout.addView(img);
    }
}