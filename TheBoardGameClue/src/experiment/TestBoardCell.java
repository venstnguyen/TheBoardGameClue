package experiment;
/**
 * TestBoardCell
 *
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
 *
 * Purpose: Representation of a cell in our board
 */

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
    private int row;
    private int col;
    private boolean isRoom = false;
    private boolean isOccupied = false;
    private Set<TestBoardCell> adjList = new HashSet<>();

    /** Construct a cell at the given row/column. */
    public TestBoardCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** Add an adjacent cell to this cell’s adjacency list. */
    public void addAdjacency(TestBoardCell cell) {
        adjList.add(cell);
    }

    /** Retrieve this cell’s adjacency list. */
    public Set<TestBoardCell> getAdjList() {
        return adjList;
    }

    /** Mark this cell as part of a room. */
    public void setRoom(boolean isRoom) {
        this.isRoom = isRoom;
    }

    /** True if this cell represents a room. */
    public boolean isRoom() {
        return isRoom;
    }

    /** Mark this cell as occupied. */
    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    /** True if another player occupies this cell. */
    public boolean getOccupied() {
        return isOccupied;
    }

    /** Convenience check for occupied status. */
    public boolean isOccupied() {
        return isOccupied;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}