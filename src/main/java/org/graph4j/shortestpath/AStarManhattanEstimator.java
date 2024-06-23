package org.graph4j.shortestpath;

/**
 * Manhattan distance.
 *
 * @author Cristian Ivan
 * @author Cristian Frăsinaru
 */
public class AStarManhattanEstimator implements AStarEstimator {

    private final int gridSize;

    public AStarManhattanEstimator(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public double estimate(int vertex, int target) {
        int x1 = vertex / gridSize;
        int y1 = vertex % gridSize;

        int x2 = target / gridSize;
        int y2 = target % gridSize;

        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
