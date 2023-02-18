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
package ro.uaic.info.graph.alg;

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.VertexList;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.util.IntArrays;

/**
 * An <i>acyclic orientation</i> of an undirected graph is an assignment of a
 * direction to each edge (an orientation) that does not form any directed cycle
 * and therefore makes it into a directed acyclic graph. Every graph has an
 * acyclic orientation.
 *
 * @author Cristian Frăsinaru
 */
public class AcyclicOrientation extends SimpleGraphAlgorithm {

    private final int[] vertexOrdering;

    /**
     *
     * @param graph
     */
    public AcyclicOrientation(Graph graph) {
        this(graph, graph.vertices());
    }

    /**
     *
     * @param graph
     * @param vertexOrdering an ordering of the graph vertices
     */
    public AcyclicOrientation(Graph graph, int[] vertexOrdering) {
        super(graph);
        if (!IntArrays.sameValues(graph.vertices(), vertexOrdering)) {
            throw new IllegalArgumentException(
                    "The ordering is invalid - it must contain the vertices "
                    + "of the graph in any given order.");
        }
        this.vertexOrdering = vertexOrdering;
    }

    /**
     *
     * @return A directed acyclic graph, corresponding to the input graph
     */
    public Digraph create() {
        int[] vertexIndex = new int[graph.maxVertexNumber() + 1];
        for (int i = 0; i < vertexOrdering.length; i++) {
            vertexIndex[vertexOrdering[i]] = i;
        }
        var digraph = GraphBuilder.verticesFrom(graph).buildDigraph();
        for (int v : vertexOrdering) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (vertexIndex[v] < vertexIndex[u]) {
                    digraph.addEdge(it.edge());
                }
            }
        }
        return digraph;
    }
}
