package org.graph4j.alg.sp;

/**
 * Estimates the cost of the shortest path from a vertex to the target.
 *
 * @author Cristian Ivan
 * @author Cristian Frăsinaru
 */
@FunctionalInterface
public interface AStarEstimator {

    /**
     * Estimates the cost to reach the target from the given node.
     *
     * @param current The current vertex index.
     * @param target The target vertex index.
     * @return The estimated cost to reach the target.
     */
    double estimate(int current, int target);
}
