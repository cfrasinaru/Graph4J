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

import com.google.common.graph.EndpointPair;
import java.util.HashSet;
import java.util.Set;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class RemoveEdgesDemo extends PerformanceDemo {
    
    private Set jgraphEdges, guavaEdges, jungEdges;
    
    public RemoveEdgesDemo() {
        numVertices = 2000;
        //runGuava = true;
        //runJung = true;
        //runJGraphT = true;
    }
    
    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(numVertices);
    }
    
    @Override
    protected void prepareGraphs() {
        super.prepareGraphs();
        if (runJGraphT) {
            jgraphEdges = new HashSet<>(jgrapht.edgeSet());
        }
        if (runGuava) {
            guavaEdges = new HashSet<>(guavaGraph.edges());
        }
        if (runJung) {
            jungEdges = new HashSet<>(jungGraph.getEdges());
        }
    }
    
    @Override
    protected void testGraph4J() {
        /*
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();                
                it.removeEdge();
            }
        }*/
        
        for (int v = 0; v < numVertices - 1; v++) {
            for (int u = v + 1; u < numVertices; u++) {
                graph.removeEdge(v, u);
            }
        }
        System.out.println(graph.numEdges());
    }
    
    @Override
    protected void testJGraphT() {
        //jgraphEdges = new HashSet<>(jgrapht.edgeSet());
        for (var e : jgraphEdges) {
            jgrapht.removeEdge(e);
        }
        System.out.println(jgrapht.edgeSet().size());
    }
    
    @Override
    protected void testGuava() {
        //guavaEdges = new HashSet<>(guavaGraph.edges());
        for (var e : guavaEdges) {
            guavaGraph.removeEdge((EndpointPair) e);
        }
        System.out.println(guavaGraph.edges().size());
    }
    
    @Override
    protected void testJung() {
        //jungEdges = new HashSet<>(jungGraph.getEdges());
        for (var e : jungEdges) {
            jungGraph.removeEdge(e);
        }
        System.out.println(jungGraph.getEdgeCount());
    }
    
    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 200 * (i + 1);
        }
    }
}
