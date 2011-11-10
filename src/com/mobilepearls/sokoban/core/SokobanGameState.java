package com.mobilepearls.sokoban.core;

import java.util.LinkedList;
import java.util.Queue;

import android.view.KeyEvent;

@SuppressWarnings("serial")
public class SokobanGameState extends SokobanArrayMap {

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
		if (map instanceof SokobanArrayMap) {
			position[0] = ((SokobanArrayMap) map).getPlayerX();
			position[1] = ((SokobanArrayMap) map).getPlayerY();
		} else {
			for (int x = 0; x < map.getWidth(); x++) {
				for (int y = 0; y < map.getHeight(); y++) {
					char c = map.getItemAt(x, y);
					if (CHAR_MAN_ON_FLOOR == c || CHAR_MAN_ON_TARGET == c) {
						position[0] = x;
						position[1] = y;
					}
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

	private transient StringBuilder buffer = new StringBuilder();

	private transient final int[] playerPosition = new int[2];

	public SokobanGameState(SokobanMap map) {
		super(map);
	}

	public boolean canUndo() {
		return moves.length() > 0;
	}

	// hal: find last moves from same position in same direction
	public CharSequence computeLastMovesInDirection(char move) {
		int nx = getPlayerX(), ny = getPlayerY();
		int[] direction = directionFor(move, DIRECTIONS_CHAR_POS);
		int pos = moves.length() - 1;
		while (pos >= 0) {
			char c = moves.charAt(pos);
			int[] undoDirection = directionFor(Character.toLowerCase(c), DIRECTIONS_CHAR_POS);
			nx -= undoDirection[0];
			ny -= undoDirection[1];
			if (nx == playerPosition[0] && ny == playerPosition[1] && undoDirection == direction) {
				return moves.substring(pos);
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
		int nx = getPlayerX(), ny = getPlayerY();
		outer: while (true) {
			int[] possibleDirection = null;
			for (int i = 0; i < directions.length; i++) {
				int[] testDirection = directions[i];
				// ignore the opposite direction
				if (testDirection[0] == -direction[0] && testDirection[1] == -direction[1]) {
					continue;
				}
				if (tryMove((char) testDirection[DIRECTIONS_CHAR_POS], 1, nx, ny, considerPushes, false) > 0) {
					if (possibleDirection != null)
						break outer;
					else
						possibleDirection = testDirection;
				}
			}
			if (possibleDirection == null || tryMove((char) possibleDirection[DIRECTIONS_CHAR_POS], 1, nx, ny, false, false) == 0) {
				break;
			}
			buffer.append((char) possibleDirection[DIRECTIONS_CHAR_POS]);
			nx += possibleDirection[0];
			ny += possibleDirection[1];
			direction = possibleDirection;
		}
		return buffer.toString();
	}

	// hal: compute moves leading to goal position
	public CharSequence computeMovesToGoal(int goalX, int goalY) {
		char goalValue = getItemAt(goalX, goalY);
		if (goalValue != CHAR_FLOOR && goalValue != CHAR_TARGET) {
			return null;
		}
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

	public boolean isDone() {
		return getCounter(CHAR_DIAMOND_ON_FLOOR) == 0;
	}

	public void restart() {
		while (moves.length() > 0) {
			tryUndo();
		}
	}

	/** Return the number of moves actually performed. */
	public int tryMove(char move) {
		return tryMove(move, 1, true, true);
	}

	/** Return the number of moves actually performed. */
	private int tryMove(char move, int steps, boolean allowPushes, boolean reallyDo) {
		return tryMove(move, steps, getPlayerX(), getPlayerY(), allowPushes, reallyDo);
	}

	/** Return the number of moves actually performed. */
	private int tryMove(char move, int steps, int playerX, int playerY, boolean allowPushes, boolean reallyDo) {
		int[] direction = directionFor(move, DIRECTIONS_CHAR_POS);
		if (direction == null) {
			return 0;
		}
		int dx = direction[0], dy = direction[1];

		for (int i = 0; i < steps; i++) {
			// if moving multiple steps at once, stop if an intermediate step may finish the game:
			if (isDone()) {
				return i;
			}
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
				if (! allowPushes) {
					ok = false;
					break;
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

			if (! ok) {
				return i;
			}
			if (! reallyDo) {
				return i + 1;
			}
			moves.append(move);

			if (Character.isUpperCase(move)) {
				byte pushedX = (byte) (newX + dx);
				byte pushedY = (byte) (newY + dy);
				setItemAt(pushedX, pushedY, newCharWhenDiamondEnters(getItemAt(pushedX, pushedY)));
			}
			setItemAt(playerX, playerY, originalCharWhenManLeaves(getItemAt(playerX, playerY)));
			setItemAt(newX, newY, newCharWhenManEnters(getItemAt(newX, newY)));

			playerX = newX;
			playerY = newY;
		}
		return steps;
	}

	/** Return the number of moves actually performed. */
	public int tryMove(int dx, int dy) {
		int steps = 0;
		if (dx != 0) {
			steps = Math.abs(dx);
			dx /= steps;
		} else if (dy != 0) {
			steps = Math.abs(dy);
			dy /= steps;
		}
		char move = moveFor(dx, dy);
		if (move == '\0' || steps == 0) {
			return 0;
		}
		return tryMove(move, steps, true, true);
	}

	public boolean tryUndo() {
		if (moves.length() == 0)
			return false;
		int pos = moves.length() - 1;
		char move = moves.charAt(pos);
		moves.setLength(pos);
		int[] direction = directionFor(Character.toLowerCase(move), DIRECTIONS_CHAR_POS);
		if (direction == null) {
			return false;
		}

		int dx = direction[0], dy = direction[1];
		int playerX = getPlayerX(), playerY = getPlayerY();

		setItemAt(playerX - dx, playerY - dy, newCharWhenManEnters(getItemAt(playerX - dx, playerY - dy)));
		if (Character.isUpperCase(move)) {
			setItemAt(playerX, playerY, newCharWhenDiamondEnters(getItemAt(playerX, playerY)));
			setItemAt(playerX + dx, playerY + dy, originalCharWhenDiamondLeaves(getItemAt(playerX + dx, playerY + dy)));
		} else {
			setItemAt(playerX, playerY, originalCharWhenManLeaves(getItemAt(playerX, playerY)));
		}
		return true;
	}
}
