
package breakthroughPP.gui;

import javax.swing.*;

import java.awt.*;

import breakthroughPP.preset.*;

/** 
 * This class provides a JPanel which prints information to the user. The information depicted
 * consists of the numer of remaining tokens in the game for the red and blue player as well as a
 * messageArea. The least inlcudes information whether a player has won or whos turn the current one
 * is according to the status of the game.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author J. B.
*/

public class InfoPanel extends JPanel implements Setting, Showable {

	// Prefent a serializable warning
	public static final long serialVersionUID = 2016L;

	// number of tokens in the game
	private int numRed;
	private int numBlue;

	// Output in the panel
	private JTextField redTokens;
	private JTextField blueTokens;
	private JTextArea messageArea;

	// Information and panel size
	private Viewer viewer;
	private Dimension panelDim;


	// ==== Constructors ==============================================================================

	/** 
	 * Default constructor
	 * calls the Constructor with a Dimension of 100,600
	 */
	public InfoPanel(){
		this(new Dimension(100,600));
	}


	/**
	 * Constructor with parameters.
	 * @param dim Dimensions of the Panel
	 */
	public InfoPanel(Dimension dim){
		panelDim = dim;
		setPreferredSize(dim);
		setOpaque(false);

		numRed = 0;
		numBlue = 0;

		redTokens = null;
		blueTokens= null;
		messageArea = null;
	}

	// ==== Getter ====================================================================================

	public int getNumRed(){
		return numRed;
	}

	public int getNumBlue(){
		return numBlue;
	}

	// ==== Instance functions/methods ===============================================================

	/** 
	 * Adds text to the Message area
	 * @param addmes added messageArea
	 * @throws VisualisationException if the InfoPanel has not been initialized yet.
 	 */
	public void appendText(String addmes) throws VisualisationException{
		if(messageArea == null){
			throw new VisualisationException("InfoPanel has not been initialised yet. Appending text"+
				" is not possible");
		}
		messageArea.append(addmes);
	}

	/** 
	 * Sets up the components of the gui components, such as the output of the amount of blue and red 
	 * tokens, the current message to the players and the author information.
	 */
	private void setupPanelComponents(){
		// Labels
		JLabel heading1 = new JLabel("Remaining tokens on the board:");
		JLabel heading2 = new JLabel("Messages:");
		JLabel red_tlabel = new JLabel("red tokens:");
		JLabel blue_tlabel = new JLabel("blue tokens:");

		// ---- Northern Text Field ---------------------------------------------------------------
		// JText Fields
		Font tf_font = new Font(null,Font.BOLD,11);
		Dimension dim_fields = new Dimension((int) (0.3*panelDim.getWidth()),30);
		
		redTokens = new JTextField();
		redTokens.setPreferredSize(dim_fields);
		redTokens.setEditable(false);
		redTokens.setHorizontalAlignment(SwingConstants.CENTER);
		redTokens.setBackground(Color.RED);
		redTokens.setFont(tf_font);
		
		blueTokens= new JTextField();
		blueTokens.setPreferredSize(dim_fields);
		blueTokens.setEditable(false); 
		blueTokens.setHorizontalAlignment(SwingConstants.CENTER); 
		blueTokens.setBackground(Color.BLUE); 
		blueTokens.setFont(tf_font);

		// Subpanel for the numbers
		JPanel subpanel = new JPanel(new GridLayout(0,2));
		subpanel.add(red_tlabel);
		subpanel.add(redTokens);
		subpanel.add(blue_tlabel);
		subpanel.add(blueTokens);
		subpanel.setOpaque(false);

		// Creating the top most panel
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		north.add(heading1, BorderLayout.NORTH);
		north.add(subpanel, BorderLayout.CENTER);
		subpanel.setOpaque(false);

		// ---- Central Text Field ----------------------------------------------------------------	
		// JText Area
		messageArea = new JTextArea(10,20);
		messageArea.setPreferredSize(new Dimension((int) (panelDim.getWidth()*0.7),(int) (panelDim.getWidth()*0.7)));
		messageArea.setLineWrap(true);	// Line break
		messageArea.setWrapStyleWord(true);	// whole words
		messageArea.setEditable(false);
        JScrollPane scrollpane = new JScrollPane(messageArea);       

		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.add(heading2, BorderLayout.NORTH);
		center.add(scrollpane, BorderLayout.CENTER);
		center.setOpaque(false);

		// ----  South field ----------------------------------------------------------------------
		JPanel south = new JPanel();
		Font font = new Font(null,Font.ITALIC,10);
		JLabel authors = new JLabel("Breakthrough by CodeSalat");
		authors.setFont(font);
		south.add(authors);
		
		
		// ---- Joined ----------------------------------------------------------------------------
		add(north, BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		add(south,BorderLayout.SOUTH);
	}


	/** 
	 * This function counts the remaining tokens on the board.
	 * @throws VisualisationException if InfoPanel has not been initialized yet.
     */
	private void countTokensOnBoard() throws VisualisationException{
		if(viewer != null){	
			if(redTokens == null || blueTokens == null){
				throw new VisualisationException("InfoPanel has not been initialized yet");
			}


			// count the remaining red and blue tokens
			int red = 0;
			int blue= 0;
			for(int i=0; i < viewer.getDimY(); i++){			// numbers
				for(int j=0; j < viewer.getDimX(); j++){		// letters
					switch(viewer.getColor(j,i)){
						case RED:	red++;
									break;
						case BLUE:	blue++;
									break;
						default:
					}
				}
			}
			numRed = red;
			numBlue= blue;

			redTokens.setText(""+numRed);
			blueTokens.setText(""+numBlue);
		}else{
			throw new VisualisationException("Source of data (viewer) is null");
		}
	}

	/**
	 * Evaluate the status of the board and adjusts the output in the message area
	 * @throws VisualisationException if the InfoPanel has not been initialized yet or
	 * the source of data, the viewer is null, set in {@link #InfoPanel(Dimension)} 
     */
	private void evaluateBoardStatus() throws VisualisationException{

		// Is everything there what we need
		if(messageArea == null){
			throw new VisualisationException("InfoPanel has not been initialized yet");
		}
		if(viewer == null){
			throw new VisualisationException("Source of data (viewer) is null");
		}

		// Set the message area text according to the status of the board
		Status boardstatus = viewer.getStatus();	
		if(boardstatus.isRedWin()){
			messageArea.setText("The game is over. Congratulations to the red player.");
		}
		else if(boardstatus.isBlueWin()){
			messageArea.setText("The game is over. Congratulations to the blue player.");
		}
		else if(boardstatus.isOk()){
			if(viewer.turn() == RED){
				messageArea.setText("Red player, make your move carefully.");
			}else if(viewer.turn() == BLUE){
				messageArea.setText("Blue player, make your move carefully.");
			}
		}else if(boardstatus.isIllegal()){
			messageArea.setText("Illegal");
		}else{
			messageArea.setText("Undefined");
		}
	}

	
    // ==== Interface functions/ methods ==============================================================

	/**
	 * Initialise the Panel with all necessary information
	 * @see breakthroughPP.gui.Showable
	 * @param viewerclass class acting as connector to the board
	 * @param color color of the player pocessing this panel, is not relevant
	 * @throws VisualisationException if something goes wrong in {@link #countTokensOnBoard()} or 
	 * {@link #evaluateBoardStatus()} or {@link #countTokensOnBoard()} 
	 */
	@Override
	public void init(Viewer viewerclass, int color) throws VisualisationException{

		viewer = viewerclass;

		// Build the Panel
		setupPanelComponents();

		// an inital update to cound and set the status
		update();
	}

	/**
	 * Updates the output by using the Functions of the Viewer interface
	 * @see breakthroughPP.gui.Showable
	 * @throws VisualisationException like in {@link #countTokensOnBoard()} or 
	 * {@link #evaluateBoardStatus()}
	 */
	@Override
	public void update() throws VisualisationException{
			
		// Count the number of tokens on the board
		countTokensOnBoard();

		// set the Message
		evaluateBoardStatus();
	}

}
