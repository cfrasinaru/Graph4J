package org.graph4j.alg.assignment;


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

    private final StableSet workerSide;
    private final StableSet taskSide;
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
        StableSet leftSide = alg.getLeftSide(), rightSide = alg.getRightSide();
        // algorithm requires that there be more workers than tasks when assigning
        if (leftSide.size() < rightSide.size()) {
            this.workerSide = rightSide;
            this.taskSide = leftSide;
        }
        else {
            this.workerSide = leftSide;
            this.taskSide = rightSide;
        }
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
        // algorithm requires that there be more workers than tasks when assigning
        if (leftSide.size() < rightSide.size()) {
            this.workerSide = rightSide;
            this.taskSide = leftSide;
        }
        else {
            this.workerSide = leftSide;
            this.taskSide = rightSide;
        }
        int[] vertices = IntArrays.union(workerSide.vertices(), rightSide.vertices());
        if (!IntArrays.sameValues(vertices, graph.vertices())) {
            throw new IllegalArgumentException("Invalid bipartition");
        }
    }

    private boolean isDense() {
        if (isDense == null) {
            isDense = ((double) graph.numEdges() / ((long) graph.numVertices() * (graph.numVertices() - 1))) > 0.1;
        }
        return isDense;
    }

    private void computeSparse() {
        final double INF = Double.MAX_VALUE;

        int[] workerVertices = workerSide.vertices();
        int[] taskVertices = taskSide.vertices();

        int[] taskAssignment = new int[workerVertices.length + 1];
        Arrays.fill(taskAssignment, -1);
        double[] johnsonPotentials = new double[workerVertices.length + 1];

        double[] distances = new double[workerVertices.length + 1];
        boolean[] visited = new boolean[workerVertices.length + 1];
        int[] previousWorker = new int[workerVertices.length + 1];

        for (int taskIndex = 0; taskIndex < taskVertices.length; ++taskIndex) {
            int currentWorker = workerVertices.length;
            taskAssignment[currentWorker] = taskIndex;

            Arrays.fill(distances, INF);
            distances[currentWorker] = 0;
            Arrays.fill(visited, false);
            Arrays.fill(previousWorker, -1);
            while (taskAssignment[currentWorker] != -1) {
                double minDistance = INF;
                visited[currentWorker] = true;
                int nextWorker = -1;

                for (int workerIndex = 0; workerIndex < workerVertices.length; ++workerIndex) {
                    if (visited[workerIndex]) {
                        continue;
                    }
                    double assignmentCost = graph.getEdgeWeight(taskVertices[taskAssignment[currentWorker]], workerVertices[workerIndex]) - johnsonPotentials[workerIndex];
                    if (currentWorker != workerVertices.length) {
                        assignmentCost -= graph.getEdgeWeight(taskVertices[taskAssignment[currentWorker]], workerVertices[currentWorker]) - johnsonPotentials[currentWorker];
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

        produceMatching(workerVertices, taskVertices, taskAssignment);
    }

    private void produceMatching(int[] workerVertices, int[] taskVertices, int[] taskAssignment) {
        matching = new Matching(graph, taskVertices.length);
        for (int i = 0; i < workerVertices.length; ++i) {
            if (taskAssignment[i] != -1) {
                matching.add(workerVertices[i], taskVertices[taskAssignment[i]]);
            }
        }
    }

    private void computeDense() {
        final double INF = Double.MAX_VALUE;

        int[] workerVertices = workerSide.vertices();
        int[] taskVertices = taskSide.vertices();

        // cache costs into a matrix to increase efficiency
        double[][] costs = new double[taskVertices.length][workerVertices.length];
        Arrays.stream(costs).forEach(a -> Arrays.fill(a, INF));
        for (int i = 0; i < taskVertices.length; ++i) {
            for (int j = 0; j < workerVertices.length; ++j) {
                // access edges by index in leftSide and rightSide to account for complicated graphs
                costs[i][j] = graph.getEdgeWeight(taskVertices[i], workerVertices[j]);
            }
        }

        // adding a surplus worker for convenience
        int[] taskAssignment = new int[workerVertices.length + 1];
        Arrays.fill(taskAssignment, -1);
        double[] johnsonPotentials = new double[workerVertices.length + 1];

        double[] distances = new double[workerVertices.length + 1];
        boolean[] visited = new boolean[workerVertices.length + 1];
        int[] previousWorker = new int[workerVertices.length + 1];

        // assign the indexed task to a worker using Dijkstra with potentials
        for (int taskIndex = 0; taskIndex < taskVertices.length; ++taskIndex) {
            int currentWorker = workerVertices.length; // the surplus worker
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
                for (int workerIndex = 0; workerIndex < workerVertices.length; ++workerIndex) {
                    if (visited[workerIndex]) {
                        continue;
                    }
                    // sum of reduced edge weights by following currentWorker -> taskAssignment[currentWorker] -> workerIndex
                    double assignmentCost = costs[taskAssignment[currentWorker]][workerIndex] - johnsonPotentials[workerIndex];
                    if (currentWorker != workerVertices.length) {
                        assignmentCost -= costs[taskAssignment[currentWorker]][currentWorker] - johnsonPotentials[currentWorker];
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

        produceMatching(workerVertices, taskVertices, taskAssignment);
    }

    private void updateDistancesAndPotentials(int[] taskAssignment, double[] johnsonPotentials, double[] distances, int[] previousWorker, int currentWorker) {
        for (int workerIndex = 0; workerIndex < workerSide.size(); ++workerIndex) {
            distances[workerIndex] = Double.min(distances[workerIndex], distances[currentWorker]);
            johnsonPotentials[workerIndex] += distances[workerIndex];
        }
        for (int workerIndex = 0; workerIndex != workerSide.size(); currentWorker = workerIndex) {
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

    /**
     * This algorithm performs better on dense graphs in terms of speed of execution,
     * however it consumes more memory.
     * Setting this fields forgoes the recommended density and
     * forcefully uses the given density-specific implementation.
     *
     * @param isDense marks whether the graph is dense or not
     */
    public void setDense(boolean isDense) {
        this.isDense = isDense;
    }

    /**
     * Calls the algorithm to determine the lowest cost assignment possible for the
     * given problem. If {@code setDensity} was not called, will deduce the appropriate
     * implementation to use.
     *
     * @return the matching that represents the lowest cost assignment possible
     */
    public Matching getMatching() {
        if (matching == null) {
            compute();
        }
        return matching;
    }
}
