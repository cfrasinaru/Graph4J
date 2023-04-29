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

import java.util.BitSet;
import org.graph4j.Graph;
import org.graph4j.util.VertexHeap;

/**
 * {@inheritDoc}
 *
 * <p>
 * DSatur (Degree of Saturation) is an <em>adaptive</em> coloring algorithm in
 * which the vertex ordering is not computed statically at the beginning.
 *
 * Once a new vertex has been colored, the algorithm determines which of the
 * remaining uncolored vertices has the highest number of distinct colors in its
 * neighbourhood and colors this vertex next. This number is called <em>the
 * degree of saturation</em> of a given vertex.
 *
 * DSatur produces exact results for bipartite, cycle, and wheel graphs.
 *
 * The complexity is O((n+m)lg n).
 *
 * @author Cristian Frăsinaru
 */
public class DSaturGreedyColoring extends GreedyColoringBase {

    private VertexHeap heap;
    private int[] degree;
    private BitSet[] dsatur; //distinct adjacent colors

    public DSaturGreedyColoring(Graph graph) {
        super(graph);
    }

    @Override
    protected void init() {
        int n = graph.numVertices();
        this.degree = graph.degrees();
        this.dsatur = new BitSet[n];
        for (int i = 0; i < n; i++) {
            dsatur[i] = new BitSet();
        }
        this.heap = new VertexHeap(graph, this::compareUncoloredVertices);
    }

    private int compareUncoloredVertices(int vi, int ui) {
        int ret = dsatur[ui].cardinality() - dsatur[vi].cardinality();
        if (ret != 0) {
            //highest degree of saturation
            return ret;
        }
        return degree[ui] - degree[vi];  //largest degree
    }

    @Override
    protected boolean hasUncoloredVertices() {
        return !heap.isEmpty();
    }

    @Override
    protected int nextUncoloredVertex() {
        return graph.vertexAt(heap.poll());
    }

    @Override
    protected void update(int v) {
        //v was colored and removed from the heap
        int color = colors[graph.indexOf(v)];
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int ui = graph.indexOf(it.next());
            dsatur[ui].set(color); //saturation may increase
            degree[ui]--;
            heap.update(ui);
        }
    }

}
