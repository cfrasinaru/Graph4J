/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.mst;

import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.EdgeSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class MinimumSpanningTreeBase extends GraphAlgorithm
        implements MinimumSpanningTreeAlgorithm {

    protected Graph tree;
    protected EdgeSet treeEdges;
    protected Double minWeight;

    public MinimumSpanningTreeBase(Graph graph) {
        super(graph);
    }

    @Override
    public double getWeight() {
        if (minWeight == null) {
            compute();
        }
        return minWeight;
    }

    @Override
    public EdgeSet getEdges() {
        if (treeEdges == null) {
            compute();
        }
        return treeEdges;
    }

    @Override
    public Graph getTree() {
        if (tree == null) {
            tree = graph.subgraph(getEdges());
        }
        return tree;
    }

    //does the main work
    protected abstract void compute();
}
