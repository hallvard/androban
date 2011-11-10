package com.mobilepearls.sokoban.core;

public interface SokobanMap {

	public static final char CHAR_DIAMOND_ON_FLOOR = '$';
	public static final char CHAR_DIAMOND_ON_TARGET = '*';
	public static final char CHAR_FLOOR = ' ';
	public static final char CHAR_MAN_ON_FLOOR = '@';
	public static final char CHAR_MAN_ON_TARGET = '+';
	public static final char CHAR_OUTSIDE = '\'';
	public static final char CHAR_TARGET = '.';
	public static final char CHAR_WALL = '#';

	public static final String CHARS_ALL = " $*@+.#'";

	public int getHeight();
	public char getItemAt(int x, int y);
	public CharSequence getMoves();
	public int getWidth();
}
