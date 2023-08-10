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
package org.graph4j.alg.coloring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.alg.connectivity.ConnectivityAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 * Base class for exact vertex coloring algorithms.
 *
 * @author Cristian Frăsinaru
 */
public abstract class ExactColoringBase extends SimpleGraphAlgorithm
        implements ColoringAlgorithm {

    protected long timeLimit;
    protected long startTime;
    protected boolean timeExpired;
    protected Coloring initialColoring;
    protected List<VertexSet> components; //connected components
    protected Set<Coloring> solutions;
    protected int solutionsLimit = 1;
    protected boolean outputEnabled = true;
    //
    private Clique maxClique;

    public ExactColoringBase(Graph graph) {
        this(graph, null, 0);
    }

    /**
     *
     * @param graph the input graph.
     * @param initialColoring an initial coloring of the vertices.
     */
    public ExactColoringBase(Graph graph, Coloring initialColoring) {
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
    public ExactColoringBase(Graph graph, Coloring coloring, long timeLimit) {
        super(graph);
        this.initialColoring = new Coloring(graph);
        if (coloring != null) {
            for (int v : graph.vertices()) {
                int color = coloring.getColor(v);
                if (color >= 0) {
                    initialColoring.setColor(v, color);
                }
            }
        }
        this.timeLimit = timeLimit;
    }

    protected abstract ColoringAlgorithm getInstance(Graph graph, long timeLimit);

    @Override
    public Clique getMaximalClique() {
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getMaximalClique();
            //maxClique = new MaximalCliqueFinder(graph).findMaximumClique(timeLimit);
            if (outputEnabled) {
                System.out.println("Maximal clique of size " + maxClique.size() + " found: " + maxClique);
            }
        }
        return maxClique;
    }

    @Override
    public Coloring findColoring() {
        Coloring coloring = getHeuristicColoring();
        int upperBound = coloring.maxColorNumber();
        int lowerBound = getLowerBound();
        int i = upperBound;
        while (i >= lowerBound) {
            if (outputEnabled) {
                System.out.println(this.getClass().getSimpleName() + ": trying " + i + " colors");
            }
            var c = findColoring(i);
            if (c == null) {
                if (outputEnabled) {
                    System.out.println(timeExpired ? "\tTime expired" : "\tNo solution");
                }
                if (isStoppingOnFailure()) {
                    break;
                } else {
                    i--;
                }
            } else {
                System.out.println("\tSolution found");
                coloring = c;
                i = c.maxColorNumber();
                if (isOptimalityEnsured()) {
                    break;
                }
            }
        }
        return coloring;
    }

    /**
     * Finding all colorings is suitable for small graphs only.
     *
     * @param numColors the maximum number of colors to be used.
     * @param solutionsLimit a limit on the number of colorings to be found (use
     * 0 in order to find all the colorings).
     * @return all the vertex colorings of the graph.
     */
    public Set<Coloring> findAllColorings(int numColors, int solutionsLimit) {
        this.startTime = System.currentTimeMillis();
        this.timeExpired = false;
        if (solutionsLimit <= 0) {
            solutionsLimit = Integer.MAX_VALUE;
        }
        this.solutionsLimit = solutionsLimit;
        solve(numColors);
        return solutions;
    }

    @Override
    public Coloring findColoring(int numColors) {
        this.startTime = System.currentTimeMillis();
        this.timeExpired = false;
        solutionsLimit = 1;
        if (components == null) {
            components = new ConnectivityAlgorithm(graph).getConnectedSets();
        }
        if (components.size() > 1 && isSolvingComponents()) {
            solveDisconnected(numColors);
        } else {
            solve(numColors);
        }
        return solutions.isEmpty() ? null : solutions.iterator().next();
    }

    //invoked if the graph is not connected
    //TODO How to cumulate nodesExplored
    protected void solveDisconnected(int numColors) {
        solutions = new HashSet<>();
        Coloring coloring = new Coloring(graph);
        for (var cc : components) {
            var subgraph = graph.subgraph(cc);
            var alg = ((ExactColoringBase) getInstance(subgraph, timeLimit));
            alg.solve(numColors);
            if (alg.timeExpired) {
                timeExpired = true;
                return;
            }
            var partialCol = alg.solutions.isEmpty()
                    ? null : alg.solutions.iterator().next();
            if (partialCol == null) {
                return;
            }
            for (int v : subgraph.vertices()) {
                coloring.setColor(v, partialCol.getColor(v));
            }
        }
        solutions.add(coloring);
    }

    //invoked if the graph is connected
    protected abstract void solve(int numColors);

    /**
     *
     * @return {@code true} if time expired before determining the optimum.
     */
    public boolean isTimeExpired() {
        return timeExpired;
    }

    protected boolean checkTime() {
        if (timeLimit <= 0) {
            return true;
        }
        if (System.currentTimeMillis() - startTime > timeLimit) {
            timeExpired = true;
            return false;
        }
        return true;
    }

    /**
     *
     * @return the solutions limit.
     */
    public int getSolutionsLimit() {
        return solutionsLimit;
    }

    /**
     *
     * @return {@code true} if the output of the algorithm is enabled.
     */
    public boolean isOutputEnabled() {
        return outputEnabled;
    }

    /**
     *
     * @param outputEnabled if {@code true}, the output is enabled.
     */
    public void setOutputEnabled(boolean outputEnabled) {
        this.outputEnabled = outputEnabled;
    }

}
