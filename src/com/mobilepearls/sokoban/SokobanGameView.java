package com.mobilepearls.sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class SokobanGameView extends SokobanMapView {

	class MovesAnimation implements Runnable {
		private final int delay;
		private CharSequence moves;
		private int pos;
		public MovesAnimation(CharSequence moves, int delay) {
			this.moves = moves;
			this.delay = delay;
		}
		@Override
		public void run() {
			if (moves != null && pos < moves.length()) {
				char move = moves.charAt(pos++);
				if (! performMove(Character.toLowerCase(move), false)) {
					moves = null;
				}
			}
			movesAnimationHandler.removeCallbacks(this);
			if (movesAnimationRunnable == this && moves != null) {
				if (pos < moves.length()) {
					movesAnimationHandler.postDelayed(this, delay);
				} else {
					moves = null;
				}
			}
		}
	}

	class TouchHandler extends SimpleOnGestureListener implements OnTouchListener {

		private int xDown;
		private int xOffset;
		private int xTouch;
		private int yDown;
		private int yOffset;

		private int yTouch;

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			if (Math.abs(velocityX) > FLING_VELOCITY_THRESHOLD || Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
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
				startMovesAnimation(moves, MOVES_REPEATLAST_ANIMATION_DELAY);
				return true;
			}
			return false;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (gestureDetector.onTouchEvent(event)) {
				return true;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ignoreDrag = (movesAnimationRunnable != null);
				stopMovesAnimation();
				xDown = xTouch = (int) event.getX();
				yDown = yTouch = (int) event.getY();
				xOffset = 0;
				yOffset = 0;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (ignoreDrag)
					return true;
				// perhaps move to clicked tile? if not is moving?
				if (Math.abs(xDown - event.getX()) < metrics.tileSize && Math.abs(yDown - event.getY()) < metrics.tileSize) {
					CharSequence moves = getSokobanGame().computeMovesToGoal((xTouch - tileRect.left) / metrics.tileSize, (yTouch - tileRect.top) / metrics.tileSize);
					startMovesAnimation(moves, MOVES_ANIMATION_DELAY);
				}
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (ignoreDrag)
					return true;

				xOffset += xTouch - (int) event.getX();
				yOffset += yTouch - (int) event.getY();

				int dx = 0, dy = 0;

				if (Math.abs(xOffset) >= Math.abs(yOffset)) {
					// perhaps move x?
					dx = xOffset / metrics.tileSize;
					if (dx != 0) {
						yOffset = 0; // <= since we move horizontally, reset vertical offset
						xOffset -= dx * metrics.tileSize;
					}
				} else {
					// perhaps move y?
					dy = yOffset / metrics.tileSize;
					if (dy != 0) {
						xOffset = 0; // <= since we move vertically, reset horizontal offset
						yOffset -= dy * metrics.tileSize;
					}
				}

				performMove(-dx, -dy, true);

				xTouch = (int) event.getX();
				yTouch = (int) event.getY();
			}
			return true;
		}
	}

	class UndoAnimation implements Runnable {
		@Override
		public void run() {
			SokobanGameState game = getSokobanGame();
			if (game.canUndo()) {
				backPressed();
			}
			movesAnimationHandler.removeCallbacks(this);
			if (movesAnimationRunnable == this && game.canUndo()) {
				movesAnimationHandler.postDelayed(this, MOVES_ANIMATION_DELAY);
			}
		}
	}
	private final static int FLING_VELOCITY_THRESHOLD = 100;
	public static final int MOVES_ANIMATION_DELAY = 50;
	private static final int MOVES_REPEATLAST_ANIMATION_DELAY = MOVES_ANIMATION_DELAY * 2;

	private final GestureDetector gestureDetector;

	private boolean hapticFeedback;
	boolean ignoreDrag;
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
		if (game.performUndo()) {
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
			ignoreDrag = true;
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
		int levelIndex = levelSet.getLevelIndex(getSokobanGame().getCurrentLevel());
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
	boolean performMove(char move, boolean hapticFeedback) {
		int[] direction = SokobanGameState.directionFor(move, SokobanGameState.DIRECTIONS_CHAR_POS);
		return performMove(direction[0], direction[1], hapticFeedback);
	}

	boolean performMove(int dx, int dy, boolean hapticFeedback) {
		SokobanGameState game = getSokobanGame();
		if (game.tryMove(dx, dy)) {
			if (hapticFeedback && this.hapticFeedback) {
				performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
			}
			centerScreenOnPlayerIfNecessary();
			invalidate();

			if (game.isDone()) {
				gameOver();
				return false;
			}
			return true;
		}
		return false;
	}

	boolean performMoves(CharSequence moves) {
		SokobanGameState game = getSokobanGame();
		boolean ok = true;
		for (int i = 0; i < moves.length() && ok; i++) {
			char move = moves.charAt(i);
			int[] direction = SokobanGameState.directionFor(move, SokobanGameState.DIRECTIONS_CHAR_POS);
			int dx = direction[0], dy = direction[1];
			if ((! game.tryMove(dx, dy)) || game.isDone()) {
				ok = false;
			}
		}
		centerScreenOnPlayerIfNecessary();
		invalidate();
		return ok;
	}
	public void setLevelSet(SokobanLevels levelSet) {
		this.levelSet = levelSet;
	}

	public void startMovesAnimation(CharSequence moves, int delay) {
		stopMovesAnimation();
		movesAnimationRunnable = new MovesAnimation(moves, delay);
		movesAnimationHandler.postDelayed(movesAnimationRunnable, delay);
	}

	public void startUndoAnimation() {
		stopMovesAnimation();
		movesAnimationRunnable = new UndoAnimation();
		movesAnimationHandler.postDelayed(movesAnimationRunnable, MOVES_ANIMATION_DELAY);
	}

	private void stopMovesAnimation() {
		if (movesAnimationRunnable != null)
			movesAnimationHandler.removeCallbacks(movesAnimationRunnable);
		movesAnimationRunnable = null;
	}
}
