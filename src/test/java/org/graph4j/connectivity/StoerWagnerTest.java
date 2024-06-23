/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.connectivity;

import org.graph4j.GraphBuilder;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.EdgeSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cristian Frăsinaru
 */
public class StoerWagnerTest {

    @Test
    public void empty() {
        var g = GraphBuilder.numVertices(10).buildGraph();
        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(0, alg.getMinimumCutWeight());
    }

    @Test
    public void complete() {
        int n = 10;
        var g = GraphGenerator.complete(n);
        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(n-1, alg.getMinimumCutWeight());
    }
    
    @Test
    public void simple1() {
        var g = GraphBuilder.numVertices(6).buildGraph();
        g.addEdge(0, 1, 3);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 5, 3);

        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(1, alg.getMinimumCutWeight());

        EdgeSet sol = new EdgeSet(g);
        sol.add(2, 3);
        assertEquals(sol, alg.getMinimumCut().edges());
        assertEquals(3, alg.getMinimumCut().leftSide().size());
        assertEquals(3, alg.getMinimumCut().rightSide().size());
    }

    @Test
    public void simple2() {
        var g = GraphBuilder.numVertices(4).buildGraph();
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 0, 2);

        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(2, alg.getMinimumCutWeight());

        EdgeSet sol = new EdgeSet(g);
        sol.add(0, 1);
        sol.add(2, 3);
        assertEquals(sol, alg.getMinimumCut().edges());
        assertEquals(2, alg.getMinimumCut().leftSide().size());
        assertEquals(2, alg.getMinimumCut().rightSide().size());
    }
    
@Test
    public void infinity() {
        var g = GraphBuilder.numVertices(4).buildGraph();
        g.addEdge(0, 1, Double.POSITIVE_INFINITY);
        g.addEdge(1, 2, Double.POSITIVE_INFINITY);
        g.addEdge(2, 3, Double.POSITIVE_INFINITY);
        g.addEdge(3, 0, Double.POSITIVE_INFINITY);

        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(Double.POSITIVE_INFINITY, alg.getMinimumCutWeight());
    }    

    @Test
    public void paper() {
        //19994-stoer-wagner-A Simple Min-Cut Algorithm
        //https://dl.acm.org/doi/pdf/10.1145/263867.263872
        var g = GraphBuilder.vertexRange(1, 8).buildGraph();
        g.addEdge(1, 2, 2);
        g.addEdge(1, 5, 3);
        g.addEdge(2, 3, 3);
        g.addEdge(2, 5, 2);
        g.addEdge(2, 6, 2);
        g.addEdge(3, 4, 4);
        g.addEdge(3, 7, 2);
        g.addEdge(4, 7, 2);
        g.addEdge(4, 8, 2);
        g.addEdge(5, 6, 3);
        g.addEdge(6, 7, 1);
        g.addEdge(7, 8, 3); //12 edges
        var alg = new StoerWagnerMinimumCut(g);
        assertEquals(4, alg.getMinimumCutWeight());
    }

}
