/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.support;

import org.graph4j.exceptions.NotChordalException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graph4j.Graph;
import org.graph4j.GraphUtils;
import org.graph4j.UndirectedGraphAlgorithm;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.GreedyColoring;
import org.graph4j.shortestpath.BFSSinglePairShortestPath;
import org.graph4j.generators.RandomChordalGraphGenerator;
import org.graph4j.ordering.VertexOrderings;
import org.graph4j.util.Clique;
import org.graph4j.util.Cycle;
import org.graph4j.util.IntArrays;
import org.graph4j.util.Path;
import org.graph4j.util.StableSet;
import org.graph4j.util.VertexList;
import org.graph4j.util.VertexSet;

/**
 * Support class for chordal graphs. A <em>chordal</em> graph is a graph where
 * every cycle of four or more vertices has a chord. A <em>chord</em> is an edge
 * that connects two non-consecutive vertices within the cycle, but it's not
 * part of the cycle itself.
 *
 * In a chordal graph, all induced cycles must be triangles. A graph which is
 * not chordal must contain a <em>hole</em>, that is an induced cycle on four or
 * more vertices.
 *
 * A graph is chordal if and only if it has a perfect elimination ordering.
 *
 * @see RandomChordalGraphGenerator
 * @author Cristian Frăsinaru
 */
public class ChordalGraphSupport extends UndirectedGraphAlgorithm {

    private Boolean chordal;
    private int[] ordering; //peo
    private int[] positions; //map
    private int holeOrderingPos, holeVertex0, holeVertex1, holeVertex2;
    private Cycle hole;
    private int maxCliquePos;
    private int maxCliqueSize;
    private Clique maximumClique;
    //private List<Clique> cliques; //corresponding to each vertex in ordering
    private List<Clique> maximalCliques;
    private Coloring coloring;
    private StableSet maximumStableSet;
    private List<Clique> minimumCliqueCover;
    private Map<Clique, Integer> minimalVertexSeparators;

    /**
     * Creates an instance of the support class.
     *
     * @param graph the input graph.
     */
    public ChordalGraphSupport(Graph graph) {
        super(graph);
    }

    /**
     * Determines if the graph is chordal using the property that a graph is
     * chordal if and only if it has a perfect elimination ordering.
     *
     * @see VertexOrderings
     * @return {@code true} if the graph is chordal, {@code false} otherwise.
     */
    public boolean isChordal() {
        if (chordal == null) {
            compute();
        }
        return chordal;
    }

    /**
     * Finds a perfect elimination ordering in a chordal graph.
     *
     * @return a perfect elimination ordering if the graph is chordal,
     * {@code null} otherwise;
     */
    public int[] findPerfectEliminationOrdering() {
        if (chordal == null) {
            compute();
        }
        if (!chordal) {
            return null;
        }
        return ordering;
    }

    private void compute() {
        //apply MCS algorithm and reverse the obtained ordering
        ordering = IntArrays.reverse(
                VertexOrderings.maximumCardinality(graph));

        positions = IntArrays.positions(ordering); //Map?
        maxCliqueSize = -1;
        maxCliquePos = -1;
        for (int i = 0, n = graph.numVertices(); i < n - 1; i++) {
            int v = ordering[i];
            //check if the neighbors of v, after the position i, form a clique
            //it is enough to check if the first neighbor in the ordering
            //is connected to all the other neighbors
            var successors = new VertexList(graph);
            int firstSuccesorPos = n;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int pos = positions[u];
                if (pos <= i) {
                    continue; //u is before v in the ordering
                }
                successors.add(u);
                if (pos < firstSuccesorPos) {
                    firstSuccesorPos = pos;
                }
            }
            if (firstSuccesorPos == n) {
                continue;
            }
            //check if the first succesor is connected to the others  
            int firstSuccesor = ordering[firstSuccesorPos];
            for (int succesor : successors.vertices()) {
                if (succesor != firstSuccesor && !graph.containsEdge(firstSuccesor, succesor)) {
                    chordal = false;
                    holeOrderingPos = i;
                    holeVertex0 = v;
                    holeVertex1 = firstSuccesor;
                    holeVertex2 = succesor;
                    return;
                }
            }
            int cliqueSize = 1 + successors.size();
            if (cliqueSize > maxCliqueSize) {
                maxCliqueSize = cliqueSize;
                maxCliquePos = i;
            }
        }
        chordal = true;
    }

    /**
     * Finds a hole, that is an induced cycle on four or more vertices, if the
     * graph is not chordal.
     *
     * @return a hole, if the graph is not chordal, {@code null} otherwise.
     */
    public Cycle findHole() {
        if (hole != null) {
            return hole;
        }
        if (chordal == null) {
            compute();
        }
        if (chordal) {
            return null;
        }
        var forbidden = new VertexSet(graph, Arrays.copyOfRange(ordering, 0, holeOrderingPos + 1));
        forbidden.addAll(graph.neighbors(holeVertex0));
        forbidden.remove(holeVertex1);
        forbidden.remove(holeVertex2);
        Path path = new BFSSinglePairShortestPath(graph, holeVertex1, holeVertex2,
                forbidden.vertices()).findPath();
        assert path != null;

        hole = new Cycle(graph);
        hole.add(holeVertex0);
        hole.addAll(path.vertices());
        assert hole.isValid() && hole.isInduced() && hole.size() >= 4;
        return hole;
    }

    /**
     * Determines the maximum clique size of a chordal graph, without computing
     * the maximal cliques.
     *
     * @return the maximum clique size of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public int getMaximumCliqueSize() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        return maxCliqueSize;
    }

    /**
     * Determines a clique of maximum size in a chordal graph.
     *
     * @return a clique of maximum size in a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public Clique getMaximumClique() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (maximumClique == null) {
            maximumClique = createMaximalClique(maxCliquePos);
        }
        return maximumClique;
    }

    /**
     * Determines all maximal cliques of a chordal graph. A chordal graph can
     * have at most |V| maximal cliques.
     *
     * @return the maximal cliques of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public List<Clique> getMaximalCliques() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (maximalCliques != null) {
            return maximalCliques;
        }
        createMaximalCliques();
        return maximalCliques;
    }

    private void createMaximalCliques() {
        //cliques = new ArrayList<>();
        maximalCliques = new ArrayList<>();
        for (int i = 0, n = graph.numVertices(); i < n - 1; i++) {
            var clique = createMaximalClique(i);
            //cliques.add(clique);
            if (clique.isMaximal()) {
                maximalCliques.add(clique);
            }
        }
    }

    private Clique successors(int pos) {
        int v = ordering[pos];
        var clique = new Clique(graph);
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            if (positions[u] <= pos) {
                continue;
            }
            clique.add(u);
        }
        assert clique.isValid();
        return clique;
    }

    private Clique createMaximalClique(int pos) {
        var clique = successors(pos);
        clique.add(ordering[pos]);
        assert clique.isValid();
        return clique;
    }

    /**
     * Determines a maximum stable set of a chordal graph.
     *
     * @return a maximum stable set of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public StableSet getMaximumStableSet() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (maximumStableSet != null) {
            return maximumStableSet;
        }
        createMaximumStable();
        return maximumStableSet;
    }

    private void createMaximumStable() {
        maximumStableSet = new StableSet(graph);
        var restricted = new VertexSet(graph, graph.numVertices());
        for (int v : ordering) {
            if (restricted.contains(v)) {
                continue;
            }
            maximumStableSet.add(v);
            //restricted.addAll(createMaximalClique(positions[v]).vertices());
            int pos = positions[v];
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (positions[u] > pos) {
                    restricted.add(u);
                }
            }
        }
        assert maximumStableSet.isStableSet();
    }

    /**
     * Determines a minimum clique cover of a chordal graph.
     *
     * @return a minimum clique cover of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public List<Clique> getMinimumCliqueCover() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (minimumCliqueCover != null) {
            return minimumCliqueCover;
        }
        createMinimumCliqueCover();
        return minimumCliqueCover;
    }

    private void createMinimumCliqueCover() {
        if (maximalCliques == null) {
            createMaximalCliques();
        }
        minimumCliqueCover = new ArrayList<>();
        var restricted = new VertexSet(graph, graph.numVertices());
        for (int v : ordering) {
            if (restricted.contains(v)) {
                continue;
            }
            var clique = createMaximalClique(positions[v]);
            for (var other : minimumCliqueCover) {
                clique.removeAll(other.vertices());
            }
            minimumCliqueCover.add(clique);
            restricted.addAll(clique.vertices());
        }
        assert GraphUtils.isPartitionValid(graph, minimumCliqueCover);
    }

    /**
     * Determines the chromatic number of a chordal graph, without computing the
     * optimal coloring. Since chordal graphs are perfect, this is equal to the
     * maximum clique number.
     *
     * @return the chromatic number of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public int getChromaticNumber() {
        return getMaximumCliqueSize();
    }

    /**
     * Creates an optimal coloring of a chordal graph, using the greedy
     * algorithm with the reversed perfect elimination ordering.
     *
     * @return an optimal coloring of a chordal graph.
     * @throws NotChordalException if the graph is not chordal.
     */
    public Coloring getOptimalColoring() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (coloring != null) {
            return coloring;
        }
        coloring = new GreedyColoring(graph, IntArrays.reverse(ordering), false)
                .findColoring();
        return coloring;
    }

    /**
     * Determines a map of all minimal vertex separators, together with their
     * multiplicities. The <em>multiplicity</em> of of a minimal vertex
     * separator is the number of different pairs of vertices separated by it.
     * To determine only the set of minimal separators, use
     * {@link Map#keySet()}.
     *
     * Note that an undirected graph is chordal if and only every minimal vertex
     * separator of it induces a clique (Dirac).
     *
     * See: Kumar, P. Sreenivasa &amp; Madhavan, C. E. Veni. (1998). Minimal
     * vertex separators of chordal graphs. Discrete Applied Mathematics. 89.
     * 155-168. 10.1016/S0166-218X(98)00123-1.
     *
     * @return the map of all minimal vertex separators, with multiplicities.
     */
    public Map<Clique, Integer> getMinimumVertexSeparators() {
        if (!isChordal()) {
            throw new NotChordalException();
        }
        if (minimalVertexSeparators != null) {
            return minimalVertexSeparators;
        }
        createMinimalVertexSeparators();
        return minimalVertexSeparators;
    }

    private void createMinimalVertexSeparators() {
        minimalVertexSeparators = new HashMap<>();
        Clique current, next = new Clique(graph, 0);
        for (int i = graph.numVertices() - 2; i >= 0; i--) {
            current = successors(i);
            if (current.size() <= next.size()) {
                Integer multiplicity = minimalVertexSeparators.get(current);
                if (multiplicity != null) {
                    minimalVertexSeparators.put(current, multiplicity + 1);
                } else {
                    minimalVertexSeparators.put(current, 1);
                }
            }
            next = current;
        }
    }

}
