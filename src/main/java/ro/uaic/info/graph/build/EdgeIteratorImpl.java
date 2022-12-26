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
package ro.uaic.info.graph.build;

import java.util.Iterator;
import java.util.NoSuchElementException;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.NeighborIterator;

/**
 * Iterates through all the edges of the graph.
 *
 * In order to iterate through all edges incident with a specific vertex use
 * {@link Graph#neighborIterator(int)}.
 *
 * @see NeighborIterator
 * @author Cristian Frăsinaru
 */
class EdgeIteratorImpl implements Iterator<Edge> {

    private final Graph graph;
    private NeighborIterator neighbors;
    private int index; //the current vertex index
    private Edge nextEdge;

    public EdgeIteratorImpl(Graph graph) {
        this.graph = graph;
        if (graph.numEdges() > 0) {
            int v = -1;
            do {
                v = graph.vertexAt(index);
                neighbors = graph.neighborIterator(v);
            } while (!neighbors.hasNext());
            int u = neighbors.next();
            nextEdge = graph.edge(v, u);
        }
    }

    @Override
    public boolean hasNext() {
        return nextEdge != null;
    }

    @Override
    public Edge next() {
        if (nextEdge == null) {
            throw new NoSuchElementException();
        }
        var edge = nextEdge; //to return                
        nextEdge = null; //prepare the next one
        over:
        while (true) {
            int v = graph.vertexAt(index);
            while (neighbors.hasNext()) {
                int u = neighbors.next();
                if (v <= u || graph.isDirected()) {
                    nextEdge = graph.edge(v, u);
                    break over;
                }
            }
            index++;
            if (index == graph.numVertices()) {
                break;
            }
            neighbors = graph.neighborIterator(graph.vertexAt(index));
        }
        return edge;
    }
}
