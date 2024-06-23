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
package org.graph4j.coloring.eq;

import org.graph4j.alg.coloring.*;
import org.graph4j.Graph;
import org.graph4j.alg.coloring.Node;

/**
 * Attempts at finding an optimum equitable coloring of the vertices of a graph
 * using a systematic exploration of the search space. The backtracking is
 * implemented in a non-recursive manner, using multiple threads.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackEquitableColoring extends BacktrackColoringBase
        implements EquitableColoringAlgorithm {

    private int maxClassSize;  //maximum coloring class size
    private int maxClassCount; //how many coloring classes are at maximum

    public BacktrackEquitableColoring(Graph graph) {
        super(graph);
    }

    public BacktrackEquitableColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    public BacktrackEquitableColoring(Graph graph, Coloring initialColoring) {
        super(graph, initialColoring);
    }

    public BacktrackEquitableColoring(Graph graph, Coloring initialColoring, long timeLimit) {
        super(graph, initialColoring, timeLimit);
    }

    @Override
    protected BacktrackEquitableColoring getInstance(Graph graph, long timeLimit) {
        return new BacktrackEquitableColoring(graph, initialColoring, timeLimit);
    }
   
    @Override
    protected void solve(int numColors) {
        int n = graph.numVertices();
        int p = numColors;
        maxClassSize = n % p == 0 ? n / p : 1 + n / p;
        maxClassCount = n % p == 0 ? p : n % p;
        super.solve(numColors);
    }

    //after the assignment v=color, prune other domains
    @Override
    protected boolean propagateAssignment(int v, int color, Node node, int[][] assignQueue) {
        var coloring = node.coloring();
        var domains = node.domains();

        assert coloring.isColorSet(v);
        assert domains[graph.indexOf(v)].size() == 1;

        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = color;
        while (i < j) {
            //if the number of large color classes reached its maximum            
            /*
            if (node.maxClassCount == maxClassCount) {
                int ret = globalPropagation(node, assignQueue, j);
                if (ret < 0) {
                    return false;
                }
                j += ret;
            }
             */
            //normal propagation
            v = assignQueue[i][0];
            color = assignQueue[i][1];
            i++;
            int[] target;
            boolean removeFromOtherClasses = false;
            //node.maxClassSize
            if (coloring.numColoredVertices(color) == maxClassSize) {
                //if the color class of v is full, 
                //remove color from all other coloring classes
                target = graph.vertices();
                removeFromOtherClasses = true;
            } else {
                //remove color only from the neigbors of v
                target = graph.neighbors(v);
            }
            for (int u : target) {
                if (removeFromOtherClasses && coloring.getColor(u) == color) {
                    continue;
                }
                int ret = removeColor(u, color, node, assignQueue, j);
                if (ret < 0) {
                    return false;
                }
                j += ret;
            }
        }
        return true;
    }

    @Override
    protected boolean propagationForcedColor(int u, int color, Node node) {
        //check if the coloring class got too big
        int classSize = node.coloring().numColoredVertices(color);
        return classSize <= maxClassSize;
        //TODO node.maxClassSize
        /*
        if (node.maxClassCount >= 0 && classSize == node.maxClassSize) {
            node.maxClassCount++;
            if (node.maxClassCount > maxClassCount) {
                //too many coloring classes of larger size
                return -1;
            }
        }*/
    }

    /*
    private int globalPropagation(Node node, int[][] assignQueue, int queuePos) {
        node.maxClassSize--;
        node.maxClassCount = -1;

        //global propagation: only once                
        //add only in the classes below the smaller class size
        int count = 0;
        for (int u : graph.vertices()) {
            if (node.coloring.isColorSet(u)) {
                continue;
            }
            //each uncolored node can only go in smallish sets
            var colorMap = node.coloring.getColorClasses();
            var colors = new HashSet<>(colorMap.keySet());
            for (var col : colors) {
                var set = colorMap.get(col);
                if (set.size() == node.maxClassSize) {
                    int ret = removeColor(u, col, node, assignQueue, queuePos);
                    if (ret < 0) {
                        return -1;
                    }
                    count++;
                }
            }
        }
        return count;
    }
     */
}
