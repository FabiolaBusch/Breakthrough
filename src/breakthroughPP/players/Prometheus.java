package breakthroughPP.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import breakthroughPP.board.Board;
import breakthroughPP.preset.*;


/**
 * Calculate the firt depth of move of a token 
 *<p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author A.Z.
 */
public class Prometheus implements Setting{
	
	/** a move with maximum score*/
	private Move maxMove;
	
	/** Random number generator for random move selection */
	private final Random random = new Random();
	
	// ==== Constructors ==============================================================================
	
	/**
	 * Default- Constructor
	 * @throws PresetException 
	 */
	public Prometheus() throws PresetException{
		this(new Board(6,6));
	}
	
	/**
	 * Constructor with parameters
	 * @param board new Board from Player
	 */
	public Prometheus(Board board){
		
	}
	
	
// ==== Getter ====================================================================================
 	
	public Move getMaxMove() {
		return maxMove;
	}
	
	
// ==== Instance functions/methods ================================================================
	
	/**Calculate the firt depth of move of a token and
	 * save the maximum move in maxMove
	 * @param newboard Board
	 * @param playerInt Color of player
	 * @param lastMove last move of the Board
	 * @throws PresetException 
	 */
	public void viterbi(Board newboard, int playerInt,Move lastMove) throws PresetException{
		
		Board runboard=null;
		Board staticboard = null;
		try {
			runboard 	= new Board(newboard);
			staticboard	= new Board(newboard);
		} catch (PresetException e) {
			e.printStackTrace();
		}	
		List<Move> validMovesList = new ArrayList<Move>(runboard.getValidMoves(playerInt));
		
		double temp=1;
		double maximum =1;
		
		for(Move eachmove : validMovesList){
			
			if(longDistance(lastMove, eachmove)){
				continue;
			}
			
			runboard.setTurn(playerInt);	
			//set a Blue Move on a simulated board 
			try{ 
				if(!runboard.move(eachmove).isOk() ){
				}
			}catch(PresetException presetex){
				continue;
			}
			
			temp = getScore(lastMove, eachmove,runboard);
			runboard = new Board(staticboard);
			
			if(maximum <= temp ){
				maximum = temp;	
				maxMove = eachmove;
			}
		}
	}
	
	/** Calculate distance of two Moves
	 * @param move1 first Move
	 * @param move2 second Move
	 * @return boolean if true then distance to long
	 */
	public boolean longDistance(Move move1, Move move2){
		
		int x = Math.abs(move1.getStart().getLetter() - move2.getStart().getLetter()); 
		int y = Math.abs(move1.getStart().getNumber() - move2.getStart().getNumber());
		//System.out.println("x"+x+"   y"+y+"kx"+kx+"  ky"+ky);
		if(x < 6 && y < 6 ){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param killmove A move that could kill a Token
	 * @param newboard Board 
	 * @param playerInt Color of Player
	 * @param opponent Color of Opponent
	 * @param d Directions of Player
	 * @return Score of killing Token
	 */
	public double killToken(Move killmove,Board newboard,int playerInt,int opponent, int d){
		double score =0;
		//int d = playerInt == RED ? 1 : -1;
		
		if(isDiagonal(killmove) 
				&&  newboard.getColor(killmove.getEnd()) == opponent 
				&&  newboard.getColor(killmove.getStart()) == playerInt){
			
			
			score += 0.2;
			
			//Its a freekill?
			int diay = killmove.getEnd().getNumber()+d;
			int yDia = (diay < newboard.getNumbers() && diay >=0) ? diay: 0;	
			int dia = killmove.getEnd().getLetter();
			int xDia  = (dia-1 < newboard.getLetters() && dia-1 >=0) ? dia-1 : 0;
			int xDia2 = (dia+1 < newboard.getLetters() && dia+1 >=0) ? dia+1 : 0;
			
			if (xDia !=0 && !(newboard.getColor(yDia,xDia) == opponent) ||
					(xDia2 !=0 && !(newboard.getColor(yDia,xDia2) == opponent)) ){		
				score +=0.3;
			}
			
			//have i defense?
			diay = killmove.getEnd().getNumber()+d*(-2);
			yDia = (diay < newboard.getNumbers() && diay >=0) ? diay: 0;	
			dia = killmove.getEnd().getLetter();
			xDia  = (dia-1 < newboard.getLetters() && dia-1 >=0) ? dia-1 : 0;
			xDia2 = (dia+1 < newboard.getLetters() && dia+1 >=0) ? dia+1 : 0;
			
			if (xDia !=0 && !(newboard.getColor(yDia,xDia) == opponent) ||
					(xDia2 !=0 && !(newboard.getColor(yDia,xDia2) == opponent)) ){		
				score -=0.5;
			}
			
		}
		
		return score; 
	}
	
	/** Calculate if move is a diagonal Move
	 * @param move a Move
	 * @return true it is diagonal move
	 */
	private boolean isDiagonal(Move move){
		Position start = move.getStart();
		Position end = move.getEnd();
		
		
		if(start.getLetter() != end.getLetter() && (end.getNumber() - 1  == start.getNumber() ||
				end.getNumber() + 1 == start.getNumber() )){
			return true;
		}else{
			return false;
		}		
	}
	
	/**Calculate score of a Token.
	 * @param opmove Move of opponent
	 * @param plmove Move of player
	 * @param newboard Board
	 * @return Score of Token
	 */
	public double getScore(Move opmove, Move plmove, Board newboard){
		double score =1;
		
		if(plmove !=null){
			int playerInt = newboard.getColor(plmove.getStart());
			int opponent = (playerInt+1) %2;
			// direction of movement
			int d = playerInt == RED ? 1 : -1;
			
			score += killToken(plmove, newboard,playerInt,opponent,d);
			score += connectedStone(plmove,newboard, playerInt, opponent,d);
			score += LivingStones(newboard, playerInt, opponent);
			score += reachEnd(plmove, newboard, playerInt);
			
			
		}
		
		return score;
	}
	
	/** Calculate if token reach End 
	 * @param plmove Move of player
	 * @param newboard Move of opponent
	 * @param playerInt Color of player
	 * @return Score of token that reach end
	 */
	public double reachEnd(Move plmove, Board newboard, int playerInt){
		double score=0;
		
		if(playerInt ==1){
			if(plmove.getStart().getNumber() == 0){
				score += 2;	
			}
		}else{
			if(plmove.getStart().getNumber() == newboard.getNumbers()){
				score += 2;	
			}
		}
		
		return score;
	}
	
	/** Calculate the dead an living score of token
	 * @param newboard Board
	 * @param playerInt Color of player
	 * @param opponent Color of opponent
	 * @return score for living token
	 */
	public double LivingStones(Board newboard, int playerInt,int opponent){
		double score=0;
		
		
		for(int i=0; i < newboard.getNumbers(); i++){			// numbers
			for(int j=0; j < newboard.getLetters(); j++){		// letters
				if(playerInt==newboard.getColor(i,j) ){
					score +=1;
				}				
				if(opponent == newboard.getColor(i,j) ){
					score -=1;
				}
			}
		}
		return score;
	}
	
	/** Calculate score if Tokens connected
	 * @param plmove Move of player
	 * @param newboard Board
	 * @param playerInt Color of player
	 * @param opponent Color of opponent
	 * @param d Directions of player
	 * @return score if Tokens connected
	 */
	public double connectedStone(Move plmove, Board newboard, int playerInt,int opponent, int d){
		
		
		int dimYend = plmove.getStart().getNumber();
		int dimXend = plmove.getStart().getLetter();
		
		int neighbourcounter =0;
		double score =0;		
		// Radius of 1 		
		for(int i =-1; i <=1;i++ ){
			for(int j =-1; j <=1;j++ ){
				if(newboard.getNumbers() > dimYend+i && newboard.getLetters() > dimXend+j &&
						0 <= dimYend+i && 0 <= dimXend+j){
					
					if (newboard.getColor(dimYend+i,dimXend+j)== playerInt && neighbourcounter <=3){
						score +=0.3;
						neighbourcounter++;
					}
					
				}
			}
			
		}
		
		return score;
	}
}


