package org.graph4j.iso;

import org.graph4j.*;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.traverse.SearchNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for graphs.
 *
 * @author Ignat Gabriel-Andrei
 */
public class GraphUtil {

    private GraphUtil() {
    }

    /**
     * Convert an undirected graph into a directed one
     * @param g: undirected or directed graph
     * @return the converted digraph
     */
    public static Digraph convertToDigraph(Graph g) {
        if (g instanceof Digraph dg)
            return dg;
        else if (g instanceof Pseudograph<?,?> p)
            return getDirectedPseudoGraphFromPseudograph(p);
        else if (g instanceof Multigraph<?,?> m)
            return getDirectedMultigraphFromMultigraph(m);
        else
            return getDigraphFromGraph(g);
    }

    /**
     *
     * @param g: pseudo graph, allowing both self loops and multiple edges
     * @return directed pseudo graph
     */
    private static Digraph getDirectedPseudoGraphFromPseudograph(Pseudograph<?,?> g) {
        DirectedPseudograph dp = GraphBuilder.verticesFrom(g).buildDirectedPseudograph();
        addDirectedEdgesFromUndirectedGraph(g, dp);

        return dp;
    }

    /**
     *
     * @param g: multi graph, allowing only multiple edges
     * @return directed multi graph
     */
    private static DirectedMultigraph<?,?> getDirectedMultigraphFromMultigraph(Multigraph g){
        DirectedMultigraph dm = GraphBuilder.verticesFrom(g).buildDirectedMultigraph();
        addDirectedEdgesFromUndirectedGraph(g, dm);

        return dm;
    }

    /**
     *
     * @param g: simple undirected graph, not allowing self loops or multiple edges
     * @return simple directed graph
     */
    private static Digraph<?,?> getDigraphFromGraph(Graph g) {
        Digraph dg = GraphBuilder.verticesFrom(g).buildDigraph();
        addDirectedEdgesFromUndirectedGraph(g, dg);

        return dg;
    }

    /**
     * For every edge in the undirected graph g, add 2 edges in the digraph dg
     * @param g: undirected graph
     * @param dg: digraph
     */
    private static void addDirectedEdgesFromUndirectedGraph(Graph g, Digraph dg) {
        for (Edge e : g.edges()) {
            int source = e.source();
            int target = e.target();

            dg.addEdge(source, target, e.weight(), e.label());
            dg.addEdge(target, source, e.weight(), e.label());
        }
    }
    
    /**
     * Method for getting the centers of a tree.
     *
     * @param graph the tree
     * @return a list containing the centers of the tree(1 or 2 centers)
     */
    public static List<Integer> getCenters(Graph<?,?> graph) {
        if(graph.isDirected())
            throw new IllegalArgumentException("Graph must be undirected");

        if(graph.numVertices() == 0)
            return new ArrayList<>();

        int[] vertices = graph.vertices();

        // We can start from any vertex
        int firstVertex = vertices[0];

        // Find the vertex furthest from the first vertex
        int furthestVertex1 = findFarthestVertex(graph, firstVertex, new int[graph.numVertices()]);

        // Find the vertex furthest from the furthest vertex
        int[] parents = new int[graph.numVertices()];
        int furthestVertex2 = findFarthestVertex(graph, furthestVertex1, parents);

        List<Integer> diameter = new ArrayList<>();

        // Reconstruct the diameter
        do {
            diameter.add(furthestVertex2);
            furthestVertex2 = parents[graph.indexOf(furthestVertex2)];
        } while(furthestVertex2 != -1);

        int diameterSize = diameter.size();

        List<Integer> centers;

        if(diameterSize % 2 == 0) {
            // If the diameter has an even number of vertices, then there are two centers
            centers = new ArrayList<>(2);
            centers.add(diameter.get(diameterSize / 2 - 1));
            centers.add(diameter.get(diameterSize / 2));
        } else {
            // If the diameter has an odd number of vertices, then there is one center
            centers = new ArrayList<>(1);
            centers.add(diameter.get(diameterSize / 2));
        }

        return centers;
    }

    /**
     * Method for finding the vertex furthest from the start vertex.
     * @param graph the graph
     * @param startVertex the start vertex
     * @param parents the parents array, that will be filled with the parents of the vertices(useful for reconstructing the diameter)
     * @return the vertex furthest from the start vertex
     */
    private static int findFarthestVertex(Graph<?,?> graph, int startVertex,
                                   int[] parents) {
        BFSIterator bfsIterator = new BFSIterator(graph, startVertex);
        parents[graph.indexOf(startVertex)] = -1;

        int furthestVertex = startVertex;
        int maxDistance = -1;

        int vertex;
        int level;

        while(bfsIterator.hasNext()) {
            SearchNode searchNode = bfsIterator.next();
            vertex = searchNode.vertex();


            parents[graph.indexOf(vertex)] = searchNode.parent() != null ? searchNode.parent().vertex() : -1;

            level = searchNode.level();
            if(level > maxDistance) {
                maxDistance = level;
                furthestVertex = vertex;
            }
        }

        return furthestVertex;
    }
}
