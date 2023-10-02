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
package org.graph4j.alg.cut;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;

/**
 * The vertex separator problem (VSP) is to find a partition of V into nonempty
 * three classes A, B, C such that there is no edge between A and B,
 * <code>max(|A|,|B|) &lt;= f(n)</code> and <code>|C|</code> is minimum.
 *
 * C is called <em>separator</em>, A and B are called <em>shores</em>.
 *
 * The value of the parameter f(n) which is more used in literature is 2n/3.
 *
 * @author Cristian Frăsinaru
 *
 */
//https://www.ic.unicamp.br/~cid/Problem-instances/VSP.html
abstract class VertexSeparatorBase extends SimpleGraphAlgorithm
        implements VertexSeparatorAlgorithm {    
    protected final int maxShoreSize;
    protected VertexSeparator solution;

    /**
     * Creates an algorithm for computing a vertex separator with the default
     * argument for the maximum shore size of <code>2n/3</code>, where
     * <code>n</code> is the number of vertices in the graph.
     *
     * @param graph the input graph.
     */
    public VertexSeparatorBase(Graph graph) {
        this(graph, 2 * graph.numVertices() / 3);
    }

    /**
     * Creates an algorithm for computing a vertex separator with the specified
     * maximum shore size.
     *
     * @param graph the input graph.
     * @param maxShoreSize the maximum shore size.
     */
    public VertexSeparatorBase(Graph graph, int maxShoreSize) {
        super(graph);
        if (maxShoreSize <= 0) {
            throw new IllegalArgumentException("The maximum shore size must be positive");
        }
        this.maxShoreSize = maxShoreSize;
    }

    /**
     * Returns the mazimum shore size used by the algorithmS.
     *
     * @return the maximum shore size.
     */
    public int maxShoreSize() {
        return maxShoreSize;
    }

    @Override
    public abstract VertexSeparator getSeparator();

}
