package org.graph4j.isomorphism;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.util.IntArrays;


/**
 * <p>
 *     Class that orders the vertices of a digraph according to their degree.
 * </p>
 * <p>
 *     Also, this class caches(if wanted) the predecessors, successors, and edges of the vertices, for a faster access.
 * </p>
 *
 * @author Ignat Gabriel-Andrei
 */
public class OrderedDigraph {
    protected final Digraph dg;
    protected final int n;
    int[][] predecessors;       // cache for the predecessors
    int[][] successors;         // cache for the successors
    Edge[][] edges;             // cache for the edges
    byte[][] adjMatrix;         // cache for the adjacency matrix
    int[] orderToVertex;
    Map<Integer, Integer> vertexToOrder;
    boolean cache;

    /**
     * Constructor that orders the vertices of the given digraph according to their degree.
     * @param dg the digraph to be ordered and possibly cached
     * @param cache if true, the algorithm will cache the predecessors, successors, the adjacency relations
     */
    public OrderedDigraph(Digraph dg, boolean cache) {
        this.dg = dg;
        this.n = dg.numVertices();
        this.cache = cache;

//        List<Integer> vertexList = IntArrays.asList(VertexOrderings.largestDegreeFirst(dg));

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
            orderToVertex[i] = v;       // at index i in the sorted list is found vertex v
            vertexToOrder.put(v, i);    // vertex v is at position i in the sorted list
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

    /**
     * @return the digraph
     */
    public Digraph getGraph() {
        return dg;
    }

    /**
     * @return the number of vertices in the digraph
     */
    public int getNumVertices() {
        return n;
    }

    /**
     * Return the vertex number of the vertex at the given position in the order.
     * @param vertexIndex the order in the sorted list of vertices
     * @return the vertex number
     */
    public int getVertexNumber(int vertexIndex) {
        assert vertexIndex >= 0 && vertexIndex < n;

        return orderToVertex[vertexIndex];
    }

    /**
     * @param vertexNumber the vertex number in the digraph
     * @return the index of the vertex in the sorted list of vertices
     */
    public int getVertexOrder(int vertexNumber) {
        return vertexToOrder.get(vertexNumber);
    }

    /**
     * @param vertexIndex1 the index of the source vertex in the sorted list of vertices
     * @param vertexIndex2 the index of the target vertex in the sorted list of vertices
     * @return the corresponding edge
     */
    public Edge getEdge(int vertexIndex1, int vertexIndex2) {
        if (cache) {
            // if cache is enabled, check if the edge was already cached
            if (adjMatrix[vertexIndex1][vertexIndex2] == (byte) 0) {   // not yet computed
                containsEdge(vertexIndex1, vertexIndex2);  // populate adjMatrix and add edge to edges cache
            }
            return edges[vertexIndex1][vertexIndex2];
        }

        int v1 = getVertexNumber(vertexIndex1);
        int v2 = getVertexNumber(vertexIndex2);
        return dg.edge(v1, v2);
    }

    /**
     * @param vertexIndex the index of the vertex in the sorted list of vertices
     * @return the indices in the order of the predecessors of the corresponding vertex number
     */
    public int[] predecessors(int vertexIndex) {
        // if cache is enabled, check if the predecessors were already cached
        if (cache && predecessors[vertexIndex] != null) {
            return predecessors[vertexIndex];
        }

        int v = getVertexNumber(vertexIndex);
        int[] predVertices = dg.predecessors(v);
        int[] predIndices = new int[predVertices.length];

        int i = 0;
        for (int pred : predVertices) {
            predIndices[i++] = getVertexOrder(pred);
        }

        if (cache) {
            predecessors[vertexIndex] = predIndices;
        }
        return predIndices;
    }

    /**
     * @param vertexIndex the order of the vertex in the sorted list of vertices
     * @return the successors of the corresponding vertex number
     */
    public int[] successors(int vertexIndex) {
        // if cache is enabled, check if the successors were already cached
        if (cache && successors[vertexIndex] != null) {
            return successors[vertexIndex];
        }

        int v = getVertexNumber(vertexIndex);

        int[] successorVertices = dg.successors(v);
        int[] sucIndices = new int[successorVertices.length];

        int i = 0;
        for (int suc : successorVertices) {
            sucIndices[i++] = getVertexOrder(suc);
        }

        if (cache) {
            successors[vertexIndex] = sucIndices;
        }
        return sucIndices;
    }

    /**
     * Checks if there is an edge between the two vertices.
     * @param vertexIndex1 the index of the source vertex in the sorted list of vertices
     * @param vertexIndex2 the index of the target vertex in the sorted list of vertices
     * @return true if there is an edge between the two vertices, false otherwise
     */
    public boolean containsEdge(int vertexIndex1, int vertexIndex2) {
        int v1 = getVertexNumber(vertexIndex1);
        int v2 = getVertexNumber(vertexIndex2);

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

    /**
     * @param vertexIndex the index of the vertex in the sorted list of vertices
     * @return the indegree of the vertex
     */
    public int indegree(int vertexIndex) {
        return dg.indegree(getVertexNumber(vertexIndex));
    }

    /**
     * @param vertexIndex the index of the vertex in the sorted list of vertices
     * @return the outdegree of the vertex
     */
    public int outdegree(int vertexIndex) {
        return dg.outdegree(getVertexNumber(vertexIndex));
    }
}
