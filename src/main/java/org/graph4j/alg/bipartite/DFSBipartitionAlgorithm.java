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
package org.graph4j.alg.bipartite;

import org.graph4j.Graph;
import org.graph4j.util.Cycle;
import org.graph4j.traverse.DFSVisitor;
import org.graph4j.traverse.DFSTraverser;
import org.graph4j.traverse.SearchNode;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DFSBipartitionAlgorithm extends BipartitionAlgorithmBase {

    public DFSBipartitionAlgorithm(Graph graph) {
        super(graph);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        this.color = new boolean[n];
        var visitor = new Visitor();
        color[0] = true;
        new DFSTraverser(graph).traverse(graph.vertexAt(0), visitor);
        bipartite = oddCycle == null;
    }

    private class Visitor implements DFSVisitor {

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            color[graph.indexOf(to.vertex())] = !color[graph.indexOf(from.vertex())];
        }

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            //a back edge produces a circuit
            //if nodes from and to have different colors, it is an even circuit
            if (color[graph.indexOf(from.vertex())] != color[graph.indexOf(to.vertex())]) {
                return;
            }
            //if from and two have the same color, it is an odd circuit
            oddCycle = new Cycle(graph);
            SearchNode firstNode = to;
            SearchNode lastNode = from;
            while (!firstNode.equals(lastNode)) {
                oddCycle.add(lastNode.vertex());
                lastNode = lastNode.parent();
            }
            oddCycle.add(firstNode.vertex());
            oddCycle.reverse();
            interrupt();
        }
    }

}
