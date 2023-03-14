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
import org.graph4j.Graph;

/**
 * Implementation of a binary heap using arrays. The keys stored in the heap
 * represent the vertex indices of a graph, from 0 to numVertices() - 1.
 *
 * The order relation is given by a comparator.
 *
 * @author Cristian Frăsinaru
 */
public class VertexHeap {

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
        if (comparator == null) {
            throw new IllegalArgumentException(
                    "The comparator cannot be null");
        }
        this.graph = graph;
        int n = graph.numVertices();
        this.keys = new int[1 + n]; //min is at keys[1]
        this.positions = new int[n];
        this.keys[0] = Integer.MIN_VALUE;
        this.comparator = comparator;
        for (int i = 0; i < n; i++) {
            add(i);
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
     * @param key a vertex index.
     */
    private void add(int key) {
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
        siftDown(1);
        return top;
    }

    /**
     *
     * @param key the index of a vertex.
     */
    public void update(int key) {
        int pos = positions[key];
        if (pos == 1) {
            return; //is top
        }
        int parent = pos >> 1;
        if (compareTo(pos, parent) < 0) {
            siftUp(pos);
        } else {
            siftDown(pos);
        }
    }

    private void swap(int pos1, int pos2) {
        int temp = keys[pos1];
        keys[pos1] = keys[pos2];
        keys[pos2] = temp;
        positions[keys[pos2]] = pos2;
        positions[keys[pos1]] = pos1;
    }

    private void siftUp(int pos) {
        int parent = pos >> 1;
        while (parent > 0 && compareTo(pos, parent) < 0) {
            swap(pos, parent);
            pos = parent;
            parent = pos >> 1;
        }
    }

    private void siftDown(int pos) {
        if (pos > size >> 1) {
            //is leaf
            return;
        }
        int leftChildPos = 2 * pos;
        int rightChildPos = 2 * pos + 1;
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
        if (pos1 > size) {
            return 1; //??
        }
        if (pos2 > size) {
            return -1;//??
        }
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

}
