package breakthroughPP.mechanics;

import breakthroughPP.players.*;
import breakthroughPP.preset.*;
import breakthroughPP.board.*;
import breakthroughPP.gui.*;
import breakthroughPP.simpleIO.*;

import java.rmi.RemoteException;

import java.text.*;
import java.util.*;
/** 
 * This class can be executed. It parses the input form the command line and defines the game,
 * consequently. Two interactive players are locally initialised. In case of a local game, the 
 * game between both players is also managed. This includes the mechanics of initialising both
 * players as well as requesting and confirming moves from one and updating the other player.
 * It privids also the opportunity to save which is activated by calling init() of this instance or
 * setEnableStore(true)
 * 
 * command line options are:
 * -d						Shows the board of the engine and additional information about the
 *							state of the game, default is off
 * -s <rows> <columns>		Sets the dimensions of the board, default 26 26
 * -l <fileName>			Load a game, with stored board dimensions and used player types.
 *						    Additional -s option has no effect.
 * -c						In case your are on a linux system you can make the terminal colorful,
 *							everywhere else you get an default output, default is off.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author J.B.
 * loading a previous game in function init()
 * @author H.A.
 */

public class Engine implements Setting, Storable, IOTransfer {

	// possible switches
	private boolean deBug;
	private int[] boardSize;
	private String fileName;
	private boolean colorTerminal;

	// The game
	private Player redPlayer;
	private Player bluePlayer;
	private Board internalBoard;

	// IO Methods
	private Requestable inputMethodRed;
	private Requestable inputMethodBlue; 
	private Showable outputMethodRed;
	private Showable outputMethodBlue;

	private Showable internalOutput;

	// Order control to assure that the command line arguments are considered before the game is
	// initialised
	private boolean inputProcessed;

	// Saving/ external switches
	private GameLog gameLog;
	private boolean enableStore;
	private int moveDelay;

// ==== Constructors ==============================================================================
	/** 
	 * Default Constructor
	 */
	public Engine(){

		// Setting of the Default Values
		deBug		= false;			// no debug
		boardSize	= new int[2]; boardSize[0] = 26; boardSize[1] = 26; // rows columns
		fileName	= null;
		colorTerminal=false;

		// The game
		redPlayer	= null;
		bluePlayer	= null;
		internalBoard = null;

		// IO Methods
		inputMethodRed	= null;
		inputMethodBlue	= null;
		outputMethodRed = null;
		outputMethodBlue= null;


		internalOutput= null;

		inputProcessed = false;

		// Switches from outside
		gameLog		= new GameLog();
		enableStore = false;
		moveDelay	= 0;
	}

// ==== Getter ====================================================================================
	/**
	 * @throws IllegalStateException if the number of columns has not been initialized yet by
	 * calling {@link #init()}
	 */
	public int getRows() throws IllegalStateException{
		if(!inputProcessed){
			throw new IllegalStateException("Before calling getRows() of an Engine instance, you "+
				"have to call setOptions on that instance first");
		}
		return boardSize[0];
	}

	/**
	 * @throws IllegalStateException if the number of columns have not been initialized yet by
	 * calling {@link #init()}
	 */
	public int getColumns() throws IllegalStateException{
		if(!inputProcessed){
			throw new IllegalStateException("Before calling getColumns() of an Engine instance, " + 
				"you have to call setOptions on that instance first");
		}
		return boardSize[1];
	}

	public Player getRedPlayer(){
		return redPlayer;
	}

	public Player getBluePlayer(){
		return bluePlayer;
	}

	public boolean getDebug(){
		return deBug;
	}

	public Viewer getBoardViewer(){
		return internalBoard.viewer();
	}

    public GameLog getGameLog() {
        return gameLog;
    }

	public String getFileName(){
		return fileName;
	}

	public boolean getColorTerminal(){
		return colorTerminal;
	}


// ==== Setter ====================================================================================
	public void setRedPlayer(Player newred){
		redPlayer = newred;
	}

	public void setBluePlayer(Player newblue){
		bluePlayer = newblue;
	}


    public void setGameLog(GameLog newgamelog){
        gameLog = newgamelog;
    }

	public void setEnableStore(boolean enablestore){
		enableStore = enablestore;
	}

	/**
	 * @throws IllegalArgumentException if the commited delay time is negative as a time which is added to
	 * the duration of {@link breakthroughPP.preset.Player#request()},
	 * {@link breakthroughPP.preset.Player#confirm(Status boardStatus)},{@link breakthroughPP.preset.Player#update(Move opponentMove, Status boardStatus)}
	 * must not be negative
     */
	public void setMoveDelay(int milliseconds) throws IllegalArgumentException{
		if(milliseconds < 0){
			throw new IllegalArgumentException("Negative time to deliver a move is not allowed");
		}
		moveDelay = milliseconds;
	}

// ==== Object functions ==========================================================================
	/** 
	 * Function which takes the command line input and processes it. This method goes through the
	 * array of strings an compares them to strings representing the different options for the game.
	 * If a match is found, the default parameters for the game are altered. It is necessary to run
	 * this function before running {@link Engine#init()}. If there are no arguments the default
	 * parameters remain unchanged.
	 * @param args Command line input arguments
	 * @throws IllegalArgumentException if one or more command line inputs do not match to the
	 * definitions of the inputs of the game
	 */
	protected void setOptions(String[] args) throws IllegalArgumentException{
		// Evaluating the input
		if(args != null){

			int n =0;
			while(n < args.length){
				// Debug switch, show the internal board of the gui
				if(args[n].equals("-d")){
					deBug = true;
				}

				// produce a terminal representation with red and blue color
				else if(args[n].equals("-c")){
					colorTerminal = true;
				}

				// Gamesize, set the size of the board to play
				else if(args[n].equals("-s")){
					
					// Get the numbers form the command line
					int[] newsize = new int[2];
					try{
						newsize[0] = readNumber(args,++n);	// rows
						newsize[1] = readNumber(args,++n);	// columns
					}catch(IllegalArgumentException iae){
						throw new IllegalArgumentException("Illegal argument for option \"-s\"" +
							"\n" + iae.getMessage());
					}

					// Ckeck if the numbers are in the required range
					if(6 <= newsize[0] && newsize[0] <=26 && 
					   2 <= newsize[1] && newsize[1] <= 26){ 
						boardSize	= newsize;
					}else{
						throw new IllegalArgumentException("Illegal Values for the board size. "+
							"Accepted values are within: 6<= rows <= 26, 2 <= columns <= 26, " +
							"Your input was (rows x columns): "+ newsize[0] + " x " + newsize[1]);
					}
				}

				// Filename form command line
				else if(args[n].equals("-l")){
					if(++n < args.length){
						fileName = args[n];
					}else{
						throw new IllegalArgumentException("Illegal argument for option \"-l\""+
							"\nFilename to load required");
					}

					// Prevent mistaking the filename with another argument
					if(fileName.startsWith("-")){
						throw new IllegalArgumentException("Filename to load is not allowed to " +
							" start with \"-\"");
					}
				}	

				// Error if there are command line arguments, which do not match with the
				// definitions
				else{
					throw new IllegalArgumentException("Unknown argument or option: " + args[n]);
				}
				n++;
			}
		}

		// To assure that the command lines are processed before the the game is initialised:
		// Consideration of the user's wishes
		inputProcessed = true;

		// Delay between one move cycle and the other
		setMoveDelay(0);
	} 


	/** 
     * The function checks if it is possible to return an integer of the element specified by its
     * index in the array args starting with 0. If it ist possible the integer is returned.
	 * @param args Array of input arguments
	 * @param index Index of the string in args which should be transformed into a number
	 * @return number which is at position index in args
	 * @throws IllegalArgumentException if args is to smaller than the index or if the conversion
	 * to int from args[index] has failed
	 */
	protected int readNumber(String[] args, int index) throws IllegalArgumentException{

		// Enough Input arguments?
		if(index >= args.length){
			throw new IllegalArgumentException("Not enough arguments for readNumber");
		} 

		// Does conversion works				
		try{
			return Integer.parseInt(args[index]);
		}catch(IllegalArgumentException iae){
			throw new IllegalArgumentException("Conversion from " + args[index] + " to int failed" +
				"\n" + iae.getMessage());
		}
	}

	/** 
	 * Initialisation of the game with possibly changed default parameters (see {@link
	 * Engine#setOptions}). This function either initiates a game according to default or set
	 * parameters or loads a previous game with its parameters (see {@link
	 * breakthroughPP.board.Storable}). In the first case two interactive
	 * players are produced. They share a common text input and a text ouput instance respectively.
	 * @return true if the executable function of this class should manage the game
	 * Important maybe for extensions of this class.
	 * @throws RemoteException if a player initalisation throws such an exception (see {@link
	 * breakthroughPP.preset.Player})
	 * @throws IllegalStateException if {@link #setOptions(String[] args)} has not been called and the
	 * user's commands have not been considered
	 * @throws VisualisationException if the text output of the internal board throws one 
	 * (see {@link breakthroughPP.gui.Showable#update()} 
	 * @throws Exception if something else fails during initialisation
	 */
	public boolean init() throws RemoteException, Exception, IllegalStateException{
		// Are all necessary switches set?
		if(!inputProcessed){
			throw new IllegalStateException("Before calling init() of an Engine instance, you " +
				"have to call setOptions() on that instance first");
		}

        showDebug("Starting Engine initalisation of the game");

		if(fileName == null){

			// Init of the board
			try{
				internalBoard = new Board(getColumns(),getRows());		// rows, columns
			} catch(PresetException e){
				e.printStackTrace();
				throw new Exception("Failure to initialise the board" +
					"\n" + e.getMessage());
			}

            // Input object
            inputMethodRed = new TextInput(this);	// only Storeable
			inputMethodBlue= inputMethodRed;

			// Output object
            outputMethodRed = new TextOutput(colorTerminal);
			outputMethodBlue= outputMethodRed;
			outputMethodRed.init(null,RED);	// poper init during player init to prevent an reference to
			outputMethodBlue.init(null,BLUE);	// board of the engine


			// Produce Player objects for red and blue
			redPlayer = new InterActivePlayer(inputMethodRed,null);
			bluePlayer= new InterActivePlayer(inputMethodBlue,null);
			// one output is null should be visible only in debug mode

			// Initialize the Players, here also the initialisation of their boards take place
			redPlayer.init(getColumns(),getRows(),RED);
			bluePlayer.init(getColumns(),getRows(),BLUE);

			showDebug("Initalisation of Engine complete");

			// Internal output
			internalOutput = new TextOutput(colorTerminal);
			internalOutput.init(internalBoard.viewer(),2);
		}else{
			System.out.println(String.format("Initializing previous game state %s...\n", fileName) +
					" all switches are neglected except -l <filename>");

            // Restore original game state
			GameState gameState = null;
			try{
				gameState = GameState.load(fileName);
			}catch(LoadStoreException lse){
				System.err.println(lse.getMessage());
				System.err.println("Exiting");
				System.exit(0);
			}
            internalBoard = new Board(gameState.getWidth(),gameState.getHeight());

            // IO Objects
            outputMethodRed = new TextOutput(colorTerminal);
			outputMethodBlue= outputMethodRed;
			outputMethodRed.init(null,0);	// poper init during player init to prevent an reference to
			outputMethodBlue.init(null,0);

            inputMethodRed = new TextInput(this);	// only Storeable
			inputMethodBlue= inputMethodRed;

			internalOutput = new TextOutput(colorTerminal);
			internalOutput.init(internalBoard.viewer(),2);

            // Restore game log
			gameLog = gameState.getGameLog();

			// Restore the players and their boards
			redPlayer = PlayerFactory.getInstance().create(this, gameState.getRedPlayerType(),RED);
			bluePlayer = PlayerFactory.getInstance().create(this, gameState.getBluePlayerType(),BLUE);
			// In Playerfactory only getInputMethod() and getOuputMethod() is used

			// Initialize the Players
			redPlayer.init(gameState.getWidth(),gameState.getHeight(),RED); 
			bluePlayer.init(gameState.getWidth(),gameState.getHeight(),BLUE);

			// Restore the old moves on the boards of the players by simulating the previous game
			// according to the saved moves
            boolean redPlayersTurn = true;
			Iterator<Move> it = gameLog.iterator();
            while(it.hasNext()){
                Move move = it.next();
                System.out.println("Applying move " + move);
                if(redPlayersTurn){
                    ((AbstractPlayer) redPlayer).setCurrentMove(move);
					((AbstractPlayer) redPlayer).setState(AbstractPlayer.State.CONFIRM); // To overgo request in cycle
                    Status status = internalBoard.move(move);
                    redPlayer.confirm(status);
                    bluePlayer.update(move, status);
                }else{
                    ((AbstractPlayer) bluePlayer).setCurrentMove(move);
					((AbstractPlayer) bluePlayer).setState(AbstractPlayer.State.CONFIRM); // To overgo request in cycle
                    Status status = internalBoard.move(move);
                    bluePlayer.confirm(status);
                    redPlayer.update(move, status);
                }
                redPlayersTurn = !redPlayersTurn;
            }

			showDebug("Initialisation of Engine with an old game is completed");
		}

        // If the debug mode is set, this shows a representation of the internal board of the engine
		if(deBug){
			internalOutput.update();
		}	

		// Engable storing
		setEnableStore(true);

		return true;	
	}	

	/** 
	 * This function administrates the game by alternatingly calling the cycle of {@link
	 * breakthroughPP.preset.Player#request()} and {@link breakthroughPP.preset.Player#confirm(Status boardStatus)} 
	 * on one player and {@link breakthroughPP.preset.Player#update(Move opponentMove, Status boardStatus)}
	 * on the other. The first request is called on the red player. To call this function {@link Engine#init()}
	 * needs to be called first. According to the internal board also the decision of 
	 * victory or defeat is reached.
	 * @throws IllegalStateException if the game has not been initialised yet
	 */
	public void runGame() throws IllegalStateException{

		// Is the game initialised?
		if(redPlayer == null || bluePlayer == null || internalBoard == null){
			throw new IllegalStateException("Before calling runGame() of an Engine instance, you " +
				"have to call successfully init() on that instance first: You need to have two " +
				"players first");
		}

		// run the game
		Status status = new Status(Status.OK);
		while(status.isOk()){
			showDebug("Red player 's move");
			showDebug("===================");
			
			// Call the procedure for the red player
			status = moveCycle(redPlayer,bluePlayer);
			showDebug("status after move cycle: " + status);

			// Win or Lose
			if(status.isIllegal() || status.isUndefined() || status.isBlueWin()){
				System.out.println("blue player wins");
				status = new Status(Status.BLUE_WIN);
			}else if(status.isRedWin()){
				System.out.println("red player wins");
				status = new Status(Status.RED_WIN);
			}
			showDebug("");

			// Inform the board of exceptions e.g. due to falure of request, confirm or update
			try{
				internalBoard.setStatus(status);
			}catch(PresetException pe){
				System.err.println(pe.getMessage());
			}
				
			// Blue move
			if(status.isOk()){
				showDebug("Blue player 's move");
				showDebug("===================");
	
				// Call the procedure for the blue player
				status = moveCycle(bluePlayer,redPlayer);
				showDebug("status after move cycle: " + status);
				
				// Win or Lose
				if(status.isIllegal() || status.isUndefined() || status.isRedWin()){
					System.out.println("red player wins");
					status = new Status(Status.RED_WIN);
				}else if(status.isBlueWin()){
					System.out.println("blue player wins");
					status = new Status(Status.BLUE_WIN);
				}
				showDebug("");

				// Inform the board of exceptions
				try{
					internalBoard.setStatus(status);
				}catch(PresetException pe){
					System.err.println(pe.getMessage());
				}
			}
		}

		// Final try to notify the players that the game is over;
		try{
			redPlayer.confirm(status);
		}catch(Exception e){
		}
		try{
			bluePlayer.confirm(status);
		}catch(Exception e){
		}
	}

	/** 
     * This function calls {@link breakthroughPP.preset.Player#request()}
	 * and after putting the move onto the internal board and receiving the status 
	 * {@link breakthroughPP.preset.Player#confirm(Status boardStatus)} on the player and 
	 * {@link breakthroughPP.preset.Player#update(Move opponentMove, Status boardStatus)} on the opponent
	 * @param player the player upon whom request and confirm is called
     * @param opponent the player receiving the move of the player and the resulting status
	 * @return the status after the entire move cycle
	 */
	private Status moveCycle(Player player, Player opponent){
		Move move = null;
		Status status = new Status(Status.OK);
		try{
			// Player move
			// request a move of the player
			showDebug("Move requested");
			move = player.request();
			showDebug("Move received: " + move);

			if(move != null){
				// put it on the internal board
				status = internalBoard.move(move);
				showDebug("Status after placing move " + move + " on the board: " + status);
			
				// Log the game
				gameLog.log(move);

			}else{
				status = new Status(Status.UNDEFINED);
			}	
		}catch(RemoteException re){
			System.err.println(re.getMessage());
			move = null;
			status = new Status(Status.UNDEFINED);
		}catch(Exception e){
			System.err.println(e.getMessage());
			move = null;
			status = new Status(Status.UNDEFINED);
		}

		// Transform UNDEFINED or ILLEGAL
		if(status.isUndefined() || status.isIllegal()){
			if(opponent.equals(bluePlayer)){
				status = new Status(Status.BLUE_WIN);
			}else{
				status = new Status(Status.RED_WIN);
			}
		}


		// Message back to the Player
		try{	
			player.confirm(status);
			showDebug("Confirmation with status " + status + " successful");
		}catch(Exception e){
			System.err.println(e.getMessage());
			move = null;
			status = new Status(Status.UNDEFINED);	
			showDebug("Confirmation with status " + status + " failed");
		}


		// Transform UNDEFINED or ILLEGAL
		if(status.isUndefined() || status.isIllegal()){
			if(opponent.equals(bluePlayer)){
				status = new Status(Status.BLUE_WIN);
			}else{
				status = new Status(Status.RED_WIN);
			}
		}

		// Notify the opponent
		try{
			showDebug("Updating the opponent with move " + move + " and engine board status: " + status);
			opponent.update(move,status);
			showDebug("Updating the opponent completed");
		}catch(Exception re){
			System.err.println(re.getMessage());
			if(opponent.equals(bluePlayer)){
				status = new Status(Status.RED_WIN);
			}else{
				status = new Status(Status.BLUE_WIN);
			}
			showDebug("Updating the opponent failed");
		}

		// Debug output of the internal board
		if(deBug){
			try{
				internalOutput.update();
			}catch(VisualisationException ve){
				System.err.println(ve.getMessage());
			}
		}	

		// Delay if wished from outside
		try{
			Thread.sleep(moveDelay);
		}catch(InterruptedException ie){
			System.err.println(ie.getMessage());
		}	

		return status;
	}

	/** 
	 * Method which produces an ouput only if the debug switch is set (option -d). This works if
	 * {@link #setOptions(String[] args)} has already been called
	 * @param content Content being shown in the terminal
  	 */
	protected void showDebug(String content){
		if(inputProcessed && deBug){ 
			System.err.println(content);
		}
	}

	/** 
	 * Output of the current initialisation status
	 */
	@Override
	public String toString(){
		return ("Engine options: \n\t" +
			"Red player: " + getRedPlayer() + "\n\t" +
			"Blue player: "+ getBluePlayer() + "\n\t" +
			"Debugmode: "+ deBug + "\n\t" +
			"Boardsize (rows x columns): " + getRows() + " x " + getColumns() + "\n\t" +
 			"Linux color terminal: " + colorTerminal + "\n\t" +
			"Filename to load: " + (fileName == null ? "" : fileName) + "\n\t");
	}

// ==== Interface functions/methods ===============================================================
	/**
	 * Offers a method to store the current status of the internal board
	 * @param filename name of the file the game is stored in
	 * @see breakthroughPP.board.Storable
	 * @throws UnsupportedOperationException if storing is not allowed
	 * @throws IllegalArgumentException if the filename cannot be used as name of a file
	 */
	@Override
	public void store(String filename) throws UnsupportedOperationException, IllegalArgumentException{
		// Storing activated
		if(!enableStore){
			throw new UnsupportedOperationException("Saving is disabled");
		}

		// Check the name
		if(filename.startsWith("-")){
			throw new IllegalArgumentException("Filename is not allowed to start with \"-\"");
		}

		// just to be on the save side
		if(filename == null || filename.equals("")){
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			filename = dateFormat.format(new Date());
		}

		try{
			String file = String.format("%s.gamelog", filename);

            GameState gameState = new GameState();
            gameState.save(this, file);
		}catch(LoadStoreException e){						
			System.err.println("Error while saving game: " + e.getMessage() + "\nGame can proceed");
		}
	}

	/**
	 * Gets the input method of the red player
     * @see breakthroughPP.mechanics.IOTransfer
     */
	@Override
	public Requestable getInputMethodRed(){
		return inputMethodRed;
	}
	
	/**
	 * Gets the input method of the blue player
     * @see breakthroughPP.mechanics.IOTransfer
     */
	@Override
	public Requestable getInputMethodBlue(){
		return inputMethodBlue;
	}
	
	/**
	 * Gets the output method of the red player
     * @see breakthroughPP.mechanics.IOTransfer
     */
	@Override
	public Showable getOutputMethodRed(){
		return outputMethodRed;
	}
	
	/**
	 * Gets the output method of the blue player
     * @see breakthroughPP.mechanics.IOTransfer
     */
	@Override
	public Showable getOutputMethodBlue(){
		return outputMethodBlue;
	}

	/**
	 * Sets a new input method for the red player
	 * @param newinputMethod instance being able to deliver a move from a users input
     * @see breakthroughPP.mechanics.IOTransfer
	 */
	@Override
	public void setInputMethodRed(Requestable newinputMethod){
		inputMethodRed = newinputMethod;
	}

	/**
	 * Sets a new input method for the blue player
	 * @param newinputMethod instance being able to deliver a move from a users input
     * @see breakthroughPP.mechanics.IOTransfer
	 */
	@Override
	public void setInputMethodBlue(Requestable newinputMethod){
		inputMethodBlue = newinputMethod;
	}

	/**
	 * Sets a new output method for the blue player
	 * @param newoutputMethod instance being able to visualise the game
     * @see breakthroughPP.mechanics.IOTransfer
	 */
	@Override
	public void setOutputMethodRed(Showable newoutputMethod){
		outputMethodRed = newoutputMethod;
	}

	/**
	 * Sets a new output method for the blue player
	 * @param newoutputMethod instance being able to visualise the game
     * @see breakthroughPP.mechanics.IOTransfer
	 */
	@Override
	public void setOutputMethodBlue(Showable newoutputMethod){
		outputMethodBlue = newoutputMethod;
	}



// ==== Main function ==============================================================================
	/** 
	 * Executable function  which starts a new Game
	 * @param args Command line inputs
	 */
	public static void main(String[] args) {

		Engine en = new Engine();
	
		// set User Options
		try{
			en.setOptions(args);
		}catch(IllegalArgumentException iae){
			System.err.println("==================================================================\n" +
				"breakthroughPP.mechanics.Engine can have the following options:\n\n"+
				"Available options:\n" +
				"-d \t \t \t Shows the board of the engine and additional information about the " +
								"state of the game, default is off \n"+
 				"-s <rows> <columns>   \t Sets the dimensions of the board, default: 26 26\n"+
				"-l <fileName> \t \t Load a game,  stored board dimensions and "+
							" used player types. Additional -s option has no effect.\n" +
				"-c	\t \t \t In case your are on a linux system you can make the terminal colorful,"+
 							" everywhere else this has no effect, default is off\n");
				System.err.println("\nThe error you have produced:\n" + iae.getMessage());	
			System.err.println("==================================================================");
			return;
		}

		
		// Initialize the game
		// return value of init() is needed because NetEngine is expanding and overwriting init()
		// and the return value determines if the instance has to manage the game by calling
		// runGame()
		boolean initialize = false;
		try{
				initialize = en.init();
				en.showDebug(en.toString());
		}catch(RemoteException re){
			System.err.println("Failure to initialise the game. Exiting");
		}catch(IllegalStateException ise){
			System.err.println("Failure due to improper usage. Mind the order how this functions "+
				" need to be called");
		}catch(Exception re){
			System.err.println("Failure to initialise the game. Exiting");
		}

		// Admin the game if needed
		if(initialize){
			en.runGame();
		}
	}

}
