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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.graph4j.Graph;

/**
 * A partition of the vertices of the graphs in subsets of a specific type.
 *
 * @author Cristian Frăsinaru
 * @param <T> the data type of the subsets.
 */
@Deprecated
public class VertexPartition<T extends VertexCollection> {

    private final Graph graph;
    private final List<T> subsets;

    /**
     * Creates a partition of the vertices of the specified graph.
     *
     * @param graph the graph whose vertices are partitioned.
     */
    public VertexPartition(Graph graph) {
        Objects.requireNonNull(graph);
        this.graph = graph;
        this.subsets = new ArrayList<>();
    }

    /**
     * Returns the subsets of the partition.
     *
     * @return the subsets of the partition.
     */
    public List<T> getSubsets() {
        return subsets;
    }

    /**
     * A partition is valid if the subsets are disjoint and covers all vertices
     * in the graph.
     *
     * @return {@code true} if the partition is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        for (int v : graph.vertices()) {
            int count = 0;
            for (var set : subsets) {
                if (set.contains(v)) {
                    count++;
                }
            }
            if (count != 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return subsets.toString();
    }

}
