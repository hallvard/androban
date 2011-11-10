package com.mobilepearls.sokoban.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.mobilepearls.sokoban.core.SokobanGameState;
import com.mobilepearls.sokoban.core.SokobanMap;
import com.mobilepearls.sokoban.io.Level;

public class SokobanGameView extends SokobanMapView {

	private class MovesAnimation extends SokobanAnimation {
		private CharSequence moves;
		private int pos;
		public MovesAnimation(CharSequence moves, int delay) {
			super(delay);
			this.moves = moves;
		}
		@Override
		public boolean step() {
			if (moves != null && pos < moves.length()) {
				char move = moves.charAt(pos++);
				if (performMove(Character.toLowerCase(move), false) <= 0) {
					moves = null;
				}
			}
			return moves != null && pos < moves.length();
		}
	}

	private abstract class SokobanAnimation implements Runnable {
		private final int delay;
		public SokobanAnimation(int delay) {
			this.delay = delay;
		}
		@Override
		public void run() {
			boolean more = step();
			movesAnimationHandler.removeCallbacks(this);
			if (movesAnimationRunnable == this && more) {
				movesAnimationHandler.postDelayed(this, delay);
			} else {
				movesAnimationRunnable = null;
			}
		}

		protected abstract boolean step();
	}

	class TouchHandler extends SimpleOnGestureListener implements OnTouchListener {

		private Point diamondDragPosition = null;
		private final Point down = new Point();
		boolean ignoreDrag;
		private final Point last = new Point();
		private final Point offset = new Point();
		private final Point tile = new Point();

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			int threshold = FLING_VELOCITY_THRESHOLD * metrics.tileSize;
			if (Math.abs(velocityX) > threshold || Math.abs(velocityY) > threshold) {
				if (Math.abs(velocityX) >= Math.abs(velocityY)) {
					velocityY = 0;
				} else {
					velocityX = 0;
				}
				char move = SokobanGameState.moveFor((int) Math.signum(velocityX), (int) Math.signum(velocityY));
				SokobanGameState game = getSokobanGame();
				CharSequence moves = game.computeLastMovesInDirection(move);
				if (moves == null)
					moves = game.computeMovesInDirection(move, true);
				startMovesAnimation(moves, MOVES_STOPABLE_ANIMATION_DELAY);
				return true;
			}
			return false;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (gestureDetector.onTouchEvent(event)) {
				return true;
			}
			tile.set(((int) event.getX() - tileRect.left) / metrics.tileSize, ((int) event.getY() - tileRect.top) / metrics.tileSize);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ignoreDrag = (movesAnimationRunnable != null);
				stopSokobanAnimation();
				down.set((int) event.getX(), (int) event.getY());
				offset.set(0, 0);
				char c = getSokobanGame().getItemAt(tile.x, tile.y);
				diamondDragPosition = (c == SokobanMap.CHAR_DIAMOND_ON_FLOOR || c == SokobanMap.CHAR_DIAMOND_ON_TARGET ? new Point(tile) : null);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (ignoreDrag)
					return true;
				// perhaps move to clicked tile? if not is moving?
				if (Math.abs(down.x - event.getX()) < metrics.tileSize && Math.abs(down.y - event.getY()) < metrics.tileSize) {
					CharSequence moves = getSokobanGame().computeMovesToGoal(tile.x, tile.y);
					if (moves != null) {
						startMovesAnimation(moves, MOVES_ANIMATION_DELAY);
					}
				}
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (ignoreDrag || movesAnimationRunnable != null)
					return true;

				offset.offset(last.x - (int) event.getX(), last.y - (int) event.getY());

				int dx = 0, dy = 0;

				if (Math.abs(offset.x) >= Math.abs(offset.y)) {
					dx = offset.x / metrics.tileSize;
				} else {
					dy = offset.y / metrics.tileSize;
				}
				if (dx != 0 || dy != 0) {
					boolean consumeOffset = false;
					if (diamondDragPosition != null) {
						int signX = (int) Math.signum(-dx), signY = (int) Math.signum(-dy);
						int otherX = diamondDragPosition.x - signX, otherY = diamondDragPosition.y - signY;
						char otherTile = getSokobanGame().getItemAt(otherX, otherY);
						CharSequence moves = getSokobanGame().computeMovesToGoal(otherX, otherY);
						if (moves != null) {
							startMovesAnimation(moves, MOVES_ANIMATION_DELAY);
							// performMoves(moves, true);
						}
						otherTile = getSokobanGame().getItemAt(otherX, otherY);
						if (otherTile == SokobanMap.CHAR_MAN_ON_FLOOR || otherTile == SokobanMap.CHAR_MAN_ON_TARGET) {
							int steps = performMove(-dx, -dy, true);
							consumeOffset = true;
							if (steps > 0) {
								diamondDragPosition.offset(steps * signX, steps * signY);
							}
						}
						// drag diamond
					} else {
						// move player
						performMove(-dx, -dy, true);
						consumeOffset = true;
					}
					if (consumeOffset) {
						if (dx != 0) {
							offset.y = 0; // <= since we move horizontally, reset vertical offset
							offset.x -= dx * metrics.tileSize;
						} else if (dy != 0) {
							offset.x = 0; // <= since we move vertically, reset horizontal offset
							offset.y -= dy * metrics.tileSize;
						}
					}
				}
			}
			last.set((int) event.getX(), (int) event.getY());
			return true;
		}
	}

	private class UndoAnimation extends SokobanAnimation {
		public UndoAnimation() {
			super(MOVES_STOPABLE_ANIMATION_DELAY);
		}
		@Override
		public boolean step() {
			SokobanGameState game = getSokobanGame();
			if (game.canUndo()) {
				backPressed();
			}
			return game.canUndo();
		}
	}

	private final static int FLING_VELOCITY_THRESHOLD = 10;
	public static final int MOVES_ANIMATION_DELAY = 10;
	private static final int MOVES_STOPABLE_ANIMATION_DELAY = MOVES_ANIMATION_DELAY * 8;

	private Level currentLevel;

	private final GestureDetector gestureDetector;
	private boolean hapticFeedback;
	private SokobanLevels levelSet;

	private final Handler movesAnimationHandler = new Handler();
	private Runnable movesAnimationRunnable = null;

	private TouchHandler touchHandler;

	public SokobanGameView(Context context, AttributeSet attributes) {
		super(context, attributes);

		hapticFeedback = getContext().getSharedPreferences(SokobanMenuActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
		.getBoolean(SokobanMenuActivity.HAPTIC_FEEDBACK_PREFS_NAME,
				SokobanMenuActivity.HAPTIC_FEEDBACK_DEFAULT_VALUE);

		touchHandler = new TouchHandler();
		gestureDetector = new GestureDetector(touchHandler);
		setOnTouchListener(touchHandler);

		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					int[] direction = SokobanGameState.directionFor(keyCode, SokobanGameState.DIRECTIONS_KEY_POS);
					if (direction != null) {
						performMove(direction[0], direction[1], true);
						return true;
					} else if (keyCode == KeyEvent.KEYCODE_DEL) {
						backPressed();
					}
				}
				return false;
			}
		});
	}

	/** Called by our own activity. */
	public void backPressed() {
		SokobanGameState game = getSokobanGame();
		if (game.tryUndo()) {
			centerScreenOnPlayerIfNecessary();
			invalidate();
		} else if (! game.canUndo()) {
			((Activity) getContext()).finish();
		}
	}

	private void centerScreenOnPlayerIfNecessary() {
		if (metrics.levelFitsOnScreen) {
			return;
		}

		SokobanGameState.getPlayerPosition(map, playerPos);
		int playerX = playerPos[0];
		int playerY = playerPos[1];

		int tileSize = metrics.tileSize;
		int tilesLeftOfPlayer = (playerX * tileSize + tileRect.left) / tileSize;
		int tilesRightOfPlayer = (getWidth() - playerX * tileSize - tileRect.left) / tileSize;
		int tilesAboveOfPlayer = (playerY * tileSize + tileRect.top) / tileSize;
		int tilesBelowOfPlayer = (getHeight() - playerY * tileSize - tileRect.top) / tileSize;

		final int THRESHOLD = 1;
		if (tilesLeftOfPlayer <= THRESHOLD || tilesRightOfPlayer <= THRESHOLD || tilesAboveOfPlayer <= THRESHOLD
				|| tilesBelowOfPlayer <= THRESHOLD) {
			centerScreenOnPlayer();
			touchHandler.ignoreDrag = true;
		}
	}

	void gameOver() {
		if (hapticFeedback) {
			Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(300);
		}
		invalidate();
		//		SharedPreferences prefs = getContext().getSharedPreferences(SokobanMenuActivity.SHARED_PREFS_NAME,
		//				Context.MODE_PRIVATE);
		//		final String maxLevelPrefName = SokobanLevelsListActivity.getMaxLevelPrefName(levelSet);
		//		int currentMaxLevel = prefs.getInt(maxLevelPrefName, 1);
		int levelIndex = levelSet.getLevelIndex(currentLevel);
		final int nextLevelIndex = Math.max(levelSet.getIndexOfRemainingLevel(levelIndex + 1), levelSet.getIndexOfRemainingLevel(0));
		//		int newMaxLevel = levelIndex + 2;
		String message = "Level cleared - opening next level!";
		if (nextLevelIndex < levelIndex) {
			message = "Level cleared - opening previously unfinished level";
		} else if (nextLevelIndex >= levelSet.getLevelCount()) {
			message = "You completed the last level!";
			//			levelSetDone = true;
			//			} else {
			//				Editor editor = prefs.edit();
			//				editor.putInt(maxLevelPrefName, newMaxLevel);
			//				editor.commit();
			//				message = "New level unlocked!";
			//			}
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
		alert.setCancelable(false);
		alert.setMessage(message);
		alert.setTitle("Congratulations");
		//		final int levelDestination = nextLevelIndex;
		//		final boolean levelSetDoneFinal = levelSetDone;
		alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) getContext()).finish();
				if (nextLevelIndex < levelSet.getLevelCount()) {
					Intent intent = SokobanGameActivity.createSokobanLevelIntent(levelSet, nextLevelIndex);
					intent.setClass(getContext(), SokobanGameActivity.class);
					getContext().startActivity(intent);
				}
			}
		});
		alert.show();
	}

	private SokobanGameState getSokobanGame() {
		return (SokobanGameState) map;
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		super.onSizeChanged(width, height, oldw, oldh);
		customSizeChanged();
	}

	private int performMove(char move, boolean hapticFeedback) {
		int[] direction = SokobanGameState.directionFor(move, SokobanGameState.DIRECTIONS_CHAR_POS);
		return performMove(direction[0], direction[1], hapticFeedback);
	}

	private int performMove(int dx, int dy, boolean hapticFeedback) {
		SokobanGameState game = getSokobanGame();
		int steps = game.tryMove(dx, dy);
		if (steps > 0) {
			if (hapticFeedback && this.hapticFeedback) {
				performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
			}
			centerScreenOnPlayerIfNecessary();
			invalidate();

			if (game.isDone()) {
				gameOver();
				return -steps;
			}
			return steps;
		}
		return 0;
	}

	private boolean performMoves(CharSequence moves, boolean hapticFeedback) {
		SokobanGameState game = getSokobanGame();
		for (int i = 0; i < moves.length(); i++) {
			if (performMove(moves.charAt(i), hapticFeedback) <= 0) {
				return false;
			}
			hapticFeedback = false;
		}
		return true;
	}

	public void setLevelInfo(SokobanLevels levelSet, Level level) {
		this.levelSet = levelSet;
		this.currentLevel = level;
	}

	public void startMovesAnimation(CharSequence moves, int delay) {
		stopSokobanAnimation();
		movesAnimationRunnable = new MovesAnimation(moves, delay);
		movesAnimationHandler.postDelayed(movesAnimationRunnable, delay);
	}

	public void startUndoAnimation() {
		stopSokobanAnimation();
		movesAnimationRunnable = new UndoAnimation();
		movesAnimationHandler.postDelayed(movesAnimationRunnable, MOVES_ANIMATION_DELAY);
	}

	private void stopSokobanAnimation() {
		if (movesAnimationRunnable != null)
			movesAnimationHandler.removeCallbacks(movesAnimationRunnable);
		movesAnimationRunnable = null;
	}
}
