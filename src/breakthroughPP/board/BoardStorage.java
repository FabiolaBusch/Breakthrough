package breakthroughPP.board;

import java.util.HashSet;
import breakthroughPP.preset.*;
/**
 * This class provides restriced access to different fields and methods of the board.
 * It works as a filter
 * 
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author Fabiola Buschendorf
 */
public class BoardStorage implements Viewer {

	private Board board;

	// ==== Constructors ==============================================================================

	/**
	 * Default Constructor
	 * @throws IllegalArgumentException if called
	 */
	public BoardStorage() throws IllegalArgumentException{
		throw new IllegalArgumentException("BoardStorage without arguments is not supported");
	}

	/**
	 * Constructor with parameter
	 * @param newboard Reference to the board the information should come from
     * @throws IllegalArgumentException if called with newboard == null
	 */
	public BoardStorage(Board newboard) throws IllegalArgumentException{
		if(newboard == null){
			throw new IllegalArgumentException("BoardStorage needs a Board reference which is not" +
				" null");
		}
		board = newboard;
	}

	// ==== Interface functions/ methods ==============================================================

	/**
	 * @return an integer describing the color of the player who can make a move. The colors are
	 * described in {@link breakthroughPP.preset.Setting}
	 * @see breakthroughPP.preset.Viewer
	 */
	@Override
	public int turn(){
		return board.getTurn();
	}

	/**
	 * @return the width of the board (in x direction)
	 * @see breakthroughPP.preset.Viewer
	 */
	@Override
	public int getDimX(){
		return board.getLetters();
	}

	/**
	 * @return the height of the board
	 * @see breakthroughPP.preset.Viewer
	 */
	public int getDimY(){
		return board.getNumbers();
	}

	/**
	 * @param letter postion in x direction
	 * @param number postion in y direction
	 * @return the color the cell described by both positions with respect to the coordinate origin
	 * @see breakthroughPP.preset.Viewer
	 */
	public int getColor(int letter, int number){
		return board.getColor(number,letter);
	}

	/**
	 * @return a reference to the status of the board
	 * @see breakthroughPP.preset.Viewer
	 */
	public Status getStatus(){
		return board.getStatus();
	}    
   
	/**
	 * @return reference to the last move that changed the board
	 * @see breakthroughPP.preset.Viewer
	 */
	public Move getLastChange(){
		return board.getLastChange();
	}
	
	/**
	 * @param color color of the player specified in {@link breakthroughPP.preset.Setting} whos
	 * valid moves should be returned
	 * @return a reference to the set of moves on the board which are valid for the specified color
	 * @see breakthroughPP.preset.Viewer
	 */
	public HashSet<Move> getValidMoves(int color) throws PresetException{
		return board.getValidMoves(color);
	}
}
