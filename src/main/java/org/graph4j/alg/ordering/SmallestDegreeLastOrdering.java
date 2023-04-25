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
package org.graph4j.alg.ordering;

import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.alg.GraphMeasures;
import org.graph4j.util.VertexSet;

/**
 *
 * Computes the vertex ordering from the end to the begining. At each step, the
 * node of minimum degree in the current graph is selected and then it is
 * removed from the graph.
 *
 * So, the first vertex to be selected is the one with the smallest degree in
 * the graph, and it is stored at the end of the ordering. Suppose that the
 * vertices <code>V'={v<sub>i+1</sub>,..., v<sub>n</sub>}</code> have been
 * already selected, the next vertex to be chosen is v<sub>i</sub> in
 * <code>V-V'</code> such that the degree of v<sub>i</sub> in the subgraph
 * induced by the remaining vertices <code>V-V'</code> is minimal.
 *
 *
 * @see VertexOrderings
 * @author Cristian Frăsinaru
 */
public class SmallestDegreeLastOrdering extends GraphAlgorithm {

    public SmallestDegreeLastOrdering(Graph graph) {
        super(graph);
    }

    /**
     * Computes the smallest-degree-last vertex ordering.
     *
     * @return the vertex ordering.
     */
    public int[] compute() {
        int n = graph.numVertices();
        int[] vertexOrdering = new int[n];
        //get the degrees
        int[] deg = graph.degrees();
        int minDeg = GraphMeasures.minDegree(graph);
        int maxDeg = GraphMeasures.maxDegree(graph);
        //prepare the buckets
        VertexSet[] bucket = new VertexSet[maxDeg + 1];
        //HashSet<Integer>[] bucket = new HashSet[maxDeg + 1];
        for (int d = 0; d <= maxDeg; d++) {
            bucket[d] = new VertexSet(graph, n);
            //bucket[d] = new HashSet();
        }
        //add vertices to the buckets        
        for (int i = 0; i < n; i++) {
            bucket[deg[i]].add(graph.vertexAt(i));
        }
        //create the vertex ordering
        int k = n - 1; //position in vertexOrdering
        int d = minDeg; //current position in the bucket array (d is a degree)
        while (d <= maxDeg) {
            if (bucket[d].isEmpty()) {
                d++;
                continue;
            }
            int v = bucket[d].pop();
            //int v = bucket[d].iterator().next();
            //bucket[d].remove(v);
            deg[graph.indexOf(v)] = -1;
            vertexOrdering[k--] = v;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (deg[ui] < 0) {
                    //this vertex has already been settled
                    continue;
                }
                bucket[deg[ui]].remove(u);
                deg[ui]--;
                bucket[deg[ui]].add(u);
                if (deg[ui] < d) {
                    d = deg[ui];
                }
            }
        }
        return vertexOrdering;
    }

}
