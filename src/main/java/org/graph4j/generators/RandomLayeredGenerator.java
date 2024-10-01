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
package org.graph4j.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.WorkInProgress;

/**
 * Generates a random layered graph.
 *
 * @author Cristian Frăsinaru
 */
@WorkInProgress
public class RandomLayeredGenerator extends AbstractGraphGenerator {

    private final int numLayers;
    private final int minVerticesLayer;
    private final int maxVerticesLayer;
    private final double intraLayerEdgeProbability;
    private final double interLayerEdgeProbability;
    private final DoubleFunction interLayerDecrease;

    /**
     *
     * @param numLayers number of layers.
     * @param minVerticesLayer minimum number of vertices per layer.
     * @param maxVerticesLayer maximum number of vertices per layer.
     * @param intraLayerEdgeProbability the probability of an intra-layer edge.
     * @param interLayerEdgeProbability the probability of an inter-layer edge.
     * @param interLayerDecrease
     */
    public RandomLayeredGenerator(int numLayers, int minVerticesLayer, int maxVerticesLayer,
            double intraLayerEdgeProbability, double interLayerEdgeProbability,
            DoubleFunction interLayerDecrease) {
        this.numLayers = numLayers;
        this.minVerticesLayer = minVerticesLayer;
        this.maxVerticesLayer = maxVerticesLayer;
        this.intraLayerEdgeProbability = intraLayerEdgeProbability;
        this.interLayerEdgeProbability = interLayerEdgeProbability;
        this.interLayerDecrease = interLayerDecrease;
    }

    /**
     *
     * @return a random k-nearest neighbor graph.
     */
    public Graph createGraph() {
        var graph = GraphBuilder.empty()
                .estimatedNumVertices(numLayers * maxVerticesLayer)
                .buildGraph();
        //graph.setSafeMode(false);
        var random = new Random();
        List<Graph> layers = new ArrayList<>(numLayers);
        int numVertices = 0;
        for (int i = 0; i < numLayers; i++) {
            int first = numVertices;
            int last = numVertices + minVerticesLayer
                    + random.nextInt(maxVerticesLayer - minVerticesLayer);
            var layer = new RandomGnpGraphGenerator(first, last, intraLayerEdgeProbability).createGraph();
            layers.add(layer);
            graph.addGraph(layer);
            numVertices = graph.numVertices();
            //add edges to previous layers
            double p = interLayerEdgeProbability;
            for (int j = i - 1; j >= 0; j--) {
                for (int v : layer.vertices()) {
                    for (int u : layers.get(j).vertices()) {
                        if (random.nextDouble() < p) {
                            graph.addEdge(v, u);
                        }
                    }
                }
                p = (Double) interLayerDecrease.apply(p);
            }
        }
        //graph.setSafeMode(true);
        return graph;
    }

}
