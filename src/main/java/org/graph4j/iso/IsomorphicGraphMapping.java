package org.graph4j.iso;

import org.graph4j.Edge;
import org.graph4j.Graph;

import java.util.Objects;

/**
 * Class for bidirectional graph mapping between two graphs
 *
 * @author Ignat Gabriel-Andrei
 */
public class IsomorphicGraphMapping implements GraphMapping{
    private final int[] forwardMap;

    private final int[] backwardMap;

    private final Graph<?,?> graph1;

    private final Graph<?,?> graph2;

    public IsomorphicGraphMapping(int[] forwardMap, int[] backwardMap,
                                  Graph<?,?> graph1, Graph<?,?> graph2) {
        this.forwardMap = Objects.requireNonNull(forwardMap);
        this.backwardMap = Objects.requireNonNull(backwardMap);
        this.graph1 = Objects.requireNonNull(graph1);
        this.graph2 = Objects.requireNonNull(graph2);
    }

    /**
     * Returns the vertex correspondence of the given vertex in the other graph
     *
     * @param vertex vertex in the given graph
     * @param forward true if the vertex is in the first graph, false if it is in the second graph
     * @return the vertex correspondence of the given vertex in the other graph
     */
    @Override
    public int getVertexCorrespondence(int vertex, boolean forward) {
        if (forward) {
            return forwardMap[graph1.indexOf(vertex)];
        } else {
            return backwardMap[graph2.indexOf(vertex)];
        }
    }

    /**
     * Returns the edge correspondence of the given edge in the other graph
     *
     * @param edge edge in the given graph
     * @param forward true if the edge is in the first graph, false if it is in the second graph
     * @return the edge correspondence of the given edge in the other graph
     */
    @Override
    public Edge<?> getEdgeCorrespondence(Edge<?> edge, boolean forward) {
        Graph<?,?> toGraph = forward ? graph2 : graph1;

        int fromVertex = edge.source();
        int toVertex = edge.target();

        int fromVertexCorrespondence = getVertexCorrespondence(fromVertex, forward);
        int toVertexCorrespondence = getVertexCorrespondence(toVertex, forward);
        if(fromVertexCorrespondence == -1 || toVertexCorrespondence == -1) {
            return null;
        }

        return toGraph.edge(fromVertexCorrespondence, toVertexCorrespondence);
    }

    public int[] getForwardMapping() {
        // unmodified copy
        return forwardMap.clone();
    }

    public int[] getBackwardMapping() {
        // unmodified copy
        return backwardMap.clone();
    }

    /**
     * Checks if the current mapping is a valid isomorphism
     * @return true if the current mapping is a valid isomorphism, false otherwise
     */
    public boolean isValidIsomorphism()
    {
        // check if the mapping is bijective: each vertex in graph1 is mapped to exactly one vertex in graph2
        for (int v : graph1.vertices()) {
            if (forwardMap[graph1.indexOf(v)] == -1 || !graph2.containsVertex(forwardMap[graph1.indexOf(v)]))
                return false;
        }

        for (int v : graph2.vertices()) {
            if (backwardMap[graph2.indexOf(v)] == -1 || !graph1.containsVertex(backwardMap[graph2.indexOf(v)]))
                return false;
        }

        // check if every edge in graph1 is mapped to an edge in graph2 and vice versa
        for (Edge<?> edge : graph1.edges()){
            Edge<?> e = getEdgeCorrespondence(edge, true);
            int u = e.source();
            int v = e.target();

            if (!graph2.containsEdge(u, v))
                return false;
        }

        for (Edge<?> edge : graph2.edges()){
            Edge<?> e = getEdgeCorrespondence(edge, false);
            int u = e.source();
            int v = e.target();

            if (!graph1.containsEdge(u, v))
                return false;
        }

        return true;
    }

}
