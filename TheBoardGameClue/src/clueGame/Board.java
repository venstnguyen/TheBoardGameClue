package clueGame;

/**
 * Board 
 *  
 * @author Benny Hoang
 * @author William O'Byrne
 * @author Steven Nguyen
 * 
 * Purpose: Creation of the board
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.awt.Color;
import java.util.Collections;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Board extends JPanel {
	
	private static Board instance = new Board();
	
	//for the Board
	private BoardCell[][] grid;
	private int numRows;
	private int numColumns;
	private List<Player> Player;
	private List<String> weapons;
	
	//for getting files
	private String layoutConfigFile;
	private String setupConfigFile;
	
	
	//To manage room
	private Map<Character, Room> roomMap;
	private Map<String, Character> roomNameToInitial = new HashMap<>();
	
	// For target calculation
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;
	
	// Folder path name
	private static final String DATA_FOLDER = "data/";
	
	// Player setup
	private List<Player> players = new ArrayList<>();
	private List<Card> deck = new ArrayList<>();
	private Solution theAnswer;
	int currentPlayerIndex = -1;
	private boolean awaitingHumanMove = false; /// NEW
	private Human humanPlayer; /// NEW
	private JFrame parentFrame;
	private GameControlPanel controlPanel;
	private KnownCardsPanel knownCardsPanel;

	private Board() {
		super();
		roomMap = new HashMap<>();
		targets = new HashSet<>();
		visited = new HashSet<>();
		weapons = new ArrayList<>();
		addMouseListener(new MouseAdapter() { /// New
			@Override
			public void mouseClicked(MouseEvent e) {
				handleClick(e.getX(), e.getY());
			}
		});

	}
	
	// Getting the instance of the Board
	public static Board getInstance() {
		return instance;
	}
	
	//Setting up config files
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = DATA_FOLDER + layoutConfigFile;
		this.setupConfigFile = DATA_FOLDER + setupConfigFile;
	}
	
	//Loading the setup Config file
	public void loadSetupConfig() throws BadConfigFormatException, FileNotFoundException{
		roomMap.clear();
		players.clear();
		weapons.clear();
		deck.clear();
		
		try(Scanner scanner = new Scanner(new FileReader(setupConfigFile))){
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				
				if(line.isEmpty() || line.startsWith("//")) {
					continue;
				}
				
				String[] parts = line.split(", ");
				if (parts.length < 2) {
					throw new BadConfigFormatException("Invalid setup - Line: " + line);
				}
				
				String type = parts[0].trim();
				String name = parts[1].trim();
				
				if(type.equals("Room") || type.equals("Space")) {
					if (parts.length != 3) {
						throw new BadConfigFormatException("Room/Space requires exactly 3 parts - Line: " + line);
					}
					char initial = parts[2].trim().charAt(0);
					roomMap.put(initial, new Room(name));
					
					// Create room card for actual rooms (not spaces like walkways)
					if(type.equals("Room")) {
						deck.add(new Card(name, CardType.ROOM));
					}
				}
				else if(type.equals("Player")) {
					if (parts.length < 6) {
						throw new BadConfigFormatException("Person requires at least 5 parts - Line: " + line);
					}
					String colorStr = parts[5].trim();
					Color color = convertColor(colorStr);
					int startRow = Integer.parseInt(parts[3].trim());
					int startCol = Integer.parseInt(parts[4].trim());
					boolean isHuman = parts.length > 5 && parts[2].trim().equalsIgnoreCase("human");
					
					Player player;
					if(isHuman) {
						player = new Human(name, color, startRow, startCol);
					} else {
						player = new CPU(name, color, startRow, startCol);
					}
					players.add(player);
					
					// Create person card
					deck.add(new Card(name, CardType.PERSON));
				}
				else if(type.equals("Weapon")) {
					if (parts.length != 2) {
						throw new BadConfigFormatException("Weapon requires exactly 2 parts - Line: " + line);
					}
					weapons.add(name);
					// Create weapon card
					deck.add(new Card(name, CardType.WEAPON));
				}
				else {
					throw new BadConfigFormatException("Invalid type: " + type);
				}
			}
		}
	}
	
	private Color convertColor(String colorStr) {
		switch(colorStr.toLowerCase()) {
			case "red": return Color.RED;
			case "blue": return Color.BLUE;
			case "green": return Color.GREEN;
			case "yellow": return Color.YELLOW;
			case "orange": return Color.ORANGE;
			case "purple": return Color.MAGENTA;
			case "white": return Color.WHITE;
			case "black": return Color.BLACK;
			default: return Color.GRAY;
		}
	}
	
	//Loading the layout from the Config file
	public void loadLayoutConfig() throws BadConfigFormatException, FileNotFoundException{
		try(Scanner scanner = new Scanner(new FileReader(layoutConfigFile))){
			
			int rowCount = 0;
			int expectedColumns = -1;
			
			java.util.List<String> lines = new java.util.ArrayList<>();
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (!line.isEmpty()) {
					lines.add(line);
					String[] cells = line.split(",");
					
					if(expectedColumns == -1) {
						expectedColumns = cells.length;
					} else if (cells.length != expectedColumns) {
						throw new BadConfigFormatException("Invalid number of columns in the layout file");
					}
					
					rowCount++;
				}
			}
			
			numRows = rowCount;
			numColumns = expectedColumns;
			grid = new BoardCell[numRows][numColumns];
			
			//Making board cells
			for(int row = 0;row < numRows; row++) {
				String[] cells = lines.get(row).split(",");
				for(int col = 0; col < numColumns; col++) {
					String cellData = cells[col].trim();
					char roomInitial = cellData.charAt(0);
					
					//Check is room is in the setup
					if(!roomMap.containsKey(roomInitial)) {
						throw new BadConfigFormatException("Room: " + roomInitial + " not in the file");
					}
					
					//Making the board cells
					BoardCell cell = new BoardCell(row, col, roomInitial);
					
					if (cellData.length() > 1) {
                        char modifier = cellData.charAt(1);
                        switch (modifier) {
                            case '^':
                                cell.setDoorway(true);
                                cell.setDoorDirection(DoorDirection.UP);
                                break;
                            case 'v':
                                cell.setDoorway(true);
                                cell.setDoorDirection(DoorDirection.DOWN);
                                break;
                            case '<':
                                cell.setDoorway(true);
                                cell.setDoorDirection(DoorDirection.LEFT);
                                break;
                            case '>':
                                cell.setDoorway(true);
                                cell.setDoorDirection(DoorDirection.RIGHT);
                                break;
                            case '*':
                                cell.setRoomCenter(true);
                                roomMap.get(roomInitial).setCenterCell(cell);
                                break;
                            case '#':
                                cell.setLabel(true);
                                roomMap.get(roomInitial).setLabelCell(cell);
                                break;
                            default:
                            	//For secret ways
                            	if (roomMap.containsKey(modifier)) {
                            		cell.setSecretPassage(modifier);
                            	}
                            	break;
						}
					}
					grid[row][col] = cell;
				}
			}
		}
	}
	
	
	public void deal() {
		if(deck.isEmpty() || players.isEmpty()) {
			return;
		}
	
		Collections.shuffle(deck);
		
		// Making solution
		Card roomCard = null, personCard = null, weaponCard = null;
		List<Card> remainingCards = new ArrayList<>();
		
		// Find a card of all types for solution
		for(Card card : deck) {
			if(roomCard == null && card.getCardType() == CardType.ROOM) {
				roomCard = card;
			} else if(personCard == null && card.getCardType() == CardType.PERSON) {
				personCard = card;
			} else if(weaponCard == null && card.getCardType() == CardType.WEAPON) {
				weaponCard = card;
			} else {
				remainingCards.add(card);
			}
		}
		
		theAnswer = new Solution(roomCard, personCard, weaponCard);
		
		// Give rest of cards to players
		Collections.shuffle(remainingCards);
		for(int i = 0; i < remainingCards.size(); i++) {
			Player player = players.get(i % players.size());
			player.updateHand(remainingCards.get(i));
		}
	}
	
	//Creating the board with both config files
	public void initialize() {
		try {
			loadSetupConfig();
			loadLayoutConfig();
			deal();
			for (Player p : players) {
				getCell(p.getRow(), p.getCol()).setOccupied(true);
			}
		} catch (BadConfigFormatException | FileNotFoundException e) {
			System.out.println("Not able to load Configuration Files - Error: " + e.getMessage());
		}
	}
	
	// Calculate adjacency list for a given cell
	public Set<BoardCell> getAdjList(int row, int col) {
		return getAdjList(getCell(row, col));
	}
	
	public Set<BoardCell> getAdjList(BoardCell cell) {
		Set<BoardCell> adjacents = new HashSet<>();
		int row = cell.getRow();
		int col = cell.getCol();
		
		// If cell is inside a room (not center, not doorway), no adjacents
		if (cell.isRoom() && !cell.isRoomCenter() && !cell.isDoorway()) {
			return adjacents;
		}
		
		// If cell is a room center
		if (cell.isRoomCenter()) {
			for (int r = 0; r < numRows; r++) {
				for (int c = 0; c < numColumns; c++) {
					BoardCell otherCell = grid[r][c];
					if (otherCell.isDoorway() && getDoorwayTarget(otherCell).equals(cell)) {
						adjacents.add(otherCell);
					}
				}
			}
			
			// Add secret passage for exist
	        Room room = roomMap.get(cell.getInitial());
	        if (room != null) {
	            for (int r = 0; r < numRows; r++) {
	                for (int c = 0; c < numColumns; c++) {
	                    BoardCell other = grid[r][c];
	                    if (other.getInitial() == cell.getInitial() && other.getSecretPassage() != 0) {
	                        Room targetRoom = roomMap.get(other.getSecretPassage());
	                        if (targetRoom != null && targetRoom.getCenterCell() != null) {
	                            adjacents.add(targetRoom.getCenterCell());
	                        }
	                    }
	                }
	            }
	        }
			return adjacents;
		}
		
		// If cell is a doorway
		if (cell.isDoorway()) {
			BoardCell roomCenter = getDoorwayTarget(cell);
			if (roomCenter != null) {
				adjacents.add(roomCenter);
			}
		}
		
		// Add normal walkway adjacents (up, down, left, right)
		int[] dr = {-1, 1, 0, 0}; 
		int[] dc = {0, 0, -1, 1};
		
		for (int i = 0; i < 4; i++) {
			int newRow = row + dr[i];
			int newCol = col + dc[i];
			
			if (isValidCell(newRow, newCol)) {
				BoardCell adjacent = grid[newRow][newCol];
				
				// Only add walkways or doorways that don't lead away from current cell
				if (adjacent.getInitial() == 'W' || 
					(adjacent.isDoorway() && !isDoorwayLeadingAway(cell, adjacent))) {
					adjacents.add(adjacent);
				}
			}
		}
		
		return adjacents;
	}
	
	// Helper method to check if a doorway leads away from the current cell
	private boolean isDoorwayLeadingAway(BoardCell from, BoardCell doorway) {
		int fromRow = from.getRow();
		int fromCol = from.getCol();
		int doorRow = doorway.getRow();
		int doorCol = doorway.getCol();
		
		DoorDirection direction = doorway.getDoorDirection();
		
		// Check if the doorway direction points away from cell
		switch (direction) {
			case UP:
				return fromRow > doorRow; 
			case DOWN:
				return fromRow < doorRow; 
			case LEFT:
				return fromCol > doorCol; 
			case RIGHT:
				return fromCol < doorCol;
			default:
				return false;
		}
	}
	
	// Helper method to get the room center that a doorway leads to
	private BoardCell getDoorwayTarget(BoardCell doorway) {
		if (!doorway.isDoorway()) return null;
		
		int row = doorway.getRow();
		int col = doorway.getCol();
		DoorDirection direction = doorway.getDoorDirection();
		
		int targetRow = row;
		int targetCol = col;
		
		switch (direction) {
			case UP:
				targetRow = row - 1;
				break;
			case DOWN:
				targetRow = row + 1;
				break;
			case LEFT:
				targetCol = col - 1;
				break;
			case RIGHT:
				targetCol = col + 1;
				break;
			default:
				return null;
		}
		
		if (isValidCell(targetRow, targetCol)) {
			BoardCell targetCell = grid[targetRow][targetCol];
			if (targetCell.isRoom()) {
				Room room = roomMap.get(targetCell.getInitial());
				return room.getCenterCell();
			}
		}
		
		return null;
	}
	
	// Calculate targets from a starting position with given number of steps
	public void calcTargets(BoardCell startCell, int pathLength) {
		targets.clear();
		visited.clear();
	    if (startCell.isRoomCenter()) {
	        targets.add(startCell);
	    }
		findAllTargets(startCell, pathLength);

		
	}
	
	public Set<BoardCell> calcTargets(int row, int col, int pathLength) {
		calcTargets(getCell(row, col), pathLength);
		return targets;
	}
	
	// Recursive helper method to find all possible targets
	private void findAllTargets(BoardCell currentCell, int stepsRemaining) {
		visited.add(currentCell);
		
		Player player = getCurrentPlayer();
		
		for (BoardCell neighbor : getAdjList(currentCell)) {
		    if (visited.contains(neighbor)) {
		        continue;
		    }

		    // Skip if occupied and NOT a room center
		    if (neighbor.isOccupied() && !neighbor.isRoomCenter()) {
		    	if (player == null || !player.wasMovedBySuggestionLastTurn()) {
		    		continue;
		    	}
		    }

		    if (stepsRemaining == 1 || neighbor.isRoomCenter()) {
		        targets.add(neighbor);
		    } else {
		        findAllTargets(neighbor, stepsRemaining - 1);
		    }
		}
		
		visited.remove(currentCell);
	}
	
	// Check if cell coordinates are valid
	private boolean isValidCell(int row, int col) {
		return row >= 0 && row < numRows && col >= 0 && col < numColumns;
	}
	
	// methods for checking an accusation and handling a suggestion (player-based methods)
	public boolean checkAccusation(Solution accusation) {
	    return theAnswer.person.equals(accusation.person)
		        && theAnswer.room.equals(accusation.room)
		        && theAnswer.weapon.equals(accusation.weapon);
	}
	
	public Card handleSuggestion(Card weapon, Card person, Card room, Player accuser) {
	    // Move suggested person to the room center
	    Player suggestedPlayer = findPlayerByCard(person);
	    if (suggestedPlayer != null) {
	        Room suggestionRoom = getRoomByName(room.getCardName());
	        if (suggestionRoom != null) {
	            BoardCell centerCell = suggestionRoom.getCenterCell();
	            suggestedPlayer.setLocation(centerCell.getRow(), centerCell.getCol());
	            suggestedPlayer.setMovedBySuggestionLastTurn(true);
	        }
	    }

	    // Find disproving player and card
	    int startIndex = players.indexOf(accuser);
	    Card disprovingCard = null;
	    Player disprover = null;
	    for (int i = 1; i < players.size(); i++) {
	        Player p = players.get((startIndex + i) % players.size());
	        Card disprove = p.disproveSuggestion(person, room, weapon);
	        if (disprove != null) {
	            disprovingCard = disprove;
	            disprover = p;
	            if (accuser.isHuman() && p != accuser) {
	                accuser.addSeenCard(disprove, p);
	                // Update KnownCardsPanel to show new seen card immediately
	                KnownCardsPanel knownPanel = getKnownCardsPanel();
	                if (knownPanel != null) {
	                    knownPanel.setSeen(accuser.getSeenCards());
	                }
	            }
	            break;
	        }
	    }

	    // Update GameControlPanel guess and result display
	    GameControlPanel controlPanel = getControlPanel();
	    if (controlPanel != null) {
	        String guessText = String.format("%s in the %s with the %s",
	                person.getCardName(), room.getCardName(), weapon.getCardName());
	        controlPanel.setGuess(guessText);

	        if (disprovingCard == null) {
	            controlPanel.setGuessResult("No one could disprove");
	        } else if (accuser.isHuman()) {
	            controlPanel.setGuessResult(String.format("%s showed %s",
	                    disprover.getName(), disprovingCard.getCardName()));
	        } else {
	            controlPanel.setGuessResult(String.format("Disproved by %s", disprover.getName()));
	        }
	    }

	    return disprovingCard;
	}
	
	private Player findPlayerByCard(Card person) {
	    for (Player p : players) {
	        if (p.getName().equals(person.getCardName())) {
	            return p;
	        }
	    }
	    return null;
	}

	// BEGIN : code for moving through and obtaining players
	public Player advanceToNextPlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		return players.get(currentPlayerIndex);
	}

	public Player getCurrentPlayer() {
		return currentPlayerIndex < 0 ? null : players.get(currentPlayerIndex);
	}

	
	public void startPlayerTurn(Player player, int roll) {
	    BoardCell start = getCell(player.getRow(), player.getCol());
	    calcTargets(start, roll);

	    if (player instanceof Human) {
	    	humanPlayer = (Human) player;
	    	awaitingHumanMove = true;
	    	for (BoardCell c : targets) c.setHighlight(true);
	    	repaint();
	    	
	    	
	    } else {
	        CPU cpu = (CPU) player;


	        if (cpu.shouldMakeAccusation()) {
	            Solution accusation = cpu.getLastSuggestion();
	            if (accusation != null) {
	                boolean correct = checkAccusation(accusation);

	                String accusationText = String.format("%s, %s, %s",
	                        accusation.getPerson().getCardName(),
	                        accusation.getRoom().getCardName(),
	                        accusation.getWeapon().getCardName());

	                System.out.println("CPU makes an accusation: " + accusationText);
	                if (correct) {
	                    System.out.println("CPU accusation correct. CPU wins!");
	                } else {
	                    System.out.println("CPU accusation incorrect. CPU loses!");
	                }

	                endGameForCPU(cpu, correct);

	                return;  // turn ends here after accusation
	            }
	        }

	        BoardCell dest = cpu.selectTarget(targets);
	        movePlayer(player, dest);
	        clearTargets();
	        repaint();

	        if (dest.isRoomCenter()) {
	            Room room = getRoom(dest);
	            Card roomCard = cpu.findRoomCard(room.getName());

	            Solution suggestion = cpu.createSuggestion(roomCard);


	            cpu.setLastSuggestion(suggestion);


	            Card disprovingCard = handleSuggestion(
	                suggestion.getWeapon(), suggestion.getPerson(), suggestion.getRoom(), cpu);

	            if (disprovingCard == null &&
	                !cpu.getSeenCards().containsKey(roomCard)) {

	                cpu.setShouldMakeAccusation(true);
	            } else {
	                cpu.setShouldMakeAccusation(false);
	            }

	            cpu.setMovedBySuggestionLastTurn(true);
	        } else {
	            cpu.setShouldMakeAccusation(false);
	            cpu.setMovedBySuggestionLastTurn(false);
	        }
	    }
	}

	private void movePlayer(Player p, BoardCell dest) {
		getCell(p.getRow(), p.getCol()).setOccupied(false);
		p.setLocation(dest.getRow(), dest.getCol());
		dest.setOccupied(true);
	}

	private void clearTargets() {
		for (BoardCell c : targets) c.setHighlight(false);
		targets.clear();
	}

	private void handleClick(int x, int y) {
		
		if (!awaitingHumanMove) return;
		
		int cellWidth  = getWidth()  / numColumns;
		int cellHeight = getHeight() / numRows;
		int col = x / cellWidth;
		int row = y / cellHeight;
		BoardCell clicked = getCell(row, col);
		
		if (targets.contains(clicked)) {
			movePlayer(humanPlayer, clicked);
			
			clearTargets();
			awaitingHumanMove = false;
			repaint();
			
			if (clicked.isRoomCenter()) {
	            Room room = getRoom(clicked);
	            if (room != null) {
	                openSuggestionDialog(room.getName());
	            }
			}
			

			
		} else {
			JOptionPane.showMessageDialog(Board.getInstance(), "Invalid target");
		}
		
	}
	
	private void openSuggestionDialog(String roomName) {
	    Card roomCard = null;
	    for (Card c : getDeck()) {
	        if (c.getCardType() == CardType.ROOM && c.getCardName().equals(roomName)) {
	            roomCard = c;
	            break;
	        }
	    }
	    if (roomCard == null) return;

	    SuggestionDialog dialog = new SuggestionDialog(
	        parentFrame,
	        roomCard,
	        getPeopleCards(),
	        getWeaponCards()
	    );

	    dialog.setVisible(true);

	    Solution suggestion = dialog.getSuggestion();
	    if (suggestion != null) {
	        handleSuggestion(
	        	suggestion.getRoom(),
	        	suggestion.getWeapon(),
	        	suggestion.getPerson(),
	            humanPlayer
	        );
	    }
	}
	
	public void endGameForCPU(CPU cpu, boolean won) {
		
	    JFrame clueGameFrame = parentFrame;

	    String message;
	    String title;
	    int messageType;

	    if (won) {
	        message = String.format("CPU %s won the game by correct accusation!", cpu.getName());
	        title = "CPU Wins!";
	        messageType = JOptionPane.INFORMATION_MESSAGE;
	    } else {
	        message = String.format("CPU %s made an incorrect accusation and lost the game!", cpu.getName());
	        title = "CPU Loses!";
	        messageType = JOptionPane.ERROR_MESSAGE;
	    }

	    javax.swing.SwingUtilities.invokeLater(() -> {
	        JOptionPane.showMessageDialog(clueGameFrame, message, title, messageType);
	        if (clueGameFrame != null) {
	            clueGameFrame.dispose();
	        }
	    });
	}
	
	// END : code for moving through and obtaining players
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int cellWidth  = getWidth()  / numColumns;
		int cellHeight = getHeight() / numRows;

		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numColumns; c++) {
				BoardCell cell = grid[r][c];
				int x = c * cellWidth;
				int y = r * cellHeight;
				cell.draw(g, x, y, cellWidth, cellHeight);
			}
		}
		

		drawRoomNames(g, cellWidth, cellHeight);

		// Draw players
		for (Player player : players) {
		    int playerRow = player.getRow();
		    int playerCol = player.getCol();
		    int x = playerCol * cellWidth;
		    int y = playerRow * cellHeight;
		    player.draw(g, x, y, cellWidth, cellHeight);
		}

	}
	
	private void drawRoomNames(Graphics g, int cellWidth, int cellHeight) {
	    for (int row = 0; row < numRows; row++) {
	        for (int col = 0; col < numColumns; col++) {
	            BoardCell cell = grid[row][col];
	            if (cell.isLabel()) {
	                // Get the room name using the room initial
	                Room room = roomMap.get(cell.getInitial());
	                if (room != null) {
	                    String name = room.getName();
	                    g.setColor(Color.BLUE);
	                    g.drawString(name, col * cellWidth + 5, row * cellHeight + 15);
	                }
	            }
	        }
	    }
    
	}

	public void setSolution(Solution solution) {
		this.theAnswer = solution;
	}
	
	public void setParentFrame(JFrame frame) {
	    this.parentFrame = frame;
	}
	
	public void setControlPanel(GameControlPanel panel) {
	    this.controlPanel = panel;
	}

	public GameControlPanel getControlPanel() {
	    return this.controlPanel;
	}

	public void setKnownCardsPanel(KnownCardsPanel panel) {
	    this.knownCardsPanel = panel;
	}

	public KnownCardsPanel getKnownCardsPanel() {
	    return this.knownCardsPanel;
	}
	
	public Room getRoomByName(String roomName) {
	    for (Room room : roomMap.values()) {
	        if (room.getName().equalsIgnoreCase(roomName)) {
	            return room;
	        }
	    }
	    return null;
	}
	
	// Getter methods
	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}
	
	public BoardCell getCellAt(int row, int col) {
		return getCell(row, col);
	}
	
	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}
	
	public Room getRoom(char roomInitial) {
		return roomMap.get(roomInitial);
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	
	public Set<BoardCell> getTargets() {
		return targets;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	public List<Card> getDeck() {
		return deck;
	}

	public Solution getSolution() {
		return theAnswer;
	}

	public List<String> getWeapons() {
	    return weapons;
	}
	
	public List<Card> getPeopleCards() {
	    List<Card> people = new ArrayList<>();
	    for (Card c : deck) {
	        if (c.getCardType() == CardType.PERSON) {
	            people.add(c);
	        }
	    }
	    return people;
	}

	public List<Card> getWeaponCards() {
	    List<Card> weapons = new ArrayList<>();
	    for (Card c : deck) {
	        if (c.getCardType() == CardType.WEAPON) {
	            weapons.add(c);
	        }
	    }
	    return weapons;
	}

	public boolean isAwaitingHumanMove() { return awaitingHumanMove; } /// NEW

}
