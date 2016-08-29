package breakthroughPP.mechanics;

import breakthroughPP.preset.Move;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Logs every game of the move making it possible to restore and store a game status
 * The first move always is made by the red player.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H.A.
 */
public class GameLog implements Serializable, Iterable<Move> {

	/** variable required by the Serializable interface */
	public static final long serialVersionUID = -1818778050969754234L;

    /** Contains list of moves in chronological order */
    private ArrayList<Move> moves = new ArrayList<>();

    /**
     * Adds a move to successful performed moves
     * Only valid moves should be added.
     * @param move Move which is saved
     */
    public void log(Move move) {
        moves.add(move);
    }

	/**
	 * Creates an iterator over the moves which have been logged
	 * @see java.lang.Iterable
     */
    @Override
    public Iterator<Move> iterator(){
        return moves.iterator();
    }

}
