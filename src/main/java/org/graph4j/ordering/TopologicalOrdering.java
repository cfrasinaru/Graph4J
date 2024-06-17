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
package org.graph4j.ordering;

import org.graph4j.Digraph;
import org.graph4j.DirectedGraphAlgorithm;
import org.graph4j.traversal.TopologicalOrderIterator;

/**
 * Computes an ordering of a directed graph vertices such that for every
 * directed edge (u,v) from vertex u to vertex v, u comes before v in the
 * ordering.
 *
 * A graph is chordal if and only if it has a perfect elimination ordering. For
 * a chordal graph, LexBFS and Maximum Cardinality Search algorithms produce
 * reversed perfect elimination orderings.
 *
 * @author Cristian Frăsinaru
 */
public class TopologicalOrdering extends DirectedGraphAlgorithm {

    private int[] ordering;
    private int[] levels;
    private boolean unique;
    private boolean computed;

    /**
     *
     * @param digraph the input directed graph.
     */
    public TopologicalOrdering(Digraph digraph) {
        super(digraph);
    }

    /**
     *
     * @return the topological order, or {@code null} if the digraph is not
     * acyclic.
     */
    public int[] findOrdering() {
        if (computed) {
            return ordering;
        }
        computed = true;
        unique = true;
        int n = graph.numVertices();
        ordering = new int[n];
        levels = new int[n];
        int i = 0;
        int prevLevel = -1;
        for (var it = new TopologicalOrderIterator(graph); it.hasNext();) {
            try {
                int vertex = it.next();
                int level = it.level();
                ordering[i++] = vertex;
                levels[graph.indexOf(vertex)] = level;
                if (level == prevLevel) {
                    unique = false;
                }
                prevLevel = level;
            } catch (IllegalArgumentException e) {
                ordering = null;
                levels = null;
                unique = false;
                return null;
            }
        }
        return ordering;
    }

    /**
     * Checks if the topological sorting is unique. If it is unique, the digraph
     * contains a Hamiltonian path.
     *
     * @return {@code true} if the topological sorting exists and it is unique.
     */
    public boolean isUnique() {
        if (!computed) {
            findOrdering();
        }
        return unique;
    }

    /**
     *
     * @return the levels of the DAG.
     */
    public int[] getLevels() {
        if (!computed) {
            findOrdering();
        }
        return levels;
    }

}
