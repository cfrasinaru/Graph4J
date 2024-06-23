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
package org.graph4j.generators;

import java.util.stream.IntStream;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.GraphUtils;
import org.graph4j.util.Validator;

/**
 * Generates Mycielski graphs. A Mycielski graph <code>M<sub>k</sub></code> of
 * order k is a triangle-free graph with chromatic number k.
 *
 * Using this construction, Mycielski showed that there exist triangle-free
 * graphs with arbitrarily large chromatic number.
 *
 * <code>M<sub>1</sub></code> is the singleton graph, <code>M<sub>2</sub></code>
 * is the 2-path graph, <code>M<sub>3</sub></code> is the 5-cycle graph.
 *
 * @author Cristian Frăsinaru
 */
public class MycielskiGenerator extends AbstractGraphGenerator {

    /**
     * Creates a Mycielski graph with the specified chromatic number. The
     * resulting graph is triangle-free.
     *
     * @param chromaticNumber the chromatic number of the generated graph.
     * @return a new triangle-free graph having the chromatic number
     * {@code chromaticNumber + 1}.
     */
    public Graph create(int chromaticNumber) {
        if (chromaticNumber == 1) {
            return GraphGenerator.path(1);
        }
        if (chromaticNumber == 2) {
            return GraphGenerator.path(2);
        }
        Graph myciel = GraphGenerator.cycle(5);
        if (chromaticNumber == 3) {
            return myciel;
        }
        for (int i = 3; i < chromaticNumber; i++) {
            myciel = createFrom(myciel, i);
        }
        return myciel;
        //TODO: implement Grötzsch graph
    }

    /**
     * Creates a Mycielski graph starting from a specified graph. The method
     * does not verify if the specified graph is triangle-free and its chromatic
     * number is the specified one.
     *
     * @param graph a triangle-free graph having the specified chromatic number.
     * @param chromaticNumber the chromatic number of {@code graph}.
     * @return a new triangle-free graph having the chromatic number
     * {@code chromaticNumber + 1}.
     */
    public Graph createFrom(Graph graph, int chromaticNumber) {
        Validator.requireNonEmpty(graph);
        int n = graph.numVertices();
        if (chromaticNumber < 1 || chromaticNumber > n) {
            throw new IllegalArgumentException("Invalid chromatic number: " + chromaticNumber);
        }
        Graph myciel = GraphBuilder.empty().estimatedNumVertices(2 * n + 1).buildGraph();
        myciel.setSafeMode(false);
        myciel.addGraph(graph);
        int delta = graph.maxVertexNumber() + 1;
        int[] additional = IntStream.of(graph.vertices()).map(v -> v + delta).toArray();
        myciel.addVertices(additional);
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (vi < ui) {
                    myciel.addEdge(additional[vi], u);
                    myciel.addEdge(additional[ui], v);
                }
            }
        }
        int extra = myciel.addVertex();
        for (int a : additional) {
            myciel.addEdge(extra, a);
        }
        myciel.setSafeMode(true);
        assert myciel.numVertices() == 2 * n + 1;
        assert myciel.numEdges() == 3 * graph.numEdges() + n;
        assert GraphTests.isTriangleFree(myciel);       
        assert GraphUtils.computeVertexConnectivity(myciel) == chromaticNumber;
        return myciel;
    }
}
