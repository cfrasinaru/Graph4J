package org.graph4j.iso.general.improved;


import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.util.IntArrays;

import java.util.*;

/**
 * Class that orders the vertices of a digraph according to their degree.
 * Also, this class caches the predecessors, successors, and edges of the vertices, for a faster access.
 *
 * @author Ignat Gabriel-Andrei
 */
public class OrderedDigraph {
    protected final Digraph dg;
    protected final int n;
    int[][] predecessors;
    int[][] successors;
    Edge[][] edges;

    int[] orderToVertex;
    Map<Integer, Integer> vertexToOrder;

    byte[][] adjMatrix;

    boolean cache;

    public OrderedDigraph(Digraph dg, boolean cache) {
        this.dg = dg;
        this.n = dg.numVertices();
        this.cache = cache;

        List<Integer> vertexList = IntArrays.asList(dg.vertices());
        Comparator<Integer> vertexComparator = (v1, v2) -> {
            int degree1 = dg.indegree(v1) + dg.outdegree(v1);
            int degree2 = dg.indegree(v2) + dg.outdegree(v2);
            // descending order
            return degree2 - degree1;
        };
        vertexList.sort(vertexComparator);
        // transform list to array

        orderToVertex = new int[n];
        vertexToOrder = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int v = vertexList.get(i);
            orderToVertex[i] = v;
            vertexToOrder.put(v, i);
        }

        if (cache) {
            adjMatrix = new byte[n][n];
            for (int i = 0; i < n; i++) {
                Arrays.fill(adjMatrix[i], (byte) 0);
            }
            edges = new Edge[n][n];
            predecessors = new int[n][];
            successors = new int[n][];
        }
    }

    public OrderedDigraph(Digraph dg) {
        this(dg, false);
    }

    public Digraph getGraph() {
        return dg;
    }

    public int getNumVertices() {
        return n;
    }

    /**
     * Return the vertex number of the vertex at the given position in the order.
     * @param vertexIndex the position in the order
     * @return the vertex number
     */
    public int getVertex(int vertexIndex) {
        assert vertexIndex >= 0 && vertexIndex < n;

        return orderToVertex[vertexIndex];
    }

    public int getOrder(int vertex) {
        return vertexToOrder.get(vertex);
    }

    public Edge getEdge(int vertexIndex1, int vertexIndex2) {
        if (cache) {
            if (adjMatrix[vertexIndex1][vertexIndex2] == (byte) 0) {   // not yet computed
                containsEdge(vertexIndex1, vertexIndex2);  // populate adjMatrix and add edge to edges cache
            }
            return edges[vertexIndex1][vertexIndex2];
        }

        int v1 = getVertex(vertexIndex1);
        int v2 = getVertex(vertexIndex2);
        return dg.edge(v1, v2);
    }

    public int[] predecessors(int vertexIndex) {
        if (cache && predecessors[vertexIndex] != null) {
            return predecessors[vertexIndex];
        }

        int v = getVertex(vertexIndex);
        int[] predVertices = dg.predecessors(v);
        int[] predIndices = new int[predVertices.length];

        int i = 0;
        for (int pred : predVertices) {
            predIndices[i++] = getOrder(pred);
        }

        if (cache) {
            predecessors[vertexIndex] = predIndices;
        }
        return predIndices;
    }

    public int[] successors(int vertexIndex) {
        if (cache && successors[vertexIndex] != null) {
            return successors[vertexIndex];
        }

        int v = getVertex(vertexIndex);

        int[] successorVertices = dg.successors(v);
        int[] sucIndices = new int[successorVertices.length];

        int i = 0;
        for (int suc : successorVertices) {
            sucIndices[i++] = getOrder(suc);
        }

        if (cache) {
            successors[vertexIndex] = sucIndices;
        }
        return sucIndices;
    }

    public boolean containsEdge(int vertexIndex1, int vertexIndex2) {
        int v1 = getVertex(vertexIndex1);
        int v2 = getVertex(vertexIndex2);

        if (cache) {
            if (adjMatrix[vertexIndex1][vertexIndex2] == (byte) 0) {   // not yet computed
                boolean has_edge = dg.containsEdge(v1, v2);
                adjMatrix[vertexIndex1][vertexIndex2] = (byte) (has_edge ? 1 : -1);

                if (has_edge) {
                    edges[vertexIndex1][vertexIndex2] = dg.edge(v1, v2);
                }
            }
            return adjMatrix[vertexIndex1][vertexIndex2] == (byte) 1;
        } else {
            return dg.containsEdge(v1, v2);
        }
    }

    public int indegree(int vertexIndex) {
        return dg.indegree(getVertex(vertexIndex));
    }

    public int outdegree(int vertexIndex) {
        return dg.outdegree(getVertex(vertexIndex));
    }
}
