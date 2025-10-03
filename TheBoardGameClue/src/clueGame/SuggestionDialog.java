package clueGame;

import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class SuggestionDialog extends JDialog {
	
    private JComboBox<Card> personBox;
    private JComboBox<Card> weaponBox;
    private Card roomCard;
    private boolean submitted = false;

    public SuggestionDialog(Frame parent, Card roomCard, List<Card> people, List<Card> weapons) {
        super(parent, "Make a Suggestion", true);
        this.roomCard = roomCard;

        personBox = new JComboBox<>(people.toArray(new Card[0]));
        weaponBox = new JComboBox<>(weapons.toArray(new Card[0]));

        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            submitted = true;
            setVisible(false);
        });

        setLayout(new GridLayout(0, 2));
        add(new JLabel("Room:")); add(new JLabel(roomCard.getCardName()));
        add(new JLabel("Person:")); add(personBox);
        add(new JLabel("Weapon:")); add(weaponBox);
        add(submit);
        pack();
    }

    public Solution getSuggestion() {
        if (!submitted) return null;
        return new Solution(roomCard, (Card)personBox.getSelectedItem(), (Card)weaponBox.getSelectedItem());
    }
    
}
