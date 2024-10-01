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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over all subsets of the set {0,1,...,n-1}.
 *
 * @author Cristian Frăsinaru
 */
public class SubsetIterator implements Iterator<int[]> {

    private final int n;
    private int current;

    public SubsetIterator(int n) {
        this.n = n;
        this.current = 0;
    }

    @Override
    public boolean hasNext() {
        return current < (1 << n);
    }

    @Override
    public int[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int[] subset = new int[Integer.bitCount(current)];
        int index = 0;
        for (int i = 0; i < n; i++) {
            if ((current & (1 << i)) != 0) {
                subset[index++] = i;
            }
        }
        current++;
        return subset;
    }
}
