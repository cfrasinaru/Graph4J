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
package org.graph4j.traversal;

/**
 * A depth first search (DFS) visitor of a graph. An implementation of this
 * interface is provided to a {@link DFSTraverser}.
 *
 * @author Cristian Frăsinaru
 */
public interface DFSVisitor {

    /**
     * Invoked whenever a vertex is reached for the first time as root or after
     * a tree edge.
     *
     * @param node the visited node.
     */
    default void startVertex(SearchNode node) {
    }

    /**
     * Invoked when leaving the subtree rooted in the current node, on the way
     * up in the DFS tree, before upward.
     *
     * @param node the current node.
     */
    default void finishVertex(SearchNode node) {
    }

    /**
     * A tree edge is part of the DFS tree obtained after the DFS traversal.
     *
     * @param from a node in the DFS tree.
     * @param to a node in the DFS tree.
     */
    default void treeEdge(SearchNode from, SearchNode to) {
    }

    /**
     * A back edge vu is such that u is the ancestor of v, but vu is not a tree
     * edge.
     *
     * @param from a node in the DFS tree.
     * @param to a node in the DFS tree.
     */
    default void backEdge(SearchNode from, SearchNode to) {
    }

    /**
     * A forward edge vu is such that u is a descendant of v, but vu is not a
     * tree edge. Forward edges can appear only in directed graph traversals.
     *
     * @param from a node in the DFS tree.
     * @param to a node in the DFS tree.
     */
    default void forwardEdge(SearchNode from, SearchNode to) {
    }

    /**
     * A cross edge connects two nodes such that they do not have any
     * relationship between them (ancestor or descendant). Cross edges can
     * appear only in directed graph traversals.
     *
     * @param from a node in the DFS tree.
     * @param to a node in the DFS tree.
     */
    default void crossEdge(SearchNode from, SearchNode to) {
    }

    /**
     * Invoked when the traversal algorithm moves up in the DFS tree.
     *
     * @param from a node in the DFS tree.
     * @param to a node in the DFS tree.
     */
    default void upward(SearchNode from, SearchNode to) {
    }

    /**
     * Interrupts the traversal.
     */
    default void interrupt() {
        throw new InterruptedVisitorException();
    }

    /**
     *
     * @param node a node in the DFS tree.
     * @return {@code true} if the given node is on first level of the tree.
     */
    default boolean isRoot(SearchNode node) {
        return node.level() == 0;
    }
}
