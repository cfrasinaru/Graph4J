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
package org.graph4j.metrics;

import org.graph4j.alg.cycle.CycleFinder;
import java.util.Arrays;
import org.graph4j.util.Cycle;
import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.alg.sp.AllPairsShortestPath;
import org.graph4j.alg.sp.SinglePairShortestPath;
import org.graph4j.alg.sp.SingleSourceShortestPath;
import org.graph4j.util.VertexSet;

/**
 * Various <i>distances</i> related to a graph.
 *
 * @author Cristian Frăsinaru
 */
public class GraphMetrics extends GraphAlgorithm {

    private final ExtremaCalculator extremaCalculator;
    private double dist[][]; //distances
    private double ecc[]; //eccentricities
    private Integer girth;
    private Double diameter;
    private Double pseudoDiameter;
    private Double radius;
    private VertexSet center;
    private VertexSet periphery;

    /**
     *
     * @param graph the input graph.
     */
    public GraphMetrics(Graph graph) {
        super(graph);
        extremaCalculator = new ExtremaCalculator(graph);
    }

    /**
     * See
     * https://cstheory.stackexchange.com/questions/10983/optimal-algorithm-for-finding-the-girth-of-a-sparse-graph
     * The girth of a graph is the length of its shortest cycle. Acyclic graphs
     * are considered to have infinite girth.
     *
     * @return the girth of the graph, or <code>Integer.MAX_VALUE</code> if the
     * graph ia acyclic.
     */
    public int girth() {
        if (girth != null) {
            return girth;
        }
        Cycle cycle = new CycleFinder(graph).findShortestCycle();
        girth = cycle != null ? cycle.length() : Integer.MAX_VALUE;
        return girth;
    }

    /**
     * Computes the eccentricity of a vertex.
     *
     * @param vertex a vertex number
     * @return the eccentricity of the vertex
     */
    public double eccentricity(int vertex) {
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
     * <code>inConnectedComponent = true</code>, it computes them, otherwise
     * returns the already computed eccentricity.
     *
     * @param vertex a vertex number
     * @param inConnectedComponent specifies to compute the eccentricity in the
     * connected component of the vertex
     * @return the eccentricity of the vertex.
     */
    public double eccentricity(int vertex, boolean inConnectedComponent) {
        if (!inConnectedComponent && ecc != null) {
            return ecc[graph.indexOf(vertex)];
        }
        var alg = SingleSourceShortestPath.getInstance(graph, vertex);
        double maxDist = Double.NEGATIVE_INFINITY;
        for (int u : graph.vertices()) {
            double d = alg.getPathWeight(u);
            if (d == Double.POSITIVE_INFINITY) {
                if (!inConnectedComponent) {
                    return Double.POSITIVE_INFINITY;
                }
                continue;
            }
            if (d > maxDist) {
                maxDist = d;
            }
        }
        return maxDist;
    }

    /**
     * The <i>distance</i> between two vertices v and u is the number of edges
     * on the shortest path from v to u. If there is no path that connects v and
     * u, the distance is infinite.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return the distance between v and u, or
     * <code>Double.POSITIVE_INFINITY</code> if there is no path from v to u.
     */
    public double distance(int v, int u) {
        if (dist != null) {
            return dist[graph.indexOf(v)][graph.indexOf(u)];
        }
        var alg = SinglePairShortestPath.getInstance(graph, v, u);
        return alg.getPathWeight();
        /*
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
         */
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
    public double[] eccentricities() {
        if (ecc != null) {
            return ecc;
        }
        if (dist == null) {
            distances();
        }
        int n = graph.numVertices();
        this.ecc = new double[n];
        Arrays.fill(ecc, Double.NEGATIVE_INFINITY);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double d = dist[i][j];
                if (ecc[i] < d) {
                    ecc[i] = d; //maximum distance from i
                }
            }
        }
        //ecc = new EccentricitiesCalculator(graph).calculate();
        return ecc;
    }

    /**
     * Computes and stores for later retrieval all the distances between graph
     * vertices. If the distances are already computed, it simply returns them.
     *
     * Note that some methods of this class might trigger this computation, in
     * order to create their response.
     *
     * @return the distances matrix
     */
    public double[][] distances() {
        if (dist == null) {
            //dist = new DistancesCalculator(graph).calculate();
            var alg = AllPairsShortestPath.getInstance(graph);
            dist = alg.getPathWeights();
        }
        return dist;
    }

    /**
     * The <i>diameter</i> of a graph is the length of the "longest shortest
     * path", that is the greatest distance between any two vertices. A
     * disconnected graph has infinite diameter.
     *
     * If the graph is weighted, the algorithm will take into consideration the
     * weight of the edges, in order to compute the diameter.
     *
     *
     * @return the diameter of the graph
     */
    public double diameter() {
        if (diameter != null) {
            return diameter;
        }
        if (ecc == null && !graph.isEdgeWeighted()) {
            diameter = (double) extremaCalculator.getDiameter();
            return diameter;
        }
        eccentricities();
        this.diameter = ecc[0];
        for (int i = 1, n = graph.numVertices(); i < n; i++) {
            if (diameter < ecc[i]) {
                diameter = ecc[i]; //maximum of them all
            }
        }
        return diameter;
    }

    /**
     * Computes an approximation of the graph diameter. It works by starting
     * from a vertex u, and finds a vertex v that is farthest away from u. This
     * process is repeated by treating v as the new starting vertex, and ends
     * when the graph distance no longer increases.
     *
     * If either the diameter or eccentricities have been computetd, it returns
     * the diameter of the graph.
     *
     * @return the psueudo-diameter of a graph.
     */
    public double pseudoDiameter() {
        if (pseudoDiameter != null) {
            return pseudoDiameter;
        }
        if (diameter != null) {
            pseudoDiameter = diameter;
        } else if (ecc != null) {
            pseudoDiameter = diameter();
        } else {
            int v = graph.vertexAt(0);
            pseudoDiameter = Double.NEGATIVE_INFINITY;
            while (true) {
                var alg = SingleSourceShortestPath.getInstance(graph, v);
                double maxDist = Double.NEGATIVE_INFINITY;
                for (int u : graph.vertices()) {
                    double d = alg.getPathWeight(u);
                    if (d != Double.POSITIVE_INFINITY && d > maxDist) {
                        maxDist = d;
                        v = u;
                    }
                }
                if (maxDist > pseudoDiameter) {
                    pseudoDiameter = maxDist;
                } else {
                    break;
                }
            }
        }
        return pseudoDiameter;
    }

    /**
     * The <i>radius</i> of a graph is the minimum eccentricity of vertices. A
     * disconnected graph has infinite radius.
     *
     * If the graph is weighted, the algorithm will take into consideration the
     * weight of the edges, in order to compute the radius.
     *
     *
     * @return the radius of the graph, or <code>Double.POSITIVE_INFINITY</code>
     * if the graph is disconnected.
     */
    public double radius() {
        if (radius != null) {
            return radius;
        }
        if (ecc == null && !graph.isEdgeWeighted()) {
            radius = (double) extremaCalculator.getRadius();
            return radius;
        }
        eccentricities();
        this.radius = ecc[0];
        for (int i = 1, n = graph.numVertices(); i < n; i++) {
            if (radius > ecc[i]) {
                radius = ecc[i]; //minimum of them all
            }
        }
        return radius;
    }

    /**
     * The <i>center</i> of a graph is the set of vertices having eccentricities
     * equal to the graph radius (the set of central points).
     *
     * If the graph is weighted, the algorithm will take into consideration the
     * weight of the edges, in order to compute the center.
     *
     * @return the graph center.
     */
    public VertexSet center() {
        if (center != null) {
            return center;
        }
        if (!graph.isEdgeWeighted()) {
            center = extremaCalculator.getCenter();
            return center;
        }
        eccentricities();
        double r = radius();
        center = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (ecc[graph.indexOf(v)] == r) {
                center.add(v);
            }
        }
        return center;
    }

    /**
     * The <i>periphery</i> of a graph is the set of vertices having
     * eccentricities equal to the graph diameter.
     *
     * If the graph is weighted, the algorithm will take into consideration the
     * weight of the edges, in order to compute the periphery.
     *
     * @return the graph periphery.
     */
    public VertexSet periphery() {
        if (periphery != null) {
            return periphery;
        }
        if (!graph.isEdgeWeighted()) {
            periphery = extremaCalculator.getPeriphery();
            return periphery;
        }
        eccentricities();
        double d = diameter();
        periphery = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (ecc[graph.indexOf(v)] == d) {
                periphery.add(v);
            }
        }
        return periphery;
    }

    /**
     * A <i>pseudo-peripheral</i> vertex v has the property that, for any vertex
     * u, if u is as far away from v as possible, then v is as far away from u
     * as possible. Computes the distances and the eccentricities.
     *
     * @return the graph pseudo periphery.
     */
    public VertexSet pseudoPeriphery() {
        distances();
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

    /**
     * The <em>average path length</em> is the average distance (number of edges
     * along the shortest path) for all possible pairs of distinct vertices. It
     * is computed as the sum of distances d(v,u) between all pairs of distinct
     * vertices (assuming d(v,u) = 0 if v and u are not connected) normalized by
     * the total number of paths n*(n-1), where n is the number of vertices in
     * G.
     *
     * @return the average path length.
     */
    public double averagePathLength() {
        if (dist == null) {
            distances();
            //dist = new DistancesCalculator(graph).calculate();
        }
        int n = graph.numVertices();
        double sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][j] != Double.POSITIVE_INFINITY) {
                    sum += dist[i][j];
                }
            }
        }
        return sum / (n * (n - 1));
    }
}
