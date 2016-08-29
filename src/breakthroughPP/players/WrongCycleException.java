package breakthroughPP.players;

/**
 * Used in the package breakthourghPP.player, when a wrong player-method 
 * is used.
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP 
 * SoSe 2016
 * @author CodeSalat
 * @author Fabiola Buschendorf
 */

public class WrongCycleException extends PlayerException {

	private static final long serialVersionUID = 156464636345546163L;

	/**
	 * Default Constructor
     */
	public WrongCycleException(){
		this(" ");
	}

	/**
	 * Constructor with one parameter
	 * @param msg description of the exception
	 */
	public WrongCycleException(String msg){
		super(msg);
	}
	
}
