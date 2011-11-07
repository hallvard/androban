package com.mobilepearls.sokoban;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import android.view.KeyEvent;

import com.mobilepearls.sokoban.io.Level;

@SuppressWarnings("serial")
public class SokobanGameState implements SokobanMap, Serializable {

	private static final int[][] directions = {
		{ 0,  1, 'd', KeyEvent.KEYCODE_DPAD_DOWN},
		{ 0, -1, 'u', KeyEvent.KEYCODE_DPAD_UP},
		{-1,  0, 'l', KeyEvent.KEYCODE_DPAD_LEFT},
		{ 1,  0, 'r', KeyEvent.KEYCODE_DPAD_RIGHT},
	};
	public final static int DIRECTIONS_CHAR_POS = 2;
	public final static int DIRECTIONS_KEY_POS = 3;

	public static int[] directionFor(int i, int pos) {
		for (int j = 0; j < directions.length; j++) {
			if (directions[j][pos] == i)
				return directions[j];
		}
		return null;
	}

	public static int getDiamondsLeft(SokobanMap map) {
		int count = 0;
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				char c = map.getItemAt(x, y);
				if (c == CHAR_DIAMOND_ON_FLOOR)
					count++;
			}
		}
		return count;
	}

	public static int getMoveCount(CharSequence moves, boolean includeMoves, boolean includePushes) {
		int count = 0;
		int n = 0;
		for (int i = 0; i < moves.length(); i++) {
			char c = moves.charAt(i);
			if (Character.isDigit(c)) {
				n = 10 * n + (c - '0');
			} else {
				if ((includeMoves && Character.isLowerCase(c)) || (includePushes && Character.isUpperCase(c))) {
					count += (n > 0 ? n : 1);
				}
				n = 0;
			}
		}
		return count;
	}

	public static int[] getPlayerPosition(SokobanMap map, int[] position) {
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				char c = map.getItemAt(x, y);
				if (CHAR_MAN_ON_FLOOR == c || CHAR_MAN_ON_TARGET == c) {
					position[0] = x;
					position[1] = y;
				}
			}
		}
		return position;
	}

	public static char moveFor(int dx, int dy) {
		for (int i = 0; i < directions.length; i++) {
			if (directions[i][0] == dx && directions[i][1] == dy)
				return (char) directions[i][DIRECTIONS_CHAR_POS];
		}
		return '\0';
	}

	private static char newCharWhenDiamondEnters(char current) {
		switch (current) {
		case CHAR_FLOOR:
		case CHAR_MAN_ON_FLOOR:
			return CHAR_DIAMOND_ON_FLOOR;
		case CHAR_TARGET:
		case CHAR_MAN_ON_TARGET:
			return CHAR_DIAMOND_ON_TARGET;
		}
		throw new RuntimeException("Invalid current char: '" + current + "'");
	}

	private static char newCharWhenManEnters(char current) {
		switch (current) {
		case CHAR_FLOOR:
		case CHAR_DIAMOND_ON_FLOOR:
			return CHAR_MAN_ON_FLOOR;
		case CHAR_TARGET:
		case CHAR_DIAMOND_ON_TARGET:
			return CHAR_MAN_ON_TARGET;
		}
		throw new RuntimeException("Invalid current char: '" + current + "'");
	}

	private static char originalCharWhenDiamondLeaves(char current) {
		return (current == CHAR_DIAMOND_ON_FLOOR) ? CHAR_FLOOR : CHAR_TARGET;
	}

	private static char originalCharWhenManLeaves(char current) {
		return (current == CHAR_MAN_ON_FLOOR) ? CHAR_FLOOR : CHAR_TARGET;
	}
	public static char[][] toCharMatrix(SokobanMap map) {
		int width = map.getWidth(), height = map.getHeight();
		char[][] result = new char[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				result[x][y] = map.getItemAt(x, y);
			}
		}
		return result;
	}
	private transient StringBuilder buffer = new StringBuilder();

	private transient final Level currentLevel;

	private final char[][] map;

	private transient final int[] playerPosition = new int[2];

	// hal: switched to char representation of move
	private final StringBuilder undos = new StringBuilder(); // new LinkedList<Undo>();

	public SokobanGameState(Level level) {
		currentLevel = level;
		undos.append(level.getMoves());
		map = toCharMatrix(currentLevel);
	}

	public boolean canUndo() {
		return undos.length() > 0;
	}

	// hal: find last moves from same position in same direction
	public CharSequence computeLastMovesInDirection(char move) {
		getPlayerPosition(this, playerPosition);
		int nx = playerPosition[0], ny = playerPosition[1];
		int[] direction = directionFor(move, DIRECTIONS_CHAR_POS);
		int pos = undos.length() - 1;
		while (pos >= 0) {
			char c = undos.charAt(pos);
			int[] undoDirection = directionFor(Character.toLowerCase(c), DIRECTIONS_CHAR_POS);
			nx -= undoDirection[0];
			ny -= undoDirection[1];
			if (nx == playerPosition[0] && ny == playerPosition[1] && undoDirection == direction) {
				return undos.substring(pos);
			}
			pos--;
		}
		return null;
	}

	// hal: compute moves in direction, until there is more than one choice
	public CharSequence computeMovesInDirection(char move, boolean considerPushes) {
		int[] direction = directionFor(move, DIRECTIONS_CHAR_POS);
		if (direction == null) {
			return null;
		}
		if (buffer == null)
			buffer = new StringBuilder();
		else
			buffer.setLength(0);
		getPlayerPosition(this, playerPosition);
		int nx = playerPosition[0], ny = playerPosition[1];
		outer: while (true) {
			int[] possibleDirection = null;
			for (int i = 0; i < directions.length; i++) {
				int[] testDirection = directions[i];
				// ignore the opposite direction
				if (testDirection[0] == -direction[0] && testDirection[1] == -direction[1]) {
					continue;
				}
				if (tryMove((char) testDirection[DIRECTIONS_CHAR_POS], 1, nx, ny, considerPushes, false)) {
					if (possibleDirection != null)
						break outer;
					else
						possibleDirection = testDirection;
				}
			}
			if (possibleDirection == null || (! tryMove((char) possibleDirection[DIRECTIONS_CHAR_POS], 1, nx, ny, false, false)))
				break;
			buffer.append((char) possibleDirection[DIRECTIONS_CHAR_POS]);
			nx += possibleDirection[0];
			ny += possibleDirection[1];
			direction = possibleDirection;
		}
		return buffer.toString();
	}

	// hal: compute moves leading to goal position
	public CharSequence computeMovesToGoal(int goalX, int goalY) {
		int startX = -1, startY = -1, height = getHeight();
		// copy the current state
		if (buffer == null)
			buffer = new StringBuilder();
		else
			buffer.setLength(0);
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < height; y++) {
				char c = getItemAt(x, y);
				buffer.append(c);
				if (c == CHAR_MAN_ON_FLOOR || c == CHAR_MAN_ON_TARGET) {
					startX = x;
					startY = y;
				}
			}
			buffer.append('\n'); // to ease debugging
		}
		height++; // count extra newline
		if (startX < 0 || startY < 0) {
			return null;
		}
		// we extend a boundary, like riples in the water, from the starting point
		Queue<Integer> boundary = new LinkedList<Integer>();
		boundary.add(startX);
		boundary.add(startY);
		// as long as there are more cells to consider
		while (boundary.size() > 0) {
			// remove current position
			int x = boundary.poll(), y = boundary.poll();
			for (int i = 0; i < directions.length; i++) {
				int[] direction = directions[i];
				int nx = x + direction[0], ny = y + direction[1];
				char c = buffer.charAt(nx * height + ny);
				// if this is a new cell
				if (c == CHAR_FLOOR || c == CHAR_TARGET) {
					// note the direction we came from
					buffer.setCharAt(nx * height + ny, (char) direction[DIRECTIONS_CHAR_POS]);
					// if this is goal, walk backwards (the opposite direction) and collect moves
					if (nx == goalX && ny == goalY) {
						int pos = buffer.length();
						while (nx != startX || ny != startY) {
							c = buffer.charAt(nx * height + ny);
							buffer.append(c);
							direction = directionFor(c, DIRECTIONS_CHAR_POS);
							nx -= direction[0];
							ny -= direction[1];
						}
						buffer.reverse();
						buffer.setLength(buffer.length() - pos);
						return buffer.toString();
					}
					// enqueue this position, so we can consider it later
					boundary.offer(nx);
					boundary.offer(ny);
				}
			}
		}
		return null;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public int getHeight() {
		return map[0].length;
	}

	public char getItemAt(int x, int y) {
		return map[x][y];
	}

	public String getUndos() {
		return undos.toString();
	}

	public int getWidth() {
		return map.length;
	}

	public boolean isDone() {
		return getDiamondsLeft(this) == 0;
	}

	public boolean performUndo() {
		if (undos.length() == 0)
			return false;
		int pos = undos.length() - 1;
		char move = undos.charAt(pos);
		undos.setLength(pos);
		int[] direction = directionFor(Character.toLowerCase(move), DIRECTIONS_CHAR_POS);
		if (direction == null) {
			return false;
		}

		int dx = direction[0], dy = direction[1];
		getPlayerPosition(this, playerPosition);
		int playerX = playerPosition[0], playerY = playerPosition[1];

		setItemAt(playerX - dx, playerY - dy, newCharWhenManEnters(getItemAt(playerX - dx, playerY - dy)));
		if (Character.isUpperCase(move)) {
			setItemAt(playerX, playerY, newCharWhenDiamondEnters(getItemAt(playerX, playerY)));
			setItemAt(playerX + dx, playerY + dy, originalCharWhenDiamondLeaves(getItemAt(playerX + dx, playerY + dy)));
		} else {
			setItemAt(playerX, playerY, originalCharWhenManLeaves(getItemAt(playerX, playerY)));
		}
		return true;
	}

	public void restart() {
		while (undos.length() > 0) {
			performUndo();
		}
	}

	public char setItemAt(int x, int y, char value) {
		return map[x][y] = value;
	}

	/** Return whether something was changed. */
	public boolean tryMove(char move) {
		return tryMove(move, 1, true, true);
	}

	/** Return whether something was changed. */
	private boolean tryMove(char move, int steps, boolean allowPushes, boolean reallyDo) {
		getPlayerPosition(this, playerPosition);
		return tryMove(move, steps, playerPosition[0], playerPosition[1], allowPushes, reallyDo);
	}

	/** Return whether something was changed. */
	private boolean tryMove(char move, int steps, int playerX, int playerY, boolean allowPushes, boolean reallyDo) {
		int[] direction = directionFor(move, DIRECTIONS_CHAR_POS);
		if (direction == null) {
			return false;
		}
		int dx = direction[0], dy = direction[1];
		boolean somethingChanged = false;

		for (int i = 0; i < steps; i++) {
			int newX = playerX + dx;
			int newY = playerY + dy;

			boolean ok = false;

			switch (getItemAt(newX, newY)) {
			case CHAR_FLOOR:
				// move to empty space
			case CHAR_TARGET:
				// move to empty target
				ok = true;
				break;
			case CHAR_DIAMOND_ON_FLOOR:
				// pushing away diamond on floor
			case CHAR_DIAMOND_ON_TARGET:
				// hal:
				if (! allowPushes) {
					return false;
				}
				// pushing away diamond on target
				char pushTo = getItemAt(newX + dx, newY + dy);
				ok = (pushTo == CHAR_FLOOR || pushTo == CHAR_TARGET);
				// ok if pushing to empty space
				if (ok) {
					move = Character.toUpperCase(move);
				}
				break;
			}

			if (ok) {
				// hal:
				if (! reallyDo) {
					return true;
				}
				if (undos.length() > 32000) {
					// size of undo: 9 bytes + object overhead = 25?
					// reuse and clear undo object
					undos.setLength(0);
				}
				undos.append(move);
				somethingChanged = true;

				if (Character.isUpperCase(move)) {
					byte pushedX = (byte) (newX + dx);
					byte pushedY = (byte) (newY + dy);
					setItemAt(pushedX, pushedY, newCharWhenDiamondEnters(getItemAt(pushedX, pushedY)));
				}
				setItemAt(playerX, playerY, originalCharWhenManLeaves(getItemAt(playerX, playerY)));
				setItemAt(newX, newY, newCharWhenManEnters(getItemAt(newX, newY)));

				playerX = newX;
				playerY = newY;
				if (isDone()) {
					// if moving multiple steps at once, stop if an intermediate step may finish the game:
					return true;
				}
			}
		}
		return somethingChanged;
	}
	/** Return whether something was changed. */
	public boolean tryMove(int dx, int dy) {
		int steps = 0;
		if (dx != 0) {
			steps = (int) (dx / Math.signum(dx));
			dx /= steps;
		} else if (dy != 0) {
			steps = (int) (dy / Math.signum(dy));
			dy /= steps;
		}
		char move = moveFor(dx, dy);
		if (move == '\0' || steps == 0) {
			return false;
		}
		return tryMove(move, steps, true, true);
	}
}
