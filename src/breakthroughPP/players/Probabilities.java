package breakthroughPP.players;

import java.util.Arrays;

import breakthroughPP.board.Board;
import breakthroughPP.preset.PresetException;
import breakthroughPP.preset.Setting;


/**
 * This class provides a double[][] matrix containing a value for each 
 * field on the board. This value indicates the ranking of the field, 
 * the higher it is, the more desireable it is for the player to move to.
 * 
 * @author Fabiola Buschendorf
 */
public class Probabilities implements Setting {

	// Contains ranking-values for each field
	private double[][] probabilities;
	
	// For which player?
	private int playerInt;
	
	// Current field - positions of tokens
	private Board board; 
	
	// Oppontents color
	private int opponent;
	
	
// ======= Constructor ============================================================================
	/**
	 * Default constructor
	 */
	public Probabilities(){
	}

	/**
	 * Constructor with parameters
	 * @param myboard Reference to the board
	 * @param color of the player specified in {@link breakthroughPP.preset.Setting}
	 */
	public Probabilities(Board myboard, int color){
		playerInt = playerInt;
		board = myboard;
		probabilities  = new double[board.getNumbers()][board.getLetters()];
		opponent = playerInt == RED ? BLUE : RED;
		
		updateProbabilities();
	}
	


// ======= Getter =================================================================================
	public double[][] getProbabilities(){
		return probabilities;
	}
	

	public double getProbabilitie(int number, int letter){
		return probabilities[number][letter];
	}
	
	

// ======= Instance methods =======================================================================
	/**
	 *  initialises the probability values for the board. Initialized value is 1.0, additionally 
	 * +0.1 the nearer the oppontent's boarder is.
	 */
	private void initProbabilities(){
		if(playerInt == RED){
			double value = 1.0;
			for(double[] row: probabilities){
				Arrays.fill(row, value);
				value += 0.1;
			}
		} else{
			double value = 1.0+(board.getNumbers()-1)*0.1;
			for(double[] row: probabilities){
				Arrays.fill(row, value);
				value -= 0.1;
			}
		}
	}
	
	/**
	 * Calculates the ranking-value of each field on the board with current status.
	 * Several factors contribute to the value of a field:
	 * - distance to oppontents side
	 * - predator density
	 *  -...
	 */
	public void updateProbabilities(){
		initProbabilities();
		
		// direction of movement
		int d = playerInt == RED ? 1 : -1;
		
		for(int number = 0; number < board.getNumbers();number++){
			for(int letter = 0; letter < board.getLetters(); letter++){
				
				// Are there direct predators?
				if(rightBorder(letter)){
					if(numberInBoundaries(number,playerInt) && board.getColor(number+1*d,letter-1) == opponent){
						probabilities[number][letter]-=0.1;
					}
				}
				else if(leftBorder(letter)){
					if(numberInBoundaries(number,playerInt) && board.getColor(number+1*d,letter+1) == opponent){
						probabilities[number][letter]-=0.1;
					}
				}
				else {
					if(numberInBoundaries(number,playerInt) && (board.getColor(number+1*d,letter+1) == opponent
					|| board.getColor(number+1*d,letter-1) == opponent)){
						probabilities[number][letter]-=0.1;
					}
				}
				
				// Can I kill an opponent?
				if(board.getColor(number,letter) == opponent){
					probabilities[number][letter]+=0.5;
				}
			}
		}
	}
	
	/**
	 * Checks if the opponent side is examined
	 * @param number the current examined field number
	 * @param color the color of current player defined in {@link breakthroughPP.preset.Setting}.
	 * @return boolean, if the number boundary (y-axis) was reached.
	 */
	private boolean numberInBoundaries(int number, int color){
		if(color == RED){
			return (number< board.getNumbers() -1);
		}else{
			return (number > 0);
		}
	}
	
	/**
	 * Checks if we examine a field at the left border
	 * @param letter x position which is checked to be one at the left margin
	 * @return boolean whether the token is at the left border.
	 */
	private boolean leftBorder(int letter){
		return (letter == 0);
	}
	
	/**
	 * Checks if we examine a field at the right border
	 * @param letter y position which is checked to be one at the right margin
	 * @return boolean whether the token is at the right border.
	 */
	private boolean rightBorder( int letter){
		return (letter == board.getLetters()-1);
	}

	
	
	

	
// ======= Test main ==============================================================================
	/**
	 * main for testing purposes
	 */
	public static void main(String[] args){
		try{
		Board board = new Board(6,6);
		Probabilities prob1 = new Probabilities(board,RED);
		Probabilities prob2 = new Probabilities(board,BLUE);
		
		//board.printField();
		
		System.out.println();
		
		System.out.println("Values Red Player: ");
		for(int i = prob1.getProbabilities()[0].length-1; i>=0;i--){
			for(int j = 0; j<prob1.getProbabilities().length;j++){
				System.out.format("%.1f ",prob1.getProbabilities()[i][j]);
			}
			System.out.println();
		}
		System.out.println("Values Blue Player: ");
		
		for(int i = prob2.getProbabilities()[0].length-1; i>=0;i--){
			for(int j = 0; j<prob2.getProbabilities().length;j++){
				System.out.format("%.1f ",prob2.getProbabilities()[i][j]);
			}
			System.out.println();
		}
		
		}catch(PresetException e){
			e.printStackTrace();
		}
	}
	

}
