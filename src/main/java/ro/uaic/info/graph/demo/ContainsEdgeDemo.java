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
package ro.uaic.info.graph.demo;

import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ContainsEdgeDemo extends PerformanceDemo {

    public ContainsEdgeDemo() {
        runGuava = true;
        runJung = false;
        runJGraphT = false;
        runAlgs4 = false;
    }

    
    @Override
    protected void createGraph() {
        int n = 10_000;
        //graph = new GnmRandomGenerator(n, n).createGraph();
        graph = GraphGenerator.complete(n);
        
    }

    @Override
    protected void testGraph4J() {
        int k = 0;
        for (int i = 0, n = graph.numVertices(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.containsEdge(i, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        int k = 0;
        for (int i = 0, n = jgraph.vertexSet().size(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (jgraph.containsEdge(i, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + jgraph.edgeSet().size());
    }

    @Override
    protected void testGuava() {
        int k = 0;
        for (int i = 0, n = guavaGraph.nodes().size(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (guavaGraph.hasEdgeConnecting(i, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + graph.numEdges());
    }

    @Override
    protected void testJung() {
        int k = 0;
        for (int i = 0, n = jungGraph.getVertexCount(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (jungGraph.isNeighbor(j, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + jgraph.edgeSet().size());
    }

    public static void main(String args[]) {
        var app = new ContainsEdgeDemo();
        app.demo();
    }
}
