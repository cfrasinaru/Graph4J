/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph.search;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SearchNode {

    private final int component;
    private final int vertex;
    private final int level;
    private final int order;

    public SearchNode(int component, int vertex, int level, int order) {
        this.component = component;
        this.vertex = vertex;
        this.level = level;
        this.order = order;
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

    @Override
    public String toString() {
        return vertex + ":" + component;
    }

}
