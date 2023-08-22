package org.graph4j.alg.sp;

import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.alg.GraphAlgorithm;

/**
 *
 * Johnson's algorithm finds the shortest paths between all pairs of vertices in
 * an edge-weighted directed graph. It allows some of the edge weights to be
 * negative numbers, but no negative-weight cycles may exist. It works by using
 * the Bellman–Ford algorithm to compute a transformation of the input graph
 * that removes all negative weights, allowing Dijkstra's algorithm to be used
 * on the transformed graph.
 *
 * The complexity of the algorithm is given by the Dijkstra algorithm which is
 * repeated for every vertex: O(nmlogn). It is best suited for sparse graphs. In
 * case of dense graphs {@link FloydWarshallShortestPath} algorithm may perform
 * better.
 *
 * @see FloydWarshallShortestPath
 * @author Cristian Frăsinaru
 * @author Cristian Ivan
 */
public class JohnsonShortestPath extends GraphAlgorithm
        implements AllPairsShortestPath {

    private Graph auxGraph;
    private double[] h; //alpha
    private SingleSourceShortestPath[] cache;

    public JohnsonShortestPath(Graph graph) {
        super(graph);
        prepare();
    }

    private void prepare() {
        int n = graph.numVertices();
        this.cache = new SingleSourceShortestPath[n];
        
        //Create a copy of the graph
        //Add a new auxiliary node connected to all vertices, with weight 0        
        this.auxGraph = graph.copy();
        int newNode = auxGraph.addVertex();
        for (int v : graph.vertices()) {
            auxGraph.addEdge(newNode, v, 0.0);
        }

        //Use Bellman–Ford algorithm O(nm) from the auxiliary node
        //to find for each vertex the shortest path to it h(v)
        //if there are no negative edges, h(v)=0 for all v
        //h(v) cannot be Infinity
        var bellmanFord = new BellmanFordShortestPath(auxGraph, newNode);
        this.h = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = bellmanFord.getPathWeight(auxGraph.vertexAt(i));
        }
        auxGraph.removeVertex(newNode);
        
        //Modify the weight of all edges
        //to make sure that they all have non-negative weights.
        for (int v : auxGraph.vertices()) {
            int vi = auxGraph.indexOf(v);
            for (var it = auxGraph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = auxGraph.indexOf(u);
                double weight = it.getEdgeWeight(); //of vu
                it.setEdgeWeight(weight + h[vi] - h[ui]);
            }
        }

        //Use Dijkstra's algorithm with binary heap O(m logn) 
        //to find the shortest paths from each node to every other node in the reweighted graph.
        //LAZY
    }

    private SingleSourceShortestPath getDijkstraInstance(int source) {
        int si = auxGraph.indexOf(source);
        SingleSourceShortestPath dijkstra = cache[si];
        if (dijkstra == null) {
            dijkstra = new DijkstraShortestPathHeap(auxGraph, source);
            cache[si] = dijkstra;
        }
        return dijkstra;

    }

    @Override
    public Path findPath(int source, int target) {
        var dijkstra = getDijkstraInstance(source);
        Path path = dijkstra.findPath(target);
        if (path != null) {
            path = new Path(graph, path.vertices());
        }
        return path;
    }

    @Override
    public double getPathWeight(int source, int target) {
        var dijkstra = getDijkstraInstance(source);
        double weight = dijkstra.getPathWeight(target);
        weight += h[auxGraph.indexOf(target)] - h[auxGraph.indexOf(source)];
        return weight;
    }

    @Override
    public double[][] getPathWeights() {
        int n = graph.numVertices();
        double[][] weights = new double[n][];
        for (int vi = 0; vi < n; vi++) {
            var dijkstra = getDijkstraInstance(graph.vertexAt(vi));
            weights[vi] = dijkstra.getPathWeights();
            for (int ui = 0; ui < n; ui++) {
                weights[vi][ui] += h[ui] - h[vi];
            }
        }
        return weights;
    }

}
