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
package ro.uaic.info.graph.alg.coloring;

import java.util.Arrays;
import java.util.BitSet;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.GraphAlgorithm;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyColoring extends GraphAlgorithm implements VertexColoringAlgorithm {

    public GreedyColoring(Graph graph) {
        super(graph);
    }

    @Override
    public VertexColoring findColoring() {
        return findColoring(graph.numVertices());
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        int colors[] = new int[numColors];
        Arrays.fill(colors, -1);
        var used = new BitSet();
        for (int v : graph.vertices()) {
            //finding the colors used by the neighbors of v
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (colors[ui] >= 0) {
                    used.set(colors[ui]);
                }
            }
            //finding a color for v, not used by its neighbors
            int color = 0;
            while (used.get(color) && color < numColors - 1) {
                color++;
            }
            if (color == numColors) {
                return null;
            }
            colors[graph.indexOf(v)] = color;
            used.clear();
        }
        return new VertexColoring(graph, colors);
    }

    /*
    @Override
    public VertexColoring findColoring(int numColors) {
        List<VertexSet> colorClasses = new ArrayList<>();
        for (int v : graph.vertices()) {
            boolean foundColorClass = false;
            nextSet:
            for (var set : colorClasses) {
                for (var u : set.vertices()) {
                    if (graph.containsEdge(v, u)) {
                        continue nextSet;
                    }
                }
                set.add(v);
                foundColorClass = true;
                break;
            }
            if (!foundColorClass) {
                if (colorClasses.size() == numColors) {
                    //numbers of colors exceeded
                    return null;
                }
                var set = new VertexSet(graph);
                set.add(v);
                colorClasses.add(set);
            }
        }
        return new VertexColoring(graph, colorClasses);
    }*/
}
