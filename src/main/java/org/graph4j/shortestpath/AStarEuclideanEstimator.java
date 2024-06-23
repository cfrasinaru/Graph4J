package org.graph4j.shortestpath;

/**
 * Euclidean distance.
 *
 * @author Cristian Ivan
 * @author Cristian Frăsinaru
 */
public class AStarEuclideanEstimator implements AStarEstimator {

    private final int gridSize;

    public AStarEuclideanEstimator(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public double estimate(int vertex, int target) {
        int x1 = vertex / gridSize;
        int y1 = vertex % gridSize;

        int x2 = target / gridSize;
        int y2 = target % gridSize;

        int dx = x2 - x1;
        int dy = y2 - y1;

        return Math.sqrt(dx * dx + dy * dy);
    }
}
