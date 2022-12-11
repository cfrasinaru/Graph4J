/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
import ro.uaic.info.graph.util.CheckArgument;

/**
 * A <i>wheel graph</i> is a graph formed by connecting a single universal
 * vertex, called <i>center</i>, to all vertices of a cycle.
 *
 * @author Cristian Frăsinaru
 */
public class WheelGenerator {

    private int n;
    private int center;

    /**
     *
     * @param n the number of vertices, including the center
     */
    public WheelGenerator(int n) {
        this(n, 0);
    }

    /**
     *
     * @param n the number of vertices, including the center
     * @param center the number of the center vertex
     */
    public WheelGenerator(int n, int center) {
        CheckArgument.numberOfVertices(n);
        if (center < 0 || center >= n) {
            throw new IllegalArgumentException(
                    "Center vertex must be in the range [0," + (n - 1) + "]");
        }
        this.n = n;
        this.center = center;
    }

    public Graph createGraph() {
        var g = GraphBuilder.numVertices(n).avgDegree(3).buildGraph();
        int[] cycle = IntStream.range(0, n).filter(v -> v != center).toArray();
        for (int i = 0; i < n - 1; i++) {
            int v = cycle[i];
            int u = cycle[(i + 1) % (n - 1)];
            g.addEdge(v, u);
        }
        for (int v : cycle) {
            g.addEdge(center, v);
        }
        return g;
    }

}
