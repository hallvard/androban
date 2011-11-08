package com.mobilepearls.sokoban.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mobilepearls.sokoban.SokobanGameState;
import com.mobilepearls.sokoban.SokobanMap;

public class Level implements SokobanMap, Iterable<CharSequence>  {

	private final static String LEVEL_CHARS = CHARS_ALL, EXTRA_CHARS = "-_";
	private final static String NAME_KEY = "Title";
	private final static char SPLIT_CHAR = '|';

	private int diamondsLeft = -1;

	private Level initialLevel = null;

	private List<CharSequence> lines = null;
	private StringBuilder moves = null;

	private String name = null;

	private int width = -1;

	public Level() {
		// nothing to do
	}

	public Level(Level initialLevel) {
		this.initialLevel = initialLevel;
	}

	public void addLevelLine(CharSequence line) {
		if (isLevelLine(line, 1.0f, false)) {
			line = RunlengthEncoding.decode(line);
			if (lines == null) {
				lines = new ArrayList<CharSequence>();
			}
			lines.add(line);
			width = Math.max(width, line.length());
		}
	}

	public void appendMoves(CharSequence line) {
		if (isMovesLine(line, 1.0f)) {
			line = RunlengthEncoding.decode(line);
			if (moves == null) {
				moves = new StringBuilder();
			}
			moves.append(line);
		}
	}

	public void clearLevelLines() {
		this.lines = null;
		diamondsLeft = -1;
	}

	public int getDiamondsLeft() {
		if (diamondsLeft < 0) {
			diamondsLeft = SokobanGameState.getDiamondsLeft(this);
		}
		return diamondsLeft;
	}

	public int getHeight() {
		return (lines != null ? lines.size() : (initialLevel != null ? initialLevel.getHeight() : 0));
	}

	public Level getInitialLevel() {
		return initialLevel;
	}

	@Override
	public char getItemAt(int x, int y) {
		if (lines != null) {
			CharSequence line = lines.get(y);
			if (x >= line.length()) {
				return CHAR_OUTSIDE;
			}
			char c = line.charAt(x);
			if (LEVEL_CHARS.indexOf(c) >= 0) {
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

	public CharSequence getMoves() {
		return getMoves(true);
	}

	private CharSequence getMoves(boolean includeInitialLevel) {
		return (moves != null ? moves : (initialLevel != null && includeInitialLevel ? initialLevel.getMoves() : ""));
	}

	public String getName() {
		return (name != null ? name : (initialLevel != null ? initialLevel.getName() : null));
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
		return (moves != null ? moves.length() > 0 : (initialLevel != null && includeInitialLevel ? initialLevel.hasMoves() : false));
	}

	protected boolean isLevelLine(CharSequence line, float factor, boolean allowSeparator) {
		int count = 0, otherCount = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			boolean isLast = (i == line.length() - 1);
			if (LEVEL_CHARS.indexOf(c) >= 0 || EXTRA_CHARS.indexOf(c) >= 0) {
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

	public Iterator<CharSequence> iterator() {
		return iterator(true);
	}

	private Iterator<CharSequence> iterator(boolean includeInitialLevel) {
		return (lines != null ? lines : (initialLevel != null && includeInitialLevel ? initialLevel : Collections.<CharSequence>emptyList())).iterator();
	}

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
		Iterator<CharSequence> it = this.iterator(includeInitialLevel);
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
