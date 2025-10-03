package clueGame;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class KnownCardsPanel extends JPanel {

    private final JPanel handPanel = new JPanel();

    // Seen panels
    private final static JPanel seenContainerPanel = new JPanel(new GridLayout(3, 1));
    private final static JPanel seenPeoplePanel = new JPanel(new GridLayout(0, 1));
    private final static JPanel seenWeaponsPanel = new JPanel(new GridLayout(0, 1));
    private final static JPanel seenRoomsPanel = new JPanel(new GridLayout(0, 1));

    public KnownCardsPanel() {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(250, 600));
        // Setup hand panel
        handPanel.setBorder(new TitledBorder("In Hand"));
        handPanel.setLayout(new GridLayout(0, 1));
        add(handPanel);

        // Setup seen panel container
        seenPeoplePanel.setBorder(new TitledBorder("Seen People"));
        seenWeaponsPanel.setBorder(new TitledBorder("Seen Weapons"));
        seenRoomsPanel.setBorder(new TitledBorder("Seen Rooms"));

        seenContainerPanel.add(seenPeoplePanel);
        seenContainerPanel.add(seenWeaponsPanel);
        seenContainerPanel.add(seenRoomsPanel);

        add(seenContainerPanel);
    }

    public void setHand(List<Card> hand, Color humanColor) {
        handPanel.removeAll();

        if (hand != null && !hand.isEmpty()) {
            addCardsByType(handPanel, hand, CardType.PERSON, "People:", humanColor);
            addCardsByType(handPanel, hand, CardType.ROOM, "Rooms:", humanColor);
            addCardsByType(handPanel, hand, CardType.WEAPON, "Weapons:", humanColor);
        } else {
            JLabel noCardsLabel = new JLabel("No cards in hand");
            noCardsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            handPanel.add(noCardsLabel);
        }

        handPanel.revalidate();
        handPanel.repaint();
    }

    private void addCardsByType(JPanel panel, List<Card> cards, CardType type, String header, Color backgroundColor) {
        boolean hasCardsOfType = cards.stream().anyMatch(card -> card.getCardType() == type);
        if (hasCardsOfType) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
            panel.add(headerLabel);

            for (Card card : cards) {
                if (card.getCardType() == type) {
                    JTextField cardField = new JTextField(card.getCardName());
                    cardField.setEditable(false);
                    cardField.setBackground(backgroundColor != null ? backgroundColor : Color.WHITE);
                    panel.add(cardField);
                }
            }
        }
    }

    public void setSeen(Map<Card, Player> seenMap) {
        // Clear previous cards
        seenPeoplePanel.removeAll();
        seenWeaponsPanel.removeAll();
        seenRoomsPanel.removeAll();

        for (Map.Entry<Card, Player> entry : seenMap.entrySet()) {
            Card card = entry.getKey();
            Player holder = entry.getValue();
            JTextField cardField = new JTextField(card.getCardName());
            cardField.setEditable(false);
            cardField.setBackground(holder != null ? holder.getColor() : Color.LIGHT_GRAY);

            switch (card.getCardType()) {
                case PERSON -> seenPeoplePanel.add(cardField);
                case WEAPON -> seenWeaponsPanel.add(cardField);
                case ROOM -> seenRoomsPanel.add(cardField);
            }
        }

        seenContainerPanel.revalidate();
        seenContainerPanel.repaint();
    }

    public void updateCards(List<Card> hand, Map<Card, Player> seen, Color humanColor) {
        setHand(hand, humanColor);
        setSeen(seen);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Known Cards Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        KnownCardsPanel panel = new KnownCardsPanel();

        frame.add(panel);
        frame.setSize(350, 700);
        frame.setVisible(true);

        Color humanColor = Color.GREEN;

        List<Card> hand = List.of(
            new Card("Inspector Slate", CardType.PERSON),
            new Card("Knife", CardType.WEAPON),
            new Card("Laboratory", CardType.ROOM)
        );

        Map<Card, Player> seen = Map.of(
                new Card("Lady Marigold", CardType.PERSON), new CPU("Lady Marigold", Color.ORANGE, 0, 0),
                new Card("Dr. Crane", CardType.PERSON), new CPU("Dr. Crane", Color.BLUE, 0, 0),
                new Card("Reverend Rouge", CardType.PERSON), new CPU("Reverend Rouge", Color.RED, 0, 0),
                new Card("Bat", CardType.WEAPON), new CPU("Chef Beaumont", Color.MAGENTA, 0, 0),
                new Card("Iron Pan", CardType.WEAPON), new CPU("Professor Umber", Color.YELLOW, 0, 0),
                new Card("Hammer", CardType.WEAPON), new CPU("Lady Marigold", Color.ORANGE, 0, 0),
                new Card("Theatre", CardType.ROOM), new CPU("Dr. Crane", Color.BLUE, 0, 0),
                new Card("Vault", CardType.ROOM), new CPU("Chef Beaumont", Color.MAGENTA, 0, 0),
                new Card("Garage", CardType.ROOM), new CPU("Reverend Rouge", Color.RED, 0, 0)
            );
        
        panel.updateCards(hand, seen, humanColor);
    }
}

