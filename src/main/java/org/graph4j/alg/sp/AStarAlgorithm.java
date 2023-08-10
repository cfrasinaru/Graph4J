package org.graph4j.alg.sp;

import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.CheckArguments;
import org.graph4j.util.Path;
import org.graph4j.util.VertexHeap;

import java.util.Arrays;

/**
 *
 * @author Cristian Ivan
 * @author Cristian Frăsinaru
 */
public class AStarAlgorithm extends GraphAlgorithm implements SinglePairShortestPath {

    protected final int source;
    protected final int target;
    protected final int[] vertices;
    protected double[] cost;
    protected int[] before;
    protected int[] size;
    protected boolean[] solved;
    protected int numSolved;
    private VertexHeap heap;
    private final AStarEstimator heuristic;

    /**
     * Creates an algorithm to find the shortest path between source and target.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @param heuristic the estimated distance from a vertex to the target.
     */
    public AStarAlgorithm(Graph graph, int source, int target, AStarEstimator heuristic) {
        super(graph);
        CheckArguments.graphContainsVertex(graph, source);
        CheckArguments.graphContainsVertex(graph, target);
        this.vertices = graph.vertices();
        this.source = source;
        this.target = target;
        this.heuristic = heuristic;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public int getTarget() {
        return target;
    }

    @Override
    public Path findPath() {
        if (before == null) {
            compute();
        }
        int ti = graph.indexOf(target);
        if (cost[ti] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return createPathEndingIn(ti);
    }

    @Override
    public double getPathWeight() {
        if (cost == null) {
            compute();
        }
        return cost[graph.indexOf(target)];
    }

    //computes the path from the source to the target
    protected void compute() {
        int n = vertices.length;
        this.cost = new double[n];
        this.before = new int[n];
        this.size = new int[n];
        this.solved = new boolean[n];
        this.numSolved = 0;
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        Arrays.fill(before, -1);
        cost[graph.indexOf(source)] = 0;
        this.heap = new VertexHeap(graph,
                (i, j) -> (int) Math.signum(cost[i] + heuristic.estimate(i, target)
                        - cost[j] - heuristic.estimate(j, target)));

        while (true) {
            int vi = heap.poll();
            solved[vi] = true;
            System.out.println("Solved " + vi + ", cost=" + cost[vi]);
            numSolved++;
            int v = vertices[vi];
            if (v == target || numSolved == n) {
                break;
            }
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (solved[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(v, u));
                }
                if (cost[ui] > cost[vi] + weight) {
                    cost[ui] = cost[vi] + weight;
                    before[ui] = vi;
                    size[ui] = size[vi] + 1;
                    heap.update(ui);
                }
            }
        }
    }

    protected Path createPathEndingIn(int vi) {
        Path path = new Path(graph, size[vi] + 1);
        while (vi >= 0) {
            path.add(vertices[vi]);
            vi = before[vi];
        }
        path.reverse();
        return path;
    }

}
