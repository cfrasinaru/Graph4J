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
package org.graph4j.coloring.bw;

import org.graph4j.util.Domain;
import org.graph4j.Graph;
import org.graph4j.coloring.BacktrackColoringBase;
import org.graph4j.coloring.Coloring;
import org.graph4j.coloring.Node;

//https://mat.gsia.cmu.edu/COLOR02/
/**
 * Attempts at finding an optimum bandwith coloring of a graph using a
 * systematic exploration of the search space. The backtracking is implemented
 * in a non-recursive manner, using multiple threads.
 *
 * Weights of the graph edges must be strictly positive.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackBandwithColoring extends BacktrackColoringBase
        implements BandwithColoringAlgorithm {

    public BacktrackBandwithColoring(Graph graph) {
        super(graph);
    }

    public BacktrackBandwithColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    public BacktrackBandwithColoring(Graph graph, Coloring initialColoring) {
        super(graph, initialColoring);
    }

    public BacktrackBandwithColoring(Graph graph, Coloring initialColoring, long timeLimit) {
        super(graph, initialColoring, timeLimit);
    }

    @Override
    protected BacktrackBandwithColoring getInstance(Graph graph, long timeLimit) {
        return new BacktrackBandwithColoring(graph, initialColoring, timeLimit);
    }
   
    //no initial coloring for the root node
    @Override
    protected boolean prepareRootColoring(Coloring rootColoring, int numColors) {
        return true;
    }

    @Override
    protected Node createNode(Node parent, int vertex, int color, Domain[] domains, Coloring coloring) {
        return new Node(this, parent, vertex, color, domains, coloring, false);
    }

    //after the assignment v=color, prunte the other domains
    //|c(v)-c(u)| > =weight(v,u)
    @Override
    protected boolean propagateAssignment(int v, int vColor, Node node, int[][] assignQueue) {
        var domains = node.domains();
        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = vColor;
        while (i < j) {
            v = assignQueue[i][0];
            vColor = assignQueue[i][1];
            i++;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                double weight = it.getEdgeWeight();
                int pos = 0;
                while (pos < domains[ui].size()) {
                    int uColor = domains[ui].valueAt(pos);
                    if (Math.abs(vColor - uColor) >= weight) {
                        pos++;
                        continue;
                    }
                    int ret = removeColor(u, uColor, node, assignQueue, j);
                    if (ret < 0) {
                        return false;
                    }
                    j += ret;
                }
            }
        }
        return true;
    }

}
