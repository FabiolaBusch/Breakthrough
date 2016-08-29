package breakthroughPP.mechanics;

import breakthroughPP.players.AbstractPlayer;
import breakthroughPP.players.PlayerType;
import java.io.*;

/**
 * Represents all settings of a game that are needed to be able to
 * store and restore a game.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author H.A.
 */
public class GameState implements Serializable {
	
    private static final long serialVersionUID = -6096740308435711692L;

    private int width = 0;
    private int height = 0;
    private PlayerType redPlayerType;
    private PlayerType bluePlayerType;

    /** Log with a list of moves that have been made in a game in chronical order */
    private GameLog gameLog;

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public PlayerType getRedPlayerType(){
        return redPlayerType;
    }

    public PlayerType getBluePlayerType(){
        return bluePlayerType;
    }

    public GameLog getGameLog(){
        return gameLog;
    }

    /**
     * Save current game state, which is logged in this instance to a file
     * @param engine The engine of which to save the current game state
     * @param fileName of the file where to save the data
     * @throws LoadStoreException if io operations while saving fails
     */
    public void save(Engine engine, String fileName) throws LoadStoreException{

        width = engine.getColumns();
        height = engine.getRows();
        gameLog = engine.getGameLog();
        redPlayerType = ((AbstractPlayer) engine.getRedPlayer()).getPlayerType();
        bluePlayerType = ((AbstractPlayer) engine.getBluePlayer()).getPlayerType();

		FileOutputStream fileOutputStream;
		try{
			fileOutputStream = new FileOutputStream(fileName);
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(this);
			objectOutputStream.close();

		}catch(FileNotFoundException fnfe){
			throw new LoadStoreException("Filename " + fileName + " can not be used for saving");
		}catch(IOException ioe){
			throw new LoadStoreException("Exception during saving: " + ioe.getMessage());
		}
    }

    /**
     * Load a game state stored in a file
     * @param fileName name of the file where the data should be load from
     * @return the GameState instance created from loading the file 
     * @throws LoadStoreException if io operations while restoring fails
     */
    public static GameState load(String fileName) throws LoadStoreException{
		FileInputStream fileInputStream;
		GameState gameState;
		try{
			fileInputStream = new FileInputStream(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			gameState = (GameState) objectInputStream.readObject();
		}catch(FileNotFoundException fnfe){
			throw new LoadStoreException("Filename " +fileName + " can not be found");
		}catch(ClassNotFoundException cnfe){
			throw new LoadStoreException("Exception during loading: " + cnfe.getMessage());
		}catch(IOException ioe){
			throw new LoadStoreException("Exception during loading: " + ioe.getMessage());
		}

		return gameState;
    }


}
