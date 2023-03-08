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
package org.graph4j.alg;

import org.graph4j.alg.cycle.CycleDetectionAlgorithm;
import java.util.Arrays;
import org.graph4j.util.Cycle;
import org.graph4j.Graph;
import org.graph4j.util.VertexSet;
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.util.CheckArguments;

/**
 * Various <i>distances</i> related to a graph.
 *
 * @author Cristian Frăsinaru
 */
public class GraphMetrics extends GraphAlgorithm {

    private int dist[][]; //distances
    private int ecc[]; //eccentricities
    private int diameter;
    private int radius;

    public GraphMetrics(Graph graph) {
        super(graph);
    }

    /**
     * The girth of a graph is the length of its shortest cycle. Acyclic graphs
     * are considered to have infinite girth.
     *
     * @return the girth of the graph, or <code>Integer.MAX_VALUE</code> if the
     * graph ia acyclic.
     */
    public int girth() {
        Cycle cycle = new CycleDetectionAlgorithm(graph).findShortestCycle();
        return cycle != null ? cycle.length() : Integer.MAX_VALUE;
    }

    /**
     * Computes the eccentricity of a vertex.
     *
     * See {@link #eccentricity(int, boolean}}.
     *
     * @param vertex a vertex number
     * @return the eccentricity of the vertex
     */
    public int eccentricity(int vertex) {
        return eccentricity(vertex, false);
    }

    /**
     * The <i>eccentricity</i> of a graph vertex v is the maximum graph distance
     * between v and any other vertex of the graph. For a disconnected graph,
     * all vertices are defined to have infinite eccentricity.
     *
     * The maximum eccentricity is the graph <i>diameter</i>. The minimum
     * eccentricity is the graph <i>radius</i>.
     *
     * If the eccentricities are not computed or
     * <code>incConnectedComponent = true</code>, performs a BFS, otherwise
     * returns the already computed eccentricity.
     *
     * @param vertex a vertex number
     * @param inConnectedComponent specifies to compute the eccentricity in the
     * connected component of the vertex
     * @return the eccentricity of the vertex
     */
    public int eccentricity(int vertex, boolean inConnectedComponent) {
        if (!inConnectedComponent && ecc != null) {
            return ecc[graph.indexOf(vertex)];
        }
        int maxLevel = -1;
        var bfs = new BFSIterator(graph, vertex);
        while (bfs.hasNext()) {
            var node = bfs.next();
            if (node.component() > 0) {
                //moved to another connected component
                if (!inConnectedComponent) {
                    return Integer.MAX_VALUE;
                }
                break;
            }
            if (maxLevel < node.level()) {
                maxLevel = node.level();
            }
        }
        return maxLevel;
    }

    /**
     * The <i>distance</i> between two vertices v and u is the number of edges
     * on the shortest path from v to u. If there is no path that connects v and
     * u, the distance is infinite.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return the distance between v and u, or <code>Integer.MAX_VALUE</code>
     * if there is no path from v to u
     */
    public int distance(int v, int u) {
        if (dist != null) {
            return dist[graph.indexOf(v)][graph.indexOf(u)];
        }
        CheckArguments.graphContainsVertex(graph, u);
        var bfs = new BFSIterator(graph, v);
        while (bfs.hasNext()) {
            var node = bfs.next();
            if (node.component() > 0) {
                return Integer.MAX_VALUE;
            }
            if (node.vertex() == u) {
                return node.level();
            }
        }
        throw new RuntimeException(); //this shouldn't happen
    }

    /**
     * Computes and stores for later retrieval all the eccentricities of the
     * graph. If the eccentrities are already computed, it simply returns them.
     *
     * Note that some methods of this class might trigger this compution, in
     * order to create their response.
     *
     * @return the eccentricities
     */
    public int[] eccentricities() {
        if (ecc != null) {
            return ecc;
        }
        if (dist == null) {
            distances();
        }
        int n = graph.numVertices();
        this.ecc = new int[n];
        Arrays.fill(ecc, Integer.MIN_VALUE);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int d = dist[i][j];
                if (ecc[i] < d) {
                    ecc[i] = d; //maximum distance from i
                }
            }
        }
        this.diameter = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (diameter < ecc[i]) {
                diameter = ecc[i]; //maximum of them all
            }
        }
        this.radius = Integer.MAX_VALUE;
        if (diameter < Integer.MAX_VALUE) {
            for (int i = 0; i < n; i++) {
                if (radius > ecc[i]) {
                    radius = ecc[i]; //minimum of them all
                }
            }
        }
        return ecc;
    }

    /**
     * Computes and stores for later retrieval all the distances between graph
     * vertices. If the distances are already computed, it simply returns them.
     *
     * Note that some methods of this class might trigger this compution, in
     * order to create their response.
     *
     * @return the distances matrix
     */
    public int[][] distances() {
        int n = graph.numVertices();
        this.dist = new int[n][n];
        var alg = new FloydWarshallShortestPath(graph);
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int v = graph.vertexAt(i);
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                int u = graph.vertexAt(j);
                double d = alg.getPathWeight(v, u);
                if (d == Double.POSITIVE_INFINITY) {
                    dist[i][j] = Integer.MAX_VALUE;
                } else {
                    dist[i][j] = (int) d;
                }
                if (!directed) {
                    dist[j][i] = dist[i][j];
                }
            }
        }
        return dist;
    }

    /**
     * The <i>diameter</i> of a graph is the length of the "longest shortest
     * path", that is the greatest distance between any two vertices.
     *
     * Computes all the eccentricities.
     *
     * @return the diameter of the graph
     */
    public int diameter() {
        if (ecc == null) {
            eccentricities();
        }
        return diameter;
    }

    /**
     * The <i>radius</i> of a graph is the minimum eccentricity of vertices. A
     * disconnected graph has infinite radius.
     *
     * Computes all the eccentricities.
     *
     * @return the radius of the graph, or <code>Integer.MAX_VALUE</code> if the
     * graph is disconnected.
     */
    public int radius() {
        if (ecc == null) {
            eccentricities();
        }
        return radius;
    }

    /**
     * The <i>center</i> of a graph is the set of vertices having eccentricities
     * equal to the graph radius (the set of central points).
     *
     * @return the graph center
     */
    public VertexSet center() {
        eccentricities(); //also computes the radius
        VertexSet set = new VertexSet(graph);
        if (radius == Integer.MAX_VALUE) {
            return set;
        }
        for (int v : graph.vertices()) {
            if (eccentricity(v) == radius) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     * The <i>periphery</i> of a graph is the set of vertices having
     * eccentricities equal to the graph diameter.
     *
     * @return the graph periphery
     */
    public VertexSet periphery() {
        eccentricities();
        VertexSet set = new VertexSet(graph);
        if (diameter == Integer.MAX_VALUE) {
            return set;
        }
        for (int v : graph.vertices()) {
            if (eccentricity(v) == diameter) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     * A <i>pseudo-peripheral</i> vertex v has the property that, for any vertex
     * u, if u is as far away from v as possible, then v is as far away from u
     * as possible
     *
     * @return the graph pseudo periphery
     */
    public VertexSet pseudoPeriphery() {
        eccentricities();
        VertexSet set = new VertexSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                if (ecc[i] == dist[i][j] && ecc[i] == ecc[j]) {
                    set.add(graph.vertexAt(i));
                }
            }
        }
        return set;
    }

}
