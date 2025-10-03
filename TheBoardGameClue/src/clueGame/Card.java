package clueGame;
/*
 * Card class
 * This represents a card with its name and type for the clue game
 * 
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
 */

public class Card {
	private String cardName;
	private CardType cardType;
	
	public Card(String cardName, CardType cardType) {
		this.cardName = cardName;
		this.cardType = cardType;
	}
	
	/*
	 * Card comparison
	 */
	
	public boolean equals(Card target) {
		if (target == null) {
			return false;
		}
		return this.cardName.equals(target.cardName) && this.cardType == target.cardType;
	}
	
	
	/*
	 * Override for object comparison
	 */
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
	
		if(obj == null || getClass() != obj.getClass())
				return false;
		
		Card card = (Card) obj;
		return equals(card);
	}
	
	/*
	 * Override hash for hash collections usage
	 */
	
	@Override
	public int hashCode() {
		return cardName.hashCode() + cardType.hashCode();
	}
	
	/*
	 * Getters for CardName and CardType
	 */
	
	public String getCardName() {
		return cardName;
	}
	
	public CardType getCardType() {
		return cardType;
	}
	
	public String toString() {
		return cardName + "(" + cardType + ")";
	}
	
	
}
