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
package org.graph4j.demo;

import org.graph4j.Edge;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GnmGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class WeightedGraphDemo2 extends PerformanceDemo {

    int n = 3000;

    public WeightedGraphDemo2() {
        //runJGraphT = true;
        runGuava = true;
        //runJung = true;
    }

    @Override
    protected void prepareGraphs() {
    }

    @Override
    protected void testGraph4J() {
        //graph = GraphGenerator.complete(n);
        n = 20_000;
        graph = new GnmGraphGenerator(n, 10 * n).createGraph();
        
        EdgeWeightsGenerator.fill(graph, 1);
        //double cost[][] = graph.costMatrix();
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                double w = graph.getEdgeWeight(i, j);
                d += w == Double.POSITIVE_INFINITY ? 0 : w;
                //d += cost[i][j];
            }
        }
        System.out.println(d);
    }

    @Override
    protected void testJGraphT() {
        jgrapht = Converter.createJGraphT(graph);
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                var e = jgrapht.getEdge(i, j);
                d += e==null ? 0 : jgrapht.getEdgeWeight(e);
            }
        }
        System.out.println(d);
    }

    @Override
    protected void testGuava() {
        guavaValueGraph = Converter.createGuavaValueGraph(graph);
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                d += (Double) guavaValueGraph.edgeValue(i, j).orElse(0.0);
            }
        }
        System.out.println(d);
    }

    @Override
    protected void testJung() {
        //jungGraph = Tools.createJungGraph(graph);
        jungGraph = new edu.uci.ics.jung.graph.SparseGraph<Integer, JungEdge>();
        for (int i = 0; i < graph.numVertices(); i++) {
            jungGraph.addVertex(i);
        }
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            jungGraph.addEdge(new JungEdge(e.weight()), e.source(), e.target());

        }
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                var e = (JungEdge) jungGraph.findEdge(i, j);
                d += e == null ? 0 : e.weight;
            }
        }
        System.out.println(d);
    }

    private class JungEdge {

        public double weight;

        public JungEdge(double weight) {
            this.weight = weight;
        }

    }

    public static void main(String args[]) {
        var app = new WeightedGraphDemo2();
        app.demo();
    }

}
