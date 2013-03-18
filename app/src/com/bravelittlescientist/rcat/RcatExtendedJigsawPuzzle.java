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

        /*for (String puzzleKey : pieces.keySet()) {
            // Create mapping between original puzzle management and new one
            Bundle piece = pieces.getBundle(puzzleKey);
            int pieceDropX = piece.getInt("c");
            int pieceDropY = piece.getInt("r");
            int legacyMapping = puzzlePieceTargetPositions[pieceDropX][pieceDropY];
            rcatPieceMapping[legacyMapping] = puzzleKey;

            // Update Piece Lockage
            pieceLocked[legacyMapping] = piece.getBoolean("b");
        }   */
    }

    public String[] getLegacyPieceMapping() {
        return rcatPieceMapping;
    }
}
