package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;
import de.tavendo.autobahn.WebSocketConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class RcatExtendedPuzzleSurface extends PuzzleCompactSurface {

    private RcatExtendedJigsawPuzzle future;
    private Context msgContext;
    private boolean[] otherPlayerMoving;
    private boolean[] thisPlayerMoving;
    private Paint lockedPaint;
    private Paint controlledPaint;
    private WebSocketConnection mConn;

    public RcatExtendedPuzzleSurface(Context context, WebSocketConnection conn) {
        super(context);
        //MAX_PUZZLE_PIECE_SIZE = 80;
        msgContext = context;
        mConn = conn;

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
        LOCK_ZONE_LEFT = 200;
        LOCK_ZONE_TOP = 150;
        LOCK_ZONE_RIGHT = 600;
        LOCK_ZONE_BOTTOM = 450;

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
        thisPlayerMoving = new boolean[originalPieces.length];

        // Draw pieces onto surface
        for (int i = 0; i < originalPieces.length; i++) {

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(originalPieces[i]);

            // Top left is (0,0) in Android canvas
            int topLeftX = rcatPieces.getBundle(rcatMappings[i]).getInt("x");
            int topLeftY = rcatPieces.getBundle(rcatMappings[i]).getInt("y");

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
                    topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);
            otherPlayerMoving[i] = false;
            thisPlayerMoving[i] = false;
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

                if (thisPlayerMoving[bmd]) {
                    Rect oTM = scaledSurfacePuzzlePieces[bmd].copyBounds();
                    canvas.drawRect(oTM.left, oTM.top, oTM.right, oTM.bottom, controlledPaint);
                } else if (otherPlayerMoving[bmd]) {
                    Rect oPM = scaledSurfacePuzzlePieces[bmd].copyBounds();
                    canvas.drawRect(oPM.left, oPM.top, oPM.right, oPM.bottom, lockedPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xPos =(int) event.getX();
        int yPos =(int) event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < scaledSurfacePuzzlePieces.length; i++) {
                    Rect place = scaledSurfacePuzzlePieces[i].copyBounds();

                    if (place.contains(xPos, yPos) && !puzzle.isPieceLocked(i)) {
                        found = i;

                        // Trigger puzzle piece picked up
                        onJigsawEventPieceMove(found, place.left, place.top);
                        thisPlayerMoving[found] = true;
                        otherPlayerMoving[found] = false;
                    }
                }
                break;


            case MotionEvent.ACTION_MOVE:
                if (found >= 0 && found < scaledSurfacePuzzlePieces.length && !puzzle.isPieceLocked(found)) {
                    // Lock into position...
                    if (scaledSurfaceTargetBounds[found].contains(xPos, yPos) ) {
                        scaledSurfacePuzzlePieces[found].setBounds(scaledSurfaceTargetBounds[found]);
                        puzzle.setPieceLocked(found, true);

                        // Trigger jigsaw piece events
                        onJigsawEventPieceDrop(found,
                                scaledSurfacePuzzlePieces[found].copyBounds().left,
                                scaledSurfacePuzzlePieces[found].copyBounds().top, true);
                        thisPlayerMoving[found] = false;
                    } else {
                        Rect rect = scaledSurfacePuzzlePieces[found].copyBounds();

                        rect.left = xPos - MAX_PUZZLE_PIECE_SIZE/2;
                        rect.top = yPos - MAX_PUZZLE_PIECE_SIZE/2;
                        rect.right = xPos + MAX_PUZZLE_PIECE_SIZE/2;
                        rect.bottom = yPos + MAX_PUZZLE_PIECE_SIZE/2;
                        scaledSurfacePuzzlePieces[found].setBounds(rect);

                        // Trigger jigsaw piece event
                        onJigsawEventPieceMove(found, rect.left, rect.top);
                        thisPlayerMoving[found] = true;
                        otherPlayerMoving[found] = false;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // Trigger jigsaw piece event
                if (found >= 0 && found < scaledSurfacePuzzlePieces.length) {
                    onJigsawEventPieceDrop(found, xPos, yPos, false);
                    thisPlayerMoving[found] = false;
                }
                found = -1;
                break;

        }


        return true;
    }

    /** Piece Movements **/
    public void onMovePieceFromMessage(JSONObject msg) {
        try {
            String pieceId = msg.getString("id");
            Integer moveToX = msg.getInt("x");
            Integer moveToY = msg.getInt("y");

            Bundle rcatMappings = future.getLegacyPieceMappingInverse();
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

    public void onJigsawEventPieceMove (int index, int x, int y) {
        JSONObject message = new JSONObject();
        JSONObject pm = new JSONObject();
        try {
            pm.put("id", future.getLegacyPieceMapping()[index]);
            pm.put("x", x);
            pm.put("y", y);
            message.put("pm", pm);
            mConn.sendTextMessage(message.toString());
        } catch (JSONException e) {}
    }

    public void onJigsawEventPieceDrop (int index, int x, int y, boolean b) {
        JSONObject message = new JSONObject();
        JSONObject pd = new JSONObject();
        try {
            pd.put("id", future.getLegacyPieceMapping()[index]);
            pd.put("x", x);
            pd.put("y", y);
            pd.put("b", b);
            message.put("pd", pd);
            mConn.sendTextMessage(message.toString());
        } catch (JSONException e) {}
    }
}
