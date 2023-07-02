/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.coloring.bw;

import org.graph4j.Graph;
import org.graph4j.alg.coloring.GreedyColoring;
import org.graph4j.alg.coloring.Coloring;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyBandwithColoring extends GreedyColoring 
        implements BandwithColoringAlgorithm {

    public GreedyBandwithColoring(Graph graph) {
        super(graph);
    }

    @Override
    public Coloring findColoring() {
        //find an upper bound of the coloring number
        double ub = 1;
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                ub += it.getEdgeWeight();
            }
        }
        numColors = (int) Math.ceil(ub);
        return findColoring(numColors);
    }

    @Override
    protected void markUsedColor(int u, double weight) {
        int ui = graph.indexOf(u);
        int c = colors[ui];
        for (int e = 0; e < numColors; e++) {
            if (Math.abs(c - e) < weight) {
                used.set(e);
            }
        }
    }
}
