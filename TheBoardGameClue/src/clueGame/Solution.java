package clueGame;

// Basic Setup
public class Solution {
    final Card person;
    final Card weapon;
    final Card room;

    public Solution(Card person, Card weapon, Card room) {
        this.person = person;
        this.weapon = weapon;
        this.room = room;
    }

    public Card getPerson() {
        return person;
    }

    public Card getWeapon() {
        return weapon;
    }

    public Card getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Solution)) return false;
        Solution other = (Solution) obj;
        return person.equals(other.person) && weapon.equals(other.weapon) && room.equals(other.room);
    }

    @Override
    public int hashCode() {
        return person.hashCode() ^ weapon.hashCode() ^ room.hashCode();
    }

    @Override
    public String toString() {
        return person + ", " + weapon + ", " + room;
    }
}