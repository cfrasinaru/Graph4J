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
package ro.uaic.info.graph.gen;

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

/**
 * Generates complete graphs or digraphs. A complete graph contains edges
 * between all pairs of vertices. A complete digraph contains symmetrical arcs
 * (oriented edges) between all pairs of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class CompleteTreeGenerator extends AbstractGenerator {

    private final int numLevels;
    private final int degree;

    /**
     * The root is the vertex with the number 0.
     *
     * @param numLevels number of levels, each level will be complete
     * @param degree the degree of the internal nodes
     */
    public CompleteTreeGenerator(int numLevels, int degree) {
        if (numLevels < 2) {
            throw new IllegalArgumentException("Number of levels must be at least 2: " + numLevels);
        }
        if (degree < 2) {
            throw new IllegalArgumentException("The degree of the internal nodes must be at least 2: " + degree);
        }
        this.numLevels = numLevels;
        this.degree = degree;
    }

    /**
     *
     * @return a complete graph
     */
    public Graph create() {
        int n = (int) (Math.pow(degree, numLevels) - 1) / (degree - 1);
        var g = new GraphBuilder().numVertices(n).estimatedAvgDegree(degree).buildGraph();
        //each level k has d^k vertices (root is on 0)
        //level k>0 has vertices from d^(k-1)+1 to d^(k)
        //vertex at position i on level k has childrens on the k+1 level,
        //at positions: d^k + degree*i + 1 to d^k + (degree+1)*i
        int pow = 1;
        int first = 0;
        int last = 0;
        for (int level = 0; level < numLevels - 1; level++) {
            for (int v = first; v <= last; v++) {
                int pos = v - first;
                for (int u = last + degree * pos + 1; u <= last + degree * (pos + 1); u++) {
                    g.addEdge(v, u);
                }
            }
            pow = pow * degree;
            first = last + 1;
            last = first + pow - 1;
        }
        return g;
    }

}
