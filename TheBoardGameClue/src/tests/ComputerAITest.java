package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.*;

/**
 * Tests createSuggestion() and selectTarget() methods
 */
public class ComputerAITest {
    private static Board board;
    private static CPU testPlayer;
    
    // Sample cards 
    private static Card labCard, vaultCard, galleryCard;
    private static Card umberCard, marigoldCard, beaumontCard;
    private static Card knifeCard, revolverCard, poisonCard;
    
    @BeforeAll
    public static void setUp() {
        board = Board.getInstance();
        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
        board.initialize();
        
        labCard = new Card("Laboratory", CardType.ROOM);
        vaultCard = new Card("Vault", CardType.ROOM);
        galleryCard = new Card("Art Gallery", CardType.ROOM);

        umberCard = new Card("Professor Umber", CardType.PERSON);
        marigoldCard = new Card("Lady Marigold", CardType.PERSON);
        beaumontCard = new Card("Chef Beaumont", CardType.PERSON);

        knifeCard = new Card("Knife", CardType.WEAPON);
        revolverCard = new Card("Revolver", CardType.WEAPON);
        poisonCard = new Card("Poison", CardType.WEAPON);
    }
    
    @BeforeEach
    public void setUpEach() {
        testPlayer = new CPU("Test Player", Color.RED, 1, 1);
    }
    
    @Test
    public void testCreateSuggestion_RoomMatches() {
        // Test that suggestion uses the given room
        Solution suggestion = testPlayer.createSuggestion(labCard);
        assertEquals(labCard, suggestion.getRoom(), "Suggestion should use the provided room");
    }
    
    @Test
    public void testCreateSuggestion_OnlyOneWeaponNotSeen() {
        // Get weapon cards 
        List<Card> deck = board.getDeck();
        Card unseenWeapon = null;
        
        // Get all weapon cards and mark all but one as seen
        for (Card card : deck) {
            if (card.getCardType() == CardType.WEAPON) {
                if (unseenWeapon == null) {
                    unseenWeapon = card; 
                } else {
                    testPlayer.updateSeen(card); 
                }
            }
        }
        
        assertNotNull(unseenWeapon, "Should have at least one weapon in deck");
        
        Solution suggestion = testPlayer.createSuggestion(vaultCard);
        assertEquals(unseenWeapon, suggestion.getWeapon(), 
            "Should select the only unseen weapon");
    }
    
    @Test 
    public void testCreateSuggestion_OnlyOnePersonNotSeen() {
        // Actual person cards from the deck
        List<Card> deck = board.getDeck();
        Card unseenPerson = null;
        
        // Get person cards and mark all but one as seen
        for (Card card : deck) {
            if (card.getCardType() == CardType.PERSON) {
                if (unseenPerson == null) {
                    unseenPerson = card;
                } else {
                    testPlayer.updateSeen(card);
                }
            }
        }
        
        assertNotNull(unseenPerson, "Should have at least one person in deck");
        
        Solution suggestion = testPlayer.createSuggestion(galleryCard);
        assertEquals(unseenPerson, suggestion.getPerson(),
            "Should select the only unseen person");
    }
    
    @Test
    public void testCreateSuggestion_MultipleWeaponsRandom() {
   
        List<Card> deck = board.getDeck();
        Card seenWeapon = null;
        Card unseenWeapon1 = null;
        Card unseenWeapon2 = null;
        
        // Find weapons and mark only one as seen
        for (Card card : deck) {
            if (card.getCardType() == CardType.WEAPON) {
                if (seenWeapon == null) {
                    seenWeapon = card;
                    testPlayer.updateSeen(card); 
                } else if (unseenWeapon1 == null) {
                    unseenWeapon1 = card; 
                } else if (unseenWeapon2 == null) {
                    unseenWeapon2 = card; 
                    break; 
                }
            }
        }
        
        // Need 2 unseen weapons for test
        if (unseenWeapon1 == null || unseenWeapon2 == null) {
            return; 
        }
        
        boolean sawWeapon1 = false;
        boolean sawWeapon2 = false;
        
        for (int i = 0; i < 50; i++) {
            Solution suggestion = testPlayer.createSuggestion(labCard);
            Card selectedWeapon = suggestion.getWeapon();
            
            if (selectedWeapon.equals(unseenWeapon1)) sawWeapon1 = true;
            if (selectedWeapon.equals(unseenWeapon2)) sawWeapon2 = true;
            
            assertNotEquals(seenWeapon, selectedWeapon, "Should not select seen weapon");
            
            if (sawWeapon1 && sawWeapon2) break;
        }
        assertTrue(sawWeapon1 && sawWeapon2, 
            "Should randomly select from multiple unseen weapons");
    }
    
    @Test
    public void testCreateSuggestion_MultiplePersonsRandom() {
        // Get person cards from deck
        List<Card> deck = board.getDeck();
        Card seenPerson = null;
        Card unseenPerson1 = null;
        Card unseenPerson2 = null;
        
        // Find persons and mark only one as seen
        for (Card card : deck) {
            if (card.getCardType() == CardType.PERSON) {
                if (seenPerson == null) {
                    seenPerson = card;
                    testPlayer.updateSeen(card);
                } else if (unseenPerson1 == null) {
                    unseenPerson1 = card; 
                } else if (unseenPerson2 == null) {
                    unseenPerson2 = card; 
                    break;
                }
            }
        }
        
        if (unseenPerson1 == null || unseenPerson2 == null) {
            return; 
        }
        
        boolean sawPerson1 = false;
        boolean sawPerson2 = false;
        
        for (int i = 0; i < 50; i++) {
            Solution suggestion = testPlayer.createSuggestion(vaultCard);
            Card selectedPerson = suggestion.getPerson();
            
            if (selectedPerson.equals(unseenPerson1)) sawPerson1 = true;
            if (selectedPerson.equals(unseenPerson2)) sawPerson2 = true;
            
          
            assertNotEquals(seenPerson, selectedPerson, "Should not select seen person");
            
            if (sawPerson1 && sawPerson2) break;
        }
        
        assertTrue(sawPerson1 && sawPerson2,
            "Should randomly select from multiple unseen persons");
    }
    
    
    
    @Test
    public void testSelectTarget_NoRoomsInList() {
        // Create targets only walkways
        Set<BoardCell> targets = new HashSet<>();
        
        // Find walkway 
        BoardCell walkway1 = null, walkway2 = null, walkway3 = null;
        for (int r = 0; r < board.getNumRows() && walkway3 == null; r++) {
            for (int c = 0; c < board.getNumColumns() && walkway3 == null; c++) {
                BoardCell cell = board.getCell(r, c);
                if (cell.getInitial() == 'W') { // Walkway
                    if (walkway1 == null) walkway1 = cell;
                    else if (walkway2 == null) walkway2 = cell;
                    else walkway3 = cell;
                }
            }
        }
        
        if (walkway1 != null) targets.add(walkway1);
        if (walkway2 != null) targets.add(walkway2);
        if (walkway3 != null) targets.add(walkway3);
        
        assertTrue(targets.size() >= 2, "Should have at least 2 walkway targets for testing");
        
        boolean sawDifferentTargets = false;
        BoardCell firstSelection = testPlayer.selectTarget(targets);
        
        // Test random over calls
        for (int i = 0; i < 10; i++) {
            BoardCell selected = testPlayer.selectTarget(targets);
            assertTrue(targets.contains(selected), "Selected target should be from target list");
            
            if (!selected.equals(firstSelection)) {
                sawDifferentTargets = true;
                break;
            }
        }
        
        // random should see some variety
        assertTrue(targets.contains(firstSelection), "All selections should be from target list");
    }
    
    @Test
    public void testSelectTarget_UnseenRoom() {
        // Find a room center in board
        BoardCell roomCenter = null;
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumColumns(); c++) {
                BoardCell cell = board.getCell(r, c);
                if (cell.isRoomCenter()) {
                    roomCenter = cell;
                    break;
                }
            }
            if (roomCenter != null) break;
        }
        
        assertNotNull(roomCenter, "Should find a room center for testing");
        
        // Find walkway cell
        BoardCell walkway = null;
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumColumns(); c++) {
                BoardCell cell = board.getCell(r, c);
                if (cell.getInitial() == 'W') {
                    walkway = cell;
                    break;
                }
            }
            if (walkway != null) break;
        }
        
        // Create targets with room center and walkway
        Set<BoardCell> targets = new HashSet<>();
        targets.add(roomCenter);
        if (walkway != null) targets.add(walkway);
        
        BoardCell selected = testPlayer.selectTarget(targets);
        assertEquals(roomCenter, selected, "Should select unseen room over walkway");
    }
    
    @Test
    public void testSelectTarget_SeenRoomRandomSelection() {
        // Find room center
        BoardCell roomCenter = null;
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumColumns(); c++) {
                BoardCell cell = board.getCell(r, c);
                if (cell.isRoomCenter()) {
                    roomCenter = cell;
                    break;
                }
            }
            if (roomCenter != null) break;
        } 
        assertNotNull(roomCenter, "Should find a room center for testing");
        
        // Add room to seen cards
        Room room = board.getRoom(roomCenter);
        Card roomCard = new Card(room.getName(), CardType.ROOM);
        testPlayer.updateSeen(roomCard);
        
        BoardCell walkway1 = null, walkway2 = null;
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumColumns(); c++) {
                BoardCell cell = board.getCell(r, c);
                if (cell.getInitial() == 'W') {
                    if (walkway1 == null) walkway1 = cell;
                    else if (walkway2 == null) {
                        walkway2 = cell;
                        break;
                    }
                }
            }
            if (walkway2 != null) break;
        }
        
        // Create targets
        Set<BoardCell> targets = new HashSet<>();
        targets.add(roomCenter);
        if (walkway1 != null) targets.add(walkway1);
        if (walkway2 != null) targets.add(walkway2);
        
        assertTrue(targets.size() >= 2, "Should have multiple targets for randomness test");
        
        boolean sawRoom = false;
        boolean sawWalkway = false;
        
        // Test that selection is now random (includes seen room)
        for (int i = 0; i < 30; i++) {
            BoardCell selected = testPlayer.selectTarget(targets);
            assertTrue(targets.contains(selected), "Selected target should be from list");
            
            if (selected.equals(roomCenter)) sawRoom = true;
            if (!selected.equals(roomCenter)) sawWalkway = true;
            
            if (sawRoom && sawWalkway) break;
        }
        
        assertTrue(sawRoom && sawWalkway, 
            "Should randomly select all targets when room is seen");
    }
}