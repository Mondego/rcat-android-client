package com.bravelittlescientist.rcat;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: Eugenia Gabrielova <genia.likes.science@gmail.com>
 * Date: 2/11/13
 * Time: 4:58 AM
 */
public class PuzzlePieceView extends ImageView {

    private Integer targetRow;
    private Integer targetCol;
    private Boolean placed;
    private String pieceId;
    private Integer remoteX;
    private Integer remoteY;

    public PuzzlePieceView(Context c, String id, boolean b, int tR, int tC, int rY, int rX) {
        super(c);

        pieceId = id;
        placed = b;
        targetRow = tR;
        targetCol = tC;
        remoteX = rX;
        remoteY = rY;
    }

    /** Get attributes **/
    public Integer getTargetRow() {
        return targetRow;
    }

    public Integer getTargetCol () {
        return targetCol;
    }

    public Boolean isPlaced() {
        return placed;
    }

    public String getPieceId () {
        return pieceId;
    }

    public Integer getRemoteX () {
        return remoteX;
    }

    public Integer getRemoteY () {
        return remoteY;
    }
}
