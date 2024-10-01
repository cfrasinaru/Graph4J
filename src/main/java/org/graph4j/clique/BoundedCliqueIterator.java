/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.clique;

import java.util.NoSuchElementException;
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.util.BoundedSubsetIterator;
import org.graph4j.util.Clique;

/**
 * Uses BronKerboschCliqueIterator in order to iterate through the cliques of
 * specified sizes. Slower than {@link DFSCliqueIterator}.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class BoundedCliqueIterator extends SimpleGraphAlgorithm
        implements CliqueIterator {

    private final int minSize, maxSize;
    private final long timeout;
    private Clique currentClique;
    private final BronKerboschCliqueIterator bkIterator;
    private BoundedSubsetIterator subsetIterator;
    private boolean timeExpired;

    /**
     *
     * @param graph the input graph.
     * @param minSize the minimum size of a clique.
     * @param maxSize the maximum size of a clique.
     */
    public BoundedCliqueIterator(Graph graph, int minSize, int maxSize) {
        this(graph, minSize, maxSize, 0);
    }

    /**
     *
     * @param graph the input graph.
     * @param minSize the minimum size of a clique.
     * @param maxSize the maximum size of a clique.
     * @param timeout timeout in milliseconds.
     */
    public BoundedCliqueIterator(Graph graph, int minSize, int maxSize, long timeout) {
        super(graph);
        if (minSize <= 0 || minSize > maxSize || maxSize > graph.numVertices()) {
            throw new IllegalArgumentException();
        }
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.timeout = timeout;
        bkIterator = new BronKerboschCliqueIterator(graph);
    }

    @Override
    public Clique next() {
        if (currentClique != null) {
            var temp = currentClique;
            currentClique = null;
            return temp;
        }
        if (hasNext()) {
            return currentClique;
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasNext() {
        if (currentClique != null) {
            return true;
        }
        if (timeExpired) {
            return false;
        }
        long t0 = System.currentTimeMillis();
        while (subsetIterator == null) {
            if (timeout > 0 && System.currentTimeMillis() - t0 > timeout) {
                timeExpired = true;
                return false;
            }
            if (!bkIterator.hasNext()) {
                return false;
            }
            var maximalClique = bkIterator.next();
            if (maximalClique.size() < minSize) {
                continue;
            }
            subsetIterator = new BoundedSubsetIterator(maximalClique.vertices(), minSize);
            break;
        }
        int[] vertices = subsetIterator.next();
        currentClique = new Clique(graph, vertices);
        if (!subsetIterator.hasNext()) {
            subsetIterator = null;
        }
        return true;
    }

}
