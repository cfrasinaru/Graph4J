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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.graph4j.Graph;

/**
 * Implementation of a binary min heap using arrays. The keys stored in the heap
 * represent the vertex indices of a graph, from 0 to numVertices() - 1.
 *
 * The order relation is given by a comparator.
 *
 * @author Cristian Frăsinaru
 */
public class VertexHeap implements Iterable<Integer> {

    private final Graph graph;
    private final IntComparator comparator;
    private int[] keys; //the heap content containing graph indices
    private int[] positions; //on which position in the heap is a given key
    private int size;

    /**
     *
     * @param graph the graph the vertices of the heap belong to.
     * @param comparator a comparator for vertices.
     */
    public VertexHeap(Graph graph, IntComparator comparator) {
        this(graph, true, comparator);
    }

    /**
     *
     * @param graph the graph the vertices of the heap belong to.
     * @param comparator a comparator for vertices.
     * @param addAll add all graph vertices in the heap.
     */
    public VertexHeap(Graph graph, boolean addAll, IntComparator comparator) {
        Objects.nonNull(graph);
        Objects.nonNull(comparator);
        this.graph = graph;
        int n = graph.numVertices();
        this.keys = new int[1 + n]; //min is at keys[1]
        this.positions = new int[n];
        this.keys[0] = Integer.MIN_VALUE;
        this.comparator = comparator;
        if (addAll) {
            for (int i = 0; i < n; i++) {
                add(i);
            }
        }
    }

    /**
     *
     * @return the number of vertices in the heap.
     */
    public int size() {
        return size;
    }

    /**
     *
     * @return {@code true} if the heap contains no vertices.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *
     * @return a new array containing the keys in the heap.
     */
    public int[] keys() {
        return Arrays.copyOf(keys, size);
    }

    /**
     *
     * @return an iterator over the vertices in the heap.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new VertexHeapIterator();
    }

    /**
     *
     * @param key a vertex index.
     * @return {@code true} if the key is contained in the heap.
     */
    public boolean contains(int key) {
        return positions[key] > 0;
    }

    /**
     *
     * @param key a vertex index.
     */
    public final void add(int key) {
        if (size + 1 >= keys.length) {
            grow();
        }
        size++;
        keys[size] = key;
        positions[key] = size;
        siftUp(size);
    }

    /**
     *
     * @return the key (index) corresponding to the minimum value.
     */
    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("The heap is empty.");
        }
        return keys[1];
    }

    /**
     * Returns and removes the minimum.
     *
     * @return the key (index) corresponding to the minimum value.
     */
    public int poll() {
        if (isEmpty()) {
            throw new IllegalStateException("The heap is empty.");
        }
        int top = keys[1];
        keys[1] = keys[size--];
        positions[keys[1]] = 1;
        positions[top] = -1;
        siftDown(1);
        return top;
    }

    /**
     * Removes a specified key from the heap.
     *
     * @param key the index of a vertex.
     * @return {@code true} if the heap has changed.
     */
    public boolean remove(int key) {
        if (key < 0 || key >= positions.length) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        int pos = positions[key];
        if (pos < 0) {
            return false;
        }
        keys[pos] = keys[size--];
        positions[keys[pos]] = pos;
        positions[key] = -1;
        siftDown(pos);
        return true;
    }

    /**
     *
     * @param key the index of a vertex.
     * @return {@code true} if the heap has changed.
     */
    public boolean update(int key) {
        int pos = positions[key];
        if (pos <= 1) {
            return false; //is top or does not exist
        }
        updateAtPos(pos);
        return true;
    }

    private void updateAtPos(int pos) {
        int parent = pos >> 1;
        if (compareTo(pos, parent) < 0) {
            siftUp(pos);
        } else {
            siftDown(pos);
        }
    }

    /**
     *
     * @param key the index of a vertex.
     * @return {@code true} if the heap has changed.
     */
    public boolean addOrUpdate(int key) {
        int pos = positions[key];
        if (pos == 1) {
            return false;
        }
        if (pos < 1) {
            add(key);
            return true;
        }
        updateAtPos(pos);
        return true;
    }

    private void swap(int pos1, int pos2) {
        int temp = keys[pos1];
        keys[pos1] = keys[pos2];
        keys[pos2] = temp;
        positions[keys[pos2]] = pos2;
        positions[keys[pos1]] = pos1;
    }

    //swapping a node with its parent, and repeating the process on the parent 
    //until the root is reached or the heap property is satisfied.
    private void siftUp(int pos) {
        int parent = pos >> 1;
        while (parent > 0 && compareTo(pos, parent) < 0) {
            swap(pos, parent);
            pos = parent;
            parent = pos >> 1;
        }
    }

    //moves the value down the tree by successively exchanging the value with the smaller of its two children;
    //the operation continues until the value reaches a position where it is less than both its children, 
    //or, failing that, until it reaches a leaf.
    private void siftDown(int pos) {
        if (pos > size >> 1) {
            //is leaf
            return;
        }
        int leftChildPos = pos << 1;
        int rightChildPos = (pos << 1) + 1;
        if (compareTo(pos, leftChildPos) <= 0 && compareTo(pos, rightChildPos) <= 0) {
            return;
        }
        int swapPos;
        if (rightChildPos <= size) {
            //we have both child nodes
            swapPos = compareTo(leftChildPos, rightChildPos) < 0 ? leftChildPos : rightChildPos;
        } else {
            //only left node
            swapPos = leftChildPos;
        }
        swap(pos, swapPos);
        siftDown(swapPos);
    }

    private int compareTo(int pos1, int pos2) {
        return comparator.compareTo(keys[pos1], keys[pos2]);
    }

    private void grow() {
        int oldLen = keys.length;
        int newLen = oldLen + (oldLen >> 1);
        keys = Arrays.copyOf(keys, newLen);
    }

    private boolean verify() {
        for (int i = 2; i < size; i++) {
            if (compareTo(1, i) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 1; i <= size; i++) {
            if (i > 1) {
                sb.append(", ");
            }
            sb.append(graph.vertexAt(keys[i]));
        }
        sb.append("]");
        return sb.toString();

    }

    private class VertexHeapIterator implements Iterator<Integer> {

        private int pos = 0;

        @Override
        public Integer next() {
            if (pos > size) {
                throw new NoSuchElementException();
            }
            return keys[++pos];
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public void remove() {
            if (pos <= 0) {
                throw new NoSuchElementException();
            }
            VertexHeap.this.remove(keys[pos--]);
        }
    }

}
