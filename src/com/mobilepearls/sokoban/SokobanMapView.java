package com.mobilepearls.sokoban;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class SokobanMapView extends View {

	static class GameMetrics {
		boolean levelFitsOnScreen;
		int tileSize = -1;
	}

	private Bitmap bitmapDiamondOnFloor;
	private Bitmap bitmapDiamondOnTarget;
	private Bitmap bitmapFloor;
	private Bitmap bitmapManOnFloor;
	private Bitmap bitmapManOnTarget;
	private final BitmapFactory.Options bitmapOptions = null;
	private Bitmap bitmapOutside;
	private Bitmap bitmapTarget;

	private Bitmap bitmapWall;

	//	private int imageSize = -1;

	protected SokobanMap map;

	protected final GameMetrics metrics = new GameMetrics();

	protected final int[] playerPos = new int[2];

	protected final Rect tileRect = new Rect(0, 0, 0, 0);

	public SokobanMapView(Context context, AttributeSet attributes) {
		super(context, attributes);
		if (context instanceof SokobanMapProvider) {
			this.map = ((SokobanMapProvider) context).getSokobanMap();
		}
	}

	protected void centerScreenOnPlayer() {
		SokobanGameState.getPlayerPosition(map, playerPos);
		int centerX = playerPos[0] * metrics.tileSize + metrics.tileSize / 2;
		int centerY = playerPos[1] * metrics.tileSize + metrics.tileSize / 2;
		// // offset + width/2 = centerX =>
		tileRect.left = getWidth() / 2 - centerX;
		tileRect.top = getHeight() / 2 - centerY;
	}

	//	private void computeMetrics() {
	//		metrics = new GameMetrics();
	//		// "-1" since the whole border tiles does not need to fit on screen:
	//		metrics.levelFitsOnScreen = ((map.getWidth() - 1) * metrics.tileSize <= getWidth() && (map.getHeight() - 1) * metrics.tileSize <= getHeight());
	//	}

	void customSizeChanged() {
		// "-1" since the whole border tiles does not need to fit on screen:
		if (metrics.tileSize <= 0) {
			metrics.tileSize = Math.min(getWidth() / map.getWidth(), getHeight() / map.getHeight());
			metrics.levelFitsOnScreen = true;
		} else {
			metrics.levelFitsOnScreen = ((map.getWidth() - 1) * metrics.tileSize <= getWidth() && (map.getHeight() - 1) * metrics.tileSize <= getHeight());
		}

		Resources resources = getResources();
		if (bitmapOptions == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;

			bitmapDiamondOnFloor = BitmapFactory.decodeResource(resources, R.drawable.diamond_on_floor_96, options);
			bitmapDiamondOnTarget = BitmapFactory.decodeResource(resources, R.drawable.diamond_on_target_96, options);
			bitmapFloor = BitmapFactory.decodeResource(resources, R.drawable.floor_96, options);
			bitmapManOnFloor = BitmapFactory.decodeResource(resources, R.drawable.man_on_floor_96, options);
			bitmapManOnTarget = BitmapFactory.decodeResource(resources, R.drawable.man_on_target_96, options);
			bitmapOutside = BitmapFactory.decodeResource(resources, R.drawable.outside_96, options);
			bitmapTarget = BitmapFactory.decodeResource(resources, R.drawable.target_96, options);
			bitmapWall = BitmapFactory.decodeResource(resources, R.drawable.wall_96, options);
		}

		//		float scaleFactor = metrics.tileSize / 96.0f;
		//		Matrix matrix = new Matrix();
		//		matrix.postScale(scaleFactor, scaleFactor);
		//
		//		int imageSize = 96;
		//		// recreate the new Bitmap
		//		bitmapDiamondOnFloor = Bitmap.createBitmap(bitmapDiamondOnFloor, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapDiamondOnTarget = Bitmap.createBitmap(bitmapDiamondOnTarget, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapFloor = Bitmap.createBitmap(bitmapFloor, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapManOnFloor = Bitmap.createBitmap(bitmapManOnFloor, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapManOnTarget = Bitmap.createBitmap(bitmapManOnTarget, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapOutside = Bitmap.createBitmap(bitmapOutside, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapTarget = Bitmap.createBitmap(bitmapTarget, 0, 0, imageSize, imageSize, matrix, true);
		//		bitmapWall = Bitmap.createBitmap(bitmapWall, 0, 0, imageSize, imageSize, matrix, true);

		if (metrics.levelFitsOnScreen) {
			// 2*offsetX + w = getWidth() =>
			tileRect.left = (getWidth() - map.getWidth() * metrics.tileSize) / 2;
			tileRect.top = (getHeight() - map.getHeight() * metrics.tileSize) / 2;
		} else {
			centerScreenOnPlayer();
		}
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		canvas.setDensity(Bitmap.DENSITY_NONE);

		final int x0 = tileRect.left, y0 = tileRect.top;
		final int tileSize = metrics.tileSize;
		Rect clip = canvas.getClipBounds();

		try {
			for (int tx = 0; tx < map.getWidth(); tx++) {
				int x = x0 + tileSize * tx;
				if (clip.isEmpty());
				else if (x + tileSize < clip.left)
					continue;
				else if (x > clip.right)
					break;
				tileRect.left = x;
				tileRect.right = x + tileSize;
				for (int ty = (clip.isEmpty() ? 0 : Math.max(0, (clip.top - y0) / tileSize)); ty < map.getHeight(); ty++) {
					int y = y0 + tileSize * ty;
					if (clip.isEmpty());
					else if (y + tileSize < clip.top)
						continue;
					else if (y > clip.bottom)
						break;
					tileRect.top = y;
					tileRect.bottom = y + tileSize;
					char c = map.getItemAt(tx, ty);
					boolean ok = drawTile(canvas, c, tileRect);
					if (! ok) {
						throw new IllegalArgumentException(String.format("Problem drawing character at (%d,%d): %c", tx, ty, c));
					}
				}
			}
		} finally {
			tileRect.left = x0;
			tileRect.top = y0;
		}
	}

	//	public int getImageSize() {
	//		int imageSize = this.imageSize;
	//		if (imageSize <= 0) {
	//			imageSize = Math.min(getWidth(), getHeight()) / 11; // 11 = tile size of first level
	//			if (imageSize % 2 != 0)
	//				imageSize--;
	//		}
	//		return imageSize;
	//	}

	protected boolean drawTile(Canvas canvas, char c, Rect rect) {
		Bitmap tileBitmap;
		switch (c) {
		case SokobanGameState.CHAR_OUTSIDE:
			tileBitmap = bitmapOutside;
			break;
		case SokobanGameState.CHAR_WALL:
			tileBitmap = bitmapWall;
			break;
		case SokobanGameState.CHAR_MAN_ON_FLOOR:
			tileBitmap = bitmapManOnFloor;
			break;
		case SokobanGameState.CHAR_MAN_ON_TARGET:
			tileBitmap = bitmapManOnTarget;
			break;
		case SokobanGameState.CHAR_FLOOR:
			tileBitmap = bitmapFloor;
			break;
		case SokobanGameState.CHAR_DIAMOND_ON_FLOOR:
			tileBitmap = bitmapDiamondOnFloor;
			break;
		case SokobanGameState.CHAR_DIAMOND_ON_TARGET:
			tileBitmap = bitmapDiamondOnTarget;
			break;
		case SokobanGameState.CHAR_TARGET:
			tileBitmap = bitmapTarget;
			break;
		default:
			return false;
		}
		canvas.drawBitmap(tileBitmap, null, rect, null);
		return true;
	}

	public int getTileSize() {
		return (metrics == null ? metrics.tileSize : 0);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);
		customSizeChanged();
	}

	//	public void setImageSize(int imageSize) {
	//		this.imageSize = imageSize;
	//	}

	public void setSokobanMap(SokobanMap map) {
		this.map = map;
	}

	void setTileSize(int size, boolean relative) {
		int newSize = (relative ? metrics.tileSize + size : size);
		if (newSize >= 70 || newSize <= 10) {
			return;
		}
		metrics.tileSize = size;
		customSizeChanged();
	}
}
