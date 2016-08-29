package breakthroughPP.players;

import breakthroughPP.preset.*;

import java.rmi.RemoteException;
import breakthroughPP.gui.Showable;

/**
 * Implementation of an interactive player playing the game breakthroughPP
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016 
 * @author H.A.
 * @author A.Z.
 */
public class InterActivePlayer extends AbstractPlayer {

	/**
	 * Create an interactive player with a certain requestable input to read moves
	 * from some source
	 * @param input instance offering an input method
     */
	public InterActivePlayer(Requestable input){
		super(input,null);
	}
	
	/**
	 * Constructor offering the possibility that an interactive player instance knows its graphical
	 * user interface, produced by another programm
     * @param input new Requestable allowing to ask for moves on the gui
     * @param output new output instance is bound to the player
	 */
	public InterActivePlayer(Requestable input, Showable output) {
		super(input, output);
	}

	/**
	 * @return the type of the player
	 * @see breakthroughPP.players.PlayerType
	 * @see breakthroughPP.players.AbstractPlayer
	 */
	@Override
    public PlayerType getPlayerType(){
		return PlayerType.INTERACTIVE;
    }

	/**
	 * @see breakthroughPP.preset.Player#request()
	 * @return the move that this player wants to do next
	 * @throws PresetException if an input instance is not connected with this player
	 * @throws RemoteException if the connection with the net throws one
	 * @throws Exception if another exception occures
     */
	@Override
	public Move request() throws Exception, RemoteException {

		assert(input != null);

		// Verify and forward state of turn cycle state machine
		super.request();

		// Save the move that the player wants to do. The move will be executed when confirm is called.
		currentMove = input.deliver();

		return currentMove;
	}

}

