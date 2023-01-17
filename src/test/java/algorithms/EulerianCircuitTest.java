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
package algorithms;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.alg.eulerian.HierholzerEulerianCircuit;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EulerianCircuitTest {

    public EulerianCircuitTest() {
    }

    @Test
    public void twoIntersectingCycles() {
        var g = new GraphBuilder().numVertices(8)
                .addCycle(0, 1, 2, 3, 4, 5)
                .addCycle(6, 1, 5, 7, 4, 2).buildGraph();

        var alg = new HierholzerEulerianCircuit(g);
        assertTrue(alg.isEulerian());
        assertNotNull(alg.findCircuit());
    }

    @Test
    public void completeGraphs() {
        var g1 = GraphGenerator.complete(6);
        var alg1 = new HierholzerEulerianCircuit(g1);
        assertFalse(alg1.isEulerian());
        assertNull(alg1.findCircuit());

        var g2 = GraphGenerator.complete(7);
        var alg2 = new HierholzerEulerianCircuit(g2);
        assertTrue(alg2.isEulerian());
        assertNotNull(alg2.findCircuit());
    }

    @Test
    public void completeDigraphs() {
        var g1 = new CompleteGenerator(6).createDigraph();
        var alg1 = new HierholzerEulerianCircuit(g1);
        assertTrue(alg1.isEulerian());
        assertNotNull(alg1.findCircuit());

        var g2 = new CompleteGenerator(7).createDigraph();
        var alg2 = new HierholzerEulerianCircuit(g2);
        assertTrue(alg2.isEulerian());
        assertNotNull(alg2.findCircuit());
    }
    
}
