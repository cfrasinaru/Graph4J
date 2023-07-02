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
package org.graph4j.alg.coloring;

import org.graph4j.Graph;

/**
 * Attempts at finding the optimum coloring of a graph using a systematic
 * exploration of the search space. The backtracking is implemented in a
 * non-recursive manner, using multiple threads.
 *
 * <p>
 * First, a maximal clique is computed that offers a lower bound <code>q</code>
 * of the chromatic number. The colors of the vertices in the maximal clique are
 * fixed before the backtracking algorithm starts.
 *
 * Secondly, an initial coloring is computed using a simple heuristic.
 * This gives an upper bound <code>k</code>of the chromatic number.
 *
 * Next, the algorithm will attemtp to color the graph using a number of colors
 * ranging from <code>k-1</code> to <code>q</code>, determining the optimal
 * coloring.
 *
 * <p>
 * A time limit may be imposed. If the algorithm stops due to the time limit, it
 * will return the best coloring found until then.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackColoring extends BacktrackColoringBase {

    public BacktrackColoring(Graph graph) {
        super(graph);
    }

    public BacktrackColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    public BacktrackColoring(Graph graph, Coloring initialColoring) {
        super(graph, initialColoring);
    }

    public BacktrackColoring(Graph graph, Coloring initialColoring, long timeLimit) {
        super(graph, initialColoring, timeLimit);
    }

    @Override
    protected BacktrackColoring getInstance(Graph graph, long timeLimit) {
        return new BacktrackColoring(graph, initialColoring, timeLimit);
    }

    //after the assignment v=color, prunte the other domains
    @Override
    protected boolean propagateAssignment(int v, int color, Node node, int[][] assignQueue) {
        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = color;
        while (i < j) {
            v = assignQueue[i][0];
            color = assignQueue[i][1];
            i++;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ret = removeColor(u, color, node, assignQueue, j);
                if (ret < 0) {
                    return false;
                }
                j += ret;
            }
        }
        return true;
    }

}
