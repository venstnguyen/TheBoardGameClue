package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.*;

public class GameSolutionTest {
    private static Board board;
    private static Player testPlayer;
    // cards used in the tests
    private static Card person1;
	private static Card person2;
    private static Card weapon1;
	private static Card weapon2;
    private static Card room1;
	private static Card room2;

    @BeforeAll
    public static void setUp() {
        board = Board.getInstance();
        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
        board.initialize();

        testPlayer = new CPU("Test Player", Color.BLACK, 0, 0);
        
        person1 = new Card("Dr. Crane", CardType.PERSON);
        person2 = new Card("Chef Beaumont", CardType.PERSON);
        weapon1 = new Card("Knife", CardType.WEAPON);
        weapon2 = new Card("Hammer", CardType.WEAPON);
        room1 = new Card("Laboratory", CardType.ROOM);
        room2 = new Card("Vault", CardType.ROOM);
        
        JFrame frame = new JFrame();
        GameControlPanel panel = new GameControlPanel();
        frame.add(panel);
        frame.pack();
        frame.setVisible(false); // keep it hidden during tests

        // If GameControlPanel uses a singleton pattern, make sure this panel is registered as the instance
        //GameControlPanel.setInstance(panel);
        
    }

    // checkAccusation for 4 requirements

    @Test
    public void testCheckAccusation_Correct() {
        Solution accusation = new Solution(person1, weapon1, room1);
        board.setSolution(accusation);
        assertTrue(board.checkAccusation(accusation));
    }

    @Test
    public void testCheckAccusation_WrongPerson() {
        Solution accusation = new Solution(person1, weapon1, room1);
        board.setSolution(accusation);
        Solution guess = new Solution(person2, weapon1, room1);
        assertFalse(board.checkAccusation(guess));
    }

    @Test
    public void testWrongWeaponAccusation() {
        Solution accusation = new Solution(person1, weapon1, room1);
        board.setSolution(accusation);
        Solution guess = new Solution(person1, weapon2, room1);
        assertFalse(board.checkAccusation(guess));
    }

    @Test
    public void testWrongRoomAccusation() {
        Solution accusation = new Solution(person1, weapon1, room1);
        board.setSolution(accusation);
        Solution guess = new Solution(person1, weapon1, room2);
        assertFalse(board.checkAccusation(guess));
    }
    
    // disproveSuggestion for 3 requirements

    @Test
    public void testDisproveSuggestionOneMatch() {
        testPlayer.updateHand(weapon1);
        Card result = testPlayer.disproveSuggestion(person2, room2, weapon1);
        assertEquals(weapon1, result);
    }

    @Test
    public void testDisproveSuggestionMultipleMatches() {
        testPlayer.updateHand(person1);
        testPlayer.updateHand(weapon1);

        Set<Card> possible = new HashSet<>();
        possible.add(person1);
        possible.add(weapon1);

        // Run multiple times to check randomness
        for (int i = 0; i < 10; i++) {
            Card result = testPlayer.disproveSuggestion(person1, room2, weapon1);
            assertTrue(possible.contains(result));
        }
    }

    @Test
    public void testDisproveSuggestionNoMatch() {
        Card result = testPlayer.disproveSuggestion(person2, room2, weapon2);
        assertNull(result);
    }

    // handleSuggestion for 4 requirements

    @Test
    public void testSuggestionNoOneCanDisprove() {
        Card suggestionPerson = new Card("Nonexistent Person", CardType.PERSON);
        Card suggestionRoom = new Card("Nonexistent Room", CardType.ROOM);
        Card suggestionWeapon = new Card("Nonexistent Weapon", CardType.WEAPON);

        Card result = board.handleSuggestion(suggestionPerson, suggestionRoom, suggestionWeapon, board.getPlayers().get(0));
        assertNull(result);
    }

    @Test
    public void testSuggestionOnlyHumanDisproves() {
        Player human = board.getPlayers().get(0);
        Player cpu1 = board.getPlayers().get(1);
        Player cpu2 = board.getPlayers().get(2);
        
        for(Player p : board.getPlayers()) {
        	p.getHand().clear();
        }
        
        human.updateHand(weapon1);

        Card result = board.handleSuggestion(person2, room2, weapon1, cpu1);
        assertEquals(weapon1, result);
    }

    @Test
    public void testSuggestionOnlySuggestingPlayerCanDisprove() {
        Player suggestingPlayer = board.getPlayers().get(0); // Inspector Slate

        for(Player p : board.getPlayers()) {
        	p.getHand().clear();
        }
        
        suggestingPlayer.updateHand(person1);
        Card result = board.handleSuggestion(person1, room2, weapon2, suggestingPlayer);
        assertNull(result);
    }
    
    @Test
    public void testSuggestionTwoPlayersCanDisprove() {

        List<Player> players = board.getPlayers();
        Player suggestor = players.get(0);
        Player p1 = players.get(1);
        Player p2 = players.get(2);

        p1.updateHand(weapon1);
        p2.updateHand(weapon1);

        Card result = board.handleSuggestion(person2, room2, weapon1, suggestor);
        assertEquals(weapon1, result);
    }
    
}