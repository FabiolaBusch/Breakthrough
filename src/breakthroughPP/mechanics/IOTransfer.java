package breakthroughPP.mechanics;

import breakthroughPP.preset.Requestable;
import breakthroughPP.gui.Showable;

/**
 * Class to transport chosen input and output Methods
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author J.B.
*/
public interface IOTransfer{
	/**
	 * Gets the input method of the red player
     */
	public Requestable getInputMethodRed();

	/**
	 * Gets the input method of the blue player
     */
	public Requestable getInputMethodBlue();
	
	/**
	 * Gets the output method of the red player
     */
	public Showable getOutputMethodRed();
	
	/**
	 * Gets the output method of the blue player
     */
	public Showable getOutputMethodBlue();

	/**
	 * Sets a new input method for the red player
	 * @param newinputMethod instance being able to deliver a move from a users input
	 */
	public void setInputMethodRed(Requestable newinputMethod);

	/**
	 * Sets a new input method for the blue player
	 * @param newinputMethod instance being able to deliver a move from a users input
	 */
	public void setInputMethodBlue(Requestable newinputMethod);

	/**
	 * Sets a new output method for the blue player
	 * @param newoutputMethod instance being able to visualise the game
	 */
	public void setOutputMethodRed(Showable newoutputMethod);

	/**
	 * Sets a new output method for the blue player
	 * @param newoutputMethod instance being able to visualise the game
	 */
	public void setOutputMethodBlue(Showable newoutputMethod);

}
