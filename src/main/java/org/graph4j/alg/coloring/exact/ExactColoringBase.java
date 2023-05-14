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
package org.graph4j.alg.coloring.exact;

import java.util.List;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.alg.coloring.RecursiveLargestFirstColoring;
import org.graph4j.alg.coloring.VertexColoring;
import org.graph4j.alg.coloring.VertexColoringAlgorithm;
import org.graph4j.alg.connectivity.ConnectivityAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class ExactColoringBase extends SimpleGraphAlgorithm
        implements VertexColoringAlgorithm {

    protected Clique maxClique;
    protected long timeLimit;
    protected long startTime;
    protected boolean timeExpired;
    protected VertexColoring initialColoring;

    public ExactColoringBase(Graph graph) {
        this(graph, null, 0);
    }

    /**
     *
     * @param graph the input graph.
     * @param initialColoring an initial coloring of the vertices.
     */
    public ExactColoringBase(Graph graph, VertexColoring initialColoring) {
        this(graph, initialColoring, 0);
    }

    /**
     *
     * @param graph the input graph.
     * @param timeLimit in milliseconds.
     */
    public ExactColoringBase(Graph graph, long timeLimit) {
        this(graph, null, timeLimit);
    }

    /**
     *
     * @param graph the input graph.
     * @param coloring an initial coloring of the vertices.
     * @param timeLimit in milliseconds.
     */
    public ExactColoringBase(Graph graph, VertexColoring coloring, long timeLimit) {
        super(graph);
        this.initialColoring = new VertexColoring(graph);
        if (coloring != null) {
            for(int v : graph.vertices()) {
                int color = coloring.getColor(v);
                if (color >= 0) {
                    initialColoring.setColor(v, color);
                }
            }
            initialColoring.checkValidity();
        }        
        this.timeLimit = timeLimit;
    }

    protected abstract VertexColoringAlgorithm getInstance(Graph graph, long timeLimit);

    /**
     *
     * @return {@code true} if time expired before determining the optimum.
     */
    public boolean isTimeExpired() {
        return timeExpired;
    }

    @Override
    public VertexColoring findColoring() {
        var components = new ConnectivityAlgorithm(graph).getConnectedSets();
        if (components.size() > 1) {
            return solveDisconnected(components);
        }
        return solve();
    }

    //invoked if the graph is not connected
    private VertexColoring solveDisconnected(List<VertexSet> components) {
        this.startTime = System.currentTimeMillis();
        VertexColoring coloring = new VertexColoring(graph);
        for (var cc : components) {
            var subgraph = graph.subgraph(cc);
            var partial = getInstance(subgraph, timeLimit).findColoring();
            if (partial == null) {
                return null;
            }
            for (int v : subgraph.vertices()) {
                coloring.setColor(v, partial.getColor(v));
            }
        }
        return coloring;
    }

    //invoked if the graph is connected
    protected VertexColoring solve() {
        this.startTime = System.currentTimeMillis();
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getMaximalClique();
            System.out.println("Maximal Clique: " + maxClique.size());
        }
        VertexColoring coloring = new RecursiveLargestFirstColoring(graph).findColoring();
        int lowerBound = maxClique.size();
        int upperBound = coloring.numUsedColors() - 1;
        for (int i = upperBound, k = lowerBound; i >= k; i--) {
            System.out.println("trying " + i + " colors");
            var c = findColoring(i);
            if (c == null) {
                if (timeExpired) {
                    System.out.println("Time expired.");
                }
                break;
            } else {
                coloring = c;
            }
        }
        return coloring;
    }

    @Override
    public abstract VertexColoring findColoring(int numColors);

}
