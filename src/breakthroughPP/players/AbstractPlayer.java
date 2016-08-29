package breakthroughPP.players;

import java.rmi.RemoteException;
import breakthroughPP.preset.*;
import breakthroughPP.board.Board;
import breakthroughPP.gui.*;
/**
 * A player class that implements methods of the PLayer interface
 * that all players have in common. All concrete player classes should
 * inherit from this class.
 *
 * Georg- August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H. A.
 */
public abstract class AbstractPlayer implements Player, Setting {

	/**
	 * Possible states for the turn cycle state machine of every player.
	 * Initial state is init
	 * Request, confirm and update follow.
	 * Request tells a player to make a move and return it
	 * Confirm
	 */
	public enum State {

		/** Player is in initialization state */
		INIT,

		/** Player will be asked to choose a move */
		REQUEST,

		/** Player waits for his move to be confirmed by game engine / other player */
		CONFIRM,

		/** Player waits for the other player to do his move. */
		UPDATE

	}

	/** Current turn cycle state of the player */
	protected State state = State.INIT;

	/** The view of the board which this player has */
	protected Board board;
	
	/** The color of this player */
	protected int color;

	/** Reference to the gui */
	protected Showable output;

	/** Input reader to read inputs from a source */
	protected Requestable input;

	/** The current move that is being made */
	protected Move currentMove;
	
    public AbstractPlayer(Requestable input, Showable output) {
        this.input = input;
        this.output = output;
    }

	public int getColor(){
		return color;
	}

	public void setCurrentMove(Move move){
		currentMove = move;
	}

	/**
	 * Abstract method to force inheriting classes to reveal their player type
	 * @return Type of the player
     */
	public abstract PlayerType getPlayerType();

	/**
	 * String representation of the current player
	 */
	@Override
	public String toString() {
		String out = "" + getPlayerType();
		out = out + ", Color: ";
		switch(getColor()){
			case RED:	out = out + "red"; break;
			case BLUE:	out = out + "blue"; break;
			default: out = out + "undefined";
		}
		return out;
	}

	/**
	 * Initialisation of the player. If the color is specified with 2 then a red player is produced
	 * and if an output object exists this is configurated for two players
	 * @see breakthroughPP.preset.Player
	 * @param dimX width of the board in x direction
	 * @param dimY height of the board in y direction
	 * @param color color of the player specified in {@link breakthroughPP.preset.Setting} or 2
	 * this instance should show the board for both players
	 * @throws RemoteException if the connection with the net fails
	 * @throws PresetException if the color of the player is not 0,1 or 2
	 * @throws Exception if another exception occures
	 */
	@Override
	public void init(int dimX, int dimY, int color) throws Exception, RemoteException {

		board = new Board(dimX,dimY);

		assert(color == RED || color == BLUE || color == 2); // color = 2 by agreement with the engines

		// Necessary to adjust the color if color is 2. By aggrement with NetEngine init() and
		// prepareIOForPlayer(...) in NetEngine, color 2 indicates a red player (color = RED) but a
		// GUI for both players (color = 2);
		if(color == RED || color == 2) this.color = RED;
		if(color == BLUE) this.color = BLUE;

		this.board = new Board(dimX, dimY);

		// Initialize current player state
		state = State.INIT;
		verifiyState(State.INIT);
		if (color == RED) state = State.REQUEST;
		if (color == BLUE) state = State.UPDATE;
		if (color == 2) state = State.REQUEST; // by agreement with the engines

		// Initialize output (e.g. gui or textouput)
		if(output != null) {
			try{
				output.init(board.viewer(), color); // init with the aktual parameter
				output.update();
			}catch(VisualisationException e) {
				System.err.println(e.getMessage());
			}				
		}

	}
	
	/**
	 * Report of the game controls on a previouse delivered move by this instance
	 * @see breakthroughPP.preset.Player
	 * @param boardStatus commenting on the last move which was requested from this
	 * @throws RemoteException if the connection with the net fails	
	 * @throws PresetException if the status of the internal board with the controls' status
	 * disagrees
	 * @throws Exception if another exception occures
	 */
	@Override
	public void confirm(Status boardStatus) throws Exception, RemoteException {

		// Disagreement between engine and the player leads to an exception -> 
		//	engine enforces its evaluation leading to a victory of the one making the move
		Status state = board.move(currentMove);

		assert(state.equals(boardStatus));

		// Just for notification to enforce Engine's decision
		board.setStatus(boardStatus);

		verifiyState(State.CONFIRM);
		this.state = State.UPDATE;

		updateOutput();

	}

	/**
	 * Updates this player about a move the opponent has made by reporting the move as well as the
	 * new status resulting from that move
	 * @param opponentMove move of the opponent
     * @param boardStatus status resulting from the opponent 's move	
	 * @see breakthroughPP.preset.Player
	 * @throws RemoteException if the connection with the net fails	
	 * @throws PresetException if the status of the internal board with the controls' status
	 * disagrees
	 * @throws Exception if another exception occures
	 */
	@Override
	public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException {
		
		// Disagreement between engine and the player leads to an exception -> 
		//	engine enforces its evaluation leading to a victory of the one making the move
		Status state = board.move(opponentMove);

		if(!(state.equals(boardStatus))) {
			throw new PresetException("Player board status " + state + " and engine status " +
				boardStatus + " are disagreeing");
		}

		// Just for notification to enforce Engine's decision
		board.setStatus(boardStatus);

		verifiyState(State.UPDATE);
		this.state = State.REQUEST;

		updateOutput();

	}

	/**
	 * Asks the player for a move. Here no real implementation only
	 * checking and advancing of the state machine that represents
	 * the move cycle of a player.
	 * @return null the correct move will be returned by a child class. 
	 * @throws Exception
	 */
	@Override
	public Move request() throws Exception {
		verifiyState(State.REQUEST);
		this.state = State.CONFIRM;
		return null;
	}

	/**
	 * Verify the current state of the cycle state machine
	 * @param state the expected state
	 * @throws IllegalStateException If current player state is not the expected state
	 */
	protected void verifiyState(State state) throws IllegalStateException {
		if(this.state != state) throw new IllegalStateException(
				String.format("Invalid player state: %s but should be %s!", this.state, state)
		);
	}

	/**
	 * Tell the output interface to refresh itself. (e.g. GUI or text output)
	 */
	protected void updateOutput() {
		if(output != null) {
			try {
				output.update();
			} catch(VisualisationException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void setState(State state) {
		this.state = state;
	}

}
