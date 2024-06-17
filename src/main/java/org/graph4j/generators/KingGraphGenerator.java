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

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class KingGraphGenerator extends AbstractGraphGenerator {

    private final int rows, cols;

    /**
     * @param rows the number of rows.
     * @param cols the number of columns.
     */
    public KingGraphGenerator(int rows, int cols) {
        super(rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Vertices are numbered from left to right and from top to bottom.
     *
     * @return a King's graph.
     */
    public Graph create() {
        var g = GraphBuilder.vertices(vertices)
                .estimatedAvgDegree(8)
                .buildGraph();
        g.setSafeMode(false);

        //add horizontal lines
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                int v = i * cols + j;
                int u = v + 1;
                g.addEdge(v, u);
            }
        }
        //add vertical lines and both diagonals
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 1; i++) {
                //vertical
                int v = i * cols + j;
                int u = v + cols;
                g.addEdge(v, u);
                
                //main diagonal
                if (j < cols - 1) {
                    g.addEdge(v, u + 1);
                }
                //other diagonal
                if (j > 0) {
                    g.addEdge(v, u - 1);
                }
            }
        }

        g.setSafeMode(true);
        return g;
    }

}
