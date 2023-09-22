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
package org.graph4j;

/**
 * An edge is a pair of vertices.It may or may not be part of the graph. Edges
 * are not stored as objects in the graph structure, instead they are created on
 * demand.
 *
 * @author Cristian Frăsinaru
 * @param <E> the type of edge labels in this graph.
 */
public class Edge<E> implements Comparable<Edge> {

    protected boolean directed;
    protected int source;
    protected int target;
    protected Double weight;
    protected E label;

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     */
    public Edge(int source, int target) {
        this(source, target, false, null, null);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param weight the weight of the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, Double weight, E label) {
        this(source, target, false, weight, label);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param weight the weight of the edge.
     */
    public Edge(int source, int target, Double weight) {
        this(source, target, false, weight, null);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, E label) {
        this(source, target, false, null, label);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target target the target endpoint of the edge.
     * @param directed {@code true} if the edge has a direction (in case of
     * digraphs).
     */
    public Edge(int source, int target, boolean directed) {
        this(source, target, directed, null, null);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target target the target endpoint of the edge.
     * @param directed {@code true} if the edge has a direction (in case of
     * digraphs).
     * @param weight the weight of the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, boolean directed, Double weight, E label) {
        this.source = source;
        this.target = target;
        this.directed = directed;
        this.weight = weight;
        this.label = label;
    }

    /**
     *
     * @return the source endpoint of the edge.
     */
    public int source() {
        return source;
    }

    /**
     *
     * @return the target (sink) endpoint of the edge.
     */
    public int target() {
        return target;
    }

    /**
     *
     * @return {@code true} if the edge is directed (is case of digraphs).
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     *
     * @return {@code true} if the source and the target are the same.
     */
    public boolean isSelfLoop() {
        return source == target;
    }

    /**
     *
     * @return the weight associated with the edge, or {@code 1} in the case of
     * unweighted graphs.
     */
    public double weight() {
        //tricky, don't invoke this method if you expect null
        if (weight == null) {
            return Graph.DEFAULT_EDGE_WEIGHT;
        }
        return weight;
    }

    //internal use only
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     *
     * @return the label associated with the edge, or {@code null} in the case
     * of unlabeled graphs.
     */
    public E label() {
        return label;
    }

    void setLabel(E label) {
        this.label = label;
    }

    /**
     * Flips source and target, for directed edges.
     *
     * @return a new edge with the direction reversed.
     */
    public Edge<E> flip() {
        return new Edge(target, source, directed, weight, label);
    }

    /**
     *
     * @param other another edge.
     * @return {@code true} if this edge has a common endpoint with the other.
     */
    public boolean isAdjacentTo(Edge other) {
        return this.source == other.source || this.source == other.target
                || this.target == other.source || this.target == other.target;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(source).append(directed ? "->" : "-").append(target);
        if (label != null) {
            sb.append(":").append(label);
        }
        if (weight != null) {
            sb.append("=").append(weight);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        if (directed) {
            return 31 * (31 * this.source + this.target);
        } else {
            return (31 * this.source + 1) * (31 * this.target + 1);
        }
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
        final Edge other = (Edge) obj;
        if (directed) {
            return (this.source == other.source && this.target == other.target);
        }
        return (this.source == other.source && this.target == other.target && !isSelfLoop())
                || (this.source == other.target && this.target == other.source && !isSelfLoop());
    }

    @Override
    public int compareTo(Edge o) {
        int s = this.source - o.source;
        if (s != 0) {
            return s < 0 ? -1 : 1;
        }
        return this.target - o.target;
    }

}
