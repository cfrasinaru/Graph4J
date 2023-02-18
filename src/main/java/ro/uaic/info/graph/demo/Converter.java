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
package ro.uaic.info.graph.demo;

import edu.princeton.cs.algs4.AdjMatrixEdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.FlowEdge;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.util.SupplierUtil;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Converter {

    public static org.jgrapht.Graph createJGraphT(Graph g) {
        org.jgrapht.Graph jg;
        /*
        org.jgrapht.Graph<Integer, DefaultEdge> graph
                = GraphTypeBuilder.<Integer, DefaultEdge>directed()
                        .allowingMultipleEdges(true)
                        .allowingSelfLoops(false)
                        .edgeClass(DefaultEdge.class)
                        .buildGraph();
         */
        if (g != null) {
            if (g.isDirected()) {
                if (g.isEdgeWeighted()) {
                    jg = new org.jgrapht.graph.SimpleDirectedWeightedGraph<Integer, DefaultEdge>(DefaultEdge.class);
                } else {
                    jg = new org.jgrapht.graph.SimpleDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
                }
            } else {
                if (g.isEdgeWeighted()) {
                    jg = new org.jgrapht.graph.SimpleWeightedGraph<Integer, DefaultEdge>(DefaultEdge.class);
                } else {
                    jg = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

                }
            }
            for (int v : g.vertices()) {
                jg.addVertex(v);
            }
            for (var it = g.edgeIterator(); it.hasNext();) {
                Edge e = it.next();
                var je = jg.addEdge(e.source(), e.target());
                if (g.isEdgeWeighted()) {
                    jg.setEdgeWeight(je, it.getWeight());
                }
            }
        } else {
            jg = new org.jgrapht.graph.SimpleGraph<>(
                    SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        }
        return jg;
    }

    public static org.jgrapht.Graph createJGraphF(Graph g) {
        GraphType type;
        if (g.isDirected()) {
            if (g.isEdgeWeighted()) {
                type = DefaultGraphType.directedSimple().asWeighted();
            } else {
                type = DefaultGraphType.directedSimple();
            }
        } else {
            if (g.isEdgeWeighted()) {
                type = DefaultGraphType.simple().asWeighted();
            } else {
                type = DefaultGraphType.simple();
            }
        }

        org.jgrapht.Graph jg;
        jg = new org.jgrapht.opt.graph.fastutil.FastutilMapIntVertexGraph<>(SupplierUtil.createIntegerSupplier(),
                SupplierUtil.createDefaultEdgeSupplier(), type, true);
        for (int v : g.vertices()) {
            jg.addVertex(v);
        }
        for (var it = g.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            var je = jg.addEdge(e.source(), e.target());
            if (g.isEdgeWeighted()) {
                jg.setEdgeWeight(je, it.getWeight());
            }
        }
        return jg;
    }

    public static com.google.common.graph.MutableGraph createGuavaGraph(Graph graph) {
        com.google.common.graph.MutableGraph g;
        if (!graph.isDirected()) {
            g = com.google.common.graph.GraphBuilder
                    .undirected()
                    .expectedNodeCount(graph.numVertices()).build();
        } else {
            g = com.google.common.graph.GraphBuilder
                    .directed()
                    .expectedNodeCount(graph.numVertices()).build();
        }
        for (int i = 0; i < graph.numVertices(); i++) {
            g.addNode(i);
        }
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.putEdge(e.source(), e.target());
        }
        return g;
    }

    public static com.google.common.graph.MutableValueGraph createGuavaValueGraph(Graph graph) {
        var g = com.google.common.graph.ValueGraphBuilder
                .undirected()
                .expectedNodeCount(graph.numVertices()).build();
        for (int i = 0; i < graph.numVertices(); i++) {
            g.addNode(i);
        }
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.putEdgeValue(e.source(), e.target(), e.weight());
        }
        return g;
    }

    public static com.google.common.graph.MutableNetwork createGuavaNetwork(Graph graph) {
        com.google.common.graph.MutableNetwork g;
        if (!graph.isDirected()) {
            g = com.google.common.graph.NetworkBuilder
                    .undirected()
                    .expectedNodeCount(graph.numVertices()).build();
        } else {
            g = com.google.common.graph.NetworkBuilder
                    .directed()
                    .expectedNodeCount(graph.numVertices()).build();
        }
        for (int i = 0; i < graph.numVertices(); i++) {
            g.addNode(i);
        }
        int k = 0;
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.addEdge(e.source(), e.target(), k++);
        }
        return g;
    }

    public static edu.uci.ics.jung.graph.Graph createJungGraph(Graph graph) {
        edu.uci.ics.jung.graph.Graph g;
        if (!graph.isDirected()) {
            g = new edu.uci.ics.jung.graph.UndirectedSparseGraph<Integer, Object>();
        } else {
            g = new edu.uci.ics.jung.graph.DirectedSparseGraph<Integer, Object>();
        }
        for (int i = 0; i < graph.numVertices(); i++) {
            g.addVertex(i);
        }
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.addEdge(e, e.source(), e.target());

        }
        return g;
    }

    public static EdgeWeightedGraph createAlgs4EdgeWeightedGraph(Graph graph) {
        EdgeWeightedGraph ewg = new EdgeWeightedGraph(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            ewg.addEdge(new edu.princeton.cs.algs4.Edge(e.source(), e.target(), e.weight()));
        }
        return ewg;
    }

    public static EdgeWeightedDigraph createAlgs4EdgeWeightedDigraph(Graph graph) {
        EdgeWeightedDigraph ewd = new EdgeWeightedDigraph(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            ewd.addEdge(new DirectedEdge(e.source(), e.target(), e.weight()));
            if (!graph.isDirected()) {
                ewd.addEdge(new DirectedEdge(e.target(), e.source(), e.weight()));
            }
        }
        return ewd;
    }

    public static AdjMatrixEdgeWeightedDigraph createAlgs4AdjMatrixEwd(Graph graph) {
        AdjMatrixEdgeWeightedDigraph adjMatrixEwd = new AdjMatrixEdgeWeightedDigraph(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            adjMatrixEwd.addEdge(new DirectedEdge(e.source(), e.target(), e.weight()));
            if (!graph.isDirected()) {
                adjMatrixEwd.addEdge(new DirectedEdge(e.target(), e.source(), e.weight()));
            }
        }
        return adjMatrixEwd;
    }

    public static edu.princeton.cs.algs4.Graph createAlgs4Graph(Graph graph) {
        var g = new edu.princeton.cs.algs4.Graph(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.addEdge(e.source(), e.target());
        }
        return g;
    }

    public static edu.princeton.cs.algs4.Digraph createAlgs4Digraph(Graph graph) {
        var g = new edu.princeton.cs.algs4.Digraph(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.addEdge(e.source(), e.target());
        }
        return g;
    }

    public static edu.princeton.cs.algs4.FlowNetwork createAlgs4Network(Graph graph) {
        var g = new edu.princeton.cs.algs4.FlowNetwork(graph.numVertices());
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            g.addEdge(new FlowEdge(e.source(), e.target(), e.weight()));
        }
        return g;
    }

}
