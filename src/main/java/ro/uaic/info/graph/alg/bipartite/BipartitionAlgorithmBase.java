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
package ro.uaic.info.graph.alg.bipartite;

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.SimpleGraphAlgorithm;
import ro.uaic.info.graph.model.Cycle;
import ro.uaic.info.graph.model.StableSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class BipartitionAlgorithmBase
        extends SimpleGraphAlgorithm implements BipartitionAlgorithm {

    protected boolean[] color;
    protected Boolean bipartite;
    protected Cycle oddCycle;
    protected StableSet firstPart;
    protected StableSet secondPart;

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
    public StableSet getFirstPart() {
        if (firstPart != null) {
            return firstPart;
        }
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        firstPart = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (color[i]) {
                firstPart.add(graph.vertexAt(i));
            }
        }
        return firstPart;
    }

    @Override
    public StableSet getSecondPart() {
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        secondPart = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (!color[i]) {
                secondPart.add(graph.vertexAt(i));
            }
        }
        return secondPart;
    }

    @Override
    public StableSet getPart(int v) {
        if (bipartite == null) {
            compute();
        }
        checkBipartite();
        return color[graph.indexOf(v)] ? firstPart : secondPart;
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
