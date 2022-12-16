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
package ro.uaic.info.graph.temp;

import java.util.Iterator;
import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphVertexIterator implements Iterator<Integer> {

    private final Graph graph;
    private final int numVertices;
    private int pos;

    public GraphVertexIterator(Graph graph) {
        this.graph = graph;
        this.numVertices = graph.numVertices();
    }

    @Override
    public boolean hasNext() {
        return pos < numVertices;
    }

    @Override
    public Integer next() {
        return graph.vertexAt(pos++);
    }

}
