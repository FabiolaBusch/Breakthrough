package breakthroughPP.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;

import javax.swing.JComponent;

import breakthroughPP.preset.*;

/**
 * Glass pane which can paint over other JComponents of a gui. Here all possible moves are shown by
 * lines.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author A.Z.
 */
class MyGlassPane extends JComponent implements ItemListener, Setting {

	public static final long serialVersionUID = 178514856148965L;  
	
	// Fields of an instance of the class
	private GridPanel gridPanel;
	private HashSet<Move> allPossibleMovesOfRed;
	private HashSet<Move> allPossibleMovesOfBlue;
    private Point p;
    
    
 	// ==== Constructors =============================================================================

	/**
	 * Default Constructor
	 */
    public MyGlassPane(){
    }

	/**
	 * Constructor with parameters, calls {@link #updateMoveAndPosition(Point point, GridPanel
	 * gridpanel)}
	 * @param panel panel where to get the possible moves from
	 */
    public MyGlassPane(GridPanel panel){
    	updateMoveAndPosition(new Point(0,0), panel);
    }
    
    
 	// ==== Instance function ========================================================================

	/** 
	 * This function initialises the fields of this instance. It calls the {@link
	 * breakthroughPP.gui.GridPanel#getAllPossibleMovesOfRed} and {@link
	 * breakthroughPP.gui.GridPanel#getAllPossibleMovesOfBlue} of the gridpanel. According to the
	 * color saved in the gridpanel either only the red possible moves, the blue possible moves or
	 * both are updated.
	 * @param point Center of the buttom left JButton in the gridpanel
	 * @param gridpanel Reference to the used gridpanel
	 */
     public void updateMoveAndPosition(Point point,GridPanel gridpanel){
        p = point;
        gridPanel = gridpanel;
        
        if(gridPanel.getPlayerInt() == RED){
			allPossibleMovesOfRed = gridPanel.getAllPossibleMovesOfRed();
		}
		else if(gridpanel.getPlayerInt() == BLUE){
			allPossibleMovesOfBlue = gridPanel.getAllPossibleMovesOfBlue();
		}
		else{
			allPossibleMovesOfRed = gridPanel.getAllPossibleMovesOfRed();
			allPossibleMovesOfBlue = gridPanel.getAllPossibleMovesOfBlue();
		}
    }
  


    /**
	 * This Function decides if all possible moves of red of blue or of both colors are painted
	 * through lines
     * @param g The Graphics reference where to paint the lines on
     */
    @Override
	protected void paintComponent(Graphics g){
    	// 2 Player of one Gui ----------------------
    	if(gridPanel.getPlayerInt() == NONE){
    		if(gridPanel.getWhosTurn() == RED){
    			paintMoves(g, allPossibleMovesOfRed);
    		}
    		if(gridPanel.getWhosTurn()== BLUE){
    			paintMoves(g, allPossibleMovesOfBlue);
    		}
    	}
    	
    	// every Player other Gui ------------------
    	else if(gridPanel.getPlayerInt() == RED){
    			paintMoves(g,allPossibleMovesOfRed);
    	}else if(gridPanel.getPlayerInt()== BLUE){
    			paintMoves(g, allPossibleMovesOfBlue);
      	}
        
    }
    
    /** Paint lines representing all possible moves for one color
     * @param g The Graphics reference where to paint the lines on
     * @param moveHashSet set of the possible moves which should be represented by lines 
     */
    public void paintMoves(Graphics g, HashSet<Move> moveHashSet){
    	
        for(Move move : moveHashSet){
     	   if(move != null){
     		   
     		   	g.setColor(Color.black);
     		   	Position start 	= move.getStart();
     		   	Position end 	= move.getEnd();
     	   
     		   	// Point x and y is a Correction cuz Glasspane has another start position
     		   	int x1 = p.x + gridPanel.getButtons()[start.getNumber()][start.getLetter()].getX();
     		   	int y1 = p.y + gridPanel.getButtons()[start.getNumber()][start.getLetter()].getY();
     		   	int x2 = p.x + gridPanel.getButtons()[end.getNumber()][end.getLetter()].getX();
     	   		int y2 = p.y + gridPanel.getButtons()[end.getNumber()][end.getLetter()].getY();
     	   
   
     	   		// paint line with some stroke, relatively to button size
     	   		Graphics2D g2 = (Graphics2D) g;
     	   		int stroke = gridPanel.getDimX()*gridPanel.getDimY(); 
     	   		stroke = 3 - stroke / 225 ;
     	   		g2.setStroke(new BasicStroke(stroke));
     	   
     	   		g2.drawLine(x1, y1, x2, y2); 
       	   		g2.drawPolygon(new int[] {x2,x2-3,x2+3},new int[] {y2,y2-3,y2-3},3);
     	   }
        }
    }
    

    
    /**
	 * update the location of the glasspane which depends on size of the frame
	 */
	public void update(){
		int count_buttonInX = gridPanel.getDimX(); // count only buttons in X
		int count_buttonInY	= gridPanel.getDimY(); // count only buttons in Y	
		int panelHeight 	= gridPanel.getHeight();
		int panelWidth 		= gridPanel.getWidth();
		
		int size_buttonInX 	= panelWidth/count_buttonInX; 
		int size_buttonInY	= panelHeight/count_buttonInY;
		updateMoveAndPosition(
				new Point(	gridPanel.getX()+size_buttonInX/2, 
							gridPanel.getY()+size_buttonInY/2),gridPanel);
	}

	// ==== Interface functions/ methods ==============================================================

    /**
	 * Reacts on changes of the state of the JCheckBox changeButton
     * set the Visiblity of Glasspane true or false depending on the state of the JCheckBox
	 * @see java.awt.event.ItemListener#itemStateChanged(ItemEvent e)
     */
    @Override
	public void itemStateChanged(ItemEvent e){
        setVisible(e.getStateChange() == ItemEvent.SELECTED);
    }

    
}
