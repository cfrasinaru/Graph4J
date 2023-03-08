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

import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class WeightedGraphDemo1 extends PerformanceDemo {

    int n = 3000;

    public WeightedGraphDemo1() {
        numVertices = 1000;
        //runJGraphT = true;
        runGuava = true;
        //runJung = false;
    }

    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(n);
        EdgeWeightsGenerator.fill(graph, 1);
    }

    @Override
    protected void testGraph4J() {
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                d += graph.getEdgeWeight(i, j);                
            }
        }
        System.out.println(d);
    }

    @Override
    protected void testJGraphT() {
        double d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                var e = jgrapht.getEdge(i, j);
                d += jgrapht.getEdgeWeight(e);
            }
        }
        System.out.println(d);
    }

    @Override
    protected void testGuava() {
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


}
