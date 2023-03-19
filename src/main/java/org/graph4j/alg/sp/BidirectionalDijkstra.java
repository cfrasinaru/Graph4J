/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graph4j.alg.sp;

import java.util.Arrays;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.Graphs;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.CheckArguments;
import org.graph4j.util.Path;
import org.graph4j.util.VertexHeap;

/**
 * Performs a forward search starting from a source vertex and a reverse
 * (backward) search on the transpose graph starting from the target vertex.
 *
 * @author Cristian Frăsinaru
 */
public class BidirectionalDijkstra extends GraphAlgorithm implements SinglePairShortestPath {

    private final int source;
    private final int target;
    private final int[] vertices;
    private final Graph transpose;
    //
    private double[] costF;
    private int[] beforeF;
    private boolean[] solvedF;
    private VertexHeap heapF;
    //
    private double[] costB;
    private int[] beforeB;
    private boolean solvedB[];
    private VertexHeap heapB;
    //
    private double bestPath;
    private int meetingVertex;

    /**
     * Creates an algorithm to find the shortest path between source and target.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     * @param target the target vertex number.
     */
    public BidirectionalDijkstra(Graph graph, int source, int target) {
        super(graph);
        CheckArguments.graphContainsVertex(graph, source);
        CheckArguments.graphContainsVertex(graph, target);
        this.vertices = graph.vertices();
        this.source = source;
        this.target = target;
        if (graph instanceof Digraph) {
            this.transpose = Graphs.transpose((Digraph) graph);
        } else {
            this.transpose = graph;
        }
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
        if (source == target) {
            return new Path(graph, new int[]{source});
        }
        if (beforeF == null) {
            compute();
        }
        if (meetingVertex == -1) {
            return null;
        }
        int mi = graph.indexOf(meetingVertex);
        if (costF[mi] == Double.POSITIVE_INFINITY || costB[mi] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return createPath();
    }

    @Override
    public double getPathWeight() {
        if (source == target) {
            return 0;
        }
        if (costF == null) {
            compute();
        }
        int mi = graph.indexOf(meetingVertex);
        if (mi == -1) {
            return Double.POSITIVE_INFINITY;
        }
        return costF[mi] + costB[mi];
    }

    private void compute() {
        int n = vertices.length;
        this.costF = new double[n];
        this.costB = new double[n];
        this.beforeF = new int[n];
        this.beforeB = new int[n];
        this.solvedF = new boolean[n];
        this.solvedB = new boolean[n];
        Arrays.fill(costF, Double.POSITIVE_INFINITY);
        Arrays.fill(costB, Double.POSITIVE_INFINITY);
        Arrays.fill(beforeF, -1);
        Arrays.fill(beforeB, -1);
        costF[graph.indexOf(source)] = 0;
        costB[graph.indexOf(target)] = 0;
        this.heapF = new VertexHeap(graph, (i, j) -> (int) Math.signum(costF[i] - costF[j]));
        this.heapB = new VertexHeap(graph, (i, j) -> (int) Math.signum(costB[i] - costB[j]));

        this.bestPath = Double.POSITIVE_INFINITY;
        this.meetingVertex = -1;

        while (true) {
            if (heapF.isEmpty() || heapB.isEmpty()) {
                break;
            }
            int vi = heapF.poll();
            int wi = heapB.poll();
            solvedF[vi] = true;
            solvedB[wi] = true;

            //forward
            int v = vertices[vi];
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (solvedF[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(v, u));
                }
                if (costF[ui] > costF[vi] + weight) {
                    costF[ui] = costF[vi] + weight;
                    beforeF[ui] = vi;
                    heapF.update(ui);
                }
                if (solvedB[ui] && costF[vi] + weight + costB[ui] < bestPath) {
                    bestPath = costF[vi] + weight + costB[ui];
                    meetingVertex = ui;
                }
            }

            //backward
            int w = vertices[wi];
            for (var it = transpose.neighborIterator(w); it.hasNext();) {
                int u = it.next();
                int ui = transpose.indexOf(u);
                if (solvedB[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(u, w));
                }
                if (costB[ui] > costB[wi] + weight) {
                    costB[ui] = costB[wi] + weight;
                    beforeB[ui] = wi;
                    heapB.update(ui);
                }
                if (solvedF[ui] && costB[wi] + weight + costF[ui] < bestPath) {
                    bestPath = costB[wi] + weight + costF[ui];
                    meetingVertex = ui;
                }
            }

            //check termination condition
            if (costF[vi] + costB[wi] >= bestPath) {
                break;
            }
        }
    }

    protected Path createPath() {
        //s --- meet --- t
        int vi = graph.indexOf(meetingVertex);
        Path path = new Path(graph);
        while (vi >= 0) {
            path.add(graph.vertexAt(vi));
            vi = beforeF[vi];
        }
        path.reverse();
        int wi = beforeB[graph.indexOf(meetingVertex)];
        while (wi >= 0) {
            path.add(graph.vertexAt(wi));
            wi = beforeB[wi];
        }
        return path;
    }
}
