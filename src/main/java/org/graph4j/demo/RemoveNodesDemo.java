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

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.HashSet;
import java.util.Set;
import org.graph4j.generate.GnmGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class RemoveNodesDemo extends PerformanceDemo {

    private int[] vertices;
    private Set jgraphNodes, guavaNodes, jungNodes;

    public RemoveNodesDemo() {
        numVertices = 10_000;
        runGuava = true;
        runJung = true;
        runJGraphT = true;
        //runAlgs4 = false;
    }

    @Override
    protected void createGraph() {        
        //avg deg = 2m / n = 100
        graph = new GnmGraphGenerator(numVertices, 50*numVertices).createGraph();
    }

    @Override
    protected void prepareGraphs() {
        super.prepareGraphs();
        vertices = IntArrays.copy(graph.vertices());
        if (runJGraphT) {
            jgraphNodes = new HashSet<>(jgrapht.vertexSet());
        }
        if (runGuava) {
            guavaNodes = new HashSet<>(guavaGraph.nodes());
        }
        if (runJung) {
            jungNodes = new HashSet<>(jungGraph.getVertices());
        }
    }

    @Override
    protected void testGraph4J() {
        for(int v : vertices) {
            graph.removeVertex(v);
        }
        /*
        for (var it = graph.vertexIterator(); it.hasNext();) {
            it.next();
            it.remove();
        }*/
        System.out.println(graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        //var set = new HashSet<>(jgrapht.vertexSet());
        for (var v : jgraphNodes) {
            jgrapht.removeVertex(v);
        }
        System.out.println(jgrapht.vertexSet().size());
    }

    @Override
    protected void testGuava() {
        //var set = new HashSet(guavaGraph.nodes());
        for (var v : guavaNodes) {
            guavaGraph.removeNode(v);
        }
        System.out.println(guavaGraph.nodes().size());
    }

    @Override
    protected void testJung() {
        //var set = new HashSet<>(jungGraph.getVertices());
        for (var v : jungNodes) {
            jungGraph.removeVertex(v);
        }
        System.out.println(jungGraph.getVertexCount());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 10_000 * (i + 1);
        }
    }

}
