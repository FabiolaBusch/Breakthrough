package breakthroughPP.players;

/**
 * Enumeration containing all the different players
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H.A.
 */
public enum PlayerType {

    /** Interactive player */
	INTERACTIVE("IP"),

    /** Computer player that does random moves */
	RANDOM("SC"),

    /** Artificial intelligence player that does more improved moves */
    COMPUTER("AC"),

	/** Player received over network */
	NETPLAYER("NP"),
	
	/** Even more intelligent computer player*/
	AIPLAYER("AI");

    /** String that represents the player type */
    private String typeString;

	/**
	 * Constructor with parameters
	 * @param type String abbreviation of a player
	 */ 
    PlayerType(String type){
        typeString = type;
    }

    /**
     * Generate a PlayerType object from its string representation
     * @param str the player string, like AI, AC, SC, IP or NP
     * @return the player type.
     * @throws IllegalArgumentException If str is not a valid Player Type string representation.
     */
    public static PlayerType fromString(String str) throws IllegalArgumentException {
        if(str == null) {
			throw new IllegalArgumentException("PlayerType input cannot be resolved as"+
			" your input is " + str);
		}
        for(PlayerType t : PlayerType.values()) {
            if(str.equalsIgnoreCase(t.typeString)) {
				return t;
			}
        }
        throw new IllegalArgumentException("Input for PlayerType does not match with the possible "+
			"ones");
    }

}
