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
package org.graph4j.generate;

import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.IntArrays;

/**
 * A <i>tree</i> is a connected acyclic graph.
 *
 * If n is the number of vertices in a tree, the number of edges is n-1.
 *
 * @author Cristian Frăsinaru
 */
public class RandomTreeGenerator extends AbstractGraphGenerator {
    
    public RandomTreeGenerator(int numVertices) {
        super(numVertices);
    }
    
    public RandomTreeGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    /**
     *
     * @return a random tree.
     */
    public Graph create() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(n - 1).buildGraph();
        g.setSafeMode(false);
        var random = new Random();
        int[] shuffled = IntArrays.shuffle(vertices, random);
        for (int i = 1; i < n; i++) {
            int v = shuffled[i];
            int u = shuffled[random.nextInt(i)];
            g.addEdge(v, u);
        }
        g.setSafeMode(true);
        return g;
    }
    
}
