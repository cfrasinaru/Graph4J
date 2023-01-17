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
@Deprecated
class Vertex<V> implements Comparable<Vertex> {

    private int vertex;
    private Double weight;
    private V label;

    public Vertex(int vertex) {
        this(vertex, null, null);
    }

    public Vertex(int vertex, Double weight) {
        this(vertex, weight, null);
    }

    public Vertex(int vertex, V label) {
        this(vertex, null, label);
    }

    public Vertex(int vertex, Double weight, V label) {
        this.vertex = vertex;
        this.weight = weight;
        this.label = label;
    }

    public int vertex() {
        return vertex;
    }

    public Double weight() {
        return weight;
    }

    public V label() {
        return label;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(vertex);
        if (weight != null || label != null) {
            if (label != null) {
                sb.append(":").append(label);
            }
            if (weight != null) {
                sb.append("=").append(weight);
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.vertex;
        return hash;
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
        final Vertex<?> other = (Vertex<?>) obj;
        if (this.vertex != other.vertex) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Vertex o) {
        return this.vertex - o.vertex;
    }

}
