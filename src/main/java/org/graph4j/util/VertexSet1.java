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
package org.graph4j.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import org.graph4j.Graph;

/**
 * A set of vertices of a graph. No duplicates are allowed.
 *
 * @author Cristian Frăsinaru
 */
class VertexSet1 extends VertexCollection {

    protected IntHashMap posMap; //positions
    //protected HashMap<Integer, Integer> posMap; //positions

    protected VertexSet1() {
    }

    public VertexSet1(Graph graph) {
        super(graph);
    }

    public VertexSet1(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public VertexSet1(Graph graph, int[] vertices) {
        super(graph, vertices.length);
        addAll(vertices);
    }

    public VertexSet1(VertexSet1 other) {
        this.graph = other.graph;
        this.numVertices = other.numVertices;
        this.first = other.first;
        this.vertices = Arrays.copyOf(other.vertices, other.vertices.length);
        if (other.posMap != null) {
            this.posMap = new IntHashMap(other.posMap);
            //this.posMap = new HashMap<>(other.posMap);
        }
    }

    //lazy creation
    private void createPosMap() {
        this.posMap = new IntHashMap();
        //this.posMap = new HashMap<>(numVertices);
        for (int pos = 0; pos < numVertices; pos++) {
            posMap.put(vertices[pos], pos);
        }
    }

    @Override
    public boolean add(int v) {
        if (contains(v)) {
            return false;
        }
        return addDirectly(v);
    }

    //it is added on the last position
    protected boolean addDirectly(int v) {
        boolean modified = super.add(v);
        if (modified && posMap != null) {
            posMap.put(v, numVertices - 1);
        }
        return modified;
    }

    @Override
    public final void addAll(int... vertices) {
        super.addAll(vertices);
    }

    @Override
    public boolean remove(int v) {
        return super.remove(v);
    }

    //swap with the last element
    @Override
    protected void removeFromPos(int pos) {
        int v = vertices[pos];
        vertices[pos] = vertices[numVertices - 1];
        if (posMap != null) {
            posMap.remove(v);
            if (pos < numVertices - 1) {
                posMap.put(vertices[pos], pos);
            }
        }
        numVertices--;
    }

    @Override
    public void clear() {
        super.clear();
        if (posMap != null) {
            posMap.clear();
        }
    }

    @Override
    protected int indexOf(int v) {
        //for smaller sets, just iterate        
        if (numVertices * numVertices < graph.numVertices()) {
        //if (numVertices <= DEFAULT_CAPACITY) {
            return super.indexOf(v);
        }
        //for larger sets, create the posMap and use it
        if (posMap == null) {
            createPosMap();
        }
        int pos = posMap.get(v);
        return pos == IntHashMap.NONE ? -1 : pos;
        //return posMap.getOrDefault(v, -1);

    }

    @Override
    public boolean contains(int v) {
        return indexOf(v) >= 0;
    }

    /**
     * Returns and removes an element from the set, usually the last one added.
     *
     * @return an element from the set.
     */
    public int pop() {
        if (numVertices == 0) {
            throw new NoSuchElementException("The vertex set is empty");
        }
        int v = vertices[numVertices - 1];
        if (posMap != null) {
            posMap.remove(v);
        }
        numVertices--;
        return v;
    }

    /**
     * Returns an element from the set, uauslly the last one added.
     *
     * @return an element from the set.
     */
    public int peek() {
        if (numVertices == 0) {
            throw new NoSuchElementException("The vertex set is empty");
        }
        return vertices[numVertices - 1];
    }

    /**
     *
     * @param other another vertex set.
     * @return a new set containing vertices belonging to both this and the
     * other set.
     */
    public VertexSet1 intersection(VertexSet1 other) {
        VertexSet1 set1;
        VertexSet1 set2;
        if (this.size() <= other.size()) {
            set1 = this;
            set2 = other;
        } else {
            set1 = other;
            set2 = this;
        }
        VertexSet1 result = new VertexSet1(graph, set1.size());
        for (int v : set1.vertices()) {
            if (set2.contains(v)) {
                result.addDirectly(v);
            }
        }
        return result;
    }

    /**
     * It is assumed that the other argument does not contain duplicates.
     *
     * @param other an array of vertex numbers, without duplicates.
     * @return a new set containing vertices belonging to this set and the other
     * array.
     */
    public VertexSet1 intersection(int... other) {
        int min;
        if (this.numVertices <= other.length) {
            min = this.numVertices;
        } else {
            min = other.length;
        }
        VertexSet1 result = new VertexSet1(graph, min);
        for (int v : other) {
            if (this.contains(v)) {
                result.addDirectly(v);
            }
        }
        return result;
    }

    /**
     *
     * @param other an array of vertex numbers.
     * @return a new set containing vertices belonging to this set or the other
     * array.
     */
    public VertexSet1 union(int... other) {
        VertexSet1 result = new VertexSet1(graph, this.size() + other.length);
        union(this, other, result);
        return result;
    }

    @Override
    public int hashCode() {
        return IntStream.of(vertices).sum();
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
        final VertexSet1 other = (VertexSet1) obj;
        return IntArrays.sameValues(this.vertices(), other.vertices());
    }

}
