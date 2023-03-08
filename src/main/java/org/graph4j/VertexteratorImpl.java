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
package org.graph4j;

import java.util.NoSuchElementException;

/**
 * Iterates through all the vertices of the graph.
 *
 * In order to iterate through all neighbors of a specific vertex use
 * {@link Graph#neighborIterator(int)}.
 *
 * @author Cristian Frăsinaru
 */
class VertexteratorImpl<V> implements VertexIterator<V> {

    private final Graph graph;
    private int index = -1; //the current vertex index
    private int vertex = -1;

    public VertexteratorImpl(Graph graph) {
        this.graph = graph;
    }

    private void checkCurrentVertex() {
        if (index < 0 || index >= graph.numVertices()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return index < graph.numVertices() - 1;
    }

    @Override
    public int next() {
        index++;
        vertex = graph.vertexAt(index);
        return vertex;
    }

    @Override
    public void setWeight(double weight) {
        checkCurrentVertex();
        graph.setVertexWeight(vertex, weight);
    }

    @Override
    public double getWeight() {
        checkCurrentVertex();
        return graph.getVertexWeight(vertex);
    }

    @Override
    public void setLabel(V label) {
        checkCurrentVertex();
        graph.setVertexLabel(vertex, label);
    }

    @Override
    public V getLabel() {
        checkCurrentVertex();
        return (V) graph.getVertexLabel(vertex);
    }

    @Override
    public void remove() {
        checkCurrentVertex();
        graph.removeVertex(vertex);
        index--;
        //vertex = index < 0 ? -1 : graph.vertexAt(index);
    }

}
