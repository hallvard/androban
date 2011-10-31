package com.mobilepearls.sokoban.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mobilepearls.sokoban.SokobanGameState;
import com.mobilepearls.sokoban.SokobanMap;

public class Level implements SokobanMap, Iterable<String>  {

	private final static String LEVEL_CHARS = CHARS_ALL, EXTRA_CHARS = "-_";
	private final static String NAME_KEY = "Title";
	private final static char SPLIT_CHAR = '|';

	private List<String> lines = null;
	private StringBuilder moves = null;

	private String name = null;

	private int width = -1;
	public int getHeight() {
		return lines.size();
	}

	@Override
	public char getItemAt(int x, int y) {
		String line = lines.get(y);
		char c = x < line.length() ? line.charAt(x) : CHAR_OUTSIDE;
		return (LEVEL_CHARS.indexOf(c) >= 0 ? c : CHAR_OUTSIDE);
	}
	public String getMoves() {
		return (moves != null ? moves.toString() : "");
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	protected boolean isLevelLine(String line, float factor, boolean allowSeparator) {
		int count = 0, otherCount = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			boolean isLast = (i == line.length() - 1);
			if (LEVEL_CHARS.indexOf(c) >= 0 || EXTRA_CHARS.indexOf(c) >= 0) {
				count++;
			} else if (isLast) {
			} else if (Character.isDigit(c) || (allowSeparator && c == SPLIT_CHAR)) {
				otherCount++;
			}
		}
		return (count + otherCount) >= line.length() * factor && count > otherCount;
	}

	protected boolean isMovesLine(String line, float factor) {
		int count = 0, otherCount = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			boolean isLast = (i == line.length() - 1);
			if (SokobanGameState.directionFor(c, SokobanGameState.DIRECTIONS_CHAR_POS) != null) {
				count++;
			} else if (isLast) {
			} else if (Character.isDigit(c)) {
				otherCount++;
			}
		}
		return (count + otherCount) >= line.length() * factor && count > otherCount;
	}

	//

	public Iterator<String> iterator() {
		return (lines != null ? lines : Collections.<String>emptyList()).iterator() ;
	}

	public void read(Level level, BufferedReader bufferedReader) throws IOException {
		String line = null;
		// we allow a sequence of invalid lines (e.g. natural language),
		// followed by a sequence of valid lines
		while ((line = bufferedReader.readLine()) != null) {
			// if line contains : it is meta data
			int pos = line.indexOf(':');
			if (pos > 0 && pos < line.length() - 1) {
				if (line.startsWith(";")) {
					line = line.substring(1);
					pos--;
				}
				String key = line.substring(0, pos), value = line.substring(pos + 1);
				if (NAME_KEY.equals(key)) {
					setName(value);
				}
			} else if (line.trim().length() == 0) {
				if (level.iterator().hasNext()) {
					break;
				}
			} else if (isLevelLine(line, 1.0f, false)) {
				if (lines == null) {
					lines = new ArrayList<String>();
				}
				int start = 0;
				while (start < line.length()) {
					int end = line.indexOf(SPLIT_CHAR, start);
					if (end < 0) {
						end = line.length();
					}
					String newLine = RunlengthEncoding.decode(line.substring(start, end));
					lines.add(newLine);
					width = Math.max(width, newLine.length());
					start = end + 1;
				}
			} else if (isMovesLine(line, 1.0f)) {
				if (this.moves == null) {
					this.moves = new StringBuilder();
				}
				this.moves.append(RunlengthEncoding.decode(moves.toString()));
			} else if (line.startsWith(";") && level.getName() == null) {
				level.setName(line.substring(1).trim());
			}
		}
	}

	//

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		Iterator<String> it = this.iterator();
		if (name != null) {
			buffer.append(NAME_KEY);
			buffer.append(": ");
			buffer.append(name);
			buffer.append("\n");
		}
		while (it.hasNext()) {
			buffer.append(it.next());
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
