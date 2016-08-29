package breakthroughPP.players;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import breakthroughPP.preset.*;

/**
 * The NetPlayer class works as a wrapper class to be able to store any {@link
 * breakthroughPP.preset.Player} as well as to contain the funcions required for accessing a remote
 * player on another computer in the net.
 *
 * source code origin from: Brosenne, H.: Allgemeines Programmierpraktikum, Sommersemester 2016,
 * Georg-August-Universität Göttingen, S.156.
 *
 * modified by
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * CodeSalat
 * J.B.
 */

public class NetPlayer extends UnicastRemoteObject implements Player, Setting {

	private static final long serialVersionUID = -571283382353280216L;

	private Player myplayer;

// ==== Constructors ==============================================================================
	/**
	 * Default Constructor
	 * @throws RemoteException if the other constructore throws one
	 */
	public NetPlayer() throws RemoteException{
		this(null);
	}
	
	/**
	 * Constuctor with parameter
	 * @param player the player to be stored inside
	 * @throws RemoteException if the remote connection fails
	 */
    public NetPlayer(Player player) throws RemoteException{
        myplayer = player;
    }

// ==== Instance functions/ methods ===============================================================
	/**
	 * @return the player type
	 * @see breakthroughPP.players.AbstractPlayer
	 * @see breakthroughPP.players.PlayerType
	 */
	public PlayerType getPlayerType() {
		return PlayerType.NETPLAYER;
	}

// ==== Interface functions/ methods ==============================================================
	/**
	 * Requests from the player stored in this class a move
	 * @see breakthroughPP.preset.Player
	 * @throws RemoteException if an exception occures in the remote connection
	 * @throws PresetException if the player equals null
	 * @throws Exception if any other kind of error occures 
	 */
	@Override
    public Move request() throws Exception, RemoteException{
		if(myplayer == null){
			throw new PresetException("Player upon whom request() is called is null");
		}
        return myplayer.request();
    }

	/** 
	 * Updates the stored player about an opponent's move
	 * @param opponentMove move an opponent has made
	 * @param boardStatus status resulting from that move according to the game controls
	 * @see breakthroughPP.preset.Player
	 * @throws RemoteException if an exception occures in the remote connection
	 * @throws PresetException if the player equals null
	 * @throws Exception if any other kind of error occures 
	 */
	@Override
    public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException{
		if(myplayer == null){
			throw new PresetException("Player upon whom update(Move, Status) is called is null");
		}
		myplayer.update(opponentMove, boardStatus);
    }

	/**
	 * Report of the game control about a move the stored player has returned with {@link
	 * #request()} previously.
	 * @param boardStatus status resulting from a previous call of {@link
	 * #request()} of this player
	 * @see breakthroughPP.preset.Player
	 * @throws RemoteException if an exception occures in the remote connection
	 * @throws PresetException if the player equals null
	 * @throws Exception if any other kind of error occures 
	 */
	@Override
    public void confirm(Status boardStatus) throws Exception, RemoteException{
		if(myplayer == null){
			throw new PresetException("Player upon whom confirm(Status) is called is null");
		}
        myplayer.confirm(boardStatus);
    }

	/**
	 * Initialisation of the player with the size of the game board and the color of the player
	 * @param dimx game board size in x direction
	 * @param dimy game board size in y direction#
	 * @param color color of the player specified in {@link breakthroughPP.preset.Setting}
	 * @see breakthroughPP.preset.Player
	 * @throws RemoteException if an exception occures in the remote connection
	 * @throws PresetException if the player equals null or if the color does not fit to those  
	 * in {@link breakthroughPP.preset.Setting}
	 * @throws Exception if any other kind of error occures 
	 */
	@Override
	public void init(int dimx, int dimy, int color) throws Exception, RemoteException{
		if(myplayer == null){
			throw new PresetException("Player upon whom init(int,int,int) is called is null");
		}
		if(color != RED && color != BLUE){
			throw new PresetException("Players can possess only the colors defined for the game"+
				" in Setting");
		}	
		myplayer.init(dimx,dimy,color);
	}
}
