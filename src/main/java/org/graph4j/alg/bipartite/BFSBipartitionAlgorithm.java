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
import org.graph4j.traverse.BFSVisitor;
import org.graph4j.traverse.BFSTraverser;
import org.graph4j.traverse.SearchNode;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BFSBipartitionAlgorithm extends BipartitionAlgorithmBase {

    public BFSBipartitionAlgorithm(Graph graph) {
        super(graph);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        this.color = new boolean[n];
        var visitor = new Visitor();
        color[0] = true;
        new BFSTraverser(graph).traverse(graph.vertexAt(0), visitor);
        bipartite = oddCycle == null;
    }

    private class Visitor implements BFSVisitor {

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            color[graph.indexOf(to.vertex())] = !color[graph.indexOf(from.vertex())];
        }

        @Override
        public void crossEdge(SearchNode from, SearchNode to) {
            //a cross edge produces a circuit
            //if from and two are on same level, it is an odd circuit
            //otherwise, they are one above the other
            if (from.level() != to.level()) {
                return;
            }
            oddCycle = new Cycle(graph);
            oddCycle.add(from.vertex());
            var parent = from.parent();
            oddCycle.add(parent.vertex());
            while (!parent.isAncestorOf(to)) {
                parent = parent.parent();
                oddCycle.add(parent.vertex());
            }
            oddCycle.reverse();
            while (!to.equals(parent)) {
                oddCycle.add(to.vertex());
                to = to.parent();
            }
            interrupt();
        }
    }

}
