//package org.graph4j.iso.jgrapht_util;
//
//import org.graph4j.Digraph;
//import org.graph4j.Graph;
//import org.graph4j.GraphBuilder;
//import org.jgrapht.graph.DefaultEdge;
//
//public class TestIsomorphism {
//    protected static Graph<?,?> tree1_4j, tree2_4j;
//    protected static org.jgrapht.Graph<Integer, DefaultEdge> tree1_jgrapht, tree2_jgrapht;
//    protected static Integer root1, root2;
//    public static org.graph4j.Digraph<?,?> convertToGraph4J_better(org.jgrapht.Graph<Integer, DefaultEdge> graph)
//    {
//        int n = graph.vertexSet().size();
//
//        Digraph graph_4J = GraphBuilder.empty().estimatedNumVertices(n).buildDigraph();
//
//        for(Integer vertex : graph.vertexSet()){
//            graph_4J.addVertex(vertex);
//        }
//
//        // get the edges
//        for(DefaultEdge edge : graph.edgeSet()){
//            Integer source  = graph.getEdgeSource(edge);
//            Integer target = graph.getEdgeTarget(edge);
//            graph_4J.addEdge(source, target);
//        }
//
//        return graph_4J;
//    }
//
//    public static org.jgrapht.Graph<Integer, DefaultEdge> convertToJGraphT(Graph<?,?> graph)
//    {
//        if (graph instanceof Digraph) {
//            return convertToJGraphTFromDigraph((Digraph<?,?>) graph);
//        }
//
//        org.jgrapht.Graph<Integer, DefaultEdge> graph_jgrapht = new org.jgrapht.graph.SimpleGraph<>(DefaultEdge.class);
//
//        for(Integer vertex : graph.vertices()){
//            graph_jgrapht.addVertex(vertex);
//        }
//
//        for(var edge : graph.edges()){
//            graph_jgrapht.addEdge(edge.source(), edge.target());
//        }
//
//        return graph_jgrapht;
//    }
//
//    private static org.jgrapht.Graph<Integer, DefaultEdge> convertToJGraphTFromDigraph(Digraph<?,?> graph) {
//        org.jgrapht.Graph<Integer, DefaultEdge> graph_jgrapht = new org.jgrapht.graph.DefaultDirectedGraph<>(DefaultEdge.class);
//        for(Integer vertex : graph.vertices()){
//            graph_jgrapht.addVertex(vertex);
//        }
//        for(var edge : graph.edges()){
//            graph_jgrapht.addEdge(edge.source(), edge.target());
//        }
//        return graph_jgrapht;
//    }
//}
