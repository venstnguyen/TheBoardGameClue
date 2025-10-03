package tests;

/**
 * BoardAdjTargetTest
 *
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
 **/

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {

	private static Board board;

	@BeforeEach
	public void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}



	@Test
	public void testAdjacency_WalkwayOnly() {
		BoardCell cell = board.getCell(8, 16);
		Set<BoardCell> adj = board.getAdjList(cell);
		assertTrue(adj.size() > 0, "Walkway should have at least one adjacent");
		for (BoardCell adjCell : adj) {
			assertTrue(adjCell.getInitial() == 'W' || adjCell.isDoorway(), 
				"Adjacent should be walkway or doorway");
		}
	}

	@Test
	public void testAdjacency_InsideRoom_NoAdjacents() {
		BoardCell roomCell = board.getCell(2, 2); // Inside Laboratory
		Set<BoardCell> adj = board.getAdjList(roomCell);
		assertEquals(0, adj.size(), "Room interior should have no adjacents");
	}

	@Test
	public void testAdjacency_AtBoardEdge() {
		BoardCell edgeCell = board.getCell(0, 5); // Top edge
		Set<BoardCell> adj = board.getAdjList(edgeCell);

		assertEquals(0, adj.size(), "Room interior should have no adjacents, even if it is an edge");
	}


	@Test
	public void testAdjacency_NextToRoom_NotDoor() {
		BoardCell cell = board.getCell(5, 3); // Next to room but not a doorway
		Set<BoardCell> adj = board.getAdjList(cell);
		for (BoardCell adjCell : adj) {
			assertFalse(adjCell.isRoom());
		}
	}

	@Test
	public void testAdjacency_DoorwayConnectsToCenter() {
		// Find a doorway and test that it connects to its room center
		BoardCell doorCell = board.getCell(5, 10); // Should be a doorway facing UP
			if (doorCell.isDoorway()) {
				Set<BoardCell> adj = board.getAdjList(doorCell);					
				boolean hasRoomCenter = false;
				for (BoardCell adjCell : adj) {
					if (adjCell.isRoomCenter()) {
						hasRoomCenter = true;
						break;
					}
				}
				assertTrue(hasRoomCenter, "Doorway should connect to room center");
		}
	}

	@Test
	public void testAdjacency_SecretPassage() {
		BoardCell centerCell = null; // Test a room center that has a secret passage
		for (int r = 0; r < board.getNumRows(); r++) { // Look for room centers with secret passages
			for (int c = 0; c < board.getNumColumns(); c++) {
				BoardCell cell = board.getCell(r, c);
				if (cell.isRoomCenter() && cell.getSecretPassage() != 0) {
					centerCell = cell;
					break;
				}
			}
			if (centerCell != null) break;
		}
		if (centerCell != null) {
			Set<BoardCell> adj = board.getAdjList(centerCell);
			boolean foundSecret = false;
			for (BoardCell adjCell : adj) {
				if (adjCell.isRoomCenter() && 
					adjCell.getInitial() == centerCell.getSecretPassage()) {
					foundSecret = true;
					break;
				}
			}
		assertTrue(foundSecret);
		}
	}
	/** Targets along walkways at various distances. */
	@Test
	public void testWalkwayTargets() {
		// start on an interior walkway with no nearby doors
		int r0 = 5, c0 = 7, steps = 2;
		Set<BoardCell> targets = board.calcTargets(r0, c0, steps);
		// from (5,7) in 2 steps you can reach exactly 7 distinct walkway cells
		assertEquals(5, targets.size());
		// e.g. one of them is (3,7)
		assertTrue(targets.contains(board.getCellAt(3, 7)));
	}

	/** Targets that allow entry into a room. */
	@Test
	public void testTargetsEnterRoom() {
		// start on a walkway next to a doorway (door at 4,6 -> room above)
		int r1 = 4, c1 = 7, steps1 = 2;
		Set<BoardCell> targets = board.calcTargets(r1, c1, steps1);
		// with 2 steps you can step into that room's center
		assertTrue(targets.stream().anyMatch(BoardCell::isRoomCenter));
	}

	/** Targets when leaving a room without using secret passage. */
	@Test
	public void testTargetsExitRoomNoPassage() {
		// start at the Ballroom center (no secret passage)
		int r2 = 20, c2 = 11, steps2 = 1;
		Set<BoardCell> targets = board.calcTargets(r2, c2, steps2);
		// only door‐way walkways are reachable, no room centers
		for (BoardCell c : targets) {
			assertFalse(c.isRoomCenter());
		}
	}

	/** Targets when leaving a room using secret passage. */
	@Test
	public void testTargetsExitRoomWithPassage() {
		// start at Study center (has secret passage to Kitchen)
		int r3 = 3, c3 = 3, steps3 = 1;
		Set<BoardCell> targets = board.calcTargets(r3, c3, steps3);
		// one of the reachable cells should be another room center
		assertTrue(targets.stream().anyMatch(BoardCell::isRoomCenter));
	}

	/** Targets blocked by occupied cells. */
	@Test
	public void testTargetsBlockedByOccupied() {
		// block one of the adjacent walkways from (5,7)
		BoardCell blocker = board.getCellAt(4, 7);
		blocker.setOccupied(true);
		Set<BoardCell> targets = board.calcTargets(5, 7, 1);
		// the blocked cell must NOT appear in the result
		assertFalse(targets.contains(blocker));
		// restore
		blocker.setOccupied(false);
	}
}