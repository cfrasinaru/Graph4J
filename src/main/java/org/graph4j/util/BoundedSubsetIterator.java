/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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

/**
 *
 * @author Cristian Frăsinaru
 */
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BoundedSubsetIterator implements Iterator<int[]> {

    private final int[] elements;
    private final int numElements;
    private final int maxSubsetSize;
    private int currentSubsetSize;
    private int[] indices;

    public BoundedSubsetIterator(int[] elements, int minSize) {
        this.elements = elements;
        this.numElements = elements.length;
        this.currentSubsetSize = minSize;
        this.maxSubsetSize = elements.length;
        if (minSize <= maxSubsetSize) {
            this.indices = new int[minSize];
            for (int i = 0; i < minSize; i++) {
                indices[i] = i;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return currentSubsetSize <= maxSubsetSize;
    }

    @Override
    public int[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        int[] subset = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            subset[i] = elements[indices[i]];
        }

        advance();

        return subset;
    }

    private void advance() {
        for (int i = currentSubsetSize - 1; i >= 0; i--) {
            if (indices[i] < numElements - currentSubsetSize + i) {
                indices[i]++;
                for (int j = i + 1; j < currentSubsetSize; j++) {
                    indices[j] = indices[j - 1] + 1;
                }
                return;
            }
        }

        currentSubsetSize++;
        if (currentSubsetSize <= maxSubsetSize) {
            indices = new int[currentSubsetSize];
            for (int i = 0; i < currentSubsetSize; i++) {
                indices[i] = i;
            }
        }
    }

    public static void main(String[] args) {
        int[] set = {1, 2, 3, 4, 5};
        int minSize = 5;
        var iterator = new BoundedSubsetIterator(set, minSize);

        while (iterator.hasNext()) {
            System.out.println(Arrays.toString(iterator.next()));
        }
    }
}
