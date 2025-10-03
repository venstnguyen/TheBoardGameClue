package clueGame;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * GameControlPanel
 *
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
**/

public class GameControlPanel extends JPanel {

    private JTextField rollField;
    private JTextField turnField;
    private JTextField guessField;
    private JTextField resultField;

    private final JButton nextPlayerButton = new JButton("Next Player");
    private final JButton accusationButton = new JButton("Make Accusation");
    private final Board board = Board.getInstance();

    public GameControlPanel() {
        setLayout(new GridLayout(2, 1));

        // top row: whose turn, buttons
        JPanel top = new JPanel(new GridLayout(1, 4));
        
        JPanel turnPanel = new JPanel();
        turnPanel.add(new JLabel("Whose turn?"));
        turnField = new JTextField(10);
        turnField.setEditable(false);
        turnPanel.add(turnField);
        top.add(turnPanel);
        
        JPanel rollPanel = new JPanel();
        rollPanel.add(new JLabel("Roll:"));
        rollField = new JTextField(4);
        rollField.setEditable(false);
        rollPanel.add(rollField);
        top.add(rollPanel);
        
        top.add(accusationButton);
        top.add(nextPlayerButton);
        add(top);

        // bottom row: roll, guess, result
        JPanel bottom = new JPanel(new GridLayout(1, 2));
        
        JPanel guessPanel = new JPanel();
        guessPanel.setBorder(new TitledBorder("Guess"));
        guessField = new JTextField(25);
        guessField.setEditable(false);
        guessPanel.add(guessField);
        bottom.add(guessPanel);
        
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder("Guess"));
        resultField = new JTextField(25);
        resultField.setEditable(false);
        resultPanel.add(resultField);
        bottom.add(resultPanel);        

        add(bottom);
        
        //C24A logic for game flow
        nextPlayerButton.addActionListener(e -> {
            if (board.isAwaitingHumanMove()) {
                JOptionPane.showMessageDialog(board, "Finish your move first.");
                return;
            }
            Player next = board.advanceToNextPlayer();
            int roll = rollDie();
            setTurn(next, roll);
            setGuess("");
            setGuessResult("");
            board.startPlayerTurn(next, roll);
        });
        
        accusationButton.addActionListener(e -> {
            Player currentPlayer = board.getCurrentPlayer();
            
            // Check player's turn
            if (currentPlayer == null || !currentPlayer.isHuman()) {
                JOptionPane.showMessageDialog(this, 
                    "You can only make an accusation on your turn!", 
                    "Invalid Action", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if player is moving
            if (board.isAwaitingHumanMove()) {
                JOptionPane.showMessageDialog(this,
                    "You must finish your move before making an accusation!",
                    "Invalid Action",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // accusation dialog
            JFrame parentFrame = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
            AccusationDialog dialog = new AccusationDialog(parentFrame);
            dialog.setVisible(true);
            
            // Process accusation
            if (!dialog.wasCancelled()) {
                Solution accusation = dialog.getAccusation();
                boolean isCorrect = board.checkAccusation(accusation);
                
                String accusationText = String.format("%s, %s, %s", 
                    accusation.getPerson().getCardName(),
                    accusation.getRoom().getCardName(),
                    accusation.getWeapon().getCardName());
                
                String resultMessage;
                String dialogTitle;
                
                if (isCorrect) {
                    resultMessage = String.format("Congratulations!\n\nYour accusation was correct:\n%s\n\nYou win the game!", 
                        accusationText);
                    dialogTitle = "You Win!";
                } else {
                    resultMessage = String.format("Sorry!\n\nYour accusation was incorrect:\n%s\n\nYou lose the game!", 
                        accusationText);
                    dialogTitle = "You Lose!";
                }
                
                // Show result
                JOptionPane.showMessageDialog(this, 
                    resultMessage, 
                    dialogTitle, 
                    isCorrect ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
               
                setGuess("Accusation: " + accusationText);
                setGuessResult(isCorrect ? "CORRECT - You Win!" : "INCORRECT - You Lose!");            
                nextPlayerButton.setEnabled(false);
                accusationButton.setEnabled(false);
            }
        });
    }

    /// helper for dice
    public int rollDie() {
        return (int) (Math.random() * 6) + 1;
    }

    /// --------- setters ---------
    public void setTurn(Player p, int roll) {
        turnField.setText(p.getName());
        rollField.setText(Integer.toString(roll));
    }

    public void setGuess(String guess) {
        guessField.setText(guess);
    }

    public void setGuessResult(String result) {
        resultField.setText(result);
    }

    /// --------- getters ---------
    public JButton getNextPlayerButton() {
        return nextPlayerButton;
    }

    public JButton getAccusationButton() {
        return accusationButton;
    }

    public String getGuess() {
        return guessField.getText();
    }

    public String getGuessResult() {
        return resultField.getText();
    }

    public int getRoll() {
        try {
            return Integer.parseInt(rollField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public static void main(String[] args) {

        GameControlPanel panel = new GameControlPanel();


        JFrame frame = new JFrame("Clue Game - Control Panel");
        frame.setContentPane(panel);
        frame.setSize(750, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        Player currentPlayer = new Human("Inspector Slate",  Color.green, 11, 19);
        int diceRoll = 5;
        panel.setTurn(currentPlayer, diceRoll);

        String guess = "Reverend Rouge in the Bathroom with the Poison";
        panel.setGuess(guess);

        String guessResult = "Dr. Crane shows the Poison";
        panel.setGuessResult(guessResult);
    }
    
}