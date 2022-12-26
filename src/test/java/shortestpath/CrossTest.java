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
package shortestpath;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.alg.sp.BellmanFordShortestPath;
import ro.uaic.info.graph.alg.sp.DijkstraShortestPathDefault;
import ro.uaic.info.graph.alg.sp.FloydWarshallShortestPath;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CrossTest {

    @Test
    public void random() {
        int n = 20;
        var g = new GnpRandomGenerator(n, 0.5).createGraph();
        EdgeWeightsGenerator.randomDoubles(g, 0, 1);

        var dij = new DijkstraShortestPathDefault(g, 0);
        double x = dij.getPathWeight(n - 1);

        var bfm = new BellmanFordShortestPath(g, 0);
        double y = bfm.getPathWeight(n - 1);

        var fw = new FloydWarshallShortestPath(g);
        double z = fw.getPathWeight(0, n - 1);

        assertEquals(x, y);
        assertEquals(x, z);
    }

}
