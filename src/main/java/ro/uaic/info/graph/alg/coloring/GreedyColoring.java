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

import java.util.ArrayList;
import java.util.List;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.GraphAlgorithm;
import ro.uaic.info.graph.model.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyColoring extends GraphAlgorithm implements VertexColoringAlgorithm {

    protected List<VertexSet> colorClasses;

    public GreedyColoring(Graph graph) {
        super(graph);
    }

    @Override
    public VertexColoring findColoring() {
        return findColoring(graph.numVertices());
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        colorClasses = new ArrayList<>();
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
    }

}
