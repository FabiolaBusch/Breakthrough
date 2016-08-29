package breakthroughPP.preset;

import java.util.HashSet;

/**
 * Interface allowing to receive information from the game board
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * expanded by CodeSalat
 */
public interface Viewer {

    /**
     * Indicate whose players turn is. 0 for player red, 1 for player blue
     * @see Setting
     * @return the player who's turn it is.
     */
    int turn();

    /**
     * Get the width of the board
     * @return the number of rows/ width.
     */
    int getDimX();

    /**
     * Get the height of the board
     * @return the number of lines/ height.
     */
    int getDimY();

    /**
     * Get the color of a certain field on the board {@link Setting}
     * @param letter x dimension position
     * @param number y dimension position
     * @return the color integer.
     */
    int getColor(int letter, int number);

    /**
     * Get the current status of the board
     * @return the current status
     */
    Status getStatus();

	/**
	 * Function to return the last change on the board.
	 * @return the last move, which was placed on the board
	 */
	Move getLastChange();
	
	/**
	 * Returns a set of valid moves for a player based on the current situation on the game board.
	 * @param color the color of the player
	 * @return HashSet the valid moves of a player with color "color"
	 * @throws PresetException when the {@link breakthroughPP.board.Board#getValidMoves(int color)}
	 * throws an exception
	 */
	HashSet<Move> getValidMoves(int color) throws PresetException; 
}
