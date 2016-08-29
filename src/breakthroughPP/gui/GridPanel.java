package breakthroughPP.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import javax.swing.JPanel;

import breakthroughPP.preset.*;

/**
 * This is the main panel for the game gui.
 * It provides a grid of buttons with the 
 * according tokens on which the moves can 
 * be executed.
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016 
 * Gr CodeSalat
 * @author Fabiola Buschendorf
 * @author A.Z.
 */
public class GridPanel extends JPanel implements Setting, Requestable, Showable, ActionListener {

	public final static long serialVersionUID = 2457894314794L;
	
	// Fields of an instance of the class
	private Viewer storage;
	private int playerInt; // 0 - red, 1 - blue, 2 - Computer (red and blue)
	
	// Fields of the class
	//Button equipment
	private GameButton[][] buttons;
	private Move currentMove;
	private GameButton[] swapArrayGameButton; // swap to GameButtons
	private int buttonSize;
	private HashSet<Move> allPossibleMovesOfRed;
	private HashSet<Move> allPossibleMovesOfBlue;
	
	//Special Gui equipment -------------------------
	private int dimX;
	private int dimY;
	private int swapCounter; // to count for swap methods
	private Status status;
	
	//indirect Player ability-------------------------
	private int whosTurn;
	private boolean canIplay; // if false token can't be moved. 
	private boolean gameOver;

// ==== Constructors ==============================================================================
	/** 
	 * Default constructor
	 */	
	public GridPanel(){
	}

	/**
	 *  A GridPanel with GameButtons is initialized.  
	 *  All buttons are placed. 
	 *  In the South Red tokens
	 *  In the North Blue tokens
	 *  @param viewer contains board information.
	 *  @param playerInt the player in this board.
	 */
	public GridPanel(Viewer viewer, int playerInt){
		try{
			init(viewer, playerInt);
		}catch(VisualisationException ve){
			System.err.println(ve.getMessage());
		}
	}

// ==== Getter ===============================================================================================
	public Status getStatus(){
		return status;
	}

	public int getPlayerInt(){
		return playerInt;
	}
	
	public GameButton[][] getButtons(){
		return buttons;
	}

	public Move getCurrentMove(){
		return currentMove;
	}

	public HashSet<Move> getAllPossibleMovesOfRed(){
		return allPossibleMovesOfRed;
	}

	public HashSet<Move> getAllPossibleMovesOfBlue(){
		return allPossibleMovesOfBlue;
	}

	public int getButtonSize(){
		return buttonSize;
	}
	
	public int getWhosTurn(){
		return whosTurn;
	}

	public boolean getCanIplay(){
		return canIplay;
	}

	public int getDimX(){
		return dimX;
	}
	
	public int getDimY(){
		return dimY;
	}
	
// ==== Setter =================================================================================================

		protected void setGameOver(boolean newGameOver){
			gameOver = newGameOver;
		}
	
		public void setCurrentMove(Move mov){
			currentMove = mov;
		}

		protected void setCanIplay(boolean canIplay){
			this.canIplay = canIplay;
		}
		
		public void setButtonSize(int buttonSizeX, int buttonSizeY){
			buttons[0][0].setSize(buttonSizeX, buttonSizeY);
			for(GameButton[] buttonElementDim1 : buttons){
				for(GameButton buttonElement : buttonElementDim1){
					buttonElement.setSize(buttonSizeX, buttonSizeY);
				}
			}
			repaint();
			revalidate();
			
		}
		
		private void setWhosTurn(int colorofturn){
			whosTurn = colorofturn;
		}

// ==== Instance functions ========================================================================
	/**
	 * Takes the current fields[][] values and draws a new button-grid by initialising the buttons.
	 * Adds the buttons to the panel, adds an ActionListener to each of them
	 * and sets Button names, needed by ActionListener. 
	 */
	private void initButtons(){
	
		for(int i=dimX-1;i>=0;i--){ // letters/ columns  
			for(int j=0;j<dimY;j++){	// rows/ numbers
				
				if(storage.getColor(i,j) == RED){
					//Remind: colorString from Setting with colorString["RED","BlUE","GRAY","NONE"] 
					//Remind: GameButton (String colorString, int colorInt,int letter, int number)
					buttons[j][i] = new GameButton(colorString[0],RED,i,j);
				}
				else if(storage.getColor(i,j) == BLUE){
					buttons[j][i] = new GameButton(colorString[1],BLUE,i,j);
				} else if(storage.getColor(i,j) == NONE){
					buttons[j][i] = new GameButton(colorString[2],NONE,i,j);
				}
				
				// Add Button to Panel and add an identifying name
				add(buttons[j][i]);
				buttons[j][i].setName(j+" "+i); 
				
				//add an ActionListener with method actionPerformed
				//if you choose one token, you had to play with this token.
				buttons[j][i].addActionListener(this);
			}
		}
	}

	/**
     * Calculate all valid moves for a player with a certain color by accessing the function {@link
     * breakthroughPP.board.Board#getValidMoves(int color)}.
     * @param color color of the player whose valid moves are to be calculated (See @link{#Setting}
     * @return a set containing all valid moves for the color.
     * @throws PresetException if a Position in this function throws one
     */
	public HashSet<Move> getValidMoves(int color) throws PresetException{
	 return storage.getValidMoves(color);
	}


	/**
	 * The function updates the button size depending on the size of the frame
	 */
	public void updateButtons(){
		int count_buttonInX = dimX; // count only buttons in X
		int count_buttonInY	= dimY; // count only buttons in Y	
		int panelHeight 	= getHeight();
		int panelWidth 		= getWidth();
		
		int size_buttonInX 	= panelWidth/count_buttonInX; 
		int size_buttonInY	= panelHeight/count_buttonInY;
		
		setButtonSize(size_buttonInX, size_buttonInY);
	}

	/**
	 * According to the playerInt, the type of the player specified by its color, the valid moves
	 * are updated
	 */
	protected void updateAllValidMoves(){
		try{
			if(playerInt==RED){
				allPossibleMovesOfRed = getValidMoves(playerInt);
			}
			if(playerInt==BLUE){
				allPossibleMovesOfBlue = getValidMoves(playerInt);
			}
			if(playerInt ==2){
				allPossibleMovesOfRed = getValidMoves(0);
				allPossibleMovesOfBlue = getValidMoves(1);
			}

		}catch (PresetException e){
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Swaps buttons which were moved. This means that the colors are changed.
	 * @param start one button to be switched
	 * @param end button the first one is switched with
	 */
	private void swapButtons(GameButton start, GameButton end){
		
		// color for swap
		Color startColor 	= start.getColor();
		Color endColor 		= end.getColor();
		
		// colorInt for swap. Remind ColorInt is RED =0, BLUE =1, GRAY=3
		
		int startColorInt 	= start.getColorInt();
		int endColorInt 	= end.getColorInt();
		
		start.setColor(endColor);
		end.setColor(startColor);
		start.setColorInt(endColorInt);
		end.setColorInt(startColorInt);
		
	}
	
	/** set a Button from GUI. Button will be shown as Gray field with colorInt.GRAY. 
	 * @param removebutton Button that should be removed from color
	 */
	private void setButtonToGray(GameButton removebutton){
		removebutton.setColor(new Color(0,0,0,0));
		removebutton.setColorInt(2);
	}
	
	
	/** 
	 * Checks if a move is within the set of moves which are valid
	 * @param start start Position of the move to be checked
	 * @param end	end Position  of the move to be checked
	 * @return true if the move is valid, false if not
	 */
	private boolean checkValidMove(GameButton start, GameButton end){
		Move move = createMove(start,end);
		if(playerInt == RED){
			return allPossibleMovesOfRed.contains(move);
		}
		if (playerInt==BLUE){
			return allPossibleMovesOfBlue.contains(move);
		}
		if(playerInt==2){
			if(this.storage.turn()==RED){
				return allPossibleMovesOfRed.contains(move);
			}
			if(this.storage.turn()==BLUE){
				return allPossibleMovesOfBlue.contains(move);
			}
		}		
		return false;
	}
	
	/**
	 * Checks if one player reached opponent's side
	 * or if one player is dead and then updates status
	 */
	protected void updateStatus(){
		boolean redDead = true;
		boolean blueDead = true;

		for(int i=dimX-1; i>=0; i--){		// letters/ columns
			if(buttons[0][i].getColorInt() == BLUE){
				status.setStatus(BLUE_WIN);
			}
			if(buttons[dimY-1][i].getColorInt() == RED){
				status.setStatus(RED_WIN);
			}
			for(int j=0; j<dimY; j++){		// rows/ numbers
				if(buttons[j][i].getColorInt() == BLUE){
					blueDead = false;
				}
				if(buttons[j][i].getColorInt() == RED){
					redDead = false;
				}
			}
		}
		
		if(blueDead){
			status.setStatus(RED_WIN);
		}
		if(redDead){
			status.setStatus(BLUE_WIN);	
		}
		
		// increase player turn
		whosTurn = storage.turn();
	}

	/** 
	 * The function creates a Move from the GameButtons it receives.
	 * @param start GameButton representing the start position of the move
	 * @param end GameButton representing the end position of the move
	 * @return Move produced for the start and end position
	 */
	private Move createMove(GameButton start, GameButton end){
		int startx 	= start.getLetter();
		int starty 	= start.getNumber();
		int endx 	= end.getLetter();
		int endy 	= end.getNumber();
		
		Move move = null;
		try{
			move = new Move(new Position(startx,starty), new Position(endx,endy));
		}catch(PresetException e){
			System.err.println(e.getMessage());
		}
		return move;
	}
	
	/** 
	 * Performes the changes on the coloring of this panel necessary to alter the game state after
	 * receiving a move. It also sets the currentMove as this move.
	 * @param start button where the move starts
	 * @param end button where the move ends
	 */
	private void performeMoveOnPanel(GameButton start, GameButton end){
		
			if( (start.getColorInt() == RED || start.getColorInt() == BLUE ) && end.getColorInt()== NONE){
				setCurrentMove(createMove(start,end));
				swapButtons(start, end);
				
			}
			else if( start.getColorInt() == BLUE && end.getColorInt()== RED){
				setCurrentMove(createMove(start,end));
				setButtonToGray(end);
				swapButtons(start, end);
				
			}
			else if( start.getColorInt() == RED && end.getColorInt()== BLUE){
				setCurrentMove(createMove(start,end));
				setButtonToGray(end);
				swapButtons(start, end);
		}

		//Need to repaint to set Color on Board
		revalidate();
		repaint();
	}
// ==== Interface fucntions/ methods  =============================================================
	/**
	 * This function waits for the user to make a move. This move is returned to the instance
	 * calling this function as the next move one performes. The move can be null indicating the
	 * player's surrender
	 * @see breakthroughPP.preset.Requestable#deliver()
	 */
	@Override
	public Move deliver(){
		
		setCanIplay(true); //case to make a move
		setCurrentMove(null); //need a new move
		
		while(currentMove == null && getCanIplay() && !gameOver ){
			try{
				Thread.sleep(250);
			}catch(InterruptedException e){
				System.err.println(e.getMessage());
			}
		}
		return currentMove;
	}

	/**
	 * Initialisation of the gridpanel
	 * @see breakthroughPP.gui.Showable
	 * @param newstorage Class containing information about the board of the player (see {@link
	 * breakthroughPP.preset.Viewer})
	 * @param newcolor color of the player
	 * @throws VisualisationException if the during initialisation an error occures which makes it
	 * impossible to show the game state
	 */
	@Override
	public void init(Viewer newstorage, int newcolor) throws VisualisationException{
		swapArrayGameButton =new GameButton[2];
		canIplay = false;

		//Get information from BordStorage
		storage = newstorage; 
		status  = storage.getStatus();
		setWhosTurn(storage.turn());
		
		//player equipment
		playerInt = newcolor;
		
		//Dimensions GUI-Board
		dimX = storage.getDimX();
		dimY = storage.getDimY();
		setLayout(new GridLayout(dimY,dimX));
		
		//Initialize GameButtons
		buttons = new GameButton[dimY][dimX];
		initButtons();
		
		for(int i=dimY-1;i>=0;i--){
			for(int j=0;j<dimX;j++){
			this.add(buttons[i][j]);
			}
		}
	
		updateAllValidMoves();	
	}

	/**
	 * Update of the grid panel with the latest changes on the board
	 * @see breakthroughPP.gui.Showable
	 * @throws VisualisationException if an error appears preventing this function to show the
	 * current state
	 */
	@Override
	public void update() throws VisualisationException{
		Move lastmove = storage.getLastChange();
		if(lastmove !=null){
			GameButton buttonStart 	= buttons[lastmove.getStart().getNumber()][lastmove.getStart().getLetter()];
			GameButton buttonEnd 	= buttons[lastmove.getEnd().getNumber()][lastmove.getEnd().getLetter()] ;

			performeMoveOnPanel(buttonStart, buttonEnd); //set Move on Gui (and set currentMove to this Move)
		}

		updateAllValidMoves();	
		updateButtons();
		updateStatus();
	}
	

	/** 
	 * Reaction on an user clicking on one of the buttons of the grid panel. To activate this
	 * method, the function {@link #deliver()} needed to be called previously so that canIplay is
	 * set true. After acception a move by this function canIplay is set to false.
	 * @see java.awt.event.ActionListener
	 * @param action the ActionEvent which is produced by clicking on a button
	 */
	@Override 
	public void actionPerformed(ActionEvent action) {
				
		//get clicked GameButton
		GameButton buttonClicked = (GameButton) action.getSource();
						
		// Game with only one Gui---------------------------------------------------
		//first token shloud be RED 
		if(swapCounter == 0){
			if( playerInt== NONE && storage.turn() == RED &&
				buttonClicked.getColorInt() == RED){
				swapArrayGameButton[0]= buttonClicked; // first clicked button
				swapArrayGameButton[0].setSelected(true); // need to paint button as selected
				swapCounter++;	
			}
							
			if( playerInt== NONE && storage.turn() == BLUE &&
				buttonClicked.getColorInt() == BLUE){
				swapArrayGameButton[0]= buttonClicked; // first clicked button
				swapArrayGameButton[0].setSelected(true); // need to paint button as selected
				swapCounter++;
			}
			// Game with only one Gui ---------------------------------------------------
					
			//first token should be RED for a Red Player
			if( playerInt== RED && canIplay &&
				buttonClicked.getColorInt() == RED){
				swapArrayGameButton[0]= buttonClicked; // first clicked button
				swapArrayGameButton[0].setSelected(true); // need to paint button as selected
				swapCounter++;
			}
							
			//first token should be BLUE for a Blue Player
			if( playerInt== BLUE && canIplay &&
				buttonClicked.getColorInt() == BLUE){
				swapArrayGameButton[0]= buttonClicked; // first clicked button
				swapArrayGameButton[0].setSelected(true); // need to paint button as selected
				swapCounter++;
			}
		}
		//second token need a differnt Color
		else if(swapCounter == 1 && !(swapArrayGameButton[0].equals(buttonClicked))){
			//Init GameButton 
			swapArrayGameButton[1]= buttonClicked; // Second clicked button
							
			if(checkValidMove(swapArrayGameButton[0], swapArrayGameButton[1])){
					
				//unselected first Button 
				swapArrayGameButton[0].setSelected(false);
								
				//set Move on Gui and set currentMove to this Move
				performeMoveOnPanel(swapArrayGameButton[0], swapArrayGameButton[1]);
								
				//Reset swapCounter and swapArrayGameButton and canIplay
				swapCounter =0;
				swapArrayGameButton[0]=null;
				swapArrayGameButton[1]=null;
				canIplay = false;								
			}
		}
		//to unselect a just selected button
		else if(swapArrayGameButton[0] != null 
			&& (swapArrayGameButton[0].equals(buttonClicked))){
			swapCounter = 0;
			swapArrayGameButton[0].setSelected(false); 
			swapArrayGameButton[0] = null;
			swapArrayGameButton[1] = null;
		}
	}
}

