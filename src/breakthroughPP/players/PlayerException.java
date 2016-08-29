package breakthroughPP.players;

/**
 * Used in the package breakthourghPP.player.
 * 
 * Georg-August University Goettingen
 * APP breakthroughPP 
 * SoSe 2016
 * @author Fabiola Buschendorf
 */

public class PlayerException extends Exception {

	private static final long serialVersionUID = 146464636345646163L;

	/**
	 * Default constructor
	 */
	public PlayerException(){
	}

	/**
	 * Constructor with one parameter
	 * @param msg description of the exception
	 */
	public PlayerException(String msg){
		super(msg);
	}
	
}
