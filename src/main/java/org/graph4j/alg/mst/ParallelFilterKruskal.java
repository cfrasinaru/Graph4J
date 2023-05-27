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
package org.graph4j.alg.mst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.UnionFind;

/**
 * Parallel implementation of the Filter-Kruskal algorithm.
 *
 * (see
 * https://en.wikipedia.org/wiki/Parallel_algorithms_for_minimum_spanning_trees#Approach_2:_Filter-Kruskal).
 * Filtering and partitioning are parallelized.
 *
 * @author Ioana-Larisa Cioată
 */
public class ParallelFilterKruskal extends GraphAlgorithm
        implements MinimumSpanningTreeAlgorithm {

    private static long LIMIT = 400;
    private Graph tree; // the result tree (MST)
    private final UnionFind unionFind;
    private final Edge[] edges; // the edges we work with
    private final long finalNrOfEdges; // the number of edges that the result tree should have
    private MutableDouble totalWeight; // the weight of the result tree (MST)
    private boolean calculated;

    public ParallelFilterKruskal(Graph graph) {
        super(graph);
        this.totalWeight = new MutableDouble(0.0);
        this.edges = graph.edges();

        int nrVertices = graph.numVertices();

        this.tree = GraphBuilder.empty()
                .estimatedNumVertices(nrVertices)
                .buildGraph();
        Arrays.stream(graph.vertices()).forEach(i -> tree.addVertex(i));
        this.unionFind = new UnionFind(nrVertices);
        this.finalNrOfEdges = nrVertices - 1;
    }

    private ParallelFilterKruskal(Graph graph, Graph tree, UnionFind unionFind, Edge[] edges, MutableDouble totalWeight) {
        super(graph);
        this.tree = tree;
        this.unionFind = unionFind;
        this.edges = edges;
        this.finalNrOfEdges = tree.numVertices() - 1;
        this.totalWeight = totalWeight;
    }

    private void kruskal() {
        // sorting
        Arrays.sort(edges, Comparator.comparing(Edge::weight));

        for (Edge edge : edges) {
            if (tree.numEdges() == finalNrOfEdges) {
                calculated = true;
                return;
            }

            int source = edge.source();
            int target = edge.target();
            int root1 = unionFind.find(source);
            int root2 = unionFind.find(target);

            // we add the edge only if adding it does not form a cycle
            if (root1 != root2) {
                unionFind.union(root1, root2);
                tree.addEdge(edge);
                totalWeight.add(edge.weight());
            }
        }
    }

    private void compute() {
        // check if MST has been calculated and return if true
        calculated = (tree.numEdges() == finalNrOfEdges);
        if (calculated) {
            return;
        }

        // if there are "few" edges, we use kruskal algorithm
        if (edges.length < LIMIT) {
            kruskal();
            return;
        }

        // if not we divide the edges array into 2 arrays and repeat the steps for these 2 sets of edges
        // choose a pivot
        double pivot = (edges[0].weight() + edges[1].weight()) / 2;

        // divide the edges into 2 sets
        Partition partition = new Partition(edges, pivot);
        Edge[] biggerEdges = partition.getBiggerEdges();
        Edge[] smallerOrEqualEdges = partition.getsmallerOrEqualEdges();

        if (biggerEdges.length == 0) {

            double possibleSmallerOrEqualWeight = partition.getExampleOfsmallerOrEqualWeight();

            if (possibleSmallerOrEqualWeight < pivot) {

                // for this new pivot biggerEdges will no longer be empty
                pivot = possibleSmallerOrEqualWeight;

                //new partition
                partition = new Partition(edges, pivot);
                biggerEdges = partition.getBiggerEdges();
                smallerOrEqualEdges = partition.getsmallerOrEqualEdges();
            } else {
                // if the weights of all the edges are equal
                // then biggerEdges is empty and there is no weight smaller or equal than the pivot
                // in this case we divide the array in 2 halves
                smallerOrEqualEdges = Arrays.copyOfRange(edges, 0, edges.length / 2);
                biggerEdges = Arrays.copyOfRange(edges, edges.length / 2, edges.length);
            }
        }

        // we firs work with the smaller edges
        var filterSmall = new ParallelFilterKruskal(graph, tree, unionFind, smallerOrEqualEdges, totalWeight);
        filterSmall.compute();

        // depending on the smaller edges that were added to the tree, we filter the larger edges
        biggerEdges = filter(biggerEdges);

        // if there exists any candidate edges left after filtering, we take care of them too
        if (biggerEdges.length > 0) {
            var filterBig = new ParallelFilterKruskal(graph, tree, unionFind, biggerEdges, totalWeight);
            filterBig.compute();
        }
        calculated = true;
    }

    @Override
    public Graph getTree() {
        if (!calculated) {
            compute();
        }
        return tree;
    }

    private Edge[] filter(Edge[] edges) {
        // the list of the final edges after filtering
        List<Edge> edgeList = new ArrayList<>();

        Arrays.stream(edges)
                .parallel()
                .forEach(edge -> {
                    int source = edge.source();
                    int target = edge.target();
                    int root1 = unionFind.find(source);
                    int root2 = unionFind.find(target);

                    // if the vertices are not yet connected in the tree, we add the edge to the list
                    if (root1 != root2) {
                        synchronized (edgeList) {
                            edgeList.add(edge);
                        }
                    }
                });

        return edgeList.toArray(Edge[]::new);
    }

    @Override
    public double getWeight() {
        compute();
        return totalWeight.getValue();
    }

    @Override
    public EdgeSet getEdges() {
        compute();
        return new EdgeSet(graph, tree.edges());
    }

    // The weight of each edge is compared to the pivot. 
    //If it is bigger, the edge is added in the biggerEdges array, otherwise in the smallerOrEqualEdges array
    private class Partition {

        private final Edge[] smallerOrEqualEdges;
        private final Edge[] biggerEdges;
        private double exampleOfSmallerWeight; // if the chosen pivot is not good, we will put a better pivot here

        public Partition(Edge[] edges, double pivot) {
            exampleOfSmallerWeight = pivot;
            List<Edge> smallList = new ArrayList<>();
            List<Edge> bigList = new ArrayList<>();

            int nrThreads = 5;

            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrThreads);

            // the length of the portion of the array handled by a thread (on average)
            int sectorSize = (int) Math.ceil(edges.length / nrThreads);

            for (int i = 0; i < nrThreads; i++) {
                //calculate where the sector where the thread operates starts and ends
                int start = i * sectorSize;

                int end = start + sectorSize;
                if (i == nrThreads - 1) {
                    end = edges.length;
                }

                // copy the edges handled by the thread
                Edge[] sector = Arrays.copyOfRange(edges, start, end);

                // task : compare edge's weight with the pivot and put the edge in the correct list
                Runnable task = () -> {
                    for (Edge edge : sector) {
                        if (edge.weight() <= pivot) {

                            if (exampleOfSmallerWeight != pivot && edge.weight() < pivot) {
                                exampleOfSmallerWeight = edge.weight();
                            }

                            synchronized (smallList) {
                                smallList.add(edge);
                            }
                        } else {
                            synchronized (bigList) {
                                bigList.add(edge);
                            }
                        }
                    }
                };
                executor.execute(task);
            }

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
            }

            smallerOrEqualEdges = smallList.toArray(Edge[]::new);
            biggerEdges = bigList.toArray(Edge[]::new);
        }

        public Edge[] getsmallerOrEqualEdges() {
            return smallerOrEqualEdges;
        }

        public Edge[] getBiggerEdges() {
            return biggerEdges;
        }

        public double getExampleOfsmallerOrEqualWeight() {
            return exampleOfSmallerWeight;
        }
    }

    private class MutableDouble {

        private double value;

        public MutableDouble(double value) {
            this.value = value;
        }

        public double getValue() {
            return this.value;
        }

        public void add(double value) {
            this.value += value;
        }
    }
}
