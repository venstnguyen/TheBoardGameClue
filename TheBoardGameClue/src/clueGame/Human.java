package clueGame;

import java.awt.Color;

public class Human extends Player {

    public Human(String name, Color color, int row, int column) {
        super(name, color, row, column);
    }

	@Override
	public boolean isHuman() {
		return true;
	}
}