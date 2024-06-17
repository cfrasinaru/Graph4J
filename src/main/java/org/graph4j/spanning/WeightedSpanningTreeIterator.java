/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.spanning;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.UnionFind;

/**
 * Iterates over all spanning trees of a graph, in ascending or descending order
 * by their weight.
 *
 * The iterator returns the collection of edges of a spanning tree, which can be
 * used to create the actual tree with the method
 * {@link Graph#subgraph(java.util.Collection)}.
 *
 * See: G.K. Janssens, K. Sörensen, An algorithm to generate all spanning trees
 * in order of increasing cost, Pesquisa Operacional, 2005-08, Vol. 25 (2), p.
 * 219-229, https://www.scielo.br/j/pope/a/XHswBwRwJyrfL88dmMwYNWp/?lang=en
 *
 * @see SpanningTreeIterator
 * @author Cristian Frăsinaru
 */
public class WeightedSpanningTreeIterator implements Iterator<Collection<Edge>> {

    protected final Graph graph;
    protected final int numVertices;
    protected final Edge[] edges;
    protected final PriorityQueue<Part> queue;

    /**
     * Creates an iterator over the spanning trees of the specified graph, in
     * ascending order of their weight.
     *
     * @param graph the input graph.
     */
    public WeightedSpanningTreeIterator(Graph graph) {
        this(graph, true);
    }

    /**
     * Creates an iterator over the spanning trees of the specified graph, in
     * ascending or descending order by their weight.
     *
     * @param graph the input graph.
     * @param ascending {@code true} if the spanning trees are returned in
     * ascending order by their weight, {@code false} if the order should be
     * descending.
     */
    public WeightedSpanningTreeIterator(Graph graph, boolean ascending) {

        Objects.requireNonNull(graph);
        this.graph = graph;
        this.numVertices = graph.numVertices();
        edges = graph.edges();
        int order = ascending ? 1 : -1;
        Arrays.sort(edges,
                (e1, e2) -> order * (int) Math.signum(e1.weight() - e2.weight()));
        queue = new PriorityQueue<>(
                (p1, p2) -> order * (int) Math.signum(p1.mstWeight - p2.mstWeight));
        var part = new Part();
        computeMst(part);
        queue.add(part);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Collection<Edge> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        var part = queue.poll();
        refine(part);
        return part.mstEdges;
    }

    protected void refine(Part part) {
        Set<Edge> added = new HashSet<>();
        for (Edge edge : part.mstEdges) {
            if (part.included.contains(edge)) {
                continue;
            }
            var newPart = new Part(part);
            newPart.included.addAll(added);
            newPart.excluded.add(edge);
            added.add(edge);
            if (computeMst(newPart)) {
                queue.add(newPart);
            }
        }
    }

    private boolean computeMst(Part part) {
        var uf = new UnionFind(numVertices);
        for (var e : part.included) {
            if (addEdge(part, e, uf)) {
                return true;
            }
        }
        for (Edge e : edges) {
            if (part.excluded.contains(e)) {
                continue;
            }
            if (addEdge(part, e, uf)) {
                return true;
            }
        }
        return false;
    }

    private boolean addEdge(Part part, Edge e, UnionFind uf) {
        int root1 = uf.find(graph.indexOf(e.source()));
        int root2 = uf.find(graph.indexOf(e.target()));
        if (root1 != root2) {
            uf.union(root1, root2);
            part.mstEdges.add(e);
            part.mstWeight += e.weight();
            if (part.mstEdges.size() == numVertices - 1) {
                return true;
            }
        }
        return false;
    }

    //a set of the partition
    protected class Part {

        Set<Edge> included = new HashSet<>();
        Set<Edge> excluded = new HashSet<>();
        Set<Edge> mstEdges = new HashSet<>();
        double mstWeight;

        Part() {
        }

        Part(Part other) {
            included.addAll(other.included);
            excluded.addAll(other.excluded);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.included);
            hash = 79 * hash + Objects.hashCode(this.excluded);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Part other = (Part) obj;
            if (!Objects.equals(this.included, other.included)) {
                return false;
            }
            return Objects.equals(this.excluded, other.excluded);
        }

    }

}
