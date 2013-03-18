package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import com.bravelittlescientist.android_puzzle_view.JigsawPuzzle;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;

import java.util.Random;

public class RcatExtendedPuzzleSurface extends PuzzleCompactSurface {

    public RcatExtendedPuzzleSurface(Context context) {
        super(context);
    }

    public void setPuzzle(RcatExtendedJigsawPuzzle jigsawPuzzle) {

        puzzle = jigsawPuzzle;
        Random r = new Random();

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage = new BitmapDrawable(puzzle.getBackgroundTexture());
            backgroundImage.setBounds(0, 0, jigsawPuzzle.getScaledWidthDimension(), jigsawPuzzle.getScaledHeightDimension());
        }
        framePaint = new Paint();
        framePaint.setColor(Color.BLACK);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setTextSize(20);

        /** Initialize drawables from puzzle pieces **/
        Bitmap[] originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();
        String[] rcatMappings = jigsawPuzzle.getLegacyPieceMapping();
        Bundle rcatPieces = puzzle.getConfig().getBundle("pieces");

        // Initialize piece drawable managers
        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];
        scaledSurfaceTargetBounds = new Rect[originalPieces.length];

        // Draw pieces onto surface
        for (int i = 0; i < originalPieces.length; i++) {

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(originalPieces[i]);

            // Top left is (0,0) in Android canvas
            int topLeftX = r.nextInt(jigsawPuzzle.getScaledWidthDimension() - MAX_PUZZLE_PIECE_SIZE);
            int topLeftY = r.nextInt(jigsawPuzzle.getScaledHeightDimension() - 2*MAX_PUZZLE_PIECE_SIZE);

            scaledSurfacePuzzlePieces[i].setBounds(topLeftX, topLeftY,
                    topLeftX + MAX_PUZZLE_PIECE_SIZE, topLeftY + MAX_PUZZLE_PIECE_SIZE);
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
}
