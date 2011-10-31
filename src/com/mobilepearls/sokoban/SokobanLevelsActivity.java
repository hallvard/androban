package com.mobilepearls.sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
		private final SokobanLevels levels;
		private final int maxLevel;
		public SokobanLevelsAdapter(Activity context, SokobanLevels levels, int maxLevel) {
			this.context = context;
			this.levels = levels;
			this.maxLevel = maxLevel;
		}
		@Override
		public int getCount() {
			return (maxLevel >= 0 ? maxLevel : levels.getLevelCount());
		}
		@Override
		public Object getItem(int position) {
			return levels.getLevel(position);
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
			textView.setText(level.getName());
			mapView.setSokobanMap(level);
			return useView;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			sokobanLevels.readLevels(getAssets());
			SharedPreferences prefs = getSharedPreferences(SokobanLevelsListActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
			final String maxLevelNamePref = SokobanLevelsListActivity.getMaxLevelPrefName(sokobanLevels);
			final int maxLevel = Math.min(prefs.getInt(maxLevelNamePref, -1), sokobanLevels.getLevelCount());
			setListAdapter(new SokobanLevelsAdapter(this, sokobanLevels, maxLevel));
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

	@Override
	protected void onListItemClick(ListView listView, View view, final int levelIndex, long id) {
		Intent intent = new Intent();
		SokobanLevels sokobanLevels = ((SokobanLevelsAdapter) listView.getAdapter()).levels;
		intent.putExtra(SokobanGameActivity.GAME_LEVEL_SET_EXTRA, sokobanLevels.getName());
		intent.putExtra(SokobanGameActivity.GAME_LEVEL_INTENT_EXTRA, levelIndex);
		intent.putExtra(SokobanGameActivity.SHOW_HELP_INTENT_EXTRA, true);
		intent.setClass(this, SokobanGameActivity.class);
		startActivity(intent);
	}
}
