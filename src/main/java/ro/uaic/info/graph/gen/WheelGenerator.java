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
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.util.CheckArguments;

/**
 * A <i>wheel graph</i> is a graph formed by connecting a single universal
 * vertex, called <i>center</i>, to all vertices of a cycle.
 *
 * @author Cristian Frăsinaru
 */
public class WheelGenerator {

    private int[] vertices;
    private int center;

    /**
     * 
     * @param numVertices the number of vertices, including the center
     */
    public WheelGenerator(int numVertices) {
        CheckArguments.numberOfVertices(numVertices);
        this.vertices = IntStream.range(0, numVertices).toArray();
        this.center = 0;
    }

    /**
     *
     * @param firstVertex
     * @param lastVertex
     * @param center the number of the center vertex
     */
    public WheelGenerator(int firstVertex, int lastVertex, int center) {
        CheckArguments.vertexRange(firstVertex, lastVertex);
        if (center < firstVertex || center > lastVertex) {
            throw new IllegalArgumentException(
                    "Center vertex must be in the range [" + firstVertex + "," + lastVertex + "]");
        }
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        this.center = center;
    }

    /**
     *
     * @return
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).avgDegree(3).buildGraph();
        int[] cycle = IntStream.of(vertices).filter(v -> v != center).toArray();
        for (int i = 0; i < cycle.length; i++) {
            int v = cycle[i];
            int u = cycle[(i + 1) % cycle.length];
            g.addEdge(v, u);
        }
        for (int v : cycle) {
            g.addEdge(center, v);
        }
        return g;
    }

}
