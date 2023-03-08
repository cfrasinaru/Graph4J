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
package org.graph4j.traverse;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface DFSVisitor {

    //whenever a vertex is reached for the first time
    //as root or after a tree edge
    default void startVertex(SearchNode node) {
    }

    //visiting the vertex on the way up, before upward
    default void finishVertex(SearchNode node) {
    }

    default void treeEdge(SearchNode from, SearchNode to) {
    }

    default void backEdge(SearchNode from, SearchNode to) {
    }

    default void forwardEdge(SearchNode from, SearchNode to) {
    }

    default void crossEdge(SearchNode from, SearchNode to) {
    }

    default void upward(SearchNode from, SearchNode to) {
    }

    default void interrupt() {
        throw new InterruptedVisitorException();
    }

    default boolean isRoot(SearchNode node) {
        return node.level() == 0;
    }
}
