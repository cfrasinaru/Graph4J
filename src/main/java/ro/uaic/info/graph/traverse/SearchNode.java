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
package ro.uaic.info.graph.traverse;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SearchNode {

    private final int component;
    private final int vertex;
    private final int level;
    private final int order;
    private final SearchNode parent;

    public SearchNode(int vertex) {
        this(0, vertex, 0, 0, null);
    }

    public SearchNode(int component, int vertex, int level, int order, SearchNode parent) {
        this.component = component;
        this.vertex = vertex;
        this.level = level;
        this.order = order;
        this.parent = parent;
    }

    /**
     *
     * @return the connected component number of this node, starting with 0
     */
    public int component() {
        return component;
    }

    /**
     *
     * @return the vertex corresponding to this node
     */
    public int vertex() {
        return vertex;
    }

    /**
     *
     * @return the level of this nod in the DFS tree
     */
    public int level() {
        return level;
    }

    /**
     *
     * @return the visiting time, starting with 0
     */
    public int order() {
        return order;
    }

    /**
     *
     * @return
     */
    public SearchNode parent() {
        return parent;
    }

    /**
     *
     * @param other
     * @return true, if this node is an ancestor of other
     */
    public boolean isAncestorOf(SearchNode other) {
        while (other != null) {
            if (this.equals(other)) {
                return true;
            }
            other = other.parent;
        }
        return false;
    }

    /**
     * 
     * @param a
     * @param b
     * @return 
     */
    public static SearchNode nearestAncestor(SearchNode a, SearchNode b) {
        while (!b.isAncestorOf(a)) {
            b = b.parent();
        }
        return b;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.vertex;
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
        final SearchNode other = (SearchNode) obj;
        if (this.vertex != other.vertex) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(vertex);
        //return vertex + ", component=" + component + ", order=" + order
        //+ ", level=" + level + ", parent=" + (parent != null ? parent.vertex : -1);
    }

}
