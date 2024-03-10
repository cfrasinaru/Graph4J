package org.graph4j.iso;

import org.graph4j.Graph;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.traverse.SearchNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for graphs.
 *
 * @author Ignat Gabriel-Andrei
 */
class GraphUtil {
    private GraphUtil() {
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
