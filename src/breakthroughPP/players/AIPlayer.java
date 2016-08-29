package breakthroughPP.players;


import breakthroughPP.preset.*;

import breakthroughPP.board.Board;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import breakthroughPP.gui.Showable;

/**
 * Implementation of an very intelligent player.
 * 
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * 
 * @author Fabiola Buschendorf
 * @author H.A.
 * @author A.Z.
 */


public class AIPlayer extends AbstractPlayer {
	
	/** Random number generator for random move selection */
	private final Random random = new Random();
	
	// Each field has a value, the higher it is, the better the move to that field
	SectionProbabilities[][] secProbRed;
	SectionProbabilities[][] secProbBlue;
	
	// empirical distribution of the opponents tokens in each row
	/* Maybe used one day, but not today.
	private double[] rowDistributionOpponent;
	private double[] rowDistributionMe;
	
	private int myTokens;
	private int opponentTokens;
	
	// average distance to opponents border for each row
	private double[] opponentDistance;
	private double[] myDistance;
	*/
	private int opponentColor;
	
	// For breakthrough Algorithm
	private Move[] breakMoves = null;
	private Move[] moveChain;
	private Move goodMove;	
	private ArrayList<Position> hurdles;
	private ArrayList<Position> threats;
	private ArrayList<Position> meFirstStage;
	private ArrayList<Position> meSecondStage;
	private ArrayList<Position> meThirdStage;
	
	private ArrayList<Position> opFirstStage;
	private ArrayList<Position> opSecondStage;
	private ArrayList<Position> opThirdStage;

	private int k =-1;
	private Prometheus vit;
	// ======= Constructor ===============================================================================

	public AIPlayer(Requestable input, Showable output) {
		super(input, output);
	}
	
	/**
	 * Create an KIplayer with a certain requestable input to read moves
	 * from some source
	 * @param newinput
     */
	public AIPlayer(Requestable newinput) {
		this(newinput,null);
	}
	

	// ======= Instanzmethoden ===============================================================================
	
	/**
	 * Initialization of board, color and probabilities
	 */
	@Override
	public void init(int dimX, int dimY, int color) throws Exception, RemoteException {

		super.init(dimX, dimY, color);
		opponentColor = color == RED ? BLUE : RED;
		
		vit = new Prometheus(board);		
	}
	

    @Override
    public PlayerType getPlayerType() {
        return PlayerType.AIPLAYER;
    }
 
	
	/**
	 * Chooses the move which leads to a field with the highest value. If several fields are
	 * assigned with the same value, it chooses the first field. Due to the way validMoves are
	 * calculated this will lead to a strategy to attack over preferably one side. 
	 * 
	 * @return the selected (best) move.
	 * @throws Exception
	 * @throws RemoteException
     */
	@Override
	public Move request() throws Exception {

		// Verify and set state of cycle state machine
		super.request();

		// Get the valid moves for this player
		List<Move> validListMoves = new ArrayList<>(board.getValidMoves(color));
		HashSet<Move> validHashMoves = board.getValidMoves(color);
		Move selectedMove = null;
		
		
		
		
		//Breakthrough! ---------------------------------------------------------
		int lowerBound;
		boolean canBreak=false;
		
		if(color == BLUE){
			lowerBound = 0;
		}
		else{
			lowerBound = board.getNumbers()-1;
		}
		
		//Search in every field 4 steps in front of goal for a possible breakthrough.
		for(int n = lowerBound; condition(n,color);){
			for(int l = 0;l<board.getLetters();l++){
				if(board.getColor(n,l) == color){
				
					//System.out.println("Berechnet Break: "+n+" "+l);
					breakMoves = canBreakthrough(new Position(l,n));
		
					if(breakMoves!=null && breakMoves[0] != null){
						//System.out.println(breakMoves[0]);
						canBreak = true;
					
					}
					if(canBreak){
						// Assignes null if connection between safe positions not possible
						breakMoves = breakthrough(new Position(l,n));
					}
				}
				// Execute the breakthrough, if moves are still valid.
				if(breakMoves !=null && breakMoves[0] != null  && k<2){
					k = k +1;
					for(int j =0; j < 3 ; j++){
						//System.out.println("breakmoves "+j+" "+breakMoves[j]);
					}
					
					if( validHashMoves.contains(breakMoves[k]) ){
						
						currentMove = new Move(breakMoves[k]);
						return breakMoves[k];
					}else{
						//System.out.println("Fehler in breakMoves"+breakMoves[k]+"nicht gefunden");
					}
				}
			}
			if(color == BLUE){
				n++;
			}
			else{
				n--;
			}
		}
	
		///---- Prometheus
		// choose a near random move
		if (!longDistance(board.getLastChange(), validListMoves.get(random.nextInt(validListMoves.size())))){
			int depth =3;
			Board runBoard = new Board(board);
			vit.viterbi(runBoard, color, new Move(board.getLastChange()));
			selectedMove =vit.getMaxMove();
			
			for (int i = 0; i < depth; i++) {
					
				//Opponent move
				vit.viterbi(runBoard, (color+1)%2, new Move (selectedMove));
				selectedMove =new Move (vit.getMaxMove());
				//Player move
				vit.viterbi(runBoard, color, selectedMove);
				selectedMove =new Move (vit.getMaxMove());
			}
			
			if(validHashMoves.contains(selectedMove)){
				currentMove = new Move(selectedMove);
				return selectedMove;
			}else{
			}
		}
		
		// Advanced Player Algorithm --------------------------------------------------------
        // Holds the rating of the moves that are in the selected moves list
        // this value has to be maximized
        int currentMaxRating = Integer.MIN_VALUE;

        // Holds the moves that have the max rating
        List<Move> selectedMoves = new ArrayList<>();


        int opponentColor = this.color == RED ? BLUE : RED;

        // Try every possible move that this player could do
        for(Move move : validListMoves) {

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
        
        //Go to end
        Iterator<Move> iter = validListMoves.iterator();
        while(iter.hasNext()){
        	Move move = iter.next();
        	
        	if(color == RED && move.getEnd().getNumber() == board.getNumbers()-1){
        		
        		currentMove = move;
        		return currentMove;
        	}
        	
        	if(color == BLUE && move.getEnd().getNumber() == 0){

        		currentMove = move;
        		return currentMove;
        	}
        }
        
        // Can i kill some token?
        iter = validListMoves.iterator();
        while(iter.hasNext()){
        	Move killmove = iter.next();
        	if(isDiagonal(killmove)
        		&&  board.getColor(killmove.getEnd()) == (color+1)%2 
    			&&  board.getColor(killmove.getStart()) == color){
        		currentMove = new Move(killmove);
        	}
        }
        


        return currentMove;
		
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
	
	/**
	 * Condition for breakthrough algorithm search.
	 * 
	 * @param n the number in the for loop.
	 * @param color of the Player
	 * @return true if the for-loop can go on searching.
	 */
	private boolean condition(int n,int color){
		if(color == BLUE){
			return n <= 3;
		}
		else{
			return n >= board.getNumbers()-4;
		}
	} 
    
	/** Calculate distance of two Moves
	 * @param move1 first Move
	 * @param move2 second Move
	 * @return boolean
	 */
	public boolean longDistance(Move move1, Move move2){
		if( move1 != null && move2 != null ){
			int x = Math.abs(move1.getStart().getLetter() - move2.getStart().getLetter()); 
			int y = Math.abs(move1.getStart().getNumber() - move2.getStart().getNumber());
			//System.out.println("x"+x+"   y"+y+"kx"+kx+"  ky"+ky);
			if(x < 6 && y < 6 ){
				return false;
			}
		}
		return true;
	}
    /**
     * Rate the current situation of the board for the current user
     * @param board The board whose game situation is to be rated
     * @return rated value, higher is better
     * @see breakthroughPP.players.AdvancedPlayer
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
  
    /* Maybe used one day, but not today -----------------------------------------------------------
	/**
	 * Divide the board in m times n sections and calculate for each section the normalised relative
	 * frequency of the neighbouring tokens
	 * @param ny number of section in y direction
	 * @param nx number of section in x direction
	 * @throws UnsupportedOperationException if the order of commands is not respected
	 
	private void divideInSections(int ny, int nx) throws IllegalStateException{
		Viewer viewer = board.viewer();

		int xlength = viewer.getDimX();
		int ylength = viewer.getDimY();

		
		// Define the boarders for the sections
		int[] yborders = new int[ny+1];
		int[] xborders = new int[nx+1];

		// Output
		secProbRed = new SectionProbabilities[nx][ny];
		secProbBlue= new SectionProbabilities[nx][ny];


		// Calculate the size of the sections
		int xsize = xlength/nx;
		int ysize = ylength/ny;
		
		// Calculate the borders, last one will be set to the length of the board
		for(int i = 0; i < nx; i++){
			xborders[i] = i * xsize;
		}
		for(int i = 0; i < ny; i++){
			yborders[i] = i * ysize;
		}
		xborders[nx] = xlength;
		yborders[ny] = ylength;

		for(int i = 0; i < nx; i++){
			for(int j = 0; j < ny; j++){
				secProbRed[i][j] = new SectionProbabilities();
				secProbBlue[i][j]= new SectionProbabilities();

				// calculate
				calculateSection(i,j,xborders,yborders);

				// Output enable
				secProbRed[i][j].normalise();
				secProbBlue[i][j].normalise();
			}		
		}
	} 
	
    
	/**
	 * calculates for each section the numbers at the different neighbouring positions for the red
	 * and the blue section. nx times ny SectionProbabilities must be initialised before.
	 * @param nx x index in the Array of SectionProbabilities starting with 0 in the left buttom
	 * corner
	 * @param ny y index of the array of SectionProbabilities starting with 0 in the left buttom
	 * conrer
	 * @param xborders nx+1 values representing the borders of the section in x direction. First is
	 * 0 last is the width of the board
	 * @param yborders ny+1 values representing the borders of the section in y direction. First is
	 * 0, last is the height of the board
	 * @throws IllegalStateException if {@link breakthroughPP.players.SectionProbabilities} throws
	 * onegetLetters
	 
	private void calculateSection(int nx, int ny, int[] xborders, int[] yborders) throws IllegalStateException{
		Viewer viewer = board.viewer();
		for(int i = xborders[nx]; i < xborders[nx+1]; i++){
			for(int j= yborders[ny]; j < yborders[ny+1]; j++){
				
				// Vertical
				if(isOnBoard(i,j+1)){
					switch(viewer.getColor(i,j+1)){
						case RED: secProbRed[nx][ny].inkTopCenter(); break;
						case BLUE: secProbBlue[nx][ny].inkButtomCenter();break;
						default:
					}
				}
				if(isOnBoard(i,j-1)){
					switch(viewer.getColor(i,j-1)){
						case RED: secProbRed[nx][ny].inkButtomCenter(); break;
						case BLUE: secProbBlue[nx][ny].inkTopCenter(); break;
						default:
					}
				}

				// Horizontal
				if(isOnBoard(i-1,j)){
					switch(viewer.getColor(i-1,j)){
						case RED:secProbRed[nx][ny].inkSideLeft(); break;
						case BLUE: secProbBlue[nx][ny].inkSideRight(); break;
						default:
					}
				}
				if(isOnBoard(i+1,j)){
					switch(viewer.getColor(i+1,j)){
						case RED: secProbRed[nx][ny].inkSideRight(); break;
						case BLUE: secProbBlue[nx][ny].inkSideLeft(); break;
						default:
					}
				}

				// Diagonal
				if(isOnBoard(i+1,j+1)){
					switch(viewer.getColor(i+1,j+1)){
						case RED: secProbRed[nx][ny].inkTopRight(); break;
						case BLUE: secProbBlue[nx][ny].inkButtomLeft(); break;
						default:
					}
				}
				if(isOnBoard(i-1,j+1)){
					switch(viewer.getColor(i-1,j+1)){
						case RED: secProbRed[nx][ny].inkTopLeft(); break;
						case BLUE: secProbBlue[nx][ny].inkButtomRight(); break;
						default:
					}
				}
				if(isOnBoard(i+1,j-1)){
					switch(viewer.getColor(i+1,j-1)){
						case RED: secProbRed[nx][ny].inkButtomRight(); break;
						case BLUE: secProbBlue[nx][ny].inkTopLeft(); break;
						default:
					}
				}
				if(isOnBoard(i-1,j-1)){
					switch(viewer.getColor(i-1,j-1)){
						case RED: secProbRed[nx][ny].inkButtomLeft(); break;
						case BLUE: secProbBlue[nx][ny].inkTopRight(); break;
						default:
					}
				}
			}
		}
	}
			
	/**
	 * Ckecks of the coordinates are on the board
	 * @param x x position
	 * @param y y postion
	 * @return true if on the board, else false
	 
	private boolean isOnBoard(int x, int y){
		Viewer viewer = board.viewer();
		if(0 <= x && x < viewer.getDimX() && 0 <= y && y < viewer.getDimY()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Calculates the empirical distribution of opponents tokens and my Tokens
	 * for each row.
	 * 
	 * author Fabiola
	 
	private void empiricalDistribution(){
		rowDistributionOpponent = new double[board.getLetters()];
		rowDistributionMe = new double[board.getLetters()];
		countTokens();
		
		for(int l =0; l< board.getLetters();l++ ){
			for(int n =0; n< board.getNumbers();n++){
				if(board.getColor(n,l) == opponentColor){
					rowDistributionOpponent[l]++; 
				}
				else if(board.getColor(n,l) == color){
					rowDistributionMe[l]++;
				}
			}
			rowDistributionOpponent[l]/=opponentTokens;
			rowDistributionMe[l]/=myTokens;
		}
	}
	
	/** 
	 * This function counts the remaining tokens on the board.
	 * author Fabiola
     
	private void countTokens(){
		opponentTokens = 0;
		myTokens = 0;
		
		// count the remaining opponent's tokens
		for(int i=0; i < board.getNumbers(); i++){			
			for(int j=0; j <  board.getLetters(); j++){	
				if(board.getColor(j,i) == opponentColor){
					opponentTokens ++;
				}
				else if(board.getColor(j,i) == color){
					myTokens++;
				}
			}
		}
	}
	
	/**
	 * Calculates the average distance the the opponents border
	 * for my and for opponents tokens. If the average distance 
	 * is 1, all tokens in that row reached the opponents border.
	 * author Fabiola
	 
	private void borderDistances(){
		opponentDistance =  new double[board.getLetters()];
		myDistance = new double[board.getLetters()];
		int num = 0;
		
		double[] opponentTokensInRow = new double[board.getLetters()];
		double[] myTokensInRow = new double[board.getLetters()];


		for(int l =0; l< board.getLetters();l++ ){
			for(int n =0; n< board.getNumbers();n++){
				if(board.getColor(n,l) == opponentColor){
					opponentTokensInRow[l] ++; 
				}
				else if(board.getColor(n,l) == color){
					myTokensInRow[l]++;
				}
			}
		}
		
		for(int l =0; l< board.getLetters();l++ ){
			for(int n =0; n< board.getNumbers();n++){
				if(board.getColor(n,l) == opponentColor){
					// Remember: numbers for blue are inverted. Need to revert.
					if(opponentColor == BLUE)
						num = board.getNumbers()-(n+1);
					else
						num = n;
					opponentDistance[l] += (num+1); 
				}
				else if(board.getColor(n,l) == color){
					if(color == BLUE)
						n = board.getNumbers()-(n+1);
					else
						num = n;
					myDistance[l] += (num+1); 
				}
			}
			
			opponentDistance[l] /= opponentTokensInRow[l];
			myDistance[l] /= myTokensInRow[l];
			
			opponentDistance[l]/=board.getNumbers();
			myDistance[l]/=board.getNumbers();
			
			System.out.println(myDistance[l]);
		}
	}----------------------------------------------------------------------------------------------
	*/ 
	
	/**
	 * Calculates a breakthrough Move 3 steps from goal. 
	 * 
	 * @param start the start position of my token which may breakthrough.
	 * @return a Move[] with the next moves to breakthrough the enemy.
	 * @throws PresetException if a Position cannot be created
	 * @see breakthroughPP.preset.Position
	 */
	private Move[] canBreakthrough(Position start) throws PresetException, PlayerException{
		
		int direction = color == RED ? 1 : -1;
		
		
		boolean breakthrough = false;
		
		// Hurdles = my token, threats = his tokens
		hurdles = new ArrayList<Position>();
		threats = new ArrayList<Position>();
		// The stages contain possible positions for my and for all opponents tokens
		// after the first, second and third move.
		meFirstStage = new ArrayList<Position>();
		meSecondStage = new ArrayList<Position>();
		meThirdStage = new ArrayList<Position>();
		
		opFirstStage = new ArrayList<Position>();
		opSecondStage = new ArrayList<Position>();
		opThirdStage = new ArrayList<Position>();
		
		
		// Find opponents possibly threatening tokens, search from start Position+2 'till end
		int startSearch = start.getNumber() +1*direction;
		int endSearch = color == RED ? board.getNumbers() : 0;

		int lowerBound = start.getLetter() - 3 >= 0 ? (start.getLetter() - 3) : 0;
		int upperBound =  start.getLetter() + 3 < board.getLetters() ? (start.getLetter() + 3) : board.getLetters();
				
		for(int l =lowerBound; l< upperBound ;l++ ){
			if(color == RED){
				for(int n = startSearch; n< endSearch ;n++){
					if(board.getColor(n,l) == opponentColor && (n <= start.getNumber()+2 || l == start.getLetter())){
						threats.add(new Position(l,n));
					}
					else if(board.getColor(n,l) == color){
						hurdles.add(new Position(l,n));
					}
				}
			}
			else{
				for(int n = startSearch; n >= endSearch ;n--){
					if(board.getColor(n,l) == opponentColor && (n <= start.getNumber()+2 || l == start.getLetter())){
						threats.add(new Position(l,n));
					}
					else if(board.getColor(n,l) == color){
						hurdles.add(new Position(l,n));
					}
				}
			}			
		}
		
		// Find my possible positions, look 3 steps in the future, check if border was already reached
		for(int l =start.getLetter() - 1; l<= start.getLetter() + 1 ;l++ ){
			if(!(l<0) && l < board.getLetters()){
				meFirstStage.add(new Position(l,start.getNumber()+1*direction));
			}
		}
		if(start.getNumber()+2*direction >= 0 && start.getNumber()+2*direction < board.getNumbers()){
			for(int l =start.getLetter() - 2; l<= start.getLetter() + 2 ;l++ ){
				if(!(l<0) && l < board.getLetters()){
					meSecondStage.add(new Position(l,start.getNumber()+2*direction));
				}
			}
		}
		if(start.getNumber()+3*direction >= 0 && start.getNumber()+3*direction < board.getNumbers()){
			for(int l =start.getLetter() - 3; l<= start.getLetter() + 3 ;l++ ){
				if(!(l<0) && l < board.getLetters()){
					meThirdStage.add(new Position(l,start.getNumber()+3*direction));
				}
			}
		}
		
		// Add current position of all threats to all stages - in case they don't move, 
		// this is important
		opFirstStage.addAll(threats);		
		
		// Add opponents possible positions for each threatening token, look 3 steps in the future
		Iterator<Position> iterator = threats.iterator();
			
		while(iterator.hasNext()){
			Position position = iterator.next();
			
			//Step one
			for(int l =position.getLetter() - 1; l<=position.getLetter() + 1 ;l++ ){
				if(!(l<0) && l < board.getLetters()){
					opFirstStage.add(new Position(l,position.getNumber()-1*direction));
				}
			}
			//Step two
			for(int l =position.getLetter() - 2; l<= position.getLetter() + 2 ;l++ ){
				if(!(l<0) && l < board.getLetters()){
				//	Position posi = new Position(l,position.getNumber()-2*direction);
					opSecondStage.add(new Position(l,position.getNumber()-2*direction));
				}
			}
			//Step three, check for borders
			if(position.getNumber()-3*direction >= 0 && position.getNumber()- 3*direction < board.getNumbers()){
				for(int l =position.getLetter() - 3; l<= position.getLetter() + 3 ;l++ ){
					if(!(l<0) && l < board.getLetters()){
						opThirdStage.add(new Position(l,position.getNumber()-3*direction));
					}
				}
			}
		}	
		
		// Add the direct front position
	/*	Position aThreat = new Position(start.getLetter(),start.getNumber()+1*direction);
		if(board.getColor(start.getLetter(), start.getNumber()+1*direction) != NONE){
			opFirstStage.add(aThreat);
		}*/
		
		// add all previous possible moves to the next stage
		opSecondStage.addAll(opFirstStage);
		opThirdStage.addAll(opSecondStage);
		
		// Then remove the positions where are my own tokens ("hurdles")
		meFirstStage.removeAll(hurdles);
		meSecondStage.removeAll(hurdles);
		meThirdStage.removeAll(hurdles);
		
		// Now check for each stage if opponent could kill this token
		// First move is made by my token. Then the opponent makes his "first" move.
		// So, if meFirstStage and opFirstStage overlap, my token is threatened at one position.
		Position pos = null;
		Iterator<Position> iterFirst = meFirstStage.iterator();
		
		
		while(iterFirst.hasNext()){
			pos = iterFirst.next();
			// Is there at least one move I can make, which is not threatened?
			if(!(opFirstStage.contains(pos))){
				breakthrough = true;
			}
			// If the move is threatened, remove it.
			else{
				iterFirst.remove();
			}
		}
		// Quit here, if first stage is already threatened everywhere
		if(!breakthrough){
			return null;
		}
		
		// Second stage
		Iterator<Position> iterSec = meSecondStage.iterator();
		breakthrough = false;

		
		while(iterSec.hasNext()){
			pos = iterSec.next();
			
			if(!(opSecondStage.contains(pos))){
				breakthrough = true;
			}
			else{
				iterSec.remove();
			}
		}
		if(!breakthrough && !meSecondStage.isEmpty() ){
			return null;
		}

		// Third stage
		Iterator<Position> iterThi = meThirdStage.iterator();
		breakthrough = false;
		
		while(iterThi.hasNext()){
			pos = iterThi.next();
			
			if(!(opThirdStage.contains(pos))){
				breakthrough = true;
			}
			else{
				iterThi.remove();
			}
		}
		
		
		try{
			if(breakthrough ||  meThirdStage.isEmpty()){
				moveChain = breakthrough(start);
			
				if(moveChain == null){
					return null;
				}
				return moveChain;
			}
		}catch(PresetException e){
			System.err.println(e.getMessage());
		}
			
		return null;
	}
	
	/**
	 * Constructs a move[] with connected, valid moves.
	 * 
	 * @param start the start Position
	 * @return a move[3] array with the next moves, starting with 0, to be executed to win
	 */
	private Move[] breakthrough(Position start) throws PresetException{
		Move[] moveChain = new Move[3];
		
		Iterator<Position> it1 = meFirstStage.iterator();
		Iterator<Position> it2 = meSecondStage.iterator();
		Iterator<Position> it3 = meThirdStage.iterator();
		
		while(it1.hasNext()){
			Position p1 = it1.next();
			try{
				moveChain[0] = new Move(new Position(start.getLetter(),start.getNumber()),new Position(p1.getLetter(),p1.getNumber()));
				
				//Already reached the goal?
				if(p1.getNumber() == 0 || p1.getNumber() == board.getNumbers()-1){
					return moveChain;
				}
				
			}catch(PresetException e){
				System.err.println(e.getMessage());
			}
			
			while(it2.hasNext()){
				Position p2 = it2.next();
				if(connection(new Position(p1.getLetter(),p1.getNumber()),new Position(p2.getLetter(),p2.getNumber()))){
					moveChain[1] = goodMove;
					if(p2.getNumber() == 0 || p2.getNumber() == board.getNumbers()-1){
						return moveChain;
					}
					
					while(it3.hasNext()){
						Position p3 = it3.next();
						if( connection(moveChain[1].getEnd(),new Position(p3.getLetter(),p3.getNumber()) )){
							moveChain[2] = goodMove;
							return moveChain;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets 2 positions which shall be connected to a move, if possible
	 * 
	 * @param p1 start
	 * @param p2 end
	 * @return true if the positions can be connected to a move
	 */
	private boolean connection(Position p1,Position p2){
		int direction = color == RED ? 1 : -1;
		boolean canConnect = false;
		
		if(p2.getNumber() == p1.getNumber()+1*direction){
			if(p2.getLetter() >= p1.getLetter() -1 && p2.getLetter() <= p1.getLetter() +1 ){
				canConnect = true;
				try{
					goodMove = new Move(new Position(p1.getLetter(),p1.getNumber()),new Position(p2.getLetter(),p2.getNumber())); 
				}catch(PresetException e){
				}
			}
		}
			
		return canConnect;
	}
}
