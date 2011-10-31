package com.mobilepearls.sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mobilepearls.sokoban.io.Level;

public class SokobanGameActivity extends Activity implements SokobanMapProvider {

	/** Key under which the {@link SokobanGameState} is stored in the saved instance state bundle. */
	private static final String GAME_KEY = "GAME";
	/** Key under which the level to launch is set as an extra Intent attribute. */
	public static final String GAME_LEVEL_INTENT_EXTRA = "GAME_LEVEL";
	public static final String GAME_LEVEL_SET_EXTRA = "GAME_LEVEL_SET";

	/** Key under which the image size is stored. */
	public static final String IMAGE_SIZE_PREFS_KEY = "image_size";
	/** If the help should be shown (when max level is one). */
	public static final String SHOW_HELP_INTENT_EXTRA = "SHOW_HELP";

	private ClipboardManager clipboardManager;

	SokobanGameState gameState;

	private SokobanGameView view;

	@Override
	public SokobanMap getSokobanMap() {
		return gameState;
	}

	/*
	 * Only @Override on 2.0 android - see
	 * http://android-developers.blogspot.com/2009/12/back-and-other-hard-keys-three-stories.html
	 */
	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code in onKeyPressed() on earlier versions of the
		// platform.
		view.backPressed();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			gameState = (SokobanGameState) savedInstanceState.getSerializable(GAME_KEY);
		}
		int levelIndex = 0;
		Intent intent = getIntent();
		String levelSet = "microban";
		if (intent != null && intent.getExtras() != null) {
			levelSet = intent.getExtras().getString(GAME_LEVEL_SET_EXTRA);
			levelIndex = intent.getExtras().getInt(GAME_LEVEL_INTENT_EXTRA, 0);
			if (intent.getExtras().getBoolean(SHOW_HELP_INTENT_EXTRA, false))
				showHelp(); // show when starting first level from menu
			if (levelSet == null) {
				levelSet = "microban";
			}
		}
		SokobanLevels sokobanLevels = SokobanLevels.getSokobanLevels(levelSet);
		Level level = sokobanLevels.getLevel(levelIndex, getAssets());
		if (gameState == null) {
			gameState = new SokobanGameState(level);
		}
		setContentView(R.layout.main);
		view = (SokobanGameView) findViewById(R.id.android_memoryview);
		view.setLevelSet(sokobanLevels);
		view.setTileSize(getSharedPreferences(SokobanMenuActivity.SHARED_PREFS_NAME, MODE_PRIVATE).getInt(IMAGE_SIZE_PREFS_KEY, -1), false);
		view.performMoves(level.getMoves());
		clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/** Overridden to handle back button - see {@link SokobanGameActivity#onBackPressed() } */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (android.os.Build.VERSION.SDK_INT < 5 /* =android.os.Build.VERSION_CODES.ECLAIR */
				&& keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Take care of calling this method on earlier versions of
			// the platform where it doesn't exist.
			onBackPressed();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			view.setTileSize(2, true);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			view.setTileSize(-2, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			// avoid the beep when pressing the buttons
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.back_menu) {
			finish();
		} else if (item.getItemId() == R.id.help_menu) {
			showHelp();
		} else if (item.getItemId() == R.id.copy_moves_menu) {
			clipboardManager.setText(gameState.getUndos());
		} else if (item.getItemId() == R.id.paste_moves_menu) {
			if (clipboardManager.hasText()) {
				view.startMovesAnimation(clipboardManager.getText(), SokobanGameView.MOVES_ANIMATION_DELAY);
			}
		} else if (item.getItemId() == R.id.undo_all_menu) {
			view.startUndoAnimation();
		} else if (item.getItemId() == R.id.restart_menu) {
			gameState.restart();
			view.invalidate();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(GAME_KEY, gameState);
	}

	//	private void setImageSize(int newSize) {
	//		//		IMAGE_SIZE = newSize;
	//		SharedPreferences prefs = getSharedPreferences(SokobanMenuActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
	//		Editor editor = prefs.edit();
	//		editor.putInt(IMAGE_SIZE_PREFS_KEY, newSize);
	//		editor.commit();
	//		view.customSizeChanged();
	//	}

	public void showHelp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setMessage("Push all red diamonds on the green targets to complete a level. Complete levels to unlock new ones.\n\nZoom in and out using the volume control.\n\nUndo moves with the back button.");
		builder.setPositiveButton("Ok", null);
		builder.create().show();
	}
}
