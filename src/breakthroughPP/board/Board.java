package breakthroughPP.board;

import breakthroughPP.preset.*;

import java.util.*;

/**
 * Board provides parameters for
 * an initial gaming board.
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author Fabiola Buschendorf
 * @author H.A.
 */
public class Board implements Setting, Viewable {

    /**
     * Current status of the field
     */
    private Status status;

    /**
     * The board data itself
     */
    private int[][] fields;

    /**
     * The height of the board
     */
    private int numbers;

    /**
     * The width of the board
     */
    private int letters;

    /**
     * Whose turn is it currently
     */
    private int whosTurn;

    /**
     * Accessor implementing the Viewer pattern for view access to the board
     */
    private BoardStorage boardStorage;

    /**
     * store the last change on the board
     */
    private Move lastChange;

    // ==== Constructor ====================================================================================
    /**
     * Default constructor
     *
     * @throws PresetException if Board(26,26) throws one
     * @see #Board(int, int)
     */
    public Board() throws PresetException {
        this(26, 26);
    }

    /**
     * Creates a board and its initial status and tokens.
     *
     * @param newnumbers number of rows
     * @param newletters number of columns
     * @throws PresetException if the parameters are not in this range:
     *                         numbers in (6,26), letters in (2,26)
     */
    public Board(int newletters, int newnumbers) throws PresetException {

        status = new Status(OK);
        whosTurn = RED;
        boardStorage = new BoardStorage(this);
        lastChange = null;

        if (newnumbers > 5 && newnumbers < 27 && newletters > 1 && newletters < 27) {
            numbers = newnumbers;
            letters = newletters;
        } else {
            throw new PresetException("Field dimensions out of range!");
        }

        initializeFields();
    }

    /**
     * Copy constructor for Board
     */
    public Board(Board newboard) throws PresetException {

        status = new Status(newboard.getStatus());
        whosTurn = newboard.whosTurn;
        boardStorage = new BoardStorage(this);
        lastChange = null;

        if (newboard.getNumbers() > 5 && newboard.getNumbers() < 27 && newboard.getLetters() > 1 && newboard.getLetters() < 27) {
            numbers = newboard.getNumbers();
            letters = newboard.getLetters();
        } else {
            throw new PresetException("Field dimensions out of range!");
        }

        fields = new int[numbers][letters];

        for (int i = 0; i < newboard.fields.length; i++) {
            for (int j = 0; j < newboard.fields[i].length; j++) {
                fields[i][j] = newboard.getColor(i, j);

            }

        }

    }

    // ==== Getter ====================================================================================

    public int getLetters() {
        return letters;
    }

    public int getNumbers() {
        return numbers;
    }

    public Move getLastChange() {
        return lastChange;
    }

    public int getTurn() {
        return whosTurn;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns the color of the specified field. It will not be checked of num and let are on the
     * board.
     *
     * @param num the number of the field starting in the coordinate origin
     * @param let the row of the field starting in the coordinate origin
     * @return the color of the field defined by (let,num)
     */
    public int getColor(int num, int let) {
        return fields[num][let];
    }

    public int[][] getFields() {
        return fields;
    }

    /**
     * Returns the color of the specified position. It will not be checked if position is on the
     * board.
     *
     * @param position the position of the queried field.
     * @return the color value of the field.
     */
    public int getColor(Position position) {
        return getColor(position.getNumber(), position.getLetter());
    }

    // ==== Setter ====================================================================================
    /**
     * Sets, whose current turn it is.
     *
     * @param turn the player in turn described through its color defined in {@link breakthroughPP.preset.Setting}
     * @throws PresetException if turn is not RED or BLUE
     */
    public void setTurn(int turn) throws PresetException {
        if (turn != RED && turn != BLUE) {
            throw new PresetException("setTurn can only work on RED and BLUE defined in Setting");
        }
        whosTurn = turn;
    }

    /**
     * Sets a new status on this board.
     *
     * @param newstatus the Status to be set.
     * @throws PresetException if new status doesn't contain a valid integer.
     */
    public void setStatus(Status newstatus) throws PresetException {
        if (newstatus.isOk() || newstatus.isRedWin() || newstatus.isBlueWin() ||
                newstatus.isIllegal() || newstatus.isUndefined()) {
            status = newstatus;
        } else {
            throw new PresetException("Invalid argument in Status");
        }
    }

    /**
     * Set the color of the specified field
     *
     * @param num      field position in y direction [0 n_x-1]
     * @param let      field position in x direction [0 n_y-1]
     * @param newcolor color set at (let,num).
     * @throws PresetException if the color is not valid or the Position (let,num) is not on the
     *                         board.
     */
    public void setColor(int num, int let, int newcolor) throws PresetException {
        if (newcolor < RED || newcolor >= GRAY) {
            throw new PresetException("The color " + newcolor + " you want to set is not " +
                    "specified in Setting");
        }
        if (num < 0 || num >= getNumbers() || let < 0 || let >= getLetters()) {
            throw new PresetException("The Postion (" + let + "/" + num + ") where your want to " +
                    "change the color on the board is not on the board");
        }
        fields[num][let] = newcolor;
    }

    // ==== Instancemethods ====================================================================================
    /**
     * Initialize fields array with the tokens colors depending on board
     * width (letters) and board height (numbers).
     */
    public void initializeFields() {
        fields = new int[numbers][letters];
        int k = (3 + numbers) / 4;
        for (int h = 0; h < fields.length; h++) {
            for (int l = 0; l < fields[0].length; l++) {
                if (h < k)
                    fields[h][l] = RED;
                else if (h >= fields.length - k)
                    fields[h][l] = BLUE;
                else
                    fields[h][l] = NONE;
            }
        }
    }

    /**
     * Sets a Move object on the board, verifies its validity, updates whosTurn variable to set next
     * player and returns the new status.
     *
     * @param move Move to be set and validated on the board
     * @return Status after the move has been placed on the board {@link breakthroughPP.preset.Status}
     * @throws PresetException on PresetExceptions in Position or getValidMoves or getTurn
     */
    public Status move(Move move) throws PresetException {
        // Check if opponent is surrendering
        if (move == null) {
            lastChange = null;
            if (getTurn() == RED) {
                return new Status(BLUE_WIN);
            } else {
                return new Status(RED_WIN);
            }
        }

        // Check if the move is not valid --> on board
        if (!getValidMoves(getTurn()).contains(move)) {
            throw new PresetException("Move " + move + " is not valid!");
        }

        // Set the last change
        lastChange = new Move(move);

        // Everything OK: Set the move on the board
        Position start = move.getStart();
        Position end = move.getEnd();

        fields[end.getNumber()][end.getLetter()] = fields[start.getNumber()][start.getLetter()];
        fields[start.getNumber()][start.getLetter()] = NONE;

        updateStatus();
        updateTurn();

        return status;
    }

    /**
     * Checks if one player reached opponent's side
     * or if one player is dead/ has no tokens and then sets status.
     *
     * @see breakthroughPP.preset.Status
     */
    public void updateStatus() {

        boolean redDead = true;
        boolean blueDead = true;

        for (int i = 0; i < letters; i++) {
            if (fields[0][i] == BLUE) status.setStatus(BLUE_WIN);
            if (fields[numbers - 1][i] == RED) status.setStatus(RED_WIN);
            for (int j = 0; j < numbers; j++) {
                if (fields[j][i] == BLUE) blueDead = false;
                if (fields[j][i] == RED) redDead = false;
            }
        }

        if (blueDead){
        	status.setStatus(RED_WIN);
        }
        if (redDead){
        	status.setStatus(BLUE_WIN);
        }

    }

    /**
     * Update whose current turn is.
     */
    public void updateTurn() {
        try {
            if (getTurn() == RED) {
                setTurn(BLUE);
            } else {
                setTurn(RED);
            }
        } catch (PresetException pe) {
            System.err.println("updateTurn() failed. action ignored. Exiting");
            System.exit(1);
        }
    }

    /**
     * Calculate all valid moves for a player with a certain color.
     *
     * @param color color of the player whose valid moves are to be calculated (see {@link
     *              breakthroughPP.preset.Setting})
     * @return a set containing all valid moves for the color.
     * @throws PresetException if a Position in this function throws one or if the color is not
     *                         specified as the color of a Player in {@link breakthroughPP.preset.Setting}
     */
    public HashSet<Move> getValidMoves(int color) throws PresetException {

        int direction;
        if (color == BLUE) {
            direction = -1;
        } else if (color == RED) {
            direction = 1;
        } else {
            throw new PresetException("The color you are calling getValidMoves with is not specified" +
                    " in Setting");
        }

        // Hashset containing the results
        HashSet<Move> validMoves = new HashSet<>();

        for (int y = 0; y < fields.length; y++) {
            for (int x = 0; x < fields[0].length; x++) {

                // Check if the current field is interesting (if there is a token
                // of the players color on that field)
                if (fields[y][x] != color) continue;


                // ---- Step 1: Create all possible moves -----------------------------------------
                // 3 possible moves (diagonal to the left, vertical, diagonal to the right)

                HashSet<Move> validMovesForCurrentToken = new HashSet<Move>();
                Position start = new Position(x, y);

                // diagonal left
                try {
                    validMovesForCurrentToken.add(new Move(start, new Position(x - 1, y + 1 * direction)));
                } catch (PresetException exception) {
                    // Exceptions is thrown when invalid end position of move. Handling of exception
                    // is not needed because we want to ignore that invalid moves anyways.
                }

                // vertical
                try {
                    validMovesForCurrentToken.add(new Move(start, new Position(x, y + 1 * direction)));
                } catch (PresetException exception) {
                    // Exceptions is thrown when invalid end position of move. Handling of exception
                    // is not needed because we want to ignore that invalid moves anyways.
                }

                // diagonal right
                try {
                    validMovesForCurrentToken.add(new Move(start, new Position(x + 1, y + 1 * direction)));
                } catch (PresetException exception) {
                    // Exceptions is thrown when invalid end position of move. Handling of exception
                    // is not needed because we want to ignore that invalid moves anyways.
                }

                // ---- Step 2: Remove all invalid moves from the possible moves ------------------
                // Check which of the three move options for the current token are valid
                Iterator<Move> iterator = validMovesForCurrentToken.iterator();

                while (iterator.hasNext()) {

                    Move move = iterator.next();

                    // Check valid position of the move regarding the board size

                    if (move.getEnd().getLetter() < 0 || move.getEnd().getLetter() >= getLetters()) {
                        iterator.remove();
                        continue;
                    }

                    if (move.getEnd().getNumber() < 0 || move.getEnd().getNumber() >= getNumbers()) {
                        iterator.remove();
                        continue;
                    }

                    // Check whether the move is valid according to the rules

                    // Color of where the move is targeted to
                    int endColor = getColor(move.getEnd());

                    // Is the move a diagonal move?
                    boolean isDiagonal = move.getStart().getLetter() != move.getEnd().getLetter();

                    // Is the target position of the move already occupied by another piece?
                    boolean isEndOccupied = (endColor == BLUE) || (endColor == RED);

                    // Cannot move forward vertically if the target field is already occupied
                    if (!isDiagonal && isEndOccupied) {
                        iterator.remove();
                        continue;
                    }

                    // Diagonal move is note valid when the target position is occupied by a
                    // token of my color
                    if (isDiagonal && isEndOccupied && endColor == color) {
                        iterator.remove();
                        continue;
                    }

                }

                // ---- Step 3: add the valid moves for this token, to the set of possible moves ----
                validMoves.addAll(validMovesForCurrentToken);

            }
        }

        return validMoves;
    }

    /**
     * Prints the board to the command line
     */
    public void printField() {
        for (int i = numbers - 1; i >= 0; i--) {
            for (int j = 0; j < letters; j++) {
                System.out.print(getColor(i, j) + " ");
            }
            System.out.println();
        }
    }

    
    // ==== Interfacemethods ====================================================================================
    /**
     * Lets others have view access only to the board
     *
     * @return BoardStorage storage
     */
    @Override
    public Viewer viewer() {
        return boardStorage;
    }

 }
