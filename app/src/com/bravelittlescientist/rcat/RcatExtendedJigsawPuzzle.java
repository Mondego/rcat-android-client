package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import com.bravelittlescientist.android_puzzle_view.JigsawPuzzle;

public class RcatExtendedJigsawPuzzle extends JigsawPuzzle {

    private Integer scaledWidthDimension;
    private Integer scaledHeightDimension;
    private String[] rcatPieceMapping;
    private Bundle rcatPieceMappingInverse;

    private Bundle activePieces;

    /**
     * RcatExtendedJigsawPuzzle constructor: Bundle configuration
     * @param context
     * @param configuration
     */
    public RcatExtendedJigsawPuzzle(Context context, Bundle configuration) {
        super(context, configuration);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        setPuzzleRatio(size.x, size.y);
    }

    /** Adjustments for mobile screen size **/

    public void setPuzzleRatio(int screenW, int screenH) {
        scaledWidthDimension = screenW;
        scaledHeightDimension = screenH;
    }

    public int getScaledWidthDimension() {
        return scaledWidthDimension;
    }

    public int getScaledHeightDimension() {
        return scaledHeightDimension;
    }

    public int getScaledGridPositionX() {
        int originalWidth = config.getBundle("board").getInt("w");
        int toScaleWidth = getScaledWidthDimension();
        int gridPositionX = config.getBundle("grid").getInt("x");

        if (originalWidth <= toScaleWidth) {
            return gridPositionX;
        } else {
            return (toScaleWidth - getScaledGridWidth())/2;
        }
    }

    public int getScaledGridPositionY() {
        int originalHeight = config.getBundle("board").getInt("h");
        int toScaleHeight = getScaledHeightDimension();
        int gridPositionY = config.getBundle("grid").getInt("y");

        if (originalHeight <= toScaleHeight) {
            return gridPositionY;
        } else {
            return (toScaleHeight - getScaledGridHeight())/2;
        }
    }

    public int getScaledGridWidth() {
       return config.getBundle("grid").getInt("ncols") * config.getBundle("grid").getInt("cellw");
    }

    public int getScaledGridHeight() {
        return config.getBundle("grid").getInt("nrows") * config.getBundle("grid").getInt("cellh");
    }

    /** Puzzle Piece Loading **/

    @Override
    public void loadPuzzleConfiguration() {
        Bundle grid = config.getBundle("grid");
        Bundle image = config.getBundle("img");
        Bundle pieces = config.getBundle("pieces");

        // Puzzle Grid
        puzzleGridX = grid.getInt("ncols");
        puzzleGridY = grid.getInt("nrows");
        puzzlePieceHeight = puzzleYDimension / puzzleGridY;
        puzzlePieceWidth = puzzleXDimension / puzzleGridX;

        // Fill Puzzle
        puzzlePiecesArray = new Bitmap[pieces.size()];
        pieceLocked = new boolean[pieces.size()];
        puzzlePieceTargetPositions = new int[puzzleGridX][puzzleGridY];
        rcatPieceMapping = new String[pieces.size()];
        rcatPieceMappingInverse = new Bundle();

        // Legacy Puzzle Filling
        int counter = 0;
        for (int w = 0; w < puzzleGridX; w++) {
            for (int h = 0; h < puzzleGridY; h++) {
                puzzlePiecesArray[counter] = Bitmap.createBitmap(puzzleResult, w*puzzlePieceWidth, h*puzzlePieceHeight,
                        puzzlePieceWidth, puzzlePieceHeight);

                pieceLocked[counter] = false;

                puzzlePieceTargetPositions[w][h] = counter;

                counter++;
            }
        }

        for (String puzzleKey : pieces.keySet()) {
            // Create mapping between original puzzle management and new one
            Bundle piece = pieces.getBundle(puzzleKey);
            int pieceDropX = piece.getInt("c");
            int pieceDropY = piece.getInt("r");
            int legacyMapping = puzzlePieceTargetPositions[pieceDropX][pieceDropY];
            rcatPieceMapping[legacyMapping] = puzzleKey;

            // Update Piece Lockage
            pieceLocked[legacyMapping] = piece.getBoolean("b");
            rcatPieceMappingInverse.putInt(puzzleKey, legacyMapping);
        }
    }

    public String[] getLegacyPieceMapping() {
        return rcatPieceMapping;
    }

    public Bundle getLegacyPieceMappingInverse() {
        return rcatPieceMappingInverse;
    }

    public Bitmap getPuzzleImageFull () {
        return puzzleResult;
    }
}
