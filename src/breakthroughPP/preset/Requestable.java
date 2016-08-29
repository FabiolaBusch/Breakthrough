package breakthroughPP.preset;

/**
 * Provides a function which delivers a move.
 */

public interface Requestable {
    Move deliver() throws Exception;
}
