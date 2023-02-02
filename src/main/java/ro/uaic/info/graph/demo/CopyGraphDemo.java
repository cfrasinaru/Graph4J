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

import java.util.HashSet;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CopyGraphDemo extends PerformanceDemo {

    private final int count = 10;

    public CopyGraphDemo() {
        numVertices = 1_000;
        runGuava = true;
        //runJung = true;
        runJGraphT = true;
    }

    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(numVertices);
    }

    @Override
    protected void testGraph4J() {
        for (int i = 0; i < count; i++) {
            var g = graph.copy();
            //System.out.println(g.numEdges());
        }        
    }

    @Override
    protected void testJGraphT() {
        for (int i = 0; i < count; i++) {
            var jg = Tools.createJGraph(null);
            org.jgrapht.Graphs.addGraph(jg, jgraph);
            //System.out.println(jg.edgeSet().size());
        }
    }

    @Override
    protected void testGuava() {
        for (int i = 0; i < count; i++) {
            var g = com.google.common.graph.Graphs.copyOf(guavaGraph);
            //System.out.println(g.edges().size());
        }
    }

    @Override
    protected void testJung() {
        /*
        Graph<V, E> src;
        Graph<V, E> dest;
        for (V v : src.getVertices())
            dest.addVertex(v);
        for (E e : src.getEdges())
            dest.addEdge(e, src.getIncidentVertices(e));
         */
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 100 * (i + 1);
        }
    }

}
