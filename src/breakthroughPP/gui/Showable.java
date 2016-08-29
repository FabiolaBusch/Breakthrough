package breakthroughPP.gui;

import breakthroughPP.preset.Viewer;
/** 
 * This interface provides the possibility to show a Board
 * <p>
 * Georg-August University Goettigen
 * APP breakthroughPP
 * SoSe 2016
 * @author J.B.
 */

public interface Showable {
	
	/**
	 * Initialise a class with an information class to set for instance the size of the board
	 * @param viewer Information class transfering data from a game board
	 * @param color Color of the player managing the board specified in the interface Setting
	 * @throws VisualisationException if an problem occures which makes visualisation impossible
     */
	void init(Viewer viewer, int color) throws VisualisationException;

	/**
	 * Updates the output by using the Functions of the Viewer interface.
	 * @throws VisualisationException if an error occures preventing update from successful
	 * visualisation
	 */
	void update() throws VisualisationException;
	
}
