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
package org.graph4j.sp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.Path;
import org.graph4j.alg.sp.BellmanFordShortestPath;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.sp.BellmanFordShortestPath;
import org.graph4j.alg.sp.DijkstraShortestPathDefault;
import org.graph4j.alg.sp.DijkstraShortestPathDefault;
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.alg.sp.JohnsonShortestPath;
import org.graph4j.alg.sp.JohnsonShortestPath;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BellmanFordTest {

    public BellmanFordTest() {
    }

    @Test
    public void simple() {
        var g = GraphBuilder.vertexRange(1, 5)
                .addEdges("1-2,1-3,2-3,2-4,2-5,3-5,4-5").buildGraph();
        g.setEdgeWeight(1, 2, 3);
        g.setEdgeWeight(1, 3, 1);
        g.setEdgeWeight(2, 3, 1);
        g.setEdgeWeight(2, 4, 1);
        g.setEdgeWeight(2, 5, 3);
        g.setEdgeWeight(3, 5, 9);
        g.setEdgeWeight(4, 5, 1);
        var alg = new BellmanFordShortestPath(g, 1);
        assertEquals(0, alg.findPath(1).length());//source
        assertEquals(new Path(g, new int[]{1, 3, 2, 4, 5}), alg.findPath(5));//vertex 5
    }

    @Test
    public void cross() {
        int n = 20;
        var g = new RandomGnpGraphGenerator(n, 0.5).createGraph();
        EdgeWeightsGenerator.randomDoubles(g, 0, 1);

        var dij = new DijkstraShortestPathDefault(g, 0);
        double x = dij.getPathWeight(n - 1);

        var bfm = new BellmanFordShortestPath(g, 0);
        double y = bfm.getPathWeight(n - 1);

        var fw = new FloydWarshallShortestPath(g);
        double z = fw.getPathWeight(0, n - 1);

        var johnson = new JohnsonShortestPath(g);
        double t = johnson.getPathWeight(0, n - 1);

        assertEquals(x, y);
        assertEquals(x, z);
        assertEquals(x, t);
    }
}
