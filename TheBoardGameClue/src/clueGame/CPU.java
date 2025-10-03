package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CPU extends Player {

    private boolean shouldMakeAccusation = false;
    private Solution lastSuggestion = null;
	
	public CPU(String name, Color color, int row, int col) {
		super(name, color, row, col);
		// TODO Auto-generated constructor stub
	}
	
    public boolean shouldMakeAccusation() {
        return shouldMakeAccusation;
    }

    public void setShouldMakeAccusation(boolean flag) {
        shouldMakeAccusation = flag;
    }

    public Solution getLastSuggestion() {
        return lastSuggestion;
    }

    public void setLastSuggestion(Solution suggestion) {
        lastSuggestion = suggestion;
    }
    
	
	/**
	 * Create a suggestion for the given room
	 * @param current location
	 * @return  Solution 
	 */
	public Solution createSuggestion(Card room) {
		Board board = Board.getInstance();
		List<Card> deck = board.getDeck();
		
		// unseen person cards
		List<Card> unseenPersons = new ArrayList<>();
		for (Card card : deck) {
			if (card.getCardType() == CardType.PERSON && !getSeenCards().containsKey(card)) {
				unseenPersons.add(card);
			}
		}
		
		// unseen weapon cards
		List<Card> unseenWeapons = new ArrayList<>();
		for (Card card : deck) {
			if (card.getCardType() == CardType.WEAPON && !getSeenCards().containsKey(card)) {
				unseenWeapons.add(card);
			}
		}
		
		// Random from unseen cards
		Card selectedPerson = unseenPersons.get((int) (Math.random() * unseenPersons.size()));
		Card selectedWeapon = unseenWeapons.get((int) (Math.random() * unseenWeapons.size()));
		
		return new Solution(selectedPerson, selectedWeapon, room);
	}
	
	/**
	 * Find a room card by name
	 * @param roomName 
	 * @return corresponding Card or null 
	 */
	public Card findRoomCard(String roomName) {
	    Board board = Board.getInstance();
	    for (Card card : board.getDeck()) {
	        if (card.getCardType() == CardType.ROOM && card.getCardName().equals(roomName)) {
	            return card;
	        }
	    }
	    return null;
	}
	
	/**
	 * @param targets the set of possible target cells
	 * @return selected target cell
	 */
	public BoardCell selectTarget(Set<BoardCell> targets) {
		Board board = Board.getInstance();
		List<BoardCell> roomTargets = new ArrayList<>();
		List<BoardCell> unseenRoomTargets = new ArrayList<>();
		
		
		for (BoardCell target : targets) {
			if (target.isRoomCenter()) {
				roomTargets.add(target);
				
				// Check if room has been seen
				Room room = board.getRoom(target);
				Card roomCard = findRoomCard(room.getName());
				if (roomCard != null && !getSeenCards().containsKey(roomCard)) {
					unseenRoomTargets.add(target);
				}
			}
		}
		
		// If there are unseen rooms, select one randomly
		if (!unseenRoomTargets.isEmpty()) {
			int randomIndex = (int) (Math.random() * unseenRoomTargets.size());
			return unseenRoomTargets.get(randomIndex);
		}
		
		// Otherwise, select any target randomly
		List<BoardCell> allTargets = new ArrayList<>(targets);
		int randomIndex = (int) (Math.random() * allTargets.size());
		return allTargets.get(randomIndex);
	}

	@Override
	public boolean isHuman() {
		return false;
	}
}
