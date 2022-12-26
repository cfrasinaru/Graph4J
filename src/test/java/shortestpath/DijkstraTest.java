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
import ro.uaic.info.graph.Path;
import ro.uaic.info.graph.alg.sp.DijkstraShortestPathDefault;
import ro.uaic.info.graph.build.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DijkstraTest {

    public DijkstraTest() {
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
        var alg = new DijkstraShortestPathDefault(g, 1);
        assertEquals(0, alg.getPath(1).length());//source
        assertEquals(new Path(g, 1, 3, 2, 4, 5), alg.getPath(5)); //vertex 5
    }

}
