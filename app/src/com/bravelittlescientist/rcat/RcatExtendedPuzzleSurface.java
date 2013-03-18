package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;
import org.json.JSONObject;

import java.util.Random;

public class RcatExtendedPuzzleSurface extends PuzzleCompactSurface {

    private RcatExtendedJigsawPuzzle future;
    private Context msgContext;
    private boolean[] otherPlayerMoving;
    private Paint lockedPaint;
    private Paint controlledPaint;

    public RcatExtendedPuzzleSurface(Context context) {
        super(context);
        //MAX_PUZZLE_PIECE_SIZE = 80;
        msgContext = context;

        lockedPaint = new Paint();
        lockedPaint.setColor(Color.RED);
        lockedPaint.setStyle(Paint.Style.STROKE);
        lockedPaint.setStrokeWidth(5);

        controlledPaint = new Paint();
        controlledPaint.setColor(Color.BLUE);
        controlledPaint.setStyle(Paint.Style.STROKE);
        controlledPaint.setStrokeWidth(5);
    }

    public void setPuzzle(RcatExtendedJigsawPuzzle jigsawPuzzle) {

        future = jigsawPuzzle;
        puzzle = jigsawPuzzle;
        Random r = new Random();

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage = new BitmapDrawable(puzzle.getBackgroundTexture());
            backgroundImage.setBounds(0, 0, future.getScaledWidthDimension(), future.getScaledHeightDimension());
        }
        framePaint = new Paint();
        framePaint.setColor(Color.BLACK);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setTextSize(20);

        /** Set Lock Zone **/
        LOCK_ZONE_LEFT = future.getScaledGridPositionX();
        LOCK_ZONE_TOP = future.getScaledGridPositionY();
        LOCK_ZONE_RIGHT = LOCK_ZONE_LEFT + future.getScaledGridWidth();
        LOCK_ZONE_BOTTOM = LOCK_ZONE_TOP + future.getScaledGridHeight();

        /** Initialize drawables from puzzle pieces **/
        Bitmap[] originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();
        String[] rcatMappings = future.getLegacyPieceMapping();
        Bundle rcatPieces = future.getConfig().getBundle("pieces");

        // Initialize piece drawable managers
        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];
        scaledSurfaceTargetBounds = new Rect[originalPieces.length];
        otherPlayerMoving = new boolean[originalPieces.length];

        // Draw pieces onto surface
        for (int i = 0; i < originalPieces.length; i++) {

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(originalPieces[i]);

            // Top left is (0,0) in Android canvas
            int topLeftX = rcatPieces.getBundle(rcatMappings[i]).getInt("x");
            int topLeftY = rcatPieces.getBundle(rcatMappings[i]).getInt("y");

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
                    topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);
            otherPlayerMoving[i] = false;
        }

        for (int w = 0; w < dimensions[2]; w++) {
            for (int h = 0; h < dimensions[3]; h++) {
                int targetPiece = positions[w][h];

                scaledSurfaceTargetBounds[targetPiece] = new Rect(
                        LOCK_ZONE_LEFT + w*MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_TOP + h*MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_LEFT + w*MAX_PUZZLE_PIECE_SIZE + MAX_PUZZLE_PIECE_SIZE,
                        LOCK_ZONE_TOP + h*MAX_PUZZLE_PIECE_SIZE + MAX_PUZZLE_PIECE_SIZE);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (puzzle.isPieceLocked(bmd)) {
                scaledSurfacePuzzlePieces[bmd].draw(canvas);
            }
        }

        for (int bmd = 0; bmd < scaledSurfacePuzzlePieces.length; bmd++) {
            if (!puzzle.isPieceLocked(bmd)) {
                scaledSurfacePuzzlePieces[bmd].draw(canvas);

                if (otherPlayerMoving[bmd]) {
                    Rect oPM = scaledSurfacePuzzlePieces[bmd].copyBounds();
                    canvas.drawRect(oPM.left, oPM.top, oPM.right, oPM.bottom, lockedPaint);
                }
            }
        }
    }

    /** Piece Movements **/
    public void onMovePieceFromMessage(JSONObject msg) {
        try {
            String pieceId = msg.getString("id");
            Integer moveToX = msg.getInt("x");
            Integer moveToY = msg.getInt("y");

            Bundle rcatMappings = future.getLegacyPieceMappingInverse();
            Bundle rcatPieces = future.getConfig().getBundle("pieces");
            int targetPiece = rcatMappings.getInt(pieceId);

            Rect place = scaledSurfacePuzzlePieces[targetPiece].copyBounds();
            place.left = moveToX;
            place.top = moveToY;
            place.right = moveToX + MAX_PUZZLE_PIECE_SIZE;
            place.bottom = moveToY + MAX_PUZZLE_PIECE_SIZE;

            otherPlayerMoving[targetPiece] = true;

            scaledSurfacePuzzlePieces[targetPiece].setBounds(place);

        } catch (Exception e) {}
    }

    public void onDropPieceFromMessage(JSONObject msg) {
        try {
            String pieceId = msg.getString("id");
            Integer moveToX = msg.getInt("x");
            Integer moveToY = msg.getInt("y");
            Boolean isBound = msg.getBoolean("b");

            Bundle rcatMappings = future.getLegacyPieceMappingInverse();
            Bundle rcatPieces = future.getConfig().getBundle("pieces");
            int targetPiece = rcatMappings.getInt(pieceId);

            Rect place = scaledSurfacePuzzlePieces[targetPiece].copyBounds();
            place.left = moveToX;
            place.top = moveToY;
            place.right = moveToX + MAX_PUZZLE_PIECE_SIZE;
            place.bottom = moveToY + MAX_PUZZLE_PIECE_SIZE;
            scaledSurfacePuzzlePieces[targetPiece].setBounds(place);

            otherPlayerMoving[targetPiece] = false;

            if (isBound) {
                puzzle.setPieceLocked(targetPiece, true);
                future.setPieceLocked(targetPiece, true);
            }

        } catch (Exception e) {}
    }

    protected void isPieceOnTargetLocation() {

    }
}
