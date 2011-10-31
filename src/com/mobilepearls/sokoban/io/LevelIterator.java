package com.mobilepearls.sokoban.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.res.AssetManager;

public class LevelIterator implements Iterator<Level> {

	private final AssetManager assets;

	private InputStream input = null;
	private int levelCount = 0;

	private BufferedReader levelInput = null;

	private String location;
	private Level nextLevel = null;
	private ZipEntry zipEntry = null;

	private ZipInputStream zipInput = null;

	public LevelIterator(String location, AssetManager assets) {
		this.location = location;
		this.assets = assets;
	}

	private void closeAllInputs() throws IOException {
		if (zipInput != null) {
			zipInput.close();
			zipInput = null;
		}
		if (input != null) {
			input.close();
			input = null;
		}
		location = null;
	}

	private Level getNextLevel() {
		if (location != null && input == null) {
			openStream();
			if (location == null || input == null) {
				return null;
			}
		}
		if (zipInput != null && zipEntry == null) {
			try {
				zipEntry = zipInput.getNextEntry();
			} catch (IOException e) {
				try {
					closeAllInputs();
				} catch (IOException e1) {
				}
				throw new RuntimeException("Exception when getting zip entry for " + location + ": " + e.getMessage(), e);
			}
		}
		if (levelInput == null) {
			InputStream is = null;
			if (zipEntry != null) {
				levelInput = new BufferedReader(new InputStreamReader(zipInput));
			} else if (input != null) {
				levelInput = new BufferedReader(new InputStreamReader(input));
			}
		}
		return readNextLevel();
	}

	public boolean hasNext() {
		if (location != null) {
			if (nextLevel == null) {
				nextLevel = getNextLevel();
			}
		}
		return nextLevel != null;
	}

	public Level next() {
		Level next = nextLevel;
		nextLevel = null;
		return next;
	}

	private void openStream() {
		if (location == null) {
			return;
		}
		try {
			input = (location.indexOf(":") > 2 ? new URL(location).openStream() : assets.open(location));
		} catch (IOException e) {
			if (input != null) {
				try {
					input.close();
					input = null;
				} catch (IOException e1) {
				}
			}
			throw new RuntimeException("Exception when opening InputStream for " + location + ": " + e.getMessage(), e);
		}
		if (location.endsWith(".zip")) {
			zipInput = new ZipInputStream(input);
		}
	}

	private Level readNextLevel() {
		try {
			Level level = new Level();
			levelCount++;
			level.read(level, levelInput);
			if (level.getName() == null) {
				level.setName("Level " + levelCount);
			}
			return (level.iterator().hasNext() ? level : null);
		} catch (Exception e) {
			throw new RuntimeException("Exception when reading " + location + ": " + e.getMessage(), e);
		}
	}

	public void remove() {
		throw new UnsupportedOperationException(getClass() + " does not support remove");
	}
}
