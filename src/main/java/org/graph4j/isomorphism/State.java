package org.graph4j.isomorphism;

/**
 * Interface for the state of the search algorithm. A state is a partial
 * solution of the graph isomorphism problem.
 *
 * @author Ignat Gabriel-Andrei
 */
public interface State {

    int NULL_NODE = -1;     // used to set a node as not mapped
    boolean DEBUG = false;

    /**
     * Computes the next pair of vertices to be added to the mapping.
     *
     * @return true if a pair was found, false otherwise
     */
    boolean nextPair();

    /**
     * Checks if the current pair of vertices is feasible.
     *
     * @return true if the pair is feasible, false otherwise
     */
    boolean isFeasiblePair();

    /**
     * Checks if the current state is a dead end(no solution can be found from
     * here).
     *
     * @return true if the state is a dead end, false otherwise
     */
    boolean isDead();

    /**
     * Checks if the current state is a goal state(a complete solution was
     * found).
     *
     * @return true if the state is a goal state, false otherwise
     */
    boolean isGoal();

    /**
     * Adds the current pair of vertices to the mapping.
     */
    void addPair();

    /**
     * Backtracks to the previous state.
     */
    void backTrack();

    /**
     * @return the mapping of the current state
     */
    Isomorphism getMapping();

    void resetPreviousVertices();

    int getCoreLen();
}
