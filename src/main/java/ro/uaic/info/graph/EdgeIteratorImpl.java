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
package ro.uaic.info.graph;

import java.util.NoSuchElementException;

/**
 * Iterates through all the edges of the graph.
 *
 * In order to iterate through all edges incident with a specific vertex use
 * {@link Graph#neighborIterator(int)}.
 *
 * @see NeighborIterator
 * @author Cristian Frăsinaru
 */
class EdgeIteratorImpl<E> implements EdgeIterator<E> {

    private final Graph graph;
    private NeighborIterator<E> neighbors;
    private int index = -1; //the current vertex index
    private Edge currentEdge;

    public EdgeIteratorImpl(Graph graph) {
        this.graph = graph;
        this.currentEdge = null;
        if (!graph.isEmpty()) {
            this.index = 0;
            this.neighbors = graph.neighborIterator(graph.vertexAt(0));
        }
    }

    private void checkCurrentEdge() {
        if (currentEdge == null) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        if (neighbors == null) {
            return false;
        }
        int ix = index;
        var it = graph.neighborIterator(graph.vertexAt(ix), neighbors.position());
        while (true) {
            int v = graph.vertexAt(ix);
            while (it.hasNext()) {
                int u = it.next();
                if (v <= u || graph.isDirected()) {
                    return true;
                }
            }
            ix++;
            if (ix == graph.numVertices()) {
                break;
            }
            it = graph.neighborIterator(graph.vertexAt(ix));
        }
        return false;
    }

    @Override
    public Edge next() {
        Edge nextEdge = null; //prepare the next one
        over:
        while (true) {
            int v = graph.vertexAt(index);
            while (neighbors.hasNext()) {
                int u = neighbors.next();
                if (v <= u || graph.isDirected()) {
                    nextEdge = neighbors.edge();
                    break over;
                }
            }
            index++;
            if (index == graph.numVertices()) {
                break;
            }
            neighbors = graph.neighborIterator(graph.vertexAt(index));
        }
        if (nextEdge == null) {
            throw new NoSuchElementException();
        }
        currentEdge = nextEdge;
        return currentEdge;
    }

    @Override
    public void setWeight(double weight) {
        checkCurrentEdge();
        neighbors.setEdgeWeight(weight);
    }

    @Override
    public double getWeight() {
        checkCurrentEdge();
        return neighbors.getEdgeWeight();
    }

    @Override
    public void setLabel(E label) {
        checkCurrentEdge();
        neighbors.setEdgeLabel(label);
    }

    @Override
    public E getLabel() {
        checkCurrentEdge();
        return neighbors.getEdgeLabel();
    }

    @Override
    public void remove() {
        checkCurrentEdge();
        neighbors.removeEdge();
    }

}
