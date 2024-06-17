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

import static org.graph4j.Graph.WEIGHT;

/**
 * An edge is a pair of vertices. It may or may not be part of the graph. Edges
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
    protected Double[] data;
    protected E label;

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     */
    public Edge(int source, int target) {
        this.source = source;
        this.target = target;
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param directed {@code true} if the edge has a direction (in case of
     * digraphs).
     */
    public Edge(int source, int target, boolean directed) {
        this.source = source;
        this.target = target;
        this.directed = directed;
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param weight the weight of the edge.
     */
    public Edge(int source, int target, double weight) {
        this(source, target, null, new Double[]{weight});
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, E label) {
        this(source, target, label, (Double[]) null);
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target the target endpoint of the edge.
     * @param weight the weight of the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, E label, double weight) {
        this(source, target, label, new Double[]{weight});
    }

    /**
     *
     * @param source the source endpoint of the edge.
     * @param target target the target endpoint of the edge.
     * @param data the data associated with the edge.
     * @param label the label of the edge.
     */
    public Edge(int source, int target, E label, Double... data) {
        this.source = source;
        this.target = target;
        this.data = data;
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
        if (data == null || data[WEIGHT] == null) {
            return Graph.DEFAULT_EDGE_WEIGHT;
        }
        return data[WEIGHT];
    }

    public Double data(int dataType) {
        return data[dataType];
    }

    public double dataOrDefault(int dataType, int defaultValue) {
        if (data == null || data[dataType] == null) {
            return defaultValue;
        }
        return data[dataType];
    }

    /**
     *
     * @return the label associated with the edge, or {@code null} in the case
     * of unlabeled graphs.
     */
    public E label() {
        return label;
    }

    //internal use only
    void setLabel(E label) {
        this.label = label;
    }

    /**
     * Flips source and target, for directed edges.
     *
     * @return a new edge with the direction reversed.
     */
    public Edge<E> flip() {
        Edge e = new Edge(target, source, label, data);
        e.directed = directed;
        return e;
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
        if (data != null) {
            sb.append("=");
            for (Double value : data) {
                if (value != null) {
                    if (sb.charAt(sb.length() - 1) != '=') {
                        sb.append(";");
                    }
                    sb.append(value);
                }
            }
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


/*
    protected Edge(Builder<E> builder) {
        this.directed = builder.directed;
        this.source = builder.source;
        this.target = builder.target;
        this.weight = builder.weight;
        this.label = builder.label;
    }

    public static class Builder<E> {

        private boolean directed = false;
        private int source;
        private int target;
        private Double weight;
        private E label;

        public Builder<E> directed(boolean directed) {
            this.directed = directed;
            return this;
        }

        public Builder<E> source(int source) {
            this.source = source;
            return this;
        }

        public Builder<E> target(int target) {
            this.target = target;
            return this;
        }

        public Builder<E> weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public Builder<E> label(E label) {
            this.label = label;
            return this;
        }

        public Edge<E> build() {
            return new Edge<>(this);
        }
    }

 */
