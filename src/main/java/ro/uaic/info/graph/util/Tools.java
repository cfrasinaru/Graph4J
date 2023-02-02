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
package ro.uaic.info.graph.util;

import edu.princeton.cs.algs4.AdjMatrixEdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Tools {

    private static final SimpleDateFormat dateTimeFormat
            = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static String formatTimestamp(Date date) {
        if (date == null) {
            return "";
        }
        return dateTimeFormat.format(date);
    }

    public static org.jgrapht.Graph createJGraph(Graph g) {
        org.jgrapht.Graph jg;
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

    public static com.google.common.graph.MutableGraph createGuavaGraph(Graph graph) {
        var g = com.google.common.graph.GraphBuilder
                .undirected()
                .expectedNodeCount(graph.numVertices()).build();
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

    public static edu.uci.ics.jung.graph.SparseGraph createJungGraph(Graph graph) {
        var g = new edu.uci.ics.jung.graph.SparseGraph<Integer, Object>();
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

    /**
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     *
     * @param matrix
     * @return
     */
    public static int maxValue(int[][] matrix) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (max < matrix[i][j]) {
                    max = matrix[i][j];
                }
            }
        }
        return max;
    }

    /**
     *
     * @param matrix
     * @return
     */
    public static double maxValue(double[][] matrix) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (max < matrix[i][j]) {
                    max = matrix[i][j];
                }
            }
        }
        return max;
    }

    public static int maxLength(int[][] matrix) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int len = String.valueOf(matrix[i][j]).length();
                if (max < len) {
                    max = len;
                }
            }
        }
        return max;
    }

    public static int maxLength(double[][] matrix, int decimals) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                int len;
                if (matrix[i][j] == Double.MAX_VALUE) {
                    len = "MaxValue".length();
                } else if (matrix[i][j] == Double.MIN_VALUE) {
                    len = "MinValue".length();
                } else if (matrix[i][j] == Double.POSITIVE_INFINITY) {
                    len = "Infinity".length();
                } else if (matrix[i][j] == Double.NEGATIVE_INFINITY) {
                    len = "-Infinity".length();
                } else {
                    len = String.format("%." + decimals + "f", matrix[i][j]).length();
                }
                if (max < len) {
                    max = len;
                }
            }
        }
        return max;
    }

    /**
     *
     * @param matrix
     */
    public static void printMatrix(int[][] matrix) {
        int cellWidth = maxLength(matrix);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(String.format("%" + cellWidth + "s", matrix[i][j]) + " ");
            }
            System.out.println("");
        }
    }

    /**
     *
     * @param matrix
     * @param decimals
     */
    public static void printMatrix(double[][] matrix, int decimals) {
        int cellWidth = maxLength(matrix, decimals);
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < matrix[0].length; j++) {
                String s;
                if (matrix[i][j] == Double.MAX_VALUE) {
                    s = "MaxValue";
                } else if (matrix[i][j] == Double.MIN_VALUE) {
                    s = "MinValue";
                } else if (matrix[i][j] == Double.POSITIVE_INFINITY) {
                    s = "Infinity";
                } else if (matrix[i][j] == Double.NEGATIVE_INFINITY) {
                    s = "-Infinity";
                } else {
                    s = String.format("%." + decimals + "f", matrix[i][j]);
                }
                System.out.print(String.format("%" + cellWidth + "s", s) + "| ");
            }
            System.out.println("");
        }
    }

    /**
     *
     * @param x
     * @return
     */
    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    /**
     *
     * @param str
     * @param length
     * @return
     */
    public static String padLeft(String str, int length) {
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - str.length()) {
            sb.append(' ');
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     *
     * @param str
     * @param length
     * @return
     */
    public static String padRight(String str, int length) {
        if (str.length() >= length) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     *
     * @param <T>
     * @param values
     * @return
     */
    public static <T> T coalesce(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
