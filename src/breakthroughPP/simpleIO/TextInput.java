package breakthroughPP.simpleIO;

import breakthroughPP.board.*;

import breakthroughPP.preset.*;

import java.util.regex.Pattern;
import java.io.*;

/**
 * Reads start and end coordinates from the standard input and generates a move from it. This move
 * can also be null which is aquivalent to the surrender of the player being asked to make a move.
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 *Gr CodeSalat
 * @author H.A.
 */
public class TextInput implements Requestable {

    // stores the reference of an object allowing to save
    private Storable memorise;

    // Alphabet to be able to translate a letter to its corresponding number
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Default Constructor
     */
    public TextInput() {
        this(null);
    }

    /**
     * Constructor allowing to save by receiving an non null reference
     *
     * @param store reference of a class implementing {@link breakthroughPP.board.Storable}
     */
    public TextInput(Storable store) {
        memorise = store;
    }

    /**
     * Delivers a move to the one who asks and provide an opportuninty to store if the user enters
     * the command during input
     *
     * @throws Exception if from the input no move could be formed
     * @see breakthroughPP.preset.Requestable#deliver()
     * @see breakthroughPP.board.Storable#store(String filename)
     */
    public Move deliver() throws Exception {

        // Information for the user
        System.out.println("\tPlease make your turn. After initialisation red beginns");
        System.out.println("\tFor a move type: <startY> <startY> <endX> <endY>");
        System.out.println("\tInput \"null\" to surrender");
        if (memorise != null) {
            System.out.println("\tFor saving type: store <filename>");
        }

        // Read a line from stdin
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String text = bufferedReader.readLine();

        // Check if the input is a store command
        if (memorise != null && text.startsWith("store")) {

            // Extract the file name where to save the file
            String[] args = text.split("\\s+");
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong store command. Use \"Save <filename>\"");
            }

            // Save the current game
            try {
                memorise.store(args[1]);
                System.out.println("Saving successful");
            } catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
                System.err.println("Continue to play");
            } catch (UnsupportedOperationException uoe) {
                System.err.println("Failure of saving the file:\n" + uoe.getMessage());
                System.err.println("Continue to play");
            }

            // Continue listening for an input
            return deliver();
        }

        // Check if one want to surrender
        if (text.startsWith("null")) {
            return null;
        }

        // Extract start and end coordinates from read line
        String[] coordinates = text.split(Pattern.quote(" "));

        // There have to be four coordinates
        if (coordinates.length != 4) {
            throw new FailedMoveInputException("Required length of input arguments to form a" +
                    " is 4. Your length of input was: " + coordinates.length);
        }

        try {
            int startX = alphabet.indexOf(coordinates[0].toLowerCase().charAt(0));
            int startY = Integer.parseInt(coordinates[1]) - 1;
            int endX = alphabet.indexOf(coordinates[2].toLowerCase().charAt(0));
            int endY = Integer.parseInt(coordinates[3]) - 1;
            Position startPosition = new Position(startX, startY);
            Position endPosition = new Position(endX, endY);
            return new Move(startPosition, endPosition);
        } catch (Exception e) {
            throw new FailedMoveInputException("Input could not be transformed into a move on the" +
                    "board\n" + e.getMessage());
        }

    }
    
}

