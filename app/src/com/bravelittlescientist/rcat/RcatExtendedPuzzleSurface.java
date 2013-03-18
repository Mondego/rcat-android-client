package com.bravelittlescientist.rcat;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.WindowManager;
import com.bravelittlescientist.android_puzzle_view.JigsawPuzzle;
import com.bravelittlescientist.android_puzzle_view.PuzzleCompactSurface;

import java.util.Random;

public class RcatExtendedPuzzleSurface extends PuzzleCompactSurface {

    public RcatExtendedPuzzleSurface(Context context) {
        super(context);
    }

    @Override
    public void setPuzzle(JigsawPuzzle jigsawPuzzle) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);

        puzzle = jigsawPuzzle;
        Random r = new Random();

        if (puzzle.isBackgroundTextureOn()) {
            backgroundImage = new BitmapDrawable(puzzle.getBackgroundTexture());
            backgroundImage.setBounds(0, 0, outSize.x, outSize.y);
        }
        framePaint = new Paint();
        framePaint.setColor(Color.BLACK);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setTextSize(20);

        /** Initialize drawables from puzzle pieces **/
        Bitmap[] originalPieces = puzzle.getPuzzlePiecesArray();
        int[][] positions = puzzle.getPuzzlePieceTargetPositions();
        int[] dimensions = puzzle.getPuzzleDimensions();

        scaledSurfacePuzzlePieces = new BitmapDrawable[originalPieces.length];
        scaledSurfaceTargetBounds = new Rect[originalPieces.length];

        for (int i = 0; i < originalPieces.length; i++) {

            scaledSurfacePuzzlePieces[i] = new BitmapDrawable(originalPieces[i]);

            // Top left is (0,0) in Android canvas
            int topLeftX = r.nextInt(outSize.x - MAX_PUZZLE_PIECE_SIZE);
            int topLeftY = r.nextInt(outSize.y - 2*MAX_PUZZLE_PIECE_SIZE);

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
