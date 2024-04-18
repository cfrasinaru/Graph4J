/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.CheckArguments;

/**
 *
 * Johnson's algorithm finds the shortest paths between all pairs of vertices in
 * an edge-weighted directed graph. It allows some of the edge weights to be
 * negative numbers, but no negative-weight cycles may exist. It works by using
 * the Bellman–Ford algorithm to compute a transformation of the input graph
 * that removes all negative weights, allowing Dijkstra's algorithm to be used
 * on the transformed graph.
 *
 * <p>
 * The complexity of the algorithm is O(nmlogn), given by the Dijkstra's
 * algorithm implemented with a binary heap, which is repeated for every vertex.
 * It is best suited for sparse graphs. In case of dense graphs
 * {@link FloydWarshallShortestPath} algorithm may perform better.
 *
 *
 * @see FloydWarshallShortestPath
 * @author Cristian Frăsinaru
 * @author Cristian Ivan
 */
public class JohnsonShortestPath extends GraphAlgorithm
        implements AllPairsShortestPath {

    private Graph auxGraph; //Dijkstra will be executed on this graph
    private double[] h; //used to adjust the edge weights
    private SingleSourceShortestPath[] algs; //Dijkstra alg instances
    private double[][] weights; //redundant

    public JohnsonShortestPath(Graph graph) {
        super(graph);
        prepare();
    }

    private void prepare() {
        //Create a copy of the graph
        this.auxGraph = graph.copy();
        if (!auxGraph.isEdgeWeighted()) {
            //put weight 1.0 on each edge
            for (int v : auxGraph.vertices()) {
                for (var it = auxGraph.neighborIterator(v); it.hasNext();) {
                    it.next();
                    it.setEdgeWeight(1.0);
                }
            }
        }        
        //Add a new auxiliary node connected to all vertices, with weight 0        
        int newNode = auxGraph.addVertex();
        for (int v : graph.vertices()) {
            auxGraph.addWeightedEdge(newNode, v, 0.0);            
        }
        //Use Bellman–Ford algorithm O(nm) from the auxiliary node
        //to find for each vertex the shortest path to it h(v)
        //if there are no negative edges, h(v)=0 for all v
        //h(v) cannot be Infinity
        var bellmanFord = new BellmanFordShortestPath(auxGraph, newNode);
        int n = graph.numVertices();
        this.h = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = bellmanFord.getPathWeight(auxGraph.vertexAt(i));
        }
        auxGraph.removeVertex(newNode);

        //Modify the weight of all edges
        //to make sure that they all have non-negative weights.
        for (int v : auxGraph.vertices()) {
            int vi = auxGraph.indexOf(v);
            for (var it = auxGraph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = auxGraph.indexOf(u);
                double weight = it.getEdgeWeight(); //of vu
                it.setEdgeWeight(weight + h[vi] - h[ui]);
            }
        }
        //Use Dijkstra's algorithm with binary heap O(m logn) 
        //to find the shortest paths from each node to every other node in the reweighted graph.
        //LAZY
    }

    @Override
    public Path findPath(int source, int target) {
        CheckArguments.graphContainsVertex(graph, source);
        CheckArguments.graphContainsVertex(graph, target);
        if (algs == null) {
            computeAll();
        }
        var dijkstra = algs[graph.indexOf(source)];
        Path path = dijkstra.findPath(target);
        if (path != null) {
            path = new Path(graph, path.vertices());
        }
        return path;
    }

    @Override
    public double getPathWeight(int source, int target) {
        CheckArguments.graphContainsVertex(graph, source);
        CheckArguments.graphContainsVertex(graph, target);
        if (algs == null) {
            computeAll();
        }
        int si = graph.indexOf(source);
        int ti = graph.indexOf(target);
        return weights[si][ti];
    }

    /**
     * Returns a matrix containing the weights of the shortest paths for every
     * pair of vertices. This implementation is multi-threaded.
     *
     * @return a matrix containing the weights of the shortest paths for every
     * pair of vertices.
     */
    @Override
    public double[][] getPathWeights() {
        if (algs == null) {
            computeAll();
        }
        return weights;
    }

    //runs Dijkstra algorithm from all vertices
    private void computeAll() {
        int n = graph.numVertices();
        this.algs = new SingleSourceShortestPath[n];
        this.weights = new double[n][];
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < n; i++) {
            final int v = graph.vertexAt(i);
            executor.submit(() -> compute(v));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
    }

    //runs Dijkstra algorithm from a single vertex, on the auxiliary graph
    private void compute(int v) {
        int vi = graph.indexOf(v);
        var dijkstra = new DijkstraShortestPathHeap(auxGraph, v);
        algs[vi] = dijkstra;
        weights[vi] = dijkstra.getPathWeights();
        for (int ui = 0, n = graph.numVertices(); ui < n; ui++) {
            weights[vi][ui] += h[ui] - h[vi];
        }
    }

}
