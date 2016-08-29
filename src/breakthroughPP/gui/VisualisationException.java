package breakthroughPP.gui;

/** 
 * A VisualisationException indicates that visualisation of the game state failed. It is thrown for
 * instance from methods of the {@link breakthroughPP.gui.Showable} interface. 
 * 
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author J.B.
 */

public class VisualisationException extends Exception {

	// constant used for Serialization
	public static final long serialVersionUID = 14L;

	/**
	 * Default Constructor
     */
	public VisualisationException(){
	}

	/**
	 * Constructor with message
	 * @param msg Description of the Exception which is thrown
     */
	public VisualisationException(String msg){
		super(msg);
	}
}
