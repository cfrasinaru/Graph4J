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
package ro.uaic.info.graph.generate;

import java.util.stream.IntStream;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

/**
 * A <i>star</i> is a graph formed by connecting a single universal vertex,
 * called <i>center</i>, to an independent set of vertices.
 *
 * A star graph with n vertices is actually the complete bipartite graph
 * K(1,n-1). A star with 3 edges is called a <i>claw</i>.
 *
 * @author Cristian Frăsinaru
 */
public class StarGenerator extends AbstractGraphGenerator {
    
    private int center;
    
    public StarGenerator(int numVertices) {
        super(numVertices);
        this.center = 0;
    }

    /**
     *
     * @param firstVertex
     * @param lastVertex
     * @param center the number of the center vertex
     */
    public StarGenerator(int firstVertex, int lastVertex, int center) {
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
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(1)
                .named("S" + n).buildGraph();
        addEdges(g, true);
        return g;
    }

    /**
     *
     * @param outward the orientation of the edges connecting the center
     * @return
     */
    public Digraph createDigraph(boolean outward) {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(1).buildDigraph();
        addEdges(g, outward);
        return g;
    }
    
    private void addEdges(Graph g, boolean outward) {
        int[] other = IntStream.of(vertices).filter(v -> v != center).toArray();
        for (int v : other) {
            if (outward) {
                g.addEdge(center, v);
            } else {
                g.addEdge(v, center);
            }
        }
    }
    
}
