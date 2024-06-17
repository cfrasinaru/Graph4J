package org.graph4j.isomorphism;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.IntArrays;
import org.graph4j.util.Pair;

import java.util.*;

public class TestUtil {

    public static Pair<Graph, Map<Integer, Integer>> generateIsomorphicGraph(Graph g1)
    {
        int n = g1.numVertices();
        List<Integer> permutation = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            permutation.add(i);
        }

        Random random = new Random();
        Collections.shuffle(permutation, random);

        List<Integer> vertexList = new ArrayList<>(IntArrays.asList(g1.vertices()));
        Map<Integer, Integer> mapping = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int vertex_1 = vertexList.get(i);

            int j = permutation.get(i);
            int vertex_2 = vertexList.get(j);

            mapping.put(vertex_1, vertex_2);
        }

        return new Pair(generateMappedGraph(g1, mapping), mapping);
    }

    public static Pair<Digraph, Map<Integer, Integer>> generateIsomorphicDigraph(Digraph g1)
    {
        int n = g1.numVertices();
        List<Integer> permutation = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            permutation.add(i);
        }

        Random random = new Random();
        Collections.shuffle(permutation, random);

        List<Integer> vertexList = new ArrayList<>(IntArrays.asList(g1.vertices()));
        Map<Integer, Integer> mapping = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int vertex_1 = vertexList.get(i);

            int j = permutation.get(i);
            int vertex_2 = vertexList.get(j);

            mapping.put(vertex_1, vertex_2);
        }

        return new Pair(generateMappedDigraph(g1, mapping), mapping);
    }

    public static Digraph generateMappedDigraph(Digraph g1, Map<Integer, Integer> mapping)
    {
        int n = g1.numVertices();
        Digraph g2 = GraphBuilder.empty().estimatedNumVertices(n).buildDigraph();

        for (int v : g1.vertices()) {
            int m_v = mapping.get(v);
            g2.addVertex(m_v);
            g2.setVertexWeight(m_v, g1.getVertexWeight(v));
            g2.setVertexLabel(m_v, g1.getVertexLabel(v));
        }

        for (var edge : g1.edges()) {
            int u = edge.source();
            int v = edge.target();

            double edge_weight = g1.getEdgeWeight(u, v);

            int m_u = mapping.get(u);
            int m_v = mapping.get(v);

//            System.out.println("u: " + u + " v: " + v + " m_u: " + m_u + " m_v: " + m_v);

            g2.addEdge(m_u, m_v);
            g2.setEdgeWeight(m_u, m_v, edge_weight);
            g2.setEdgeLabel(m_u, m_v, g1.getEdgeLabel(u, v));
        }

        return g2;
    }

    public static Graph generateMappedGraph(Graph g1, Map<Integer, Integer> mapping)
    {
        int n = g1.numVertices();
        Graph g2 = GraphBuilder.empty().estimatedNumVertices(n).buildGraph();

        for (int v : g1.vertices()) {
            int m_v = mapping.get(v);
            g2.addVertex(m_v);
            g2.setVertexWeight(m_v, g1.getVertexWeight(v));
            g2.setVertexLabel(m_v, g1.getVertexLabel(v));
        }

        for (var edge : g1.edges()) {
            int u = edge.source();
            int v = edge.target();

            double edge_weight = g1.getEdgeWeight(u, v);

            int m_u = mapping.get(u);
            int m_v = mapping.get(v);

//            System.out.println("u: " + u + " v: " + v + " m_u: " + m_u + " m_v: " + m_v);

            g2.addEdge(m_u, m_v);
            g2.setEdgeWeight(m_u, m_v, edge_weight);
            g2.setEdgeLabel(m_u, m_v, g1.getEdgeLabel(u, v));
        }

        return g2;
    }

    public static Graph generateSubgraph(Graph g1, double vertexPercent) {
        int n = g1.numVertices();
        int m = (int) (vertexPercent * n);

        List<Integer> vertices = new ArrayList<>(IntArrays.asList(g1.vertices()));
        Collections.shuffle(vertices);

        Graph g2 = GraphBuilder.empty().estimatedNumVertices(m).buildGraph();


        for (int i = 0; i < m; i++) {
            int v = vertices.get(i);
            g2.addVertex(v);
            g2.setVertexWeight(v, g1.getVertexWeight(v));
            g2.setVertexLabel(v, g1.getVertexLabel(v));
        }

        for (var edge : g1.edges()) {
            int u = edge.source();
            int v = edge.target();

            if (g2.containsVertex(u) && g2.containsVertex(v)) {
                g2.addLabeledEdge(u, v, g1.getEdgeLabel(u, v), g1.getEdgeWeight(u, v));
            }
        }

        return g2;
    }

    public static Digraph generateSubgraphFromDigraph(Digraph g1, double vertexPercent) {
        int n = g1.numVertices();
        int m = (int) (vertexPercent * n);

        List<Integer> vertices = new ArrayList<>(IntArrays.asList(g1.vertices()));
        Collections.shuffle(vertices);

        Digraph g2 = GraphBuilder.empty().estimatedNumVertices(m).buildDigraph();


        for (int i = 0; i < m; i++) {
            int v = vertices.get(i);
            g2.addVertex(v);
            g2.setVertexWeight(v, g1.getVertexWeight(v));
            g2.setVertexLabel(v, g1.getVertexLabel(v));
        }

        for (var edge : g1.edges()) {
            int u = edge.source();
            int v = edge.target();

            if (g2.containsVertex(u) && g2.containsVertex(v)) {
                g2.addEdge(u, v);

                g2.setEdgeWeight(u, v, g1.getEdgeWeight(u, v));
                g2.setEdgeLabel(u, v, g1.getEdgeLabel(u, v));
            }
        }

        return g2;
    }
}
