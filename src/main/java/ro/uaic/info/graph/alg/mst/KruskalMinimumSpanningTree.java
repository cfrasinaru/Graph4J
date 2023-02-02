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
public class KruskalMinimumSpanningTree extends MinimumSpanningTreeBase {

    public KruskalMinimumSpanningTree(Graph graph) {
        super(graph);
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        Edge[] allEdges = graph.edges();
        Arrays.sort(allEdges, (e1, e2) -> (int) Math.signum(e1.weight() - e2.weight()));
        var uf = new UnionFind(n);
        this.treeEdges = new EdgeSet(graph, n - 1);
        this.minWeight = 0.0;
        for (Edge e : allEdges) {
            int v = e.source();
            int u = e.target();
            int root1 = uf.find(graph.indexOf(v));
            int root2 = uf.find(graph.indexOf(u));
            if (root1 != root2) {
                uf.union(root1, root2);
                treeEdges.add(e);
                minWeight += e.weight();
                if (treeEdges.size() == n - 1) {
                    break;
                }
            }
        }
    }
}
