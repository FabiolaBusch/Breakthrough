package breakthroughPP.players;

import java.rmi.RemoteException;
import java.util.*;

import breakthroughPP.board.Board;
import breakthroughPP.preset.*;
import breakthroughPP.gui.Showable;

/**
 * Class that represents the a player with reasonable artificial intelligence
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat 
 * 
 * @author H.A.
 */
public class AdvancedPlayer extends AbstractPlayer {

	/** Random number generator for random move selection */
	private final Random random = new Random();

	public AdvancedPlayer(Requestable input, Showable output) {
		super(input, output);
	}

	/**
	 * Runs a improved strategy, by looking forward two moves and by rating different game situations
	 * @see breakthroughPP.players.AbstractPlayer
	 * @see breakthroughPP.preset.Player
	 * @return the selected (best) move
	 * @throws RemoteException if the remote connection throws an exception
	 * @throws Exception if another error occurs
     */
	@Override
    public Move request() throws Exception, RemoteException {

        // Verify state of cycle state machine
        super.request();
		
        // Holds the rating of the moves that are in the selected moves list
        // this value has to be maximized
        int currentMaxRating = Integer.MIN_VALUE;

        // Holds the moves that have the max rating
        List<Move> selectedMoves = new ArrayList<>();

        // Moves that this player could do
        List<Move> validOwnMoves = new ArrayList<>(board.getValidMoves(this.color));

        int opponentColor = this.color == RED ? BLUE : RED;

        // Try every possible move that this player could do
        for(Move move : validOwnMoves) {

            // Do the actual move on a virtual board
            // It would be more performant to not clone the board
            // but revert a move afterwards...

            Board boardAfterMyMove = new Board(this.board);
            boardAfterMyMove.move(move);

            int minRatingAfterOpponentMove = Integer.MAX_VALUE;

            // Moves that the opponent could do after the current player
            // did his move
            List<Move> validOpponentMoves = new ArrayList<>(boardAfterMyMove.getValidMoves(opponentColor));

            // Try every move of the opponent and do a rating of the board for each player afterwards.
            // Minimize the rating of the play situations
            for(Move opponentMove : validOpponentMoves) {
                Board boardAfterOpponentMove = new Board(boardAfterMyMove);
                boardAfterOpponentMove.move(opponentMove);
                minRatingAfterOpponentMove = Math.min(minRatingAfterOpponentMove, rate(boardAfterOpponentMove));
            }

            // A new higher rating was found, forget old moves that could be interesting
            // and memorise this move because it has the highest rating found until now.
            if(minRatingAfterOpponentMove > currentMaxRating) {
                selectedMoves.clear();
                selectedMoves.add(move);
                currentMaxRating = minRatingAfterOpponentMove;
            }

            // Ths rating of this move is as high as other moves that have been found until now
            // Add it to the list so that it can be selected afterwards randomly
            if(minRatingAfterOpponentMove == currentMaxRating) {
                selectedMoves.add(move);
            }

        }

        // Select a random move of the best possible move and mark is as the move to do as soon
        // as engine confirms the move
        
        currentMove = selectedMoves.get(random.nextInt(selectedMoves.size()));

        // Win game if possible
        Iterator<Move> iter = validOwnMoves.iterator();
        while(iter.hasNext()) {
        	Move move = iter.next();
        	if(color == RED && move.getEnd().getNumber() == board.getNumbers()-1){
        		currentMove = move;
        	}
        	if(color == BLUE && move.getEnd().getNumber() == 0){
        		currentMove = move;
        	}
        }

        return currentMove;
	}

    /**
     * Rate the current situation of the board for the current user
     * @param board The board whose game situation is to be rated
     * @return rated value, higher is better
     */
    private int rate(Board board) {

        // Distances of tokens to the other side of the board
        int nearestDistanceToBlueEnd = Integer.MIN_VALUE;
        int nearestDistanceToRedEnd = Integer.MAX_VALUE;

        // The number of tokens in play for a certain color
        int redTokens = 0;
        int blueTokens = 0;

        // The number of columns in which only tokens of a certain color do
        // exist
        int colsExclusivelyRed = 0;
        int colsExclusivelyBlue = 0;

        // Go through board and collect data to be able to do the rating afterwards
        for(int x = 0; x < board.getLetters(); x++) {

            boolean foundRed = false;
            boolean foundBlue = false;

            for(int y = 0; y < board.getNumbers(); y++) {

                int fieldColor = board.getColor(y, x);

                if(fieldColor == RED) {
                    nearestDistanceToBlueEnd = Math.max(y, nearestDistanceToBlueEnd);
                    redTokens++;
                    foundRed = true;
                }

                if(fieldColor == BLUE) {
                    nearestDistanceToRedEnd = Math.min(y, nearestDistanceToRedEnd);
                    blueTokens++;
                    foundBlue = true;
                }

            }

            colsExclusivelyBlue += foundBlue && !foundRed ? 1 : 0;
            colsExclusivelyRed += foundRed && !foundBlue ? 1 : 0;

        }

        int blueDistanceRating = nearestDistanceToBlueEnd - nearestDistanceToRedEnd;
        int redDistanceRating = -blueDistanceRating;

        int blueTokenRating = blueTokens - redTokens;
        int redTokenRating = - blueTokenRating;

        int blueExclusiveRating = colsExclusivelyBlue - colsExclusivelyRed;
        int redExclusiveRating = -blueExclusiveRating;

        int blueRating = blueDistanceRating + 2 * blueTokenRating * 5 * blueExclusiveRating;
        int redRating = redDistanceRating + 2 * redTokenRating * 5 * redExclusiveRating;
        
        return this.color == RED ? redRating : blueRating;

    }
    
	/**
	 * Returns the type of the current player
	 * @see breakthroughPP.players.AbstractPlayer
	 * @see breakthroughPP.players.PlayerType
	 */
    @Override
    public PlayerType getPlayerType(){
        return PlayerType.COMPUTER;
    }
}
