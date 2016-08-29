package breakthroughPP.gui;

import javax.swing.*;
import breakthroughPP.preset.Setting;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * Buttons which are placed on the board. Those are the tokens for the players.
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016 
 * Gr CodeSalat
 * 
 * @author Fabiola Buschendorf
 * @author A.Z.
 */
public class GameButton extends JButton implements Setting {


    private static final long serialVersionUID = 6485935364085498563L;
    private Color color;
	private int WIDTH;
	private int HEIGHT;
	private int colorInt;
	private int letter, number;
	private boolean isSelected;

	
	// ==== Constructors ==============================================================================

	/**
	 * Default constructor
	 */
	public GameButton(){
		this("",2,0,0);
	}

	/**
	 * letter, number same PositionPoints as in Board
	 * colorInt:  Its an Integer 
	 * with RED=0,BLUE=1, GRAY=2 Its easyier. Dont need Convert or use the Class Color
	 * @param colorString Name of a color defined in {@link java.awt.Color}
	 * @param colorPlayer Integer defining the color of a player defined in {@link
	 * breakthroughPP.preset.Setting}	
	 * @param xposition x position of the button in a grid
	 * @param yposition y position of the button in a gird
	 */
	public GameButton(String colorString, int colorPlayer,int xposition, int yposition){
		setSize(20,20);
		colorInt = colorPlayer;
		setLetter(xposition);
		setNumber(yposition);
		try {
		    Field field = Class.forName("java.awt.Color").getField(colorString);
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = new Color(0,0,0,0); // Transparent
		}
		isSelected = false;
	}

// ==== Getter ====================================================================================

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(WIDTH,HEIGHT);
	}
	
	public Color getColor(){
		return color;
	}

	public int getColorInt(){
		return colorInt;
	}

	public int getNumber(){
		return number;
	}

	public int getLetter(){
		return letter;
	}


// ==== Setter ====================================================================================
	@Override
	public void setSize(int width, int height){
		this.WIDTH = width;
		this.HEIGHT = height;
		setPreferredSize(new Dimension(width,height));	
	}
		
	public void setColor(Color newcolor){
		color = newcolor;
	}
	
	public void setColorInt(int newcolorint){
		colorInt = newcolorint;
	}

	public void setNumber(int yposition){
		number = yposition;
	}

	public void setLetter(int xposition){
		letter = xposition;
	}

	@Override
	public void setSelected(boolean selected){
		isSelected = selected;
	}

// ==== Instancemethods ===========================================================================
	/**
	 * Paints the circle of red or blue color into the JButton. In case the JButton is selected it
	 * receives an additional coloring of the background
	 * @see javax.swing.JComponent#paintComponent(Graphics g)
	 */
	@Override
	protected void paintComponent(Graphics g){

		setBackground(Color.white);
		super.paintComponent(g);
		
		int diameterX = WIDTH - 4;
		int diameterY = HEIGHT - 4;

		if(isSelected){
			Color c = new Color(0,100,100);
			g.setColor(c);
			g.fillRect(0, 0, (int) (WIDTH+WIDTH*(0.6)), (int) (HEIGHT+HEIGHT*(0.6))); // looks
																		//better with +10
		}
		
		g.setColor(color);
		g.fillOval(1,1,diameterX,diameterY);

	}


	/**
	 * Allows to compare two button instances.
	 * @param other the button to compare with this
	 * @return Two buttons are equal if they are at the same
	 * position in a grid.
	 */
	public boolean equals(GameButton other){
		return (other.getNumber() == this.getNumber()) && (other.getLetter() == this.getLetter());
	}

	/** 
	 * @return True if the button is selected, false else
	 */
	@Override
	public boolean isSelected(){
		return isSelected;
	}
}
