package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public abstract class Player {
	
	private String name;
	private Color color;
	private int row;
	private int col;
	private boolean movedBySuggestion = false;
	private List<Card> hand = new ArrayList<>();
	private Map<Card, Player> seenCards = new HashMap<>();
	
	public Player(String name, Color color, int row, int col) {
		super();
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
	}
	
	public abstract boolean isHuman();
	
	/**
	 * Draw the player on the board
	 */
	public void draw(Graphics g, int x, int y, int cellWidth, int cellHeight) {
	    // Getting player circle size 
	    int playerSize = Math.min(cellWidth, cellHeight) - 4;
	    int offsetX = (cellWidth - playerSize) / 2;
	    int offsetY = (cellHeight - playerSize) / 2;
	    
	    // Setting player
	    g.setColor(color);
	    g.fillOval(x + offsetX, y + offsetY, playerSize, playerSize);
	    g.setColor(Color.BLACK);
	    g.drawOval(x + offsetX, y + offsetY, playerSize, playerSize);
	}
	
	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public boolean wasMovedBySuggestionLastTurn() {
	    return movedBySuggestion;
	}
	public void setMovedBySuggestionLastTurn(boolean value) {
	    this.movedBySuggestion = value;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setLocation(int row, int col) { /// NEW
		this.row = row;
		this.col = col;
	}

	public void updateHand(Card card) {
		hand.add(card); 
	}
	
	public Map<Card, Player> getSeenCards() {
		return seenCards;
	}
	
	public void updateSeen(Card seenCard) {
		seenCards.put(seenCard, null);
	}
	
	public void addSeenCard(Card card, Player holder) {
		seenCards.put(card, holder);

	}
	
	public void giveCard(Card card) {
		hand.add(card);
		seenCards.put(card, this);
	}
	
	public Card disproveSuggestion(Card person, Card room, Card weapon) {
		List<Card> matches = new ArrayList<>();

		for (Card c : hand) {
			if (c.equals(person) || c.equals(room) || c.equals(weapon)) {
				matches.add(c);
			}
		}

		if (matches.isEmpty()) return null;

		Random rand = new Random();
		return matches.get(rand.nextInt(matches.size()));
	}
	
}

