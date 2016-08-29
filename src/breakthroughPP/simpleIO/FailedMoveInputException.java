package breakthroughPP.simpleIO;

import breakthroughPP.preset.PresetException;

/**
 * This Exception indicates that a move a player wants to make failed for reasons described in the
 * message
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 */
public class FailedMoveInputException extends PresetException{

	private static final long serialVersionUID = -2186016703362892293L;

	/**
	 * Default constructor
	 */
	public FailedMoveInputException() {
	}

    /**
	 * Constructor containing a message describing which error occurred
	 * @param msg The error message
     */
    public FailedMoveInputException(String msg) { 
    	super(msg);	
    }

}
