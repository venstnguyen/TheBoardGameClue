package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.CPU;
import clueGame.Human;
import clueGame.Player;
import java.util.Set;
import java.util.HashSet;
import clueGame.Card;
import clueGame.CardType;
import clueGame.Solution;

class GameSetupTests {
	private static Board board;
	
	@BeforeAll
	public static void setup() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}
	
	@Test
	public void testPlayersloaded() {
        List<Player> players = board.getPlayers();
        assertEquals(6, players.size());
        
        Player human = players.get(0);
        assertTrue(players.get(0) instanceof Human);
        assertEquals("Inspector Slate", human.getName());
        assertEquals(11, human.getRow());
        assertEquals(24, human.getCol());

        for (int i = 1; i < players.size(); i++) {
            assertTrue(players.get(i) instanceof CPU);
        }
	}
	
	@Test
	public void testDeckCreation() {
	    List<Card> deck = board.getDeck();
	    
	    assertEquals(21, deck.size(), "Deck should contain exactly 21 cards");
	    
	    // Count cards by type
	    int roomCards = 0, personCards = 0, weaponCards = 0;
	    for (Card card : deck) {
	        switch (card.getCardType()) {
	            case ROOM:
	                roomCards++;
	                break;
	            case PERSON:
	                personCards++;
	                break;
	            case WEAPON:
	                weaponCards++;
	                break;
	        }
	    }
	    
	    assertEquals(9, roomCards, "Should have 9 room cards");
	    assertEquals(6, personCards, "Should have 6 person cards");
	    assertEquals(6, weaponCards, "Should have 6 weapon cards");
	}

	@Test
	public void testWeaponsLoaded() {
	    List<String> weapons = board.getWeapons();
	    assertEquals(6, weapons.size(), "Should load exactly 6 weapons");
	    
	    // Test for specific weapons
	    assertTrue(weapons.contains("Knife"), "Should contain Knife");
	    assertTrue(weapons.contains("Revolver"), "Should contain Revolver");
	    assertTrue(weapons.contains("Poison"), "Should contain Poison");
	    assertTrue(weapons.contains("Iron Pan"), "Should contain Iron Pan");
	    assertTrue(weapons.contains("Bat"), "Should contain Bat");
	    assertTrue(weapons.contains("Hammer"), "Should contain Hammer");
	}

	@Test
	public void testSolutionCreated() {
	    Solution solution = board.getSolution();
	    assertNotNull(solution, "Solution should be created");
	    assertNotNull(solution.getRoom(), "Solution should have a room card");
	    assertNotNull(solution.getPerson(), "Solution should have a person card");
	    assertNotNull(solution.getWeapon(), "Solution should have a weapon card");

	}

	@Test
	public void testCardsDealtProperly() {
	    List<Player> players = board.getPlayers();
	    Solution solution = board.getSolution();
	    List<Card> deck = board.getDeck();
	    
	    // Calculate total cards dealt to players
	    int totalCardsDealt = 0;
	    for (Player player : players) {
	        totalCardsDealt += player.getHand().size();
	    }
	    
	    assertEquals(18, totalCardsDealt, "All non-solution cards should be dealt to players");
	    
	    for (Player player : players) {
	        assertEquals(3, player.getHand().size(), "Each player should have exactly 3 cards");
	    }
	}

	@Test
	public void testNoDuplicateCards() {
	    List<Player> players = board.getPlayers();
	    Solution solution = board.getSolution();
	    Set<Card> allDealtCards = new HashSet<>();
	    
	    // Collect cards dealt to players
	    for (Player player : players) {
	        for (Card card : player.getHand()) {
	            assertFalse(allDealtCards.contains(card), 
	                "Card " + card.getCardName() + " was dealt to multiple players");
	            allDealtCards.add(card);
	        }
	    }
	    
	    //  Solution cards arent in a player's hand
	    assertFalse(allDealtCards.contains(solution.getRoom()), 
	        "Solution room card should not be in any player's hand");
	    assertFalse(allDealtCards.contains(solution.getPerson()), 
	        "Solution person card should not be in any player's hand");
	    assertFalse(allDealtCards.contains(solution.getWeapon()), 
	        "Solution weapon card should not be in any player's hand");
	}

	@Test
	public void testCardEquality() {
	    // Test Card equals method
	    Card card1 = new Card("Study", CardType.ROOM);
	    Card card2 = new Card("Study", CardType.ROOM);
	    Card card3 = new Card("Library", CardType.ROOM);
	    Card card4 = new Card("Study", CardType.PERSON);
	    
	    assertTrue(card1.equals(card2), "Cards with same name and type should be equal");
	    assertFalse(card1.equals(card3), "Cards with different names should not be equal");
	    assertFalse(card1.equals(card4), "Cards with different types should not be equal");
	    assertFalse(card1.equals(null), "Card should not equal null");
	}
	
}
