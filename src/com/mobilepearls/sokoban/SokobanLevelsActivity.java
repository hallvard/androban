package com.mobilepearls.sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilepearls.sokoban.io.Level;

public class SokobanLevelsActivity extends ListActivity {

	private class SokobanLevelsAdapter extends BaseAdapter {

		private final Activity context;
		private final int count;
		private final SokobanLevels levels;

		public SokobanLevelsAdapter(Activity context, SokobanLevels levels, int count) {
			this.context = context;
			this.levels = levels;
			this.count = count;
		}
		@Override
		public int getCount() {
			return (count > 0 ? count : levels.getLevelCount());
		}
		@Override
		public Object getItem(int position) {
			return levels.getLevel(reverse ? getCount() - 1 - position : position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View useView, ViewGroup parent) {
			if (useView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				useView = inflater.inflate(R.layout.levels, null, true);
			}
			TextView textView = (TextView) useView.findViewById(R.id.levellabel);
			SokobanMapView mapView = (SokobanMapView) useView.findViewById(R.id.levelmap);
			Level level = (Level) getItem(position);
			String label = level.getName();
			if (level.getDiamondsLeft() == 0) {
				CharSequence moves = level.getMoves();
				label = label + " - " + SokobanGameState.getMoveCount(moves, true, false) + "/" + SokobanGameState.getMoveCount(moves, false, true);
			} else if (level.hasMoves()) {
				label = label + "  (" + level.getDiamondsLeft() + " left)";
			}
			textView.setText(label);
			Level initialLevel = level.getInitialLevel();
			mapView.setSokobanMap(showInitialLevel && initialLevel != null ? initialLevel : level);
			return useView;
		}
	}

	private final boolean reverse = true;
	private final boolean showInitialLevel = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onUpdate();
	}

	@Override
	protected void onListItemClick(ListView listView, View view, final int position, long id) {
		SokobanLevels sokobanLevels = ((SokobanLevelsAdapter) listView.getAdapter()).levels;
		Level level = (Level) listView.getAdapter().getItem(position);
		int levelIndex = sokobanLevels.getLevelIndex(level);
		Intent intent = SokobanGameActivity.createSokobanLevelIntent(sokobanLevels, levelIndex);
		intent.setClass(this, SokobanGameActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onUpdate();
	}

	private void onUpdate() {
		Intent intent = getIntent();
		String levelSet = "microban";
		if (intent != null && intent.getExtras() != null) {
			levelSet = intent.getExtras().getString(SokobanGameActivity.GAME_LEVEL_SET_EXTRA);
			if (levelSet == null) {
				levelSet = "microban";
			}
		}
		SokobanLevels sokobanLevels = SokobanLevels.getSokobanLevels(levelSet);
		try {
			if (sokobanLevels.getLevelCount() == 0 || sokobanLevels.getLevel(0) == null) {
				sokobanLevels.readLevels(this);
			}
			//			SharedPreferences prefs = getSharedPreferences(SokobanLevelsListActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
			//			final String maxLevelNamePref = SokobanLevelsListActivity.getMaxLevelPrefName(sokobanLevels);
			int maxLevel = sokobanLevels.getIndexOfLastRemainingLevel(true, true);
			setListAdapter(new SokobanLevelsAdapter(this, sokobanLevels, maxLevel + 1));
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setCancelable(false);
			alert.setMessage(e.getMessage());
			alert.setTitle("Exception!");
			alert.setPositiveButton("Back", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SokobanLevelsActivity.this.finish();
				}
			});
			alert.show();
		}
	}
}
