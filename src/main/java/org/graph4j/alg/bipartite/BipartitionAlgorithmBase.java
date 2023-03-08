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
package org.graph4j.alg.bipartite;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Cycle;
import org.graph4j.util.StableSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class BipartitionAlgorithmBase
        extends SimpleGraphAlgorithm implements BipartitionAlgorithm {

    protected boolean[] color;
    protected Boolean bipartite;
    protected Cycle oddCycle;
    protected StableSet leftSide;
    protected StableSet rightSide;

    public BipartitionAlgorithmBase(Graph graph) {
        super(graph);
    }

    @Override
    public boolean isBipartite() {
        if (bipartite == null) {
            compute();
        }
        return bipartite;
    }

    protected void checkBipartite() {
        if (!bipartite) {
            throw new UnsupportedOperationException("Graph is not bipartite.");
        }
    }

    @Override
    public StableSet getLeftSide() {
        if (leftSide != null) {
            return leftSide;
        }
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        leftSide = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (color[i]) {
                leftSide.add(graph.vertexAt(i));
            }
        }
        return leftSide;
    }

    @Override
    public StableSet getRightSide() {
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        rightSide = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (!color[i]) {
                rightSide.add(graph.vertexAt(i));
            }
        }
        return rightSide;
    }

    @Override
    public StableSet getSide(int v) {
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        return color[graph.indexOf(v)] ? leftSide : rightSide;
    }

    @Override
    public Cycle findOddCycle() {
        if (bipartite == null) {
            compute();
        }
        return oddCycle;
    }

    //does the main work
    protected abstract void compute();

}
