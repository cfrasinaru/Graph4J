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
package org.graph4j.ordering;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.util.Validator;

/**
 * An <em>acyclic orientation</em> of an undirected graph is an assignment of a
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
     * @param graph the input undirected graph.
     */
    public AcyclicOrientation(Graph graph) {
        super(graph);
        this.vertexOrdering = graph.vertices();
    }

    /**
     *
     * @param graph the input undirected graph.
     * @param vertexOrdering an ordering of the graph vertices.
     */
    public AcyclicOrientation(Graph graph, int[] vertexOrdering) {
        super(graph);
        Validator.checkVertexOrdering(graph, vertexOrdering);
        this.vertexOrdering = vertexOrdering;
    }

    /**
     *
     * @return A directed acyclic graph, corresponding to the input graph.
     */
    public Digraph create() {
        int[] vertexIndex = new int[graph.maxVertexNumber() + 1];
        for (int i = 0; i < vertexOrdering.length; i++) {
            vertexIndex[vertexOrdering[i]] = i;
        }
        var digraph = GraphBuilder.verticesFrom(graph).buildDigraph();
        digraph.setSafeMode(false);
        for (int v : vertexOrdering) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (vertexIndex[v] < vertexIndex[u]) {
                    digraph.addEdge(it.edge());
                }
            }
        }
        digraph.setSafeMode(true);
        return digraph;
    }
}
