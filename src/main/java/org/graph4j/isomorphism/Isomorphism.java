package org.graph4j.isomorphism;

import org.graph4j.Edge;
import org.graph4j.Graph;

import java.util.Objects;

/**
 * An immutable one-to-one correspondence (bijection) between the vertices of two
 * graphs that preserves the edge connections. Objects of this class are created
 * by algorithms that find tan isomorphism between two graphs.
 *
 * @see IsomorphismAlgorithm
 * @author Ignat Gabriel-Andrei
 * @author Cristian Frasinaru
 */
public class Isomorphism {

    private final Graph graph1;
    private final Graph graph2;
    private final int[] mapping;
    private final int[] inverse;

    /**
     * Creates an isomorphism between two graphs.
     *
     * If (@code mapping[i]=v} then the vertex with index i in the first graph
     * is mapped to the vertex with number v in the second graph.
     *
     * If (@code inverse[j]=u} then the vertex with index j in the second graph
     * is mapped to the vertex with number u in the first graph.
     *
     *
     * @param graph1 the first graph.
     * @param graph2 the second graph.
     * @param mapping the correspondence between the vertices in the first graph
     * and the second graph.
     * @param inverse the correspondence between the vertices in the second
     * graph and the first graph.
     */
    public Isomorphism(Graph graph1, Graph graph2, int[] mapping, int[] inverse) {
        this.graph1 = Objects.requireNonNull(graph1);
        this.graph2 = Objects.requireNonNull(graph2);
        this.mapping = Objects.requireNonNull(mapping);
        this.inverse = Objects.requireNonNull(inverse);
    }

    /**
     * Returns a copy of the mapping between the first graph and the second
     * graph.
     *
     * @return the correspondence between the vertices in the first graph and
     * the second graph.
     */
    public int[] mapping() {
        return mapping.clone();
    }

    /**
     * Returns the mapping of a vertex number in the first graph.
     *
     * @param v a vertex number in the first graph.
     * @return the vertex number in the second graph to which v is mapped to.
     */
    public int mapping(int v) {
        return mapping[graph1.indexOf(v)];
    }

    /**
     * Returns a copy of the inverse mapping between the second graph and the
     * first graph.
     *
     * @return the correspondence between the vertices in the second graph and
     * the first graph.
     */
    public int[] inverse() {
        return inverse.clone();
    }

    /**
     * Returns the inverse mapping of a vertex number in the second graph.
     *
     * @param u a vertex number in the second graph.
     * @return the vertex number in the first graph to which u is mapped to.
     */
    public int inverse(int u) {
        return inverse[graph2.indexOf(u)];
    }

    /**
     * Checks if the current mapping is a valid isomorphism, i.e. it is a
     * bijection between vertices that preserves the edges.
     *
     * @return {@code true} if the current mapping is a valid isomorphism,
     * {@code false} otherwise.
     */
    public boolean isValid() {
        // check if the mapping is bijective: each vertex in graph1 is mapped to exactly one vertex in graph2
        for (int v : graph1.vertices()) {
            if (mapping[graph1.indexOf(v)] == -1 || !graph2.containsVertex(mapping[graph1.indexOf(v)])) {
                System.out.println("bad1");
                return false;
            }
        }
        for (int v : graph2.vertices()) {
            if (inverse[graph2.indexOf(v)] == -1 || !graph1.containsVertex(inverse[graph2.indexOf(v)])) {
                System.out.println("bad2");
                return false;
            }
        }
        // check if every edge in graph1 is mapped to an edge in graph2 and vice versa
        for (Edge e : graph1.edges()) {
            int v = e.source();
            int u = e.target();
            if (!graph2.containsEdge(mapping(v), mapping(u))) {
                                System.out.println("bad3");
                return false;
            }
        }
        for (Edge e : graph2.edges()) {
            int v = e.source();
            int u = e.target();
            if (!graph1.containsEdge(inverse(v), inverse(u))) {
                                System.out.println("bad4");

                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{\n");
        for (int i = 0; i < mapping.length; i++) {
            int v1 = graph1.vertexAt(i);
            int v2 = mapping[i];
            str.append("\t").append(v1).append(" -> ").append(v2).append("\n");
        }
        str.append("}\n");
        return str.toString();
    }
}
