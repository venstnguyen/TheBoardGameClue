package tests;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

public class FileInitTests {

	// Constants for your custom configuration
	public static final int LEGEND_SIZE = 11;  // 9 rooms + 2 spaces
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 26;

	private static Board board;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	@Test
	public void testRoomLabels() {
		assertEquals("Laboratory", board.getRoom('L').getName());
		assertEquals("Vault", board.getRoom('V').getName());
		assertEquals("Garage", board.getRoom('G').getName());
		assertEquals("Bathroom", board.getRoom('R').getName());
		assertEquals("Walkway", board.getRoom('W').getName());
		assertEquals("Unused", board.getRoom('X').getName());
	}

	@Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	@Test
	public void testFourDoorDirections() {
		// DOWN
		BoardCell cell = board.getCell(18, 14);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());

		// UP
		cell = board.getCell(5, 10);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());

		// LEFT
		cell = board.getCell(3, 7);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());

		// RIGHT
		cell = board.getCell(2, 16);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());

		// Not a doorway
		cell = board.getCell(6, 6);
		assertFalse(cell.isDoorway());

		cell = board.getCell(12, 12);
		assertFalse(cell.isDoorway());
	}

	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++) {
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (cell.isDoorway()) {
					numDoors++;
				}
			}
		}
		Assert.assertEquals(12, numDoors);
	}

	@Test
	public void testRooms() {

		BoardCell cell = board.getCell(1, 1); // Lab
		Room room = board.getRoom(cell);
		assertNotNull(room);
		assertEquals("Laboratory", room.getName());
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isDoorway());


		cell = board.getCell(2, 3); // LA
		room = board.getRoom(cell);
		assertEquals("Laboratory", room.getName());
		assertTrue(cell.isLabel());
		assertEquals(cell, room.getLabelCell());


		cell = board.getCell(5, 21); // Theatre center
		room = board.getRoom(cell);
		assertEquals("Theatre", room.getName());
		assertTrue(cell.isRoomCenter());
		assertEquals(cell, room.getCenterCell());

		// Walkway
		cell = board.getCell(5, 12);
		room = board.getRoom(cell);
		assertEquals("Walkway", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());

		// Closet
		cell = board.getCell(0, 0);
		room = board.getRoom(cell);
		assertEquals("Unused", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}
}
