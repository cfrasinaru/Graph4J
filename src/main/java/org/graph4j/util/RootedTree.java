/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.graph4j.Graph;
import org.graph4j.exceptions.NotATreeException;
import org.graph4j.traversal.BFSIterator;

/**
 * Utility class representing a tree with a node designated as root.
 *
 * @author Cristian Frăsinaru
 */
public class RootedTree {

    private final Graph tree;
    private final int root;
    private int[] parents;
    private List<VertexSet> levels;

    /**
     * Creates a rooted tree. It does not verify if the input graph is actually
     * a tree until specific rooted tree methods are invoked.
     *
     * @param tree the tree.
     * @param root the root node.
     * @throws NullPointerException if {@code tree} is {@code null}.
     * @throws IllegalArgumentException if {@code root} is not a vertex number
     * in {@code tree}.
     */
    public RootedTree(Graph tree, int root) {
        Objects.requireNonNull(tree);
        Validator.containsVertex(tree, root);
        this.tree = tree;
        this.root = root;
    }

    /**
     * Returns the tree.
     *
     * @return the tree.
     */
    public Graph tree() {
        return tree;
    }

    /**
     * Returns the designated root of the tree.
     *
     * @return the root.
     */
    public int root() {
        return root;
    }

    /**
     * Returns an array holding the parents of the vertices in the tree.
     *
     * @return the parent array.
     * @throws NotATreeException if the graph is not a tree.
     */
    public int[] parents() {
        if (parents == null) {
            traverse();
        }
        return parents;
    }

    /**
     * Returns the levels of the tree.
     *
     * @return the levels of the tree.
     * @throws NotATreeException if the graph is not a tree.
     */
    public List<VertexSet> levels() {
        if (levels == null) {
            traverse();
        }
        return levels;
    }

    /**
     * Returns the height of the tree.
     *
     * @return the height of the tree.
     * @throws NotATreeException if the graph is not a tree.
     */
    public int height() {
        if (levels == null) {
            traverse();
        }
        return levels.size();
    }

    /**
     * Returns the leaves (nodes with degree 1).
     *
     * @return the leaves of the tree.
     */
    public int[] leaves() {
        return Arrays.stream(tree.vertices()).filter(v -> tree.degree(v) == 1).toArray();
    }

    private void traverse() {
        int n = tree.numVertices();
        if (tree.numEdges() != n - 1) {
            throw new NotATreeException();
        }
        parents = new int[n];
        parents[tree.indexOf(root)] = -1;
        levels = new ArrayList<>();
        for (var it = new BFSIterator(tree, root); it.hasNext();) {
            var node = it.next();
            if (node.component() > 0) {
                throw new NotATreeException();
            }
            if (node.parent() != null) {
                parents[tree.indexOf(node.vertex())] = node.parent().vertex();
            }
            int level = node.level();
            if (level >= levels.size()) {
                levels.add(new VertexSet(tree, new int[]{node.vertex()}));
            } else {
                levels.get(level).add(node.vertex());
            }
        }
    }

    @Override
    public String toString() {
        return root + ": " + tree;
    }

    /**
     * Verifies if the graph is actually a tree.
     *
     * @return {@code true} if the graph is actually a tree, {@code false}
     * otherwise.
     */
    public boolean isValid() {
        try {
            traverse();
            return true;
        } catch (NotATreeException e) {
            return false;
        }
    }

}
