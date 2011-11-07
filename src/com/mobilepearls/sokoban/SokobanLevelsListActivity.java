package com.mobilepearls.sokoban;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SokobanLevelsListActivity extends ListActivity {

	//	private static final String MAX_LEVEL_NAME = "max_level";
	//
	//	public static final String SHARED_PREFS_NAME = "game_prefs";
	//
	//	public static String getMaxLevelPrefName(SokobanLevels sokobanLevels) {
	//		int levelSetIndex = SokobanLevels.getSokobanLevelsIndex(sokobanLevels);
	//		// historical compat: first level == no suffix, levels 1-4 == index as suffix, the rest use name
	//		return MAX_LEVEL_NAME + (levelSetIndex == 0 ? "" : "_" + (levelSetIndex < 5 ? String.valueOf(levelSetIndex) : sokobanLevels.getName()));
	//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, SokobanLevels.getAllSokobanLevelsLabels()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, final int levelSetIndex, long id) {
		final SokobanLevels sokobanLevels = SokobanLevels.getSokobanLevels(levelSetIndex);
		//		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		//		final String maxLevelNamePref = getMaxLevelPrefName(sokobanLevels);
		//		final int maxLevel = Math.min(prefs.getInt(maxLevelNamePref, 1), sokobanLevels.getLevelCount());
		//
		//		if (maxLevel == 1) {
		//			Intent intent = new Intent();
		//			intent.putExtra(SokobanGameActivity.GAME_LEVEL_INTENT_EXTRA, 0);
		//			intent.putExtra(SokobanGameActivity.GAME_LEVEL_SET_EXTRA, sokobanLevels.getName());
		//			intent.putExtra(SokobanGameActivity.SHOW_HELP_INTENT_EXTRA, true);
		//			intent.setClass(this, SokobanGameActivity.class);
		//			startActivity(intent);
		//		} else {
		Intent intent = SokobanGameActivity.createSokobanLevelIntent(sokobanLevels, -1);
		intent.setClass(this, SokobanLevelsActivity.class);
		startActivity(intent);
		//		}
	}
}
