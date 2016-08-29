package breakthroughPP.gui;

import java.awt.*;
import java.awt.event.*; 

import javax.swing.*;

import breakthroughPP.board.*;
import breakthroughPP.preset.*;
/**
 * This class provides a gui to show the game. It consist out of different subpanels which implement
 * at least the {@link breakthroughPP.gui.Showable}. Additionally, the center panel implements also
 * the {@link breakthroughPP.preset.Requestable} interface to be able to deliver moves. 
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author A. Z.
 * @author J.B.
*/ 

public class MainFrame extends JFrame implements ActionListener, Setting, Requestable, Showable, ComponentListener {

	private static final long serialVersionUID = 1L;
	
	// Information
	private Viewer boardViewer;

	// Buttons
	private JButton exitButton;
	private JButton saveButton;	

	// Components
	private MyGlassPane glassPane;
	private GridPanel centerPanel;
	private InfoMovePanel westPanel;
	private InfoPanel eastPanel;
	private JPanel southPanel;	
	private JCheckBox changeButton; 

	// indicating variables
	private boolean debugMode;
	private Storable store;
	private int myColor;

	
	// ==== Constructors ==============================================================================

	/** 
	 * Default Constructor
     */
	public MainFrame(){
		this(null,0,null);
	}
	
	/**
	 * Constructor with a color, 
	 * @param color color of the player
	 */	
	public MainFrame(int color){
		this(null, color, null);
	}


	/**
	 * Constructor
	 * @param color color of the player
     * @param newboardviewer Viewer on the board of a player 
	 */
	public MainFrame(Viewer newboardviewer, int color){
		this(newboardviewer, color, null);
	}

	/**
	 * Constructor, initializes the frame.
	 * @param newboardviewer contains board information
	 * @param playercolor the player who belongs to this board
	 * @param newstore instance being in charge of saving
	 */
	public MainFrame(Viewer newboardviewer, int playercolor, Storable newstore){
		if(newboardviewer != null){		
									
			try{
				init(newboardviewer, playercolor);
			}catch(VisualisationException ve){
				System.err.println(ve.getMessage());
			}
		}
		store = newstore; 
	}

    // ==== Getter ====================================================================================
	
	public int getWhosTurn(){
		return centerPanel.getWhosTurn();
	}
		
    // ==== Setter ====================================================================================

	protected void setDebugMode(boolean showmoves){
		debugMode = showmoves; 
		setVisible(debugMode);
		changeButton.setSelected(debugMode);	
	}

    // ==== Instance functions ========================================================================
	
	/**
	 * update the location of the glasspane with depens on size of the frame
	 */
	public void updateGlassPane(){
		int count_buttonInX = centerPanel.getDimX(); // count only buttons in X
		int count_buttonInY	= centerPanel.getDimY(); // count only buttons in Y	
		int panelHeight 	= centerPanel.getHeight();
		int panelWidth 		= centerPanel.getWidth();
		
		int size_buttonInX 	= panelWidth/count_buttonInX; 
		int size_buttonInY	= panelHeight/count_buttonInY;
		glassPane.updateMoveAndPosition(
				new Point (	centerPanel.getX()+size_buttonInX/2, 
							centerPanel.getY()+size_buttonInY/2),centerPanel);
	}

	/**
	 * update button size with depens on size of the frame
	 */
	public void updateButtons(){
		int count_buttonInX = centerPanel.getDimX(); // count only buttons in X
		int count_buttonInY	= centerPanel.getDimY(); // count only buttons in Y	
		int panelHeight 	= centerPanel.getHeight();
		int panelWidth 		= centerPanel.getWidth();
		
		int size_buttonInX 	= panelWidth/count_buttonInX; 
		int size_buttonInY	= panelHeight/count_buttonInY;
		
		centerPanel.setButtonSize(size_buttonInX, size_buttonInY);
	}


    // ==== Interface functions =======================================================================

	/**
	 * Initialisation of the board
	 * @see breakthroughPP.gui.Showable
	 * @param newboardviewer contains infromation form the board
	 * @param playercolor int specified in {@link breakthroughPP.preset.Setting} and indicating the 
	 * player pocessing the board and this gui
	 * @throws VisualisationException if one of the subpanels throw such an exception during
	 * initialisation
	 */
	@Override
	public void init(Viewer newboardviewer, int playercolor) throws VisualisationException{

		myColor = playercolor;
		if(myColor == RED){
			setTitle("red player");
		}else if(myColor == BLUE){
			setTitle("blue player");		
		}else{
			setTitle("both players");
		}	

		// Do not show the possible moves
		debugMode = false;
		
		boardViewer = newboardviewer;
		setPreferredSize(new Dimension(1000,500));
		
		// Layout	
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	// for a clean exit
																	// including notification of the
																	// opponent
		setLayout(new BorderLayout());
		
		//Component Listener
		addComponentListener(this);

		// Button dimension
		Dimension buttonsize = new Dimension(200,25);

		// Exit button
		exitButton = new JButton("EXIT");
		exitButton.setPreferredSize(buttonsize);
		exitButton.addActionListener(this);

		// Store button if function is available
		if(store != null){	
			saveButton = new JButton("Save");
			saveButton.setPreferredSize(buttonsize);
			saveButton.addActionListener(this);
		}

		// Checkbox for showing the moves
		changeButton = new JCheckBox("DebugMode");
		changeButton.setSelected(false);

		// ---- South panel -----------------------------------------------------------------------
		southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southPanel.setOpaque(false);
		if(store!= null){
			southPanel.add(saveButton);
		}
		southPanel.add(exitButton);
		southPanel.add(changeButton);

		// ---- East panel ------------------------------------------------------------------------
		eastPanel = new InfoPanel(new Dimension(300,500));
		eastPanel.init(newboardviewer,playercolor); // 
		
		// ---- West panel ------------------------------------------------------------------------
		westPanel = new InfoMovePanel(new Dimension(300,500));	
		westPanel.init(newboardviewer,playercolor); // 
		
		// ---- Center panel ----------------------------------------------------------------------
		centerPanel = new GridPanel(boardViewer,playercolor);
		
		//--- Glass Pane --------------------------------------------------------------------------
		glassPane = new MyGlassPane(centerPanel); 
        changeButton.addItemListener(glassPane);
		
			
		// To catch validation problems during reinitialisation and react on the Container not being
		// in the right state
		try{
			getContentPane().removeAll(); 
		}catch(java.awt.IllegalComponentStateException icse){
			// Ignore it as a next init or change of the component may come with an update
		}

		// add on this Frame ------------------------------------------------------------------------ 
		setGlassPane(glassPane);
		add(southPanel,BorderLayout.SOUTH);
		add(centerPanel,BorderLayout.CENTER);
		add(eastPanel,  BorderLayout.EAST);
		add(westPanel, BorderLayout.WEST);
		pack();
		setVisible(true);
	}

	/** 
	 * Update the board and all its Panels
	 * @see breakthroughPP.gui.Showable
	 * @throws VisualisationException if one subpanel is unable to update
	 */
	@Override
	public void update() throws VisualisationException {
	//Important order of updates: MoveInfoPanel update before update valid moves and status
	//InfoPanel update after update valid moves and status
		//update Button Size ----------------------------------------------------------------------
		updateButtons();
		
		//update Move Info Panel -------------------------------------------------------------------
		westPanel.update();
		
		//update all valid moves and update Status if somebody win and whosTurn+1 and set request(boardViewer.getLastChange())
		centerPanel.update();
		
		//update Info Panel ------------------------------------------------------------------------
		eastPanel.update();
		
		//update GlassPane -------------------------------------------------------------------------
		glassPane.update();
		
	}

	/** 
	 * Interface function reacting on an ActionEvent e.g. of the exit button
	 * @see java.awt.event.ActionListener
	 */
	@Override
	public void actionPerformed(ActionEvent e){
		// Leave
		if(e.getSource() == exitButton){
			if(myColor != boardViewer.turn() || myColor == 2 || boardViewer.getLastChange()==null||
				boardViewer.getStatus().isRedWin() || boardViewer.getStatus().isBlueWin()){
				Object[] options = {"Wait","Force Exit (not recommended)"};
				int choice = JOptionPane.showOptionDialog(null, "Please wait for your turn", "Warning",
			        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			        null,options, options[0]);
				if(choice != 0){
					System.exit(1);
				}
			}else{ 
				int choice = JOptionPane.showConfirmDialog(null,
		        "Do you wish to surrender?", "Waterloo", JOptionPane.YES_NO_OPTION);
	
				if(choice == 0){
					// surrender case
					centerPanel.setGameOver(true);
				}
			}
		}
		// Save current game
		else if(store != null && e.getSource() == saveButton){
			try{
				String name = JOptionPane.showInputDialog("Please input the name of the file you want to"+
					"create");
				if(name != null){
					store.store(name);
					System.out.println("Saving successful");
				}else{
					System.out.println("Saving canceled");
				}
			}catch(IllegalArgumentException iae){
				System.err.println(iae.getMessage());
				System.err.println("Continue to play");
			}catch(UnsupportedOperationException uoe){
				System.err.println("Failure of saving the file\n" + uoe.getMessage());
				System.err.println("Continue to play");
			}
		}
	}

	//--- Component Listener ------------------------------------------------------------------
	/**
	 * updates the glass pane and the center panel
	 * @see java.awt.event.ComponentListener
	 */
	@Override
	public void componentHidden(ComponentEvent arg0) {
		glassPane.update();  
		centerPanel.updateButtons();
	}

	/**
	 * updates the glass pane and the center panel
	 * @see java.awt.event.ComponentListener
	 */
	@Override
	public void componentMoved(ComponentEvent arg0) {
		glassPane.update();
		centerPanel.updateButtons();
	}

	@Override
	//buttonSize depends on Height and Width of center panel 
	//Remind: center panel is a GridPanel with buttons
	/**
	 * updates the glass pane and the center panel
	 * @see java.awt.event.ComponentListener
	 */
	public void componentResized(ComponentEvent arg0) {
		glassPane.update();
		centerPanel.updateButtons();
	}

	/**
	 * updates the glass pane and the center panel
	 * @see java.awt.event.ComponentListener
	 */
	@Override
	public void componentShown(ComponentEvent arg0) {
		glassPane.update();
		centerPanel.updateButtons();
	}

	/**
	 * Deliver a move made on the GridPanel
	 * @see breakthroughPP.preset.Requestable#deliver()
	 * @return the move made on the GridPanel
	 */
	@Override
	public Move deliver() throws PresetException{	
		return centerPanel.deliver();
	}
	
    // ==== Test main =================================================================================

	public static void main(String[] args) throws Exception{

		Viewable viewable = null;
		try{
			viewable = new Board(6,6);
		}catch (PresetException e1){
			System.err.println(e1.getMessage());
		}
		/*
		* If you choose "new Mainframe(viewer, playerInt=2)" then two Players on one Board
		* If you choose "new Mainframe(viewer, playerInt=0 or 1)" 
		* 				then you will be Red or Blue (over network game or against a computer)
		*
		*	Example to use: 		Redplayer = newMainFrame(viewer, 0)
		*					 		Blueplayer = newMainFrame(viewer, 1)
		*
		*							move = Redplayer.deliver();
		*							Blueplayer(request(move));
		*							
		*							Blueplayer.setCanIPlay(true);
		*							move = Blueplayer.deliver();
		*							RedPlayer.request(move);
		*
		*							Redplayer.setCanIPlay(true). 
		*							..
		*
		*							(after a move canIPlay will be false)
		*/
		MainFrame mf = new MainFrame(viewable.viewer(),2);
		mf.setVisible(true);
		mf.setDebugMode(true);
	}

}				 


