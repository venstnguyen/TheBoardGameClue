package clueGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * AccusationDialog 
 * @author Benny Hoang
 * @author Steven Nguyen
 * 
 * Purpose: Dialog for player to make accusations
 */
public class AccusationDialog extends JDialog {
    
    private JComboBox<String> personCombo;
    private JComboBox<String> weaponCombo;
    private JComboBox<String> roomCombo;
    private JButton submitButton;
    private JButton cancelButton;
    
    private Solution accusation;
    private boolean cancelled = true;
    
    public AccusationDialog(JFrame parent) {
        super(parent, "Make an Accusation", true); 
        
        setupDialog();
        populateComboBoxes();
        setupButtons();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void setupDialog() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Room selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Room:"), gbc);
        
        roomCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(roomCombo, gbc);
        
        // Person selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Person:"), gbc);
        
        personCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(personCombo, gbc);
        
        // Weapon selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Weapon:"), gbc);
        
        weaponCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(weaponCombo, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonPanel, gbc);
    }
    
    private void populateComboBoxes() {
        Board board = Board.getInstance();
        
        // rooms
        for (Card card : board.getDeck()) {
            if (card.getCardType() == CardType.ROOM) {
                roomCombo.addItem(card.getCardName());
            }
        }
        
        // persons
        for (Card card : board.getDeck()) {
            if (card.getCardType() == CardType.PERSON) {
                personCombo.addItem(card.getCardName());
            }
        }
        
        // weapons
        for (Card card : board.getDeck()) {
            if (card.getCardType() == CardType.WEAPON) {
                weaponCombo.addItem(card.getCardName());
            }
        }
    }
    
    private void setupButtons() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // make solution
                String selectedRoom = (String) roomCombo.getSelectedItem();
                String selectedPerson = (String) personCombo.getSelectedItem();
                String selectedWeapon = (String) weaponCombo.getSelectedItem();
                
                // Find corresponding cards
                Board board = Board.getInstance();
                Card roomCard = findCardByName(selectedRoom, CardType.ROOM);
                Card personCard = findCardByName(selectedPerson, CardType.PERSON);
                Card weaponCard = findCardByName(selectedWeapon, CardType.WEAPON);
                
                accusation = new Solution(personCard, weaponCard, roomCard);
                cancelled = false;
                setVisible(false);
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                setVisible(false);
            }
        });
    }
    
    private Card findCardByName(String name, CardType type) {
        Board board = Board.getInstance();
        for (Card card : board.getDeck()) {
            if (card.getCardName().equals(name) && card.getCardType() == type) {
                return card;
            }
        }
        return null;
    }
    
    public Solution getAccusation() {
        return accusation;
    }
    
    public boolean wasCancelled() {
        return cancelled;
    }
}