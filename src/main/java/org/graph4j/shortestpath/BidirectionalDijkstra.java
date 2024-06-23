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
package org.graph4j.shortestpath;

import java.util.Arrays;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.util.Validator;
import org.graph4j.util.Path;
import org.graph4j.util.VertexHeap;

/**
 * Determines the shortest path between two vertices. Performs a forward search
 * starting from a source vertex and a reverse (backward) search on the
 * transpose graph starting from the target vertex.
 *
 * @author Cristian Frăsinaru
 */
public class BidirectionalDijkstra extends GraphAlgorithm implements SinglePairShortestPath {

    private final int source;
    private final int target;
    private final int[] vertices;
    //
    private Path bestPath;
    private double bestWeight;

    /**
     * Creates an algorithm to find the shortest path between source and target.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     * @param target the target vertex number.
     */
    public BidirectionalDijkstra(Graph graph, int source, int target) {
        super(graph);
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        this.vertices = graph.vertices();
        this.source = source;
        this.target = target;
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
        if (bestPath == null) {
            compute();
        }
        return bestPath;
    }

    @Override
    public double getPathWeight() {
        if (source == target) {
            return 0;
        }
        if (bestPath == null) {
            compute();
        }
        return bestWeight;
    }

    private void compute() {
        this.bestWeight = Double.POSITIVE_INFINITY; //bestPath is null
        int n = vertices.length;
        double[] costF = new double[n];
        double[] costB = new double[n];
        int[] beforeF = new int[n];
        int[] beforeB = new int[n];
        boolean[] solvedF = new boolean[n];
        boolean[] solvedB = new boolean[n];
        Arrays.fill(costF, Double.POSITIVE_INFINITY);
        Arrays.fill(costB, Double.POSITIVE_INFINITY);
        //Arrays.fill(beforeF, -1);
        //Arrays.fill(beforeB, -1);
        //
        int si = graph.indexOf(source);
        int ti = graph.indexOf(target);
        costF[si] = 0;
        costB[ti] = 0;
        beforeF[si] = -1;
        beforeB[ti] = -1;
        VertexHeap heapF = new VertexHeap(graph, false, (i, j) -> (int) Math.signum(costF[i] - costF[j]));
        VertexHeap heapB = new VertexHeap(graph, false, (i, j) -> (int) Math.signum(costB[i] - costB[j]));
        heapF.add(si);
        heapB.add(ti);

        int meeting = -1;
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
                int u = it.next(); //v->u
                int ui = graph.indexOf(u);
                if (solvedF[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(v, u));
                }
                double newCostF = costF[vi] + weight;
                if (costF[ui] > newCostF) {
                    costF[ui] = newCostF;
                    beforeF[ui] = vi;
                    heapF.addOrUpdate(ui);
                }
                if (solvedB[ui] && newCostF + costB[ui] < bestWeight) {
                    bestWeight = newCostF + costB[ui];
                    meeting = ui;
                }
            }

            //backward
            int w = vertices[wi];
            for (var it = directed ? ((Digraph) graph).predecessorIterator(w) : graph.neighborIterator(w); it.hasNext();) {
                int u = it.next(); //u->w
                int ui = graph.indexOf(u);
                if (solvedB[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(u, w));
                }
                double newCostB = costB[wi] + weight;
                if (costB[ui] > newCostB) {
                    costB[ui] = newCostB;
                    beforeB[ui] = wi;
                    heapB.addOrUpdate(ui);
                }
                if (solvedF[ui] && newCostB + costF[ui] < bestWeight) {
                    bestWeight = newCostB + costF[ui];
                    meeting = ui;
                }
            }

            //check termination condition
            if (costF[vi] + costB[wi] >= bestWeight) {
                break;
            }
        }

        if (meeting < 0) {
            return;
        }
        assert bestWeight != Double.POSITIVE_INFINITY;

        //compute the path
        //s --- meet --- t
        int vi = graph.indexOf(meeting);
        bestPath = new Path(graph);
        while (vi >= 0) {
            bestPath.add(graph.vertexAt(vi));
            vi = beforeF[vi];
        }
        bestPath.reverse();
        int wi = beforeB[graph.indexOf(meeting)];
        while (wi >= 0) {
            bestPath.add(graph.vertexAt(wi));
            wi = beforeB[wi];
        }
    }
}
