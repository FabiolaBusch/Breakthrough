package breakthroughPP.gui;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.Dimension;
import java.util.IllegalFormatException;
import java.awt.BorderLayout;
import java.awt.Color;

import breakthroughPP.preset.*;
import breakthroughPP.preset.Position;


/**
 * The western panel which shows a list of moves made in the past.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016 
 * Gr CodeSalat
 * @author A.Z.
 */

public class InfoMovePanel extends JPanel implements Setting, Showable {

	public static final long serialVersionUID = 1257478952697438974L;
	
	// Fields of an instance of the class
	private JTextPane moveArea;
	private Dimension panelDim;
	private JScrollPane scrollpane; 
	private Viewer viewer;
	
	/**
	 * Font size for a Move from of a Player
	 */
    private static final int FONT_SIZE = 15;
    
	/** Font family for a Move of a Player */
    private static final String FONT_FAMILY = "Arial";

	/** Fonts for a Move from a Blue Player */
	public static final SimpleAttributeSet PLAYERBLUE = new SimpleAttributeSet();
	
	/** Fonts for a Move from a Red Player */
	public static final SimpleAttributeSet PLAYERRED = new SimpleAttributeSet();
	
	/** Fonts for Content */

    public static final SimpleAttributeSet CONTENT = new SimpleAttributeSet();

	/**
	 * initialsition a  SimpleAttributeSets: PlayerBLUE, PlayerRED and CONTENT
	 */
    static {
    	
        StyleConstants.setForeground(PLAYERBLUE, Color.blue);
        StyleConstants.setFontFamily(PLAYERBLUE, FONT_FAMILY);
        StyleConstants.setFontSize(PLAYERBLUE, FONT_SIZE);
        
        
        StyleConstants.setForeground(PLAYERRED, Color.red);
        StyleConstants.setFontFamily(PLAYERRED, FONT_FAMILY);
        StyleConstants.setFontSize(PLAYERRED, FONT_SIZE);
        
        StyleConstants.setForeground(CONTENT, Color.black);
        StyleConstants.setFontFamily(CONTENT, FONT_FAMILY);
        StyleConstants.setFontSize(CONTENT, 13);
        StyleConstants.setBold(CONTENT, true);
    }

	
	// ==== Constructors ==============================================================================

	/** 
	 * Default constructor
	 * @see #InfoMovePanel(Dimension) with argument null
	 */	
	public InfoMovePanel(){
		this(null);
	}

	/**
	 * Constructor with parameters. Sets panels preferred size and sets it visible.
	 * @param dimension Dimensions of the Panel
	 */
	public InfoMovePanel(Dimension dimension){
		setPreferredSize(dimension);
		setOpaque(false);

		moveArea = null;
		viewer	 = null;
		panelDim = dimension;
		scrollpane = null;
	}
	
	
	// ==== Setter ====================================================================================

	/** 
	 * Add a move to area which shows the last moves (log). 
	 * The font-color depends on the colorPlayer.
	 * @param newtext a new line which is written in the log-box.
	 * @param colorPlayer the color of the current player.
	 */
	public void setMoveArea(String newtext, int colorPlayer){
		if(colorPlayer == BLUE){
			setText(newtext,PLAYERRED);
		}
		if (colorPlayer == RED){
			setText(newtext,PLAYERBLUE);
		}
	}

	/**
	 * add a Text with AttributeSet
	 * @param newtext Any String to add to moveArea. Warning dont need "\n"
	 * @param set Need a SimpleAttribute from static block
	 */
	public void setText(String newtext,SimpleAttributeSet set){
		try{
			moveArea.getDocument().insertString(moveArea.getDocument().getLength(), newtext+"  \n", set);
			
			if(scrollpane != null){
				/*
				int maxbar = scrollpane.getVerticalScrollBar().getMaximum();
				
				try{
					scrollpane.getVerticalScrollBar().setValue(maxbar);
				}catch(NullPointerException nulle){
					nulle.printStackTrace();
					System.out.println("scrollbar leeren");
					
					maxbar =0;
					
				}*/
				revalidate();
				//System.out.println("maxbar"+maxbar);
				
				
			}
			
		}catch (BadLocationException e){
			System.err.println(e.getMessage());
		}
	}

	// ==== Instance functions ========================================================================

	/**
	 * Build the components of the panel
	 */
	private void createPanelComponents(){
		//Labels
		JLabel moveLabel = new JLabel("Course of the game:");
		
		//JTextfield 
		JTextField courseOfGame = new JTextField();
		courseOfGame.setEditable(false);
		
		// JTextPane
		moveArea = new JTextPane();
		moveArea.setEditable(false);
		setText("Start   ->   End", CONTENT);
		
		// ScrollPane for the area depicting the moves
		scrollpane = new JScrollPane(moveArea);
		
		//set Scrollbar to scrollpane 
		JScrollBar scrollbar = new JScrollBar();
		scrollpane.setVerticalScrollBar(scrollbar);
		scrollpane.setPreferredSize(new Dimension((int) (panelDim.getWidth()*0.7),(int) (panelDim.getWidth()*0.7)));
		scrollpane.getVerticalScrollBar().setAutoscrolls(true);	
		//scrollpane.setVerticalScrollBarPolicy(scrollpane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//JPanel 
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(moveLabel, BorderLayout.NORTH);
		main.add(scrollpane, BorderLayout.CENTER);
		main.setOpaque(false);

		// ---- Joined ----------------------------------------------------------------------------
		add(main, BorderLayout.NORTH);
	}

	/**
	 * Change the String of Move. Example moveFormat ="B2   ->      C2"
	 * @param newmove the new move which is going to be written in the log-box.
	 * @return the string representation of the last move.
	 */
	private String formatMoveToString(Move newmove){
		
		Position startPos =  newmove.getStart();
		Position endPos =  newmove.getEnd();
		
		String startPosString =  startPos.toString();
		String endPosString =  endPos.toString();
		startPosString = startPosString.concat("   ->      "+endPosString);
		return startPosString;
	}

	// ==== Instance functions/ methods ===============================================================

	/**
	 * Initialisation of the Panel.
	 * @param newviewer contains the board information
	 * @param playerColor the players color. 
	 * @see breakthroughPP.gui.Showable
	 * @throws VisualisationException if the viewer is not specified.
	 */
	@Override
	public void init(Viewer newviewer, int playerColor) throws VisualisationException{

		viewer = newviewer;
		if(viewer == null){
			throw new VisualisationException("The class containing (newviewer) the data for is null");
		}

		// build the panel
		createPanelComponents();
	}

	/**
	 * Updates the list of moves.
	 * @see breakthroughPP.gui.Showable
	 * @throws VisualisationException if the new move-string is formated wrong.
	 */
	@Override
	public void update() throws VisualisationException{
		Move move = viewer.getLastChange();
		if(move!= null){
			try{
				int colorPlayer = viewer.turn();
				String moveString = formatMoveToString(move);
				setMoveArea(moveString, colorPlayer);

			}catch(IllegalFormatException e){
				throw new VisualisationException(e.getMessage());
			}
		}
	}

}
