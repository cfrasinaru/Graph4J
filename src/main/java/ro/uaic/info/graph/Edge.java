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
    private final int source;
    private final int target;
    private Double weight;
    private E label;

    public Edge(int first, int second) {
        this(first, second, false, null);
    }

    public Edge(int first, int second, boolean directed) {
        this(first, second, directed, null);
    }

    public Edge(int first, int second, boolean directed, Double weight) {
        this.source = first;
        this.target = second;
        this.directed = directed;
        this.weight = weight;
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

    public double weight() {
        return weight;
    }

    public E label() {
        return label;
    }

    @Override
    public String toString() {
        return source + (directed ? "->" : "-") + target
                + (weight == null ? "" : "(" + weight + ")");
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
