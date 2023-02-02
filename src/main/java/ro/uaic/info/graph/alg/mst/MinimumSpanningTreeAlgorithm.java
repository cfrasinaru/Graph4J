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
package ro.uaic.info.graph.alg.mst;

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.EdgeSet;

/**
 * A <em>minimum spanning tree (MST)</em> is an acyclic subgraph of an
 * edge-weighted undirected graph, that connects all the vertices, with the
 * minimum possible total edge weight.
 *
 * If the graph is not connected, a <em>minimum spanning forest</em> is the
 * union of the minimum spanning trees for its connected components.
 *
 * @author Cristian Frăsinaru
 */
public interface MinimumSpanningTreeAlgorithm {

    /**
     *
     * @return the edges of the minimum spanning tree.
     */
    EdgeSet getEdges();

    /**
     * If the graph is disconnected, this is actually a spanning forrest.
     *
     * @return a graph representing the minimum spanning tree.
     */
    Graph getTree();

    /**
     *
     * @return the minimum weight of a spanning tree.
     */
    double getWeight();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static MinimumSpanningTreeAlgorithm getInstance(Graph graph) {
        int n = graph.numVertices();
        int m = graph.numEdges();
        return new PrimMinimumSpanningTreeHeap(graph);
        //return new PrimMinimumSpanningTreeDefault(graph);
        //return new KruskalMinimumSpanningTree(graph);
    }
}
