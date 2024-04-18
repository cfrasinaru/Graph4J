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
package org.graph4j.alg.connectivity;

import java.util.List;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.Block;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface BiconnectivityAlgorithm {

    /**
     * A graph is <em>biconnected</em> if it has no cut vertex.
     *
     * @return {@code true} if the graph is 2-connected.
     */
    boolean isBiconnected();

    /**
     * A <em>block</em> of a graph is a maximal 2-connected subgraph (it has no
     * cut vertex).
     *
     * @return the blocks of the graph.
     */
    List<Block> getBlocks();

    /**
     * A <i>cut vertex</i> (cut point, articulation point, separating point) is
     * any vertex whose removal increases the number of connected components.
     *
     * @return the set of cut vertices.
     */
    VertexSet getCutVertices();

    /**
     * A <em>block-cut tree</em> (also known as BC-tree) is a tree that
     * represents the blocks and articulation points (cut vertices) of a graph.
     *
     * The tree has a vertex for each block and for each articulation point of
     * the given graph. There is an edge in the block-cut tree for each pair of
     * a block and an articulation point that belongs to that block.
     *
     * The vertices of the returned tree are labeled either with the
     * corresponding blocks or the vertex numbers of the articulation points.
     *
     *
     * @return the block-cut tree.
     */
    default Graph computeBlockCutTree() {
        List<Block> blocks = getBlocks();
        int[] cutVertices = getCutVertices().vertices();
        int k = blocks.size();
        int p = cutVertices.length;
        //vertices from 0 to k - 1 are blocks
        //vertices from k to k + p -1 are cutvertices
        var tree = GraphBuilder.empty().estimatedNumVertices(k + p).buildGraph();
        for (int i = 0; i < k; i++) {
            tree.addLabeledVertex(i, blocks.get(i));
        }
        for (int j = k; j < k + p; j++) {
            tree.addLabeledVertex(j, cutVertices[j - k]);
        }
        for (int i = 0; i < k; i++) {
            var block = blocks.get(i);
            for (int j = k; j < k + p; j++) {
                int cutVertex = cutVertices[j - k];
                if (block.contains(cutVertex)) {
                    tree.addEdge(i, j);
                }
            }
        }
        return tree;
    }

    /**
     * The <em>block graph</em> has one vertex for each block, and an edge
     * between two vertices whenever their corresponding blocks share a vertex
     * (an articulation point).
     *
     * The vertices of the returned graph are labeled with the corresponding
     * blocks and their indices (and vertex numbers) are the same with the
     * indices of the blocks int the list returned by the {@link #getBlocks()}
     * method.
     *
     * @return the block graph.
     */
    default Graph computeBlockGraph() {
        var blocks = getBlocks();
        var blockGraph = GraphBuilder.labeledVertices(blocks).buildGraph();
        int k = blocks.size();
        for (int v = 0; v < k - 1; v++) {
            var bv = blocks.get(v);
            for (int u = v + 1; u < k; u++) {
                var bu = blocks.get(u);
                if (IntArrays.intersects(bv.vertices(), bu.vertices())) {
                    blockGraph.addEdge(v, u);
                }
            }
        }
        return blockGraph;
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static BiconnectivityAlgorithm getInstance(Graph graph) {
        return new TarjanBiconnectivity(graph);
    }

}
