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

import org.graph4j.alg.flow.EdmondsKarpMaximumFlow;
import org.graph4j.alg.flow.PushRelabelMaximumFlow;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class PushRelabelTest {

    @Test
    public void simple1() {
        //0->1->2->3->4->5
        var g = GraphBuilder.numVertices(6).buildDigraph();
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 3, 3);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 5, 1);
        var alg = new PushRelabelMaximumFlow(g, 0, 5);
        assertEquals(1, alg.getValue());
    }

    @Test
    public void simple2() {
        //0->1->2->3->4->5
        var g = GraphBuilder.numVertices(6).buildDigraph();
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 5);
        g.addEdge(2, 3, 5);
        g.addEdge(3, 4, 5);
        g.addEdge(4, 5, 2);
        var alg = new PushRelabelMaximumFlow(g, 0, 5);
        assertEquals(2, alg.getValue());
    }

    @Test
    public void simple3() {
        //exemplul din curs
        var g = GraphBuilder.numVertices(6).buildDigraph();
        int s = 0;
        int t = 5;
        g.addEdge(s, 1, 9);
        g.addEdge(s, 4, 8);
        g.addEdge(1, 2, 10);
        g.addEdge(1, 3, 5);
        g.addEdge(2, t, 7);
        g.addEdge(3, t, 6);
        g.addEdge(4, 1, 2);
        g.addEdge(4, 3, 5);
        var alg = new PushRelabelMaximumFlow(g, s, t);
        assertEquals(13, alg.getValue());
        assertEquals(13, alg.getFlow().value());
        assertEquals(7, alg.getValue(2, t));
        assertEquals(6, alg.getValue(3, t));
    }

    @Test
    public void globalRelabel() {
        //[0->3=3.0, 0->4=0.0, 1->0=4.0, 1->4=4.0, 2->0=1.0, 2->1=2.0, 2->3=4.0, 
        // 2->4=0.0, 3->0=2.0, 3->2=2.0, 4->0=1.0, 4->1=0.0, 4->2=0.0, 4->3=1.0]
        var g = GraphBuilder.numVertices(5).buildDigraph();
        g.addEdge(0, 3, 3);
        g.addEdge(0, 4, 0);
        g.addEdge(1, 0, 4);
        g.addEdge(1, 4, 4);
        g.addEdge(2, 0, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(2, 3, 4);
        g.addEdge(2, 4, 0);
        g.addEdge(3, 0, 2);
        g.addEdge(3, 2, 2);
        g.addEdge(4, 0, 1);
        g.addEdge(4, 1, 0);
        g.addEdge(4, 2, 0);
        g.addEdge(4, 3, 1);

        var alg = new PushRelabelMaximumFlow(g, 0, 4);
        assertEquals(2, alg.getValue());
    }

    @Test
    public void crossTest() {
        int n = 10;
        int s = 0;
        int t = n - 1;
        var g = new GnpGraphGenerator(n, Math.random()).createDigraph();
        EdgeWeightsGenerator.randomIntegers(g, 0, n);
        var alg1 = new EdmondsKarpMaximumFlow(g, s, t);
        var alg2 = new PushRelabelMaximumFlow(g, s, t);
        double x = alg1.getValue();
        double y = alg2.getValue();
        assertEquals(x, y);
    }

    /*
    public void crossTest() {
        int n = 50;
        double p = Math.random();
        for (int i = 0; i < 200; i++) {
            var g = new GnpGraphGenerator(n, p).createDigraph();
            EdgeWeightsGenerator.randomIntegers(g, 0, n - 1);
            //EdgeWeightsGenerator.fill(g, 1);
            //System.out.println(g);
            var alg1 = new PushRelabelMaximumFlow(g, 0, n - 1);
            var alg2 = new PushRelabelMFImpl(Converter.createJGraphT(g));
            //var alg3 = new FordFulkerson(Converter.createAlgs4Network(g), 0, n - 1);
            var alg3 = new EdmondsKarpMaximumFlow(g, 0, n - 1);
            //System.out.println("Valid: " + alg1.getFlow().isValid());
            double m1 = alg1.getValue();
            double m2 = alg2.calculateMaximumFlow(0, n - 1);
            double m3 = alg3.getValue();
            if (m1 != m2 || m1 != m3) {
                System.out.println("Oops! m1=" + m1 + ", m2=" + m2 + ", m3=" + m3);
                System.out.println(g);
                break;
            }
        }
    }*/
}
