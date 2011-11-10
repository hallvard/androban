package com.mobilepearls.sokoban.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SokobanArrayMap implements SokobanMap, Serializable {

	private final int[] counters = new int[CHARS_ALL.length()];
	private final char[] map;
	protected final StringBuilder moves;
	private int playerX = -1, playerY = -1;
	private final int width;

	public SokobanArrayMap(SokobanMap map) {
		this.width = map.getWidth();
		int height = map.getHeight();
		this.map = new char[height * width];
		for (int i = 0; i < counters.length; i++) {
			counters[i] = 0;
		}
		for (int y = 0; y < height; y++) {
			// first copy all
			for (int x = 0; x < width; x++) {
				setItemAt(x, y, map.getItemAt(x, y));
			}
			// convert floor to outside from left
			for (int x = 0; x < width; x++) {
				char c = map.getItemAt(x, y);
				if (c == CHAR_FLOOR) {
					setItemAt(x, y, CHAR_OUTSIDE);
				} else if (c != CHAR_OUTSIDE) {
					break;
				}
			}
			// convert floor to outside from left
			for (int x = width - 1; x >= 0; x--) {
				char c = map.getItemAt(x, y);
				if (c == CHAR_FLOOR) {
					setItemAt(x, y, CHAR_OUTSIDE);
				} else if (c != CHAR_OUTSIDE) {
					break;
				}
			}
		}
		moves = new StringBuilder(map.getMoves());
	}

	protected int getCounter(char c) {
		int pos = CHARS_ALL.indexOf(c);
		return (pos >= 0 ? counters[pos] : -1);
	}

	@Override
	public int getHeight() {
		return map.length / width;
	}

	@Override
	public char getItemAt(int x, int y) {
		return (x >= 0 && x < width && y >= 0 && y < getHeight() ? map[y * width + x] : CHAR_OUTSIDE);
	}

	@Override
	public CharSequence getMoves() {
		return moves;
	}

	protected int getPlayerX() {
		return playerX;
	}

	protected int getPlayerY() {
		return playerY;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	public boolean hasMoves() {
		return moves != null && moves.length() > 0;
	}

	protected void setItemAt(int x, int y, char value) {
		char oldValue = map[y * width + x];
		int pos = CHARS_ALL.indexOf(oldValue);
		if (pos >= 0) {
			counters[pos]--;
		}
		map[y * width + x] = value;
		pos = CHARS_ALL.indexOf(value);
		if (pos >= 0) {
			counters[pos]++;
		}
		if (value == CHAR_MAN_ON_FLOOR || value == CHAR_MAN_ON_TARGET) {
			playerX = x;
			playerY = y;
		}
	}
}
