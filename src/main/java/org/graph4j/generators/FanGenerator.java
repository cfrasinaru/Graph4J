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
import org.graph4j.GraphUtils;
import org.graph4j.util.Validator;

/**
 * Generates fan graphs. A <em>fan graph</em> <code> F<sub>(m,n)</sub> </code>
 * is defined as the graph join <code> N<sub>m</sub> + P<sub>n</sub> </code>,
 * where <code> N<sub>m</sub> </code> is the null graph on m nodes and
 * <code> P<sub>n</sub> </code> is the path graph on n nodes.
 *
 * The fan graph <code> F<sub>(4,1)</sub> </code> is known as the gem graph.
 *
 * @author Cristian Frăsinaru
 */
public class FanGenerator extends AbstractGraphGenerator {

    private final int emptyGraphNumVertices;
    private final int pathGraphNumVertices;
    private Graph emptyGraph, pathGraph;

    /**
     * Creates a generator for fan graphs. First {@code emptyGraphNumVertices}
     * vertex numbers will form the empty graph, while then next
     * {@code pathGraphNumVertices} will represent the path graph.
     *
     * @param emptyGraphNumVertices the number of vertices in the empty graph.
     * @param pathGraphNumVertices the number of vertices in the path graph.
     */
    public FanGenerator(int emptyGraphNumVertices, int pathGraphNumVertices) {
        Validator.checkNumVertices(emptyGraphNumVertices);
        Validator.checkNumVertices(pathGraphNumVertices);
        this.emptyGraphNumVertices = emptyGraphNumVertices;
        this.pathGraphNumVertices = pathGraphNumVertices;
        this.vertices = IntStream.range(0, emptyGraphNumVertices + pathGraphNumVertices).toArray();
    }

    /**
     * Creates a fan graph by performing the union of the empty graph and the
     * path graph.
     *
     * @return a fan graph.
     */
    public Graph create() {
        emptyGraph = GraphGenerator.empty(emptyGraphNumVertices);
        pathGraph = new PathGenerator(emptyGraphNumVertices, emptyGraphNumVertices + pathGraphNumVertices - 1)
                .createGraph();
        return GraphUtils.join(emptyGraph, pathGraph);
    }

    /**
     * Returns the empty graph used to create the fan.
     *
     * @return the empty graph used to create the fan.
     */
    public Graph getEmptyGraph() {
        return emptyGraph;
    }

    /**
     * Returns the path graph used to create the fan.
     *
     * @return the path graph used to create the fan.
     */
    public Graph getPathGraph() {
        return pathGraph;
    }

}
