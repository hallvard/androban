package com.mobilepearls.sokoban.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mobilepearls.sokoban.io.Level;
import com.mobilepearls.sokoban.io.RunlengthEncoding;

public class SokobanStringListMap implements SokobanMap, Iterable<CharSequence>  {

	private int diamondsLeft = -1;

	protected final SokobanStringListMap initialLevel;

	private List<CharSequence> lines = null;
	private StringBuilder moves = null;

	private int width = -1;

	public SokobanStringListMap(SokobanStringListMap initialLevel) {
		this.initialLevel = initialLevel;
	}

	public void addLevelLine(CharSequence line) {
		line = RunlengthEncoding.decode(line);
		if (lines == null) {
			lines = new ArrayList<CharSequence>();
			diamondsLeft = 0;
		}
		lines.add(line);
		for (int x = 0; x < line.length(); x++) {
			if (line.charAt(x) == CHAR_DIAMOND_ON_FLOOR) {
				diamondsLeft++;
			}
		}
		width = Math.max(width, line.length());
	}

	public void appendMoves(CharSequence line) {
		line = RunlengthEncoding.decode(line);
		if (moves == null) {
			moves = new StringBuilder();
		}
		moves.append(line);
	}

	public void clearLevelLines() {
		this.lines = null;
		diamondsLeft = -1;
	}

	public int getDiamondsLeft() {
		return (diamondsLeft >= 0 ? diamondsLeft : (initialLevel != null ? initialLevel.getDiamondsLeft() : -1));
	}

	public int getHeight() {
		return (lines != null ? lines.size() : (initialLevel != null ? initialLevel.getHeight() : 0));
	}

	public Level getInitialLevel() {
		return (Level) initialLevel;
	}

	@Override
	public char getItemAt(int x, int y) {
		if (lines != null) {
			CharSequence line = lines.get(y);
			if (x >= line.length()) {
				return CHAR_OUTSIDE;
			}
			char c = line.charAt(x);
			if (CHARS_ALL.indexOf(c) >= 0) {
				//				if (c == CHAR_FLOOR) {
				//					for (int i = x - 1; i >= 0; i--) {
				//						if (line.charAt(i) != CHAR_FLOOR) {
				//							for (int j = x + 1; j < line.length(); j++) {
				//								if (line.charAt(j) != CHAR_FLOOR) {
				//									return c;
				//								}
				//							}
				//						}
				//					}
				//					return CHAR_OUTSIDE;
				//				}
				return c;
			}
			return CHAR_OUTSIDE;
		} else if (initialLevel != null) {
			return initialLevel.getItemAt(x, y);
		}
		return CHAR_OUTSIDE;
	}

	@Override
	public CharSequence getMoves() {
		return getMoves(true);
	}

	protected CharSequence getMoves(boolean includeInitialLevel) {
		return (moves != null ? moves : (initialLevel != null && includeInitialLevel ? ((Level) initialLevel).getMoves() : ""));
	}

	public int getWidth() {
		return (width >= 0 ? width : (initialLevel != null ? initialLevel.getWidth() : -1));
	}

	public boolean hasLevelLines() {
		return iterator().hasNext();
	}

	public boolean hasMoves() {
		return hasMoves(true);
	}

	private boolean hasMoves(boolean includeInitialLevel) {
		return (moves != null ? moves.length() > 0 : (initialLevel != null && includeInitialLevel ? ((Level) initialLevel).hasMoves() : false));
	}

	public Iterator<CharSequence> iterator() {
		return iterator(true);
	}

	//

	protected Iterator<CharSequence> iterator(boolean includeInitialLevel) {
		return (lines != null ? lines : (initialLevel != null && includeInitialLevel ? initialLevel : Collections.<CharSequence>emptyList())).iterator();
	}

	public void setLevelLines(SokobanMap map) {
		clearLevelLines();
		if (map != null) {
			char[] line = new char[map.getWidth()];
			for (int y = 0; y < map.getHeight(); y++) {
				for (int x = 0; x < map.getWidth(); x++) {
					line[x] = map.getItemAt(x, y);
				}
				addLevelLine(new String(line));
			}
		}
	}

	public void setMoves(CharSequence line) {
		this.moves = null;
		if (line != null) {
			appendMoves(line);
		}
	}
}
