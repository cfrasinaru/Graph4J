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

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

/**
 * A two-dimensional m x n grid graph is the cartesian product of the path
 * graphs P_m and P_n (m is the number of rows, n is the number of columns).
 *
 * A planar embedding of a m x n grid graph is represented by the graph with
 * vertices corresponding to points at coordinates (x,y), x=1,...,m and
 * y=1,...,n, and edges connecting vertices whose points are at distance 1.
 *
 * The grid graph is sometimes denoted L(m,n). It has has mn vertices and
 * (m-1)n+(n-1)m=2mn-m-n edges.
 *
 * @author Cristian Frăsinaru
 */
public class GridGenerator extends AbstractGraphGenerator {

    private final int rows, cols;

    /**
     *
     * @param rows the number of rows.
     * @param cols the number of columns.
     */
    public GridGenerator(int rows, int cols) {
        super(rows * cols);
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Vertices are numbered from left to right and from top to bottom.
     *
     * @return a grid graph.
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(4)
                .named("L(" + rows + "," + cols + ")").buildGraph();
        addEdges(g);
        return g;
    }

    /**
     * Edges are oriented from left to right and top to bottom.
     *
     * @return a directed grid graph.
     */
    public Digraph createDigraph() {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(2).buildDigraph();
        addEdges(g);
        return g;
    }

    private void addEdges(Graph g) {
        boolean safeMode = g.isSafeMode();
        g.setSafeMode(false);

        //add horizontal lines
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                int v = i * cols + j;
                int u = v + 1;
                g.addEdge(v, u);
            }
        }

        //add horizontal lines
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows - 1; i++) {
                int v = i * cols + j;
                int u = v + cols;
                g.addEdge(v, u);
            }
        }

        g.setSafeMode(safeMode);
    }

}
