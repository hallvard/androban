package com.mobilepearls.sokoban;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.mobilepearls.sokoban.io.Level;
import com.mobilepearls.sokoban.io.LevelIterator;

public class SokobanLevels {

	private static final List<SokobanLevels> levelMaps = new ArrayList<SokobanLevels>();

	static {
		levelMaps.add(new SokobanLevels("microban", "Microban (easy)", 50, "microban.sok"));
		levelMaps.add(new SokobanLevels("original", "Original", 155, "original.sok"));
		levelMaps.add(new SokobanLevels("mas_sasquatch", "Mas Sasquatch", 50, "mas_sasquatch.sok"));
		levelMaps.add(new SokobanLevels("sasquatch_iii", "Sasquatch III", 50, "sasquatch_iii.sok"));
		levelMaps.add(new SokobanLevels("sasquatch_iv", "Sasquatch IV", 49, "sasquatch_iv.sok"));
		levelMaps.add(new SokobanLevels("megasoft1", "Megasoft 1", 100, "megasoft1.sok"));
		levelMaps.add(new SokobanLevels("megasoft2", "Megasoft 2", 100, "megasoft2.sok"));
		levelMaps.add(new SokobanLevels("megasoft3", "Megasoft 3", 100, "megasoft3.sok"));
		levelMaps.add(new SokobanLevels("megasoft4", "Megasoft 4", 100, "megasoft4.sok"));
		levelMaps.add(new SokobanLevels("megasoft5", "Megasoft 5", 100, "megasoft5.sok"));

		levelMaps.add(new SokobanLevels("bernier", "Initial Trouble", 12, "http://sokobano.de/sets/bernier.zip"));
		levelMaps.add(new SokobanLevels("minicosmos", "Minicosmos", 40, "http://sokobano.de/sets/minicosmos.zip"));
		levelMaps.add(new SokobanLevels("microcosmos", "Microcosmos", 40, "http://sokobano.de/sets/microcosmos.zip"));
		levelMaps.add(new SokobanLevels("nabokosmos", "Nabokosmos", 40, "http://sokobano.de/sets/nabokosmos.zip"));
		levelMaps.add(new SokobanLevels("cosmopoly", "Cosmopoly", 22, "http://sokobano.de/sets/cosmopoly.zip"));
		levelMaps.add(new SokobanLevels("cosmonotes", "Cosmonotes", 20, "http://sokobano.de/sets/cosmonotes.zip"));
		levelMaps.add(new SokobanLevels("loma", "LOMA", 100, "http://sokobano.de/sets/loma.zip"));
		levelMaps.add(new SokobanLevels("myriocosmos", "Myriocosmos", 13, "http://sokobano.de/sets/myriocosmos.zip"));
		levelMaps.add(new SokobanLevels("blazz", "Blazz", 18, "http://sokobano.de/sets/blazz.zip"));
		levelMaps.add(new SokobanLevels("blazz2", "Blazz2", 17, "http://sokobano.de/sets/blazz2.zip"));
		levelMaps.add(new SokobanLevels("yasgen", "YASGen", 28, "http://sokobano.de/sets/yasgen.zip"));
		levelMaps.add(new SokobanLevels("aenigma", "Aenigma", 50, "http://sokobano.de/sets/aenigma.zip"));
		levelMaps.add(new SokobanLevels("druille", "Bruno Druille", 28, "http://sokobano.de/sets/druille.zip"));
		levelMaps.add(new SokobanLevels("disciple", "Disciple", 50, "http://sokobano.de/sets/disciple.zip"));
		levelMaps.add(new SokobanLevels("dh1", "dh1", 10, "http://sokobano.de/sets/dh1.zip"));
		levelMaps.add(new SokobanLevels("dh2", "dh2", 10, "http://sokobano.de/sets/dh2.zip"));
		levelMaps.add(new SokobanLevels("maelstrom", "maelstrom", 20, "http://sokobano.de/sets/maelstrom.zip"));
		levelMaps.add(new SokobanLevels("bagatelle", "bagatelle", 20, "http://sokobano.de/sets/bagatelle.zip"));
		levelMaps.add(new SokobanLevels("cantrip", "cantrip", 20, "http://sokobano.de/sets/cantrip.zip"));
		levelMaps.add(new SokobanLevels("bagatelle2", "bagatelle 2", 12, "http://sokobano.de/sets/bagatelle2.zip"));
		levelMaps.add(new SokobanLevels("cantrip2", "cantrip 2", 13, "http://sokobano.de/sets/cantrip2.zip"));
		levelMaps.add(new SokobanLevels("dh5", "dh5", 20, "http://sokobano.de/sets/dh5.zip"));
		levelMaps.add(new SokobanLevels("sasquatch", "Sasquatch", 50, "http://sokobano.de/sets/sasquatch.zip"));
		levelMaps.add(new SokobanLevels("sasquatch2", "Sasquatch II ", 50, "http://sokobano.de/sets/sasquatch2.zip"));
		//		levelMaps.add(new SokobanLevels("sasquatch3", "Sasquatch III", 50, "http://sokobano.de/sets/sasquatch3.zip"));
		//		levelMaps.add(new SokobanLevels("sasquatch4", "Sasquatch IV", 50, "http://sokobano.de/sets/sasquatch4.zip"));
		levelMaps.add(new SokobanLevels("sasquatch5", "Sasquatch V", 50, "http://sokobano.de/sets/sasquatch5.zip"));
		levelMaps.add(new SokobanLevels("sasquatch6", "Sasquatch VI", 50, "http://sokobano.de/sets/sasquatch6.zip"));
		levelMaps.add(new SokobanLevels("sasquatch7", "Sasquatch VII", 50, "http://sokobano.de/sets/sasquatch7.zip"));
		levelMaps.add(new SokobanLevels("sasquatch8", "Sasquatch VIII", 50, "http://sokobano.de/sets/sasquatch8.zip"));
		levelMaps.add(new SokobanLevels("sasquatch9", "Sasquatch IX", 50, "http://sokobano.de/sets/sasquatch9.zip"));
		levelMaps.add(new SokobanLevels("sasquatch10", "Sasquatch X", 50, "http://sokobano.de/sets/sasquatch10.zip"));
		levelMaps.add(new SokobanLevels("sasquatch11", "Sasquatch XI", 50, "http://sokobano.de/sets/sasquatch11.zip"));
		levelMaps.add(new SokobanLevels("sasquatch12", "Sasquatch XII (unfinished)", 8, "http://sokobano.de/sets/sasquatch12.zip"));
		//		levelMaps.add(new SokobanLevels("microban", "Microban", 155, "http://sokobano.de/sets/microban.zip"));
		levelMaps.add(new SokobanLevels("microban2", "Microban II", 135, "http://sokobano.de/sets/microban2.zip"));
		levelMaps.add(new SokobanLevels("microban3", "Microban III", 101, "http://sokobano.de/sets/microban3.zip"));
		levelMaps.add(new SokobanLevels("microban4", "Microban IV", 102, "http://sokobano.de/sets/microban4.zip"));
		levelMaps.add(new SokobanLevels("microban5", "Microban V (unfinished)", 26, "http://sokobano.de/sets/microban5.zip"));
		levelMaps.add(new SokobanLevels("demons", "Demons & Diamonds", 20, "http://sokobano.de/sets/demons.zip"));
		levelMaps.add(new SokobanLevels("cubes", "Cubes & Tubes", 20, "http://sokobano.de/sets/cubes.zip"));
		levelMaps.add(new SokobanLevels("flatland", "Flatland", 20, "http://sokobano.de/sets/flatland.zip"));
		levelMaps.add(new SokobanLevels("grigr2001", "GRIGoRusha 2001", 100, "http://sokobano.de/sets/grigr2001.zip"));
		levelMaps.add(new SokobanLevels("grigr2002", "GRIGoRusha 2002", 40, "http://sokobano.de/sets/grigr2002.zip"));
		levelMaps.add(new SokobanLevels("remodel", "GRIGoRusha Remodel Club", 108, "http://sokobano.de/sets/remodel.zip"));
		levelMaps.add(new SokobanLevels("grigrspecial", "GRIGoRusha Special", 40, "http://sokobano.de/sets/grigrspecial.zip"));
		levelMaps.add(new SokobanLevels("grigrcomet", "GRIGoRusha Comet", 30, "http://sokobano.de/sets/grigrcomet.zip"));
		levelMaps.add(new SokobanLevels("grigrstar", "GRIGoRusha Star", 30, "http://sokobano.de/sets/grigrstar.zip"));
		levelMaps.add(new SokobanLevels("grigrsun", "GRIGoRusha Sun", 10, "http://sokobano.de/sets/grigrsun.zip"));
		levelMaps.add(new SokobanLevels("pokorny", "Pokorn&yacute;", 103, "http://sokobano.de/sets/pokorny.zip"));
		levelMaps.add(new SokobanLevels("sokostation", "SokoStation", 270, "http://sokobano.de/sets/sokostation.zip"));
		levelMaps.add(new SokobanLevels("mentzer", "Still More", 35, "http://sneezingtiger.com/sokoban/SokobanMacLevels.txt"));
		levelMaps.add(new SokobanLevels("sokogen", "Sokogen", 78, "http://sokobano.de/sets/sokogen.zip"));
		levelMaps.add(new SokobanLevels("duthenkids", "Dimitri & Yorick", 61, "http://sokobano.de/sets/duthenkids.zip"));
		levelMaps.add(new SokobanLevels("jpkent", "Jean Pierre Kent", 75, "http://sokobano.de/sets/jpkent.zip"));
		levelMaps.add(new SokobanLevels("haikemono", "Haikemono collection", 35, "http://sokobano.de/sets/haikemono.zip"));
		levelMaps.add(new SokobanLevels("pufiban", "Pufiban", 246, "http://sokobano.de/sets/pufiban.zip"));
		levelMaps.add(new SokobanLevels("sopuli", "KEAS Collection", 26, "http://sokobano.de/sets/sopuli.zip"));
		levelMaps.add(new SokobanLevels("kenyam", "kenyam", 45, "http://sokobano.de/sets/kenyam.zip"));
		levelMaps.add(new SokobanLevels("sokomind", "SokoMind", 60, "http://sokobano.de/sets/sokomind.zip"));
		levelMaps.add(new SokobanLevels("kevin1", "Kevin 1", 100, "http://sokobano.de/sets/kevin1.zip"));
		levelMaps.add(new SokobanLevels("kevin2", "Kevin 2", 100, "http://sokobano.de/sets/kevin2.zip"));
		levelMaps.add(new SokobanLevels("kevin3", "Kevin 3", 100, "http://sokobano.de/sets/kevin3.zip"));
		levelMaps.add(new SokobanLevels("kevin4", "Kevin 4", 100, "http://sokobano.de/sets/kevin4.zip"));
		levelMaps.add(new SokobanLevels("kevin5", "Kevin 5", 100, "http://sokobano.de/sets/kevin5.zip"));
		levelMaps.add(new SokobanLevels("kevin6", "Kevin 6", 100, "http://sokobano.de/sets/kevin6.zip"));
		levelMaps.add(new SokobanLevels("kevin7", "Kevin 7", 100, "http://sokobano.de/sets/kevin7.zip"));
		levelMaps.add(new SokobanLevels("kevin8", "Kevin 8", 100, "http://sokobano.de/sets/kevin8.zip"));
		levelMaps.add(new SokobanLevels("kevin9", "Kevin 9", 100, "http://sokobano.de/sets/kevin9.zip"));
		levelMaps.add(new SokobanLevels("kevin10", "Kevin 10", 100, "http://sokobano.de/sets/kevin10.zip"));
		levelMaps.add(new SokobanLevels("kevin11", "Kevin 11", 100, "http://sokobano.de/sets/kevin11.zip"));
		levelMaps.add(new SokobanLevels("kevin12", "Kevin 12", 100, "http://sokobano.de/sets/kevin12.zip"));
		levelMaps.add(new SokobanLevels("kevin13", "Kevin 13", 100, "http://sokobano.de/sets/kevin13.zip"));
		levelMaps.add(new SokobanLevels("kevin14", "Kevin 14", 100, "http://sokobano.de/sets/kevin14.zip"));
		levelMaps.add(new SokobanLevels("kevin15", "Kevin 15", 100, "http://sokobano.de/sets/kevin15.zip"));
		levelMaps.add(new SokobanLevels("kevin16", "Kevin 16", 100, "http://sokobano.de/sets/kevin16.zip"));
		levelMaps.add(new SokobanLevels("kevin17", "Kevin 17", 100, "http://sokobano.de/sets/kevin17.zip"));
		levelMaps.add(new SokobanLevels("kevin18", "Kevin 18", 100, "http://sokobano.de/sets/kevin18.zip"));
		levelMaps.add(new SokobanLevels("kevin19", "Kevin 19", 100, "http://sokobano.de/sets/kevin19.zip"));
		levelMaps.add(new SokobanLevels("kevin20", "Kevin 20", 100, "http://sokobano.de/sets/kevin20.zip"));
		levelMaps.add(new SokobanLevels("kevin21", "Kevin 21", 54, "http://sokobano.de/sets/kevin21.zip"));
		levelMaps.add(new SokobanLevels("sokevo", "SokEvo", 107, "http://sokobano.de/sets/sokevo.zip"));
		levelMaps.add(new SokobanLevels("sokhard", "SokHard", 163, "http://sokobano.de/sets/sokhard.zip"));
		levelMaps.add(new SokobanLevels("palstra", "Marcus Palstra", 30, "http://sokobano.de/sets/palstra.zip"));
		levelMaps.add(new SokobanLevels("asztalos", "A.K.K. Informatika", 32, "http://sokobano.de/sets/asztalos.zip"));
		levelMaps.add(new SokobanLevels("warehouse", "Warehouse I", 50, "http://sokobano.de/sets/warehouse.zip"));
		levelMaps.add(new SokobanLevels("belyaev1", "Serg 1", 100, "http://sokobano.de/sets/belyaev1.zip"));
		levelMaps.add(new SokobanLevels("belyaev2", "Serg 2", 100, "http://sokobano.de/sets/belyaev2.zip"));
		levelMaps.add(new SokobanLevels("belyaev3", "Serg 3", 100, "http://sokobano.de/sets/belyaev3.zip"));
		levelMaps.add(new SokobanLevels("belyaev4", "Serg 4", 100, "http://sokobano.de/sets/belyaev4.zip"));
		levelMaps.add(new SokobanLevels("belyaev5", "Serg 5", 100, "http://sokobano.de/sets/belyaev5.zip"));
		levelMaps.add(new SokobanLevels("belyaev6", "Serg 6", 40, "http://sokobano.de/sets/belyaev6.zip"));
		levelMaps.add(new SokobanLevels("sven1", "Sven 1", 100, "http://sokobano.de/sets/sven1.zip"));
		levelMaps.add(new SokobanLevels("sven2", "Sven 2", 100, "http://sokobano.de/sets/sven2.zip"));
		levelMaps.add(new SokobanLevels("sven3", "Sven 3", 100, "http://sokobano.de/sets/sven3.zip"));
		levelMaps.add(new SokobanLevels("sven4", "Sven 4", 100, "http://sokobano.de/sets/sven4.zip"));
		levelMaps.add(new SokobanLevels("sven5", "Sven 5", 100, "http://sokobano.de/sets/sven5.zip"));
		levelMaps.add(new SokobanLevels("sven6", "Sven 6", 100, "http://sokobano.de/sets/sven6.zip"));
		levelMaps.add(new SokobanLevels("sven7", "Sven 7", 100, "http://sokobano.de/sets/sven7.zip"));
		levelMaps.add(new SokobanLevels("sven8", "Sven 8", 100, "http://sokobano.de/sets/sven8.zip"));
		levelMaps.add(new SokobanLevels("sven9", "Sven 9", 100, "http://sokobano.de/sets/sven9.zip"));
		levelMaps.add(new SokobanLevels("sven10", "Sven 10", 100, "http://sokobano.de/sets/sven10.zip"));
		levelMaps.add(new SokobanLevels("sven11", "Sven 11", 100, "http://sokobano.de/sets/sven11.zip"));
		levelMaps.add(new SokobanLevels("sven12", "Sven 12", 100, "http://sokobano.de/sets/sven12.zip"));
		levelMaps.add(new SokobanLevels("sven13", "Sven 13", 100, "http://sokobano.de/sets/sven13.zip"));
		levelMaps.add(new SokobanLevels("sven14", "Sven 14", 100, "http://sokobano.de/sets/sven14.zip"));
		levelMaps.add(new SokobanLevels("sven15", "Sven 15", 100, "http://sokobano.de/sets/sven15.zip"));
		levelMaps.add(new SokobanLevels("sven16", "Sven 16", 100, "http://sokobano.de/sets/sven16.zip"));
		levelMaps.add(new SokobanLevels("sven17", "Sven 17", 23, "http://sokobano.de/sets/sven17.zip"));
		levelMaps.add(new SokobanLevels("sharpen", "Sharpen", 153, "http://sokobano.de/sets/sharpen.zip"));
		levelMaps.add(new SokobanLevels("tianlang", "Tian Lang", 10, "http://sokobano.de/sets/tianlang.zip"));
		levelMaps.add(new SokobanLevels("sasquatch_arr", "Sasquatch, arranged", 37, "http://sokobano.de/sets/x_sasquatch.zip"));
		levelMaps.add(new SokobanLevels("sasquatch2_arr", "Mas Sasquatch, arranged", 52, "http://sokobano.de/sets/x_sasquatch2.zip"));
		levelMaps.add(new SokobanLevels("sasquatch3_arr", "Sasquatch III, arranged", 99, "http://sokobano.de/sets/x_sasquatch3.zip"));
		levelMaps.add(new SokobanLevels("sasquatch4_arr", "Sasquatch IV, arranged", 39, "http://sokobano.de/sets/x_sasquatch4.zip"));
		levelMaps.add(new SokobanLevels("sasquatch5_arr", "Sasquatch V, arranged", 62, "http://sokobano.de/sets/x_sasquatch5.zip"));
		levelMaps.add(new SokobanLevels("sasquatch6_arr", "Sasquatch VI, arranged", 96, "http://sokobano.de/sets/x_sasquatch6.zip"));
		levelMaps.add(new SokobanLevels("sasquatch7_arr", "Sasquatch VII, arranged", 134, "http://sokobano.de/sets/x_sasquatch7.zip"));
		levelMaps.add(new SokobanLevels("microban_arr", "Microban, arranged", 3, "http://sokobano.de/sets/x_microban.zip"));
		levelMaps.add(new SokobanLevels("masmicroban_arr", "Mas Microban, arranged", 11, "http://sokobano.de/sets/x_masmicroban.zip"));
		levelMaps.add(new SokobanLevels("yoshioauto", "autogenerated", 52, "http://sokobano.de/sets/yoshioauto.zip"));
		levelMaps.add(new SokobanLevels("yoshiohand", "handmade", 54, "http://sokobano.de/sets/yoshiohand.zip"));
		levelMaps.add(new SokobanLevels("ziko", "ZIKO", 47, "http://sokobano.de/sets/ziko.zip"));
	}

	public static String[] getAllSokobanLevelsLabels() {
		String[] labels = new String[levelMaps.size()];
		for (int i = 0; i < labels.length; i++) {
			labels[i] = levelMaps.get(i).getLabel();
		}
		return labels;
	}

	public static SokobanLevels getSokobanLevels(int i) {
		return (i < levelMaps.size() ? levelMaps.get(i) : null);
	}

	public static SokobanLevels getSokobanLevels(String name) {
		for (int i = 0; i < levelMaps.size(); i++) {
			SokobanLevels sokobanLevels = levelMaps.get(i);
			if (name.equals(sokobanLevels.getName())) {
				return sokobanLevels;
			}
		}
		for (int i = 0; i < levelMaps.size(); i++) {
			SokobanLevels sokobanLevels = levelMaps.get(i);
			if (name.equals(sokobanLevels.getLabel())) {
				return sokobanLevels;
			}
		}
		for (int i = 0; i < levelMaps.size(); i++) {
			SokobanLevels sokobanLevels = levelMaps.get(i);
			if (name.startsWith(sokobanLevels.getLabel())) {
				return sokobanLevels;
			}
		}
		return null;
	}

	public static int getSokobanLevelsCount() {
		return levelMaps.size();
	}

	public static int getSokobanLevelsIndex(SokobanLevels levels) {
		return levelMaps.indexOf(levels);
	}

	private final String label;

	private int levelCount = -1;
	private List<Level> levels = null;

	private final String location;

	private final String name;

	public SokobanLevels(String name, String label, int levelCount, String location) {
		this(name, label, location);
		this.levelCount = levelCount;
	}

	public SokobanLevels(String name, String label, String location) {
		this.name = name;
		this.label = label;
		this.location = location;
	}

	public int getIndexOfLastRemainingLevel() {
		for (int i = getLevelCount(); i > 0; i--) {
			if (getLevel(i - 1).getDiamondsLeft() == 0) {
				return i;
			}
		}
		return 0;
	}

	public int getIndexOfRemainingLevel(int start) {
		for (int i = start; i < getLevelCount(); i++) {
			if (getLevel(i).getDiamondsLeft() > 0) {
				return i;
			}
		}
		return -1;
	}

	public String getLabel() {
		return label + (levelCount > 0 ? " - " + levelCount : "");
	}

	public Level getLevel(int i) {
		return (levels != null && i < levels.size() ? levels.get(i) : null);
	}

	public Level getLevel(int i, Context context) {
		if (levels == null) {
			readLevels(context);
		}
		return (i < levels.size() ? levels.get(i) : null);
	}

	public int getLevelCount() {
		return (levels != null ? levels.size() : levelCount);
	}

	public int getLevelIndex(Level level) {
		return (levels != null ? levels.indexOf(level) : -1);
	}

	public String getLocation() {
		return (location != null ? location : name + ".sok");
	}

	//

	public String getName() {
		return name;
	}

	public String getSokobanLevelFileName(Level level, int count) {
		return getName() + "_" + count + ".sok";
	}

	public void readLevels(Context context) {
		LevelIterator it = new LevelIterator(getLocation(), context.getAssets());
		int count = 0;
		while (it.hasNext()) {
			Level level = new Level(it.next());
			count++;
			try {
				FileInputStream input = context.openFileInput(getSokobanLevelFileName(level, count));
				level.read(new BufferedReader(new InputStreamReader(input)));
			} catch (IOException e) {
				// ignore
			}
			if (levels == null) {
				levels = new ArrayList<Level>();
			}
			levels.add(level);
		}
	}

	public void writeLevel(Level level, Context context) {
		int count = levels.indexOf(level) + 1;
		if (count > 0) {
			try {
				FileOutputStream output = context.openFileOutput(getSokobanLevelFileName(level, count), Context.MODE_PRIVATE);
				level.write(new OutputStreamWriter(output), false);
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
