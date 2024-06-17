/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.metrics;

import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.traversal.SearchNode;
import org.graph4j.util.Path;
import org.graph4j.util.VertexSet;

/**
 * Determines the diameter, radius, center and periphery for unweighted,
 * undirected trees.
 *
 * @see TreeMetrics
 * @author Cristian Frăsinaru
 * @author Ignat Gabriel-Andrei
 */
public class TreeExtremaCalculator extends GraphAlgorithm
        implements ExtremaCalculator {

    private int startVertex;
    private Integer diameter;
    private VertexSet center, periphery;

    public TreeExtremaCalculator(Graph graph) {
        this(graph, graph.vertices()[0]);
    }

    public TreeExtremaCalculator(Graph graph, int startVertex) {
        super(graph);
        this.startVertex = startVertex;
    }

    @Override
    public VertexSet getCenter() {
        if (center == null) {
            computeDiameterAndCenter();
        }
        return center;
    }

    @Override
    public int getDiameter() {
        if (diameter == null) {
            computeDiameterAndCenter();
        }
        return diameter;
    }

    @Override
    public int getRadius() {
        return getDiameter() / 2;
    }

    @Override
    public VertexSet getPeriphery() {
        if (periphery != null) {
            return periphery;
        }
        periphery = new VertexSet(graph);
        computeDiameterAndCenter();
        for (int centerVertex : center.vertices()) {
            BFSIterator bfsIterator = new BFSIterator(graph, centerVertex);
            while (bfsIterator.hasNext()) {
                SearchNode searchNode = bfsIterator.next();
                int vertex = searchNode.vertex();
                int level = searchNode.level();
                if (level >= diameter / 2 && graph.degree(vertex) == 1) {
                    periphery.add(vertex);
                }
            }
        }
        return periphery;
    }

    private void computeDiameterAndCenter() {
        center = new VertexSet(graph, 2);
        if (graph.isEmpty()) {
            return;
        }
        // Find the vertex furthest from the start vertex
        int furthestVertex1 = findFurthestVertex(startVertex, null);
        // Find the vertex furthest from the furthest vertex
        int[] parents = new int[graph.numVertices()];
        int furthestVertex2 = findFurthestVertex(furthestVertex1, parents);

        //find the center vertices
        Path longestPath = new Path(graph);
        int v = furthestVertex2;
        do {
            longestPath.add(v);
            v = parents[graph.indexOf(v)];
        } while (v != -1);

        diameter = longestPath.length(); //the number of edges
        if (diameter % 2 == 0) {
            // even number of edges, odd number of vertices
            // there is a single center
            center.add(longestPath.get(diameter / 2));
        } else {
            // odd number of edges, even number of vertices
            // there are two vertices in the center            
            center.add(longestPath.get(diameter / 2));
            center.add(longestPath.get(diameter / 2 + 1));
        }
    }

    private int findFurthestVertex(int fromVertex, int[] parents) {
        BFSIterator bfsIterator = new BFSIterator(graph, fromVertex);
        if (parents != null) {
            parents[graph.indexOf(fromVertex)] = -1;
        }
        int furthestVertex = fromVertex;
        int maxDistance = -1;
        while (bfsIterator.hasNext()) {
            SearchNode searchNode = bfsIterator.next();
            if (searchNode.component() > 0) {
                //in case of forests, we compute only for the first cc
                break;
            }
            int vertex = searchNode.vertex();
            int level = searchNode.level();
            if (parents != null) {
                SearchNode parentNode = searchNode.parent();
                parents[graph.indexOf(vertex)] = parentNode != null ? parentNode.vertex() : -1;
            }
            if (level > maxDistance) {
                maxDistance = level;
                furthestVertex = vertex;
            }
        }
        return furthestVertex;
    }

}
