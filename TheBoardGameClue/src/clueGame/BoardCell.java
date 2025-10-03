package clueGame;

import java.awt.*;

/**
 * BoardCell
 *
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
**/

public class BoardCell {
	private final int row, col;
	private final char initial;

	private boolean isDoorway = false;
	private DoorDirection doorDirection = DoorDirection.NONE;
	private boolean isRoomCenter = false;
	private boolean isLabel = false;
	private char secretPassage = 0;
	private boolean isOccupied = false;
	private boolean highlight = false; /// NEW

	/** Creates a cell at (row,col) with given room initial. */
	public BoardCell(int row, int col, char initial) {
		this.row = row;
		this.col = col;
		this.initial = initial;
	}

	/** Returns row index. */
	public int getRow() {
		return row;
	}

	/** Returns column index. */
	public int getCol() {
		return col;
	}

	/** Returns room initial. */
	public char getInitial() {
		return initial;
	}

	/** True if this is a room cell. */
	public boolean isRoom() {
		return initial != 'W' && initial != 'X';
	}

	/** Marks or unmarks this as a doorway. */
	public void setDoorway(boolean isDoorway) {
		this.isDoorway = isDoorway;
	}

	/** True if this is a doorway. */
	public boolean isDoorway() {
		return isDoorway;
	}

	/** Sets the doorway’s direction. */
	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	/** Returns the doorway’s direction. */
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	/** Marks or unmarks this as a room center. */
	public void setRoomCenter(boolean isRoomCenter) {
		this.isRoomCenter = isRoomCenter;
	}

	/** True if this is the room center. */
	public boolean isRoomCenter() {
		return isRoomCenter;
	}

	/** Sets the secret passage character (0 if none). */
	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	/** Returns the secret passage character. */
	public char getSecretPassage() {
		return secretPassage;
	}

	/** Marks or unmarks this as a label cell. */
	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}

	/** True if this is a label cell. */
	public boolean isLabel() {
		return isLabel;
	}

	/** Marks or unmarks this cell as occupied. */
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	/** True if this cell is occupied. */
	public boolean isOccupied() {
		return isOccupied;
	}

	/** Highlights Cell. *//// NEW
	public void setHighlight(boolean highlight) { this.highlight = highlight; }

	/** True if this cell is highlighted. *//// NEW
	public boolean isHighlighted() { return highlight; }

	/** Alias for isOccupied(). */
	public boolean getOccupied() {
		return isOccupied;
	}

	/** Returns the cell’s coordinates as "(row,col)". */
	@Override
	public String toString() {
		return "(" + row + "," + col + ")";
	}

	/** Draw this cell on the board. */
	public void draw(Graphics g, int x, int y, int width, int height) { /// NEW (Edited)
		// Fill background based on type
		Color fill;
		if (highlight) {
			fill = Color.CYAN;
		} else if (initial == 'W') {
			fill = Color.YELLOW; /// Walkway
		} else if (isRoom()) {
			fill = Color.LIGHT_GRAY; /// Room
		} else {
			fill = Color.BLACK;
		}
		g.setColor(fill);
		g.fillRect(x, y, width, height);

		// Outline fill
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);

		// Draw doorway indicator
		if (isDoorway) {
			g.setColor(Color.BLUE);
			int doorThickness = Math.max(2, height / 6);
			switch (doorDirection) {
				case UP -> g.fillRect(x, y, width, doorThickness);
				case DOWN -> g.fillRect(x, y + height - doorThickness, width, doorThickness);
				case LEFT -> g.fillRect(x, y, doorThickness, height);
				case RIGHT -> g.fillRect(x + width - doorThickness, y, doorThickness, height);
				default -> {}
			}
		}
		// Secret Passages are Magenta
		if (secretPassage != 0) {
			g.setColor(Color.MAGENTA);
			int d = Math.min(width, height) / 2;
			int cx = x + (width - d) / 2;
			int cy = y + (height - d) / 2;
			g.fillOval(cx, cy, d, d);

			g.setColor(Color.WHITE);
			String dest = String.valueOf(secretPassage);
			java.awt.FontMetrics fm = g.getFontMetrics();
			g.drawString(dest,
					x + (width - fm.stringWidth(dest)) / 2,
					y + (height + fm.getAscent()) / 2 - 2);
		}
	}
}