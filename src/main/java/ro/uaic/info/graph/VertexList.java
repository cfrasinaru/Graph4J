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

import java.util.Arrays;

/**
 * A list of vertices of a graph. A vertex can appear multiple times.
 *
 * @see Walk
 * @see Trail
 * @see Path
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class VertexList extends VertexCollection {

    public VertexList(Graph graph) {
        super(graph);
    }

    public VertexList(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public VertexList(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    /**
     *
     * @param v a vertex number
     * @return the position of the first appearance of v in the array, or -1 if
     * v does not belong to it
     */
    @Override
    public int indexOf(int v) {
        return super.indexOf(v);
    }
        
    /**
     *
     * @param v a vertex number
     * @param startPos the start position
     * @return the position of the first appearance of v in the array, starting
     * with the given position (inclusive), or -1 if v does not belong to it
     */
    @Override
    public int indexOf(int v, int startPos) {
        return super.indexOf(v, startPos);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.vertices);
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
        final VertexList other = (VertexList) obj;
        for (int i = 0; i < numVertices; i++) {
            if (this.vertices[i] != other.vertices[i]) {
                return false;
            }
        }
        return true;
    }

}
