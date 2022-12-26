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
package ro.uaic.info.graph.util;

import java.util.Arrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IntArrayHeap {

    private IntComparator comparator;
    private int[] heap;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 100;

    public IntArrayHeap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public IntArrayHeap(int initialCapacity) {
        this(initialCapacity, null);
    }

    public IntArrayHeap(IntComparator comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    public IntArrayHeap(int initialCapacity, IntComparator comparator) {
        this.size = 0;
        this.heap = new int[1 + initialCapacity]; //min is at heap[1]
        this.heap[0] = Integer.MIN_VALUE;
        this.comparator = comparator;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *
     * @param value
     */
    public void add(int value) {
        if (size + 1 >= heap.length) {
            grow();
        }
        heap[++size] = value;
        //move up to the right place
        int current = size;
        int parent = current >> 1;
        while (parent > 0 && compareTo(current, parent) < 0) {
            swap(current, parent);
            current = parent;
            parent = current >> 1;
        }
    }

    /**
     *
     * @return
     */
    public int poll() {
        if (isEmpty()) {
            throw new IllegalStateException("The heap is empty.");
        }
        int top = heap[1];
        heap[1] = heap[size--];
        heap[size + 1] = 0;
        heapify(1);
        return top;
    }

    private void swap(int pos1, int pos2) {
        int temp = heap[pos1];
        heap[pos1] = heap[pos2];
        heap[pos2] = temp;
    }

    //move the value at pos down to the its place
    private void heapify(int pos) {
        if (pos > size / 2) {
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
        heapify(swapPos);
    }

    private int compareTo(int pos1, int pos2) {
        if (pos1 > size) {
            return 1;
        }
        if (pos2 > size) {
            return -1;
        }
        if (comparator != null) {
            return comparator.compareTo(heap[pos1], heap[pos2]);
        }
        return heap[pos1] - heap[pos2];
    }

    private void grow() {
        int oldLen = heap.length;
        int newLen = oldLen + (oldLen >> 1);
        heap = Arrays.copyOf(heap, newLen);
    }

    @Override
    public String toString() {
        return IntArrays.toString(heap, 1, size);
    }

    public static void main(String args[]) {
        var heap = new IntArrayHeap(100, (a, b) ->  b - a);
        //var heap = new IntArrayHeap(100);
        //for (int i = 9; i >= 0; i--) {
        for (int i = 10; i <= 19; i++) {
            heap.add(i);
        }
        while (!heap.isEmpty()) {
            System.out.println(heap.poll());
        }
    }
}
