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
package org.graph4j.vsp;

import org.graph4j.Graph;
import org.graph4j.util.VertexSet;

/**
 * Utility class for representing a vertex separator set.
 *
 * @see GreedyVertexSeparator
 * @author Cristian Frăsinaru
 */
public class VertexSeparator {

    private final Graph graph;
    private final VertexSet separator;
    private final VertexSet leftShore;
    private final VertexSet rightShore;
    private final int maxShoreSize;

    /**
     * The default maximum shore size is {@code 2n/3}, where {@code n} is the
     * number of vertices in the graph.
     *
     * @param graph the graph on which the separator is defined.
     */
    public VertexSeparator(Graph graph) {
        this(graph, 2 * graph.numVertices() / 3);
    }

    /**
     *
     * @param graph the graph on which the separator is defined.
     * @param maxShoreSize the maximum shore size.
     */
    public VertexSeparator(Graph graph, int maxShoreSize) {
        this.graph = graph;
        this.maxShoreSize = maxShoreSize;
        leftShore = new VertexSet(graph, maxShoreSize);
        separator = new VertexSet(graph);
        rightShore = new VertexSet(graph, maxShoreSize);
    }

    /**
     * Copy constructor.
     *
     * @param other a vertex separator.
     */
    public VertexSeparator(VertexSeparator other) {
        this.graph = other.graph;
        this.maxShoreSize = other.maxShoreSize;
        leftShore = new VertexSet(other.leftShore);
        separator = new VertexSet(other.separator);
        rightShore = new VertexSet(other.rightShore);
    }

    /**
     *
     * @param separator the separator vertex set.
     * @param leftShore the left shore.
     * @param rightShore the right shore.
     */
    @Deprecated
    public VertexSeparator(VertexSet separator, VertexSet leftShore, VertexSet rightShore) {
        this.separator = separator;
        this.leftShore = leftShore;
        this.rightShore = rightShore;
        this.graph = separator.getGraph();
        this.maxShoreSize = 2 * graph.numVertices() / 3; //not good
    }

    /**
     *
     * @return the separator.
     */
    public VertexSet separator() {
        return separator;
    }

    /**
     *
     * @return the left shore.
     */
    public VertexSet leftShore() {
        return leftShore;
    }

    /**
     *
     * @return the right shore.
     */
    public VertexSet rightShore() {
        return rightShore;
    }

    /**
     *
     * @param v a vertex number.
     * @return {@code true} if v is covered by any of the three sets.
     */
    public boolean contains(int v) {
        return leftShore.contains(v) || rightShore.contains(v) || separator.contains(v);
    }

    /**
     *
     * @return {@code true} if all the vertices of the graph are in one of the
     * three sets: leftShore, rightShore or separator.
     */
    public boolean isComplete() {
        return leftShore.size() + rightShore.size() + separator.size() == graph.numVertices();
    }

    /**
     * It does not test if it is complete, i.e. it contains all vertices of the
     * graph.
     *
     * @return {@code true} if the properties of a vertex separator are
     * satisfied.
     */
    public boolean isValid() {
        if (leftShore.size() == 0) {
            return false;
        }
        if (rightShore.size() == 0) {
            return false;
        }
        if (leftShore.size() > maxShoreSize) {
            //System.out.println("Left shore size too big: " + leftShore.size() + " > " + maxShoreSize);
            return false;
        }
        if (rightShore.size() > maxShoreSize) {
            //System.out.println("Right shore size too big: " + rightShore.size() + " > " + maxShoreSize);
            return false;
        }
        for (int v : graph.vertices()) {
            boolean a = leftShore.contains(v);
            boolean b = rightShore.contains(v);
            boolean s = separator.contains(v);
            if ((a && b) || (a && s) || (b && s)) {
                //!(a || b || s)
                System.out.println("Duplicate: " + v);
                return false;
            }
        }
        for (int v : leftShore.vertices()) {
            for (int u : rightShore.vertices()) {
                if (graph.containsEdge(v, u)) {
                    System.out.println("Illegal edge " + v + "-" + u);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Left shore: \t" + leftShore
                + "\nSeparator: \t" + separator + ", size=" + separator.size()
                + "\nRight shore: \t" + rightShore;
    }

}
