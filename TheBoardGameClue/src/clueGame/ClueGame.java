package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ClueGame extends JFrame{

	private static Board board;
	
    private GameControlPanel gameControlPanel;
    private KnownCardsPanel knownCardsPanel;

    public ClueGame() {
        setTitle("Clue Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLayout(new BorderLayout());

        board = Board.getInstance();
        board.setParentFrame(this);

        gameControlPanel = new GameControlPanel();
        knownCardsPanel = new KnownCardsPanel();


        add(board, BorderLayout.CENTER);
        add(gameControlPanel, BorderLayout.SOUTH);
        add(knownCardsPanel, BorderLayout.EAST);


        board.setControlPanel(gameControlPanel);
        board.setKnownCardsPanel(knownCardsPanel);

        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
        board.initialize();

        Player human = board.getPlayers().get(0);
        knownCardsPanel.setHand(human.getHand(), human.getColor());
        knownCardsPanel.setSeen(human.getSeenCards());
    }
	// C24A new splash screen dialog
	private static void showSplashScreen(Board board) {
	    JOptionPane.showMessageDialog(
	        board,
	        "Welcome to Clue!\n\n" +
	        "You are Inspector Slate (Green).\n" +
	        "Goal: Determine the murderer, weapon, and room.\n\n" +
	        "Click on highlighted tiles to move.\n" +
	        "Make suggestions when in a room.\n\n",
	        "Welcome to Clue",
	        JOptionPane.INFORMATION_MESSAGE
	    );
	    processInitialTurn(board);
	}
	// splash screen helper
	private static void processInitialTurn(Board board) {

		Player firstPlayer = board.advanceToNextPlayer();
		
		board.startPlayerTurn(firstPlayer, (int)(Math.random()*6)+1);
		
	}
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClueGame game = new ClueGame();
            game.setVisible(true);
    		showSplashScreen(board);
        });
    }
}