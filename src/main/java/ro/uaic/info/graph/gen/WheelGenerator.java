/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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
package ro.uaic.info.graph.gen;

import java.util.stream.IntStream;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

/**
 * A <i>wheel</i> is a graph formed by connecting a single universal vertex,
 * called <i>center</i> (or hub), to all vertices of a cycle.
 *
 * @author Cristian Frăsinaru
 */
public class WheelGenerator extends AbstractGenerator {
    
    private int center;
    
    public WheelGenerator(int numVertices) {
        super(numVertices);
        this.center = 0;
    }

    /**
     *
     * @param firstVertex
     * @param lastVertex
     * @param center the number of the center vertex
     */
    public WheelGenerator(int firstVertex, int lastVertex, int center) {
        super(firstVertex, lastVertex);
        if (center < firstVertex || center > lastVertex) {
            throw new IllegalArgumentException(
                    "Center vertex must be in the range [" + firstVertex + "," + lastVertex + "]");
        }
        this.center = center;
    }

    /**
     *
     * @return
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = new GraphBuilder().vertices(vertices).estimatedAvgDegree(3)
                .named("W" + n).buildGraph();
        addEdges(g, true, true);
        return g;
    }

    /**
     *
     * @param clockwise the orientation of the cycle
     * @param outward the orientation of the edges connecting the center
     * @return
     */
    public Digraph createDigraph(boolean clockwise, boolean outward) {
        var g = new GraphBuilder().vertices(vertices).estimatedAvgDegree(2).buildDigraph();
        addEdges(g, clockwise, outward);
        return g;
    }
    
    private void addEdges(Graph g, boolean clockwise, boolean outward) {
        int[] cycle = IntStream.of(vertices).filter(v -> v != center).toArray();
        for (int i = 0; i < cycle.length; i++) {
            int v = cycle[i];
            int u = cycle[(i + 1) % cycle.length];
            if (clockwise) {
                g.addEdge(v, u);
            } else {
                g.addEdge(u, v);
            }
        }
        for (int v : cycle) {
            if (outward) {
                g.addEdge(center, v);
            } else {
                g.addEdge(v, center);
            }
        }
    }
    
}
