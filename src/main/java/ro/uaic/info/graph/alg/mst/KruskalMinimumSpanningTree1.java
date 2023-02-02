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
package ro.uaic.info.graph.alg.mst;

import java.util.Arrays;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.EdgeSet;
import ro.uaic.info.graph.model.UnionFind;

/**
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class KruskalMinimumSpanningTree1 extends MinimumSpanningTreeBase {

    public KruskalMinimumSpanningTree1(Graph graph) {
        super(graph);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        int m = graph.numEdges();
        Integer[] allEdges = new Integer[m];
        int[][] endpoints = new int[m][2];
        double[] weights = new double[m];
        int i = 0;
        for (var v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                allEdges[i] = i++;
                endpoints[i][0] = v;
                endpoints[i][1] = u;
                weights[i] = it.getEdgeWeight();
            }
        }
        Arrays.sort(allEdges, (Integer e1, Integer e2) -> (int) Math.signum(weights[e1] - weights[e2]));
        var uf = new UnionFind(n);
        this.treeEdges = new EdgeSet(graph, n - 1);
        this.minWeight = 0.0;
        for (int e : allEdges) {
            int v = endpoints[e][0];
            int u = endpoints[e][1];
            int root1 = uf.find(graph.indexOf(v));
            int root2 = uf.find(graph.indexOf(u));
            if (root1 != root2) {
                uf.union(root1, root2);
                treeEdges.add(new Edge(v, u, weights[e]));
                minWeight += weights[e];
                if (treeEdges.size() == n - 1) {
                    break;
                }
            }
        }
    }
}
