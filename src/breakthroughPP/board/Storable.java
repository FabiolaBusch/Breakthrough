package breakthroughPP.board;

/**
 * Interface allowing to store a status of the game in a file
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H.A.
 */
public interface Storable {

	/**
	 * @param filename Name of the file, where the data should be stored in
	 * @throws UnsupportedOperationException if saving is not supported e.g. due to missing
	 * implementation of the mechanics allowing to store or if other properties of the
	 * instance where this function is implemented prohibit storing of the game
	 * @throws IllegalArgumentException of the filename is not valid for usage
	 */
	void store(String filename) throws UnsupportedOperationException, IllegalArgumentException;

}

 
