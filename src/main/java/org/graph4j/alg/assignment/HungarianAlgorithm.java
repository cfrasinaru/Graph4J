package org.graph4j.alg.assignment;


import org.graph4j.Edge;
import org.graph4j.EdgeIterator;
import org.graph4j.Graph;
import org.graph4j.alg.UndirectedGraphAlgorithm;
import org.graph4j.alg.bipartite.BipartitionAlgorithm;
import org.graph4j.util.IntArrays;
import org.graph4j.util.Matching;
import org.graph4j.util.StableSet;

import java.util.Arrays;

/**
 * The Hungarian Algorithm, also known as the Kuhn-Munkres algorithm,
 * is a combinatorial optimization algorithm used to solve the assignment problem
 * <br>
 * The algorithm finds a perfect matching and a maximum potential such that the
 * matching cost is equal to the potential value.
 *
 * @author Chirvasa Matei
 * @author Prodan Sabina
 */
public class HungarianAlgorithm extends UndirectedGraphAlgorithm {

    private final StableSet leftSide;
    private final StableSet rightSide;
    private Matching matching;
    private Boolean isDense;

    /**
     * Instantiates a new Hungarian algorithm.
     *
     * @param graph the input graph, must be bipartite
     */
    public HungarianAlgorithm(Graph graph) {
        super(graph);
        var alg = BipartitionAlgorithm.getInstance(graph);
        if (!alg.isBipartite()) {
            throw new IllegalArgumentException("The graph is not bipartite");
        }
        this.leftSide = alg.getLeftSide();
        this.rightSide = alg.getRightSide();
    }


    /**
     * Instantiates a new Hungarian algorithm.
     *
     * @param graph     the input graph, on which the bipartitions were built
     * @param leftSide  the left side of the bipartition, which will be assumed to be workers
     * @param rightSide the right side of the bipartition, which will be assumed to be tasks
     */
    public HungarianAlgorithm(Graph graph, StableSet leftSide, StableSet rightSide) {
        super(graph);
        if (!leftSide.isValid()) {
            throw new IllegalArgumentException("The left side is not a stable set.");
        }
        if (!rightSide.isValid()) {
            throw new IllegalArgumentException("The right side is not a stable set.");
        }
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        int[] vertices = IntArrays.union(leftSide.vertices(), rightSide.vertices());
        if (!IntArrays.sameValues(vertices, graph.vertices())) {
            throw new IllegalArgumentException("Invalid bipartition");
        }
    }

    private boolean isDense() {
        if (isDense == null) {
            // constant for density may need adjustment
            isDense = ((double) graph.numEdges() / ((long) graph.numVertices() * (graph.numVertices() - 1))) > 0.1;
        }
        return isDense;
    }

    private void computeSparse() {
        final double INF = Double.MAX_VALUE;

        int[] leftSideVertices = leftSide.vertices();
        int[] rightSideVertices = rightSide.vertices();

        int[] taskAssignment = new int[leftSide.size() + 1];
        Arrays.fill(taskAssignment, -1);
        double[] johnsonPotentials = new double[leftSide.size() + 1];

        double[] distances = new double[leftSide.size() + 1];
        boolean[] visited = new boolean[leftSide.size() + 1];
        int[] previousWorker = new int[leftSide.size() + 1];

        for (int taskIndex = 0; taskIndex < rightSide.size(); ++taskIndex) {
            int currentWorker = leftSide.size();
            taskAssignment[currentWorker] = taskIndex;

            Arrays.fill(distances, INF);
            distances[currentWorker] = 0;
            Arrays.fill(visited, false);
            Arrays.fill(previousWorker, -1);
            while (taskAssignment[currentWorker] != -1) {
                double minDistance = INF;
                visited[currentWorker] = true;
                int nextWorker = -1;

                for (int workerIndex = 0; workerIndex < leftSide.size(); ++workerIndex) {
                    if (visited[workerIndex]) {
                        continue;
                    }
                    double assignmentCost = graph.getEdgeWeight(rightSideVertices[taskAssignment[currentWorker]], leftSideVertices[workerIndex]) - johnsonPotentials[workerIndex];
                    if (currentWorker != leftSide.size()) {
                        assignmentCost -= graph.getEdgeWeight(rightSideVertices[taskAssignment[currentWorker]], leftSideVertices[currentWorker]) - johnsonPotentials[currentWorker];
                    }
                    if (distances[workerIndex] > distances[currentWorker] + assignmentCost) {
                        distances[workerIndex] = distances[currentWorker] + assignmentCost;
                        previousWorker[workerIndex] = currentWorker;
                    }
                    if (minDistance > distances[workerIndex]) {
                        minDistance = distances[workerIndex];
                        nextWorker = workerIndex;
                    }
                }
                currentWorker = nextWorker;
            }
            updateDistancesAndPotentials(taskAssignment, johnsonPotentials, distances, previousWorker, currentWorker);
        }

        matching = new Matching(graph, taskAssignment.length);
        for (int index : leftSide) {
            if (taskAssignment[index] != -1) {
                matching.add(index, rightSide.vertices()[taskAssignment[index]]);
            }
        }
    }

    private void computeDense() {
        final double INF = Double.MAX_VALUE;

        int[] workerVertices = leftSide.vertices();
        int[] taskVertices = rightSide.vertices();

        // cache costs into a matrix to increase efficiency
        double[][] costs = new double[workerVertices.length][taskVertices.length];
        Arrays.stream(costs).forEach(a -> Arrays.fill(a, INF));
        for (EdgeIterator it = graph.edgeIterator(); it.hasNext(); ) {
            Edge edge = it.next();
            int worker = edge.source();
            int task = edge.target();
            if (task < worker) {
                int aux = worker;
                worker = task;
                task = aux;
            }
            costs[task - taskVertices.length][worker] = edge.weight();
        }
        // adding a surplus worker for convenience
        int[] taskAssignment = new int[leftSide.size() + 1];
        Arrays.fill(taskAssignment, -1);
        double[] johnsonPotentials = new double[leftSide.size() + 1];

        double[] distances = new double[leftSide.size() + 1];
        boolean[] visited = new boolean[leftSide.size() + 1];
        int[] previousWorker = new int[leftSide.size() + 1];

        // assign the indexed task to a worker using Dijkstra with potentials
        for (int taskIndex = 0; taskIndex < rightSide.size(); ++taskIndex) {
            int currentWorker = leftSide.size(); // the surplus worker
            taskAssignment[currentWorker] = taskIndex; // assign surplus worker to the current task

            Arrays.fill(distances, INF); // johnson reduced distances
            distances[currentWorker] = 0;
            Arrays.fill(visited, false);
            Arrays.fill(previousWorker, -1); // previous worker on the shortest path
            while (taskAssignment[currentWorker] != -1) { // Dijkstra: Pop the minimum worker from the heap
                double minDistance = INF;
                visited[currentWorker] = true;
                int nextWorker = -1; // next unvisited worker with minimum distance

                // consider extending the shortest path by currentWorker -> taskAssignment[currentWorker] -> workerIndex
                for (int workerIndex = 0; workerIndex < leftSide.size(); ++workerIndex) {
                    if (visited[workerIndex]) {
                        continue;
                    }
                    // sum of reduced edge weights by following currentWorker -> taskAssignment[currentWorker] -> workerIndex
                    double assignmentCost = costs[taskVertices[taskAssignment[currentWorker]] - taskVertices.length][workerVertices[workerIndex]] - johnsonPotentials[workerIndex];
                    if (currentWorker != leftSide.size()) {
                        assignmentCost -= costs[taskVertices[taskAssignment[currentWorker]] - taskVertices.length][workerVertices[currentWorker]] - johnsonPotentials[currentWorker];
                    }
                    if (distances[workerIndex] > distances[currentWorker] + assignmentCost) {
                        distances[workerIndex] = distances[currentWorker] + assignmentCost;
                        previousWorker[workerIndex] = currentWorker;
                    }
                    if (minDistance > distances[workerIndex]) {
                        minDistance = distances[workerIndex];
                        nextWorker = workerIndex;
                    }
                }
                currentWorker = nextWorker;
            }
            updateDistancesAndPotentials(taskAssignment, johnsonPotentials, distances, previousWorker, currentWorker);
        }

        matching = new Matching(graph, taskAssignment.length);
        for (int index : leftSide) {
            if (taskAssignment[index] != -1) {
                matching.add(index, rightSide.vertices()[taskAssignment[index]]);
            }
        }
    }

    private void updateDistancesAndPotentials(int[] taskAssignment, double[] johnsonPotentials, double[] distances, int[] previousWorker, int currentWorker) {
        for (int workerIndex = 0; workerIndex < leftSide.size(); ++workerIndex) {
            distances[workerIndex] = Double.min(distances[workerIndex], distances[currentWorker]);
            johnsonPotentials[workerIndex] += distances[workerIndex];
        }
        for (int workerIndex = 0; workerIndex != leftSide.size(); currentWorker = workerIndex) {
            workerIndex = previousWorker[currentWorker];
            taskAssignment[currentWorker] = taskAssignment[workerIndex];
        }
    }

    private void compute() {
        if (isDense()) {
            computeDense();
        }
        else {
            computeSparse();
        }
    }

    public Matching getMatching() {
        if (matching == null) {
            compute();
        }
        return matching;
    }
}
