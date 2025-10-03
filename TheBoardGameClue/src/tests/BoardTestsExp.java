package tests;
/**
 * BoardTestsExp 
 *  
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
 * 
 * Purpose: JUnit tests for a 4x4 board
 */
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import experiment.TestBoard;
import experiment.TestBoardCell;

public class BoardTestsExp {
	TestBoard board;

	@BeforeEach
	public void setup() {
		board = new TestBoard();
	}

	@Test
	public void testAdjacencyTopLeft() {
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> adj = cell.getAdjList();
		assertEquals(2, adj.size());
		assertTrue(adj.contains(board.getCell(0,1)));
		assertTrue(adj.contains(board.getCell(1,0)));
	}

	@Test
	public void testAdjacencyBottomRight() {
		TestBoardCell cell = board.getCell(3, 3);
		Set<TestBoardCell> adj = cell.getAdjList();
		assertEquals(2, adj.size());
		assertTrue(adj.contains(board.getCell(2, 3)));
		assertTrue(adj.contains(board.getCell(3, 2)));
	}

	@Test
	public void testAdjacencyRightEdge() {
		TestBoardCell cell = board.getCell(1, 3);
		Set<TestBoardCell> adj = cell.getAdjList();
		assertEquals(3, adj.size());
		assertTrue(adj.contains(board.getCell(0, 3)));
		assertTrue(adj.contains(board.getCell(2, 3)));
		assertTrue(adj.contains(board.getCell(1, 2)));
	}

	@Test
	public void testAdjacencyLeftEdge() {
		TestBoardCell cell = board.getCell(3, 0);
		Set<TestBoardCell> adj = cell.getAdjList();
		assertEquals(2, adj.size());
		assertTrue(adj.contains(board.getCell(2, 0)));
		assertTrue(adj.contains(board.getCell(3, 1)));
	}

	@Test
	public void testAdjacencyCenter() {
		TestBoardCell cell = board.getCell(2, 2);
		Set<TestBoardCell> adj = cell.getAdjList();
		assertEquals(4, adj.size());
		assertTrue(adj.contains(board.getCell(1, 2)));
		assertTrue(adj.contains(board.getCell(3, 2)));
		assertTrue(adj.contains(board.getCell(2, 1)));
		assertTrue(adj.contains(board.getCell(2, 3)));
	}

	//----- Target tests
	@Test
	public void testTargetsNormalTwoSteps() {
		TestBoardCell start = board.getCell(0, 0);
		board.calcTargets(start, 2);
		Set<TestBoardCell> targets = board.getTargets();
		assertEquals(3, targets.size(), "2-step normal from (0,0) should find 3 targets");
		assertTrue(targets.contains(board.getCell(2, 0)), "Should include (2,0)");
		assertTrue(targets.contains(board.getCell(0, 2)), "Should include (0,2)");
		assertTrue(targets.contains(board.getCell(1, 1)), "Should include (1,1)");
	}

	@Test
	public void testTargetsNormalCenterThreeSteps() {
		TestBoardCell start = board.getCell(2, 2);
		board.calcTargets(start, 3);
		Set<TestBoardCell> targets = board.getTargets();
		// Expect 8 cells
		assertEquals(8, targets.size(), "3-step normal from (2,2) should find 8 targets");
	}

	@Test
	public void testTargetsMaxDieRollSixSteps() {
		TestBoardCell start = board.getCell(1, 1);
		board.calcTargets(start, 6);
		Set<TestBoardCell> targets = board.getTargets();
		// Expect 7 cells
		assertEquals(7, targets.size(), "6-step roll from (1,1) should find 7 targets");
	}

	@Test
	public void testTargetsRoom() {
		TestBoardCell roomCell = board.getCell(1, 1);
		roomCell.setRoom(true);
		board.calcTargets(board.getCell(0, 1), 2);
		Set<TestBoardCell> targets = board.getTargets();
		assertTrue(targets.contains(roomCell), "Should include room cell (1,1)");
	}

	@Test
	public void testTargetsOccupied() {
		TestBoardCell occupiedCell = board.getCell(2, 2);
		occupiedCell.setOccupied(true);
		board.calcTargets(board.getCell(2, 1), 1);
		Set<TestBoardCell> targets = board.getTargets();
		// You can’t move into occupied, but another adjacent cell should be reachable
		assertTrue(targets.contains(board.getCell(1, 1)),
				"Should still reach (1,1) when (2,2) is occupied");
	}
}