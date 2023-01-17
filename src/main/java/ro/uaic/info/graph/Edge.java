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
package ro.uaic.info.graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Edge<E> implements Comparable<Edge> {

    private boolean directed;
    private int source;
    private int target;
    private Double weight;
    private E label;

    public Edge(int first, int second) {
        this(first, second, false, null, null);
    }

    public Edge(int first, int second, Double weight, E label) {
        this(first, second, false, weight, label);
    }

    public Edge(int first, int second, Double weight) {
        this(first, second, false, weight, null);
    }

    public Edge(int first, int second, E label) {
        this(first, second, false, null, label);
    }

    public Edge(int first, int second, boolean directed) {
        this(first, second, directed, null, null);
    }

    public Edge(int first, int second, boolean directed, Double weight, E label) {
        this.source = first;
        this.target = second;
        this.directed = directed;
        this.weight = weight;
        this.label = label;
    }

    public int source() {
        return source;
    }

    public int target() {
        return target;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean isSelfLoop() {
        return source == target;
    }

    public Double weight() {
        return weight;
    }

    public E label() {
        return label;
    }

    /**
     * Flips source and target, for directed edges.
     *
     * @return
     */
    public Edge<E> flip() {
        return new Edge(target, source, directed, weight, label);
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean isAdjacentTo(Edge other) {
        return this.source == other.source || this.source == other.target
                || this.target == other.source || this.target == other.target;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(source).append(directed ? "->" : "-").append(target);
        if (weight != null || label != null) {
            sb.append("(");
            if (label != null) {
                sb.append(label);
                if (weight != null) {
                    sb.append(":").append(weight);
                }
            } else {
                sb.append(weight);
            }
            sb.append(")");
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
