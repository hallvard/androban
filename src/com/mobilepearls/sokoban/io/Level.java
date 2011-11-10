package com.mobilepearls.sokoban.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import com.mobilepearls.sokoban.core.SokobanGameState;
import com.mobilepearls.sokoban.core.SokobanStringListMap;

public class Level extends SokobanStringListMap {

	private final static String EXTRA_CHARS = "-_";
	private final static String NAME_KEY = "Title";
	private final static char SPLIT_CHAR = '|';

	private String name = null;

	public Level() {
		this(null);
	}

	public Level(Level initialLevel) {
		super(initialLevel);
	}

	@Override
	public Level getInitialLevel() {
		return (Level) initialLevel;
	}

	public String getName() {
		return (name != null ? name : (initialLevel != null ? ((Level) initialLevel).getName() : null));
	}

	protected boolean isLevelLine(CharSequence line, float factor, boolean allowSeparator) {
		int count = 0, otherCount = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			boolean isLast = (i == line.length() - 1);
			if (CHARS_ALL.indexOf(c) >= 0 || EXTRA_CHARS.indexOf(c) >= 0) {
				count++;
			} else if (isLast) {
				// nothing to do
			} else if (Character.isDigit(c) || (allowSeparator && c == SPLIT_CHAR)) {
				otherCount++;
			}
		}
		return (count + otherCount) >= line.length() * factor && count > otherCount;
	}

	protected boolean isMovesLine(CharSequence line, float factor) {
		int count = 0, otherCount = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			boolean isLast = (i == line.length() - 1);
			if (SokobanGameState.directionFor(Character.toLowerCase(c), SokobanGameState.DIRECTIONS_CHAR_POS) != null) {
				count++;
			} else if (isLast) {
				// nothing to do
			} else if (Character.isDigit(c)) {
				otherCount++;
			}
		}
		return (count + otherCount) >= line.length() * factor && count > otherCount;
	}

	//

	public void read(BufferedReader bufferedReader) throws IOException {
		String line = null;
		// we allow a sequence of invalid lines (e.g. natural language),
		// followed by a sequence of valid lines
		String lastNonLine = null;
		while ((line = bufferedReader.readLine()) != null) {
			// if line contains : it is meta data
			int pos = line.indexOf(':');
			if (pos > 0 && pos < line.length() - 1) {
				if (line.startsWith(";")) {
					line = line.substring(1);
					pos--;
				}
				String key = line.substring(0, pos);
				if (NAME_KEY.equals(key)) {
					setName(line.substring(pos + 1));
				}
			} else if (line.trim().length() == 0) {
				if (hasLevelLines()) {
					break;
				}
			} else if (isLevelLine(line, 1.0f, false)) {
				int start = 0;
				while (start < line.length()) {
					int end = line.indexOf(SPLIT_CHAR, start);
					if (end < 0) {
						end = line.length();
					}
					addLevelLine(line.substring(start, end));
					start = end + 1;
				}
			} else if (isMovesLine(line, 1.0f)) {
				appendMoves(line);
			} else if (line.startsWith(";") && getName() == null) {
				setName(line.substring(1).trim());
			} else {
				lastNonLine = line;
			}
		}
	}

	//

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			write(writer, true);
		} catch (IOException e) {
			// ignore
		}
		return writer.toString();
	}

	public void write(Writer writer, boolean includeInitialLevel) throws IOException {
		if (getName() != null) {
			writer.write(NAME_KEY);
			writer.write(": ");
			writer.write(getName());
			writer.write("\n");
		}
		Iterator<CharSequence> it = iterator(includeInitialLevel);
		while (it.hasNext()) {
			writer.write(it.next().toString());
			writer.write("\n");
		}
		CharSequence moves = getMoves(includeInitialLevel);
		if (moves != null && moves.length() > 0) {
			writer.write(RunlengthEncoding.encode(moves).toString());
		}
	}
}
