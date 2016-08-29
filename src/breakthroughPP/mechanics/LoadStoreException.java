package breakthroughPP.mechanics;

/**
 *  This exception is thrown when during loading and storing an exception occures
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H.A.
 */

public class LoadStoreException extends Exception{

	private static final long serialVersionUID = 2456746431657464L;

	/**
     * Default Constructor
	 */
	public LoadStoreException(){
	}

	/**
     * Constructor with message describing the exception
	 * @param msg description of the exception
     */
	public LoadStoreException(String msg){
		super(msg);
	}
}
