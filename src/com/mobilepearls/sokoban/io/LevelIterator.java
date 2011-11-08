package com.mobilepearls.sokoban.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LevelIterator implements Iterator<Level> {

	private InputStream input;
	private int levelCount = 0;

	private BufferedReader levelInput = null;

	private Level nextLevel = null;
	private ZipEntry zipEntry = null;

	private ZipInputStream zipInput;

	public LevelIterator(InputStream input) {
		this(input, null);
	}
	public LevelIterator(InputStream input, ZipInputStream zipInput) {
		this.input = input;
		this.zipInput = zipInput;
	}

	private void closeAllInputs() {
		if (zipInput != null) {
			if (zipEntry != null) {
				try {
					zipInput.closeEntry();
				} catch (IOException e) {
					// ignore
				}
			}
			zipEntry = null;
			try {
				zipInput.close();
			} catch (IOException e) {
				// ignore
			}
			zipInput = null;
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				// ignore
			}
			input = null;
		}
	}

	private Level getNextLevel() {
		if (input == null) {
			return null;
		}
		if (zipInput != null && zipEntry == null) {
			try {
				zipEntry = zipInput.getNextEntry();
			} catch (IOException e) {
				closeAllInputs();
				throw new RuntimeException("Exception when getting zip entry: " + e.getMessage(), e);
			}
		}
		if (levelInput == null) {
			if (zipEntry != null) {
				levelInput = new BufferedReader(new InputStreamReader(zipInput));
			} else if (input != null) {
				levelInput = new BufferedReader(new InputStreamReader(input));
			}
		}
		Level level = readNextLevel();
		if (level == null) {
			closeAllInputs();
		}
		return level;
	}

	public boolean hasNext() {
		if (nextLevel == null) {
			nextLevel = getNextLevel();
		}
		return nextLevel != null;
	}

	public Level next() {
		if (nextLevel == null) {
			nextLevel = getNextLevel();
		}
		Level next = nextLevel;
		nextLevel = null;
		return next;
	}

	private Level readNextLevel() {
		try {
			Level level = new Level();
			levelCount++;
			level.read(levelInput);
			if (level.getName() == null) {
				level.setName("Level " + levelCount);
			}
			return (level.hasLevelLines() ? level : null);
		} catch (Exception e) {
			closeAllInputs();
			throw new RuntimeException("Exception when reading: " + e.getMessage(), e);
		}
	}

	public void remove() {
		throw new UnsupportedOperationException(getClass() + " does not support remove");
	}
}
