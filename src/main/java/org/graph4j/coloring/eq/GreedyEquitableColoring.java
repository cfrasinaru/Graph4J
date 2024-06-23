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
package org.graph4j.coloring.eq;

import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.util.VertexSet;
import org.graph4j.alg.coloring.ColoringAlgorithm;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyEquitableColoring extends SimpleGraphAlgorithm
        implements EquitableColoringAlgorithm {

    private final Coloring initialColoring;
    private int numColors;

    public GreedyEquitableColoring(Graph graph) {
        this(graph, ColoringAlgorithm.getInstance(graph).findColoring());
    }

    /**
     *
     * @param graph the input graph.
     * @param initialColoring a proper vertex coloring.
     */
    public GreedyEquitableColoring(Graph graph, Coloring initialColoring) {
        super(graph);
        if (initialColoring == null) {
            throw new IllegalArgumentException("The initial coloring must be not null.");
        }
        if (!initialColoring.isProper()) {
            throw new IllegalArgumentException("The initial coloring is not proper.");
        }
        this.initialColoring = initialColoring;
    }

    @Override
    public Coloring findColoring() {
        this.numColors = graph.numVertices();
        return compute();

    }

    @Override
    public Coloring findColoring(int numColors) {
        this.numColors = numColors;
        return compute();
    }

    private Coloring compute() {
        Coloring coloring = new Coloring(graph, initialColoring);
        var colorMap = coloring.getColorClasses();
        //pick a vertex from the largest color class A
        //and move it in the smallest color class B
        //as long as A.size > B.size + 1
        while (true) {
            int minSize = Integer.MAX_VALUE, maxSize = -1;
            List<Integer> maxCols = new ArrayList<>();
            List<Integer> minCols = new ArrayList<>();
            for (var entry : colorMap.entrySet()) {
                int col = entry.getKey();
                VertexSet set = entry.getValue();
                int size = set.size();
                if (size < minSize) {
                    minSize = size;
                    minCols.clear();
                    minCols.add(col);
                } else if (size == minSize) {
                    minCols.add(col);
                }
                if (size > maxSize) {
                    maxSize = size;
                    maxCols.clear();
                    maxCols.add(col);
                } else if (size == maxSize) {
                    maxCols.add(col);
                }
            }
            if (maxSize <= minSize + 1) {
                //equitable
                break;
            }
            //try to move a vertex from a maxSet to a minSet
            boolean moved = false;
            all:
            for (int maxCol : maxCols) {
                for (int v : colorMap.get(maxCol).vertices()) {
                    next:
                    for (int minCol : minCols) {
                        for (int u : colorMap.get(minCol).vertices()) {
                            if (graph.containsEdge(v, u)) {
                                continue next;
                            }
                        }
                        coloring.setColor(v, minCol);
                        moved = true;
                        break all;
                    }
                }
            }
            if (!moved) {
                //create a new color class for a vertex in maxSet
                int newCol = coloring.numUsedColors();
                if (newCol > numColors) {
                    return null;
                }
                int v = colorMap.get(maxCols.get(0)).pop();
                coloring.setColor(v, newCol);
            }
        }
        assert isValid(coloring);
        return coloring;
    }

}
