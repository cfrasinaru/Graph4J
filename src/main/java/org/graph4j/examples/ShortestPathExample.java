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
package org.graph4j.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.sp.SinglePairShortestPath;
import org.graph4j.util.Path;

/**
 * In this example, we assume that there are <em>locations</em> described by the
 * class {@link Location}. Locations may be connected to other locations by
 * roads having a specified length. The problem is to find the shortest path
 * between two specified locations.
 *
 * @author Cristian Frăsinaru
 */
class ShortestPathExample {

    private final int numLocations = 100;
    private List<Location> locations;
    private static final double MAX_ROAD_LENGTH = 100;
    //
    private Graph graph;

    public static void main(String args[]) {
        var app = new ShortestPathExample();
        app.createUserModel();
        app.createGraph();
        app.solveProblem();
    }

    //Create randomly the cities and their connections to other cities.
    //This is the user model, it has nothing to do with Graph4J.
    //Based on this model, a graph will be created in the method createGraph.
    private void createUserModel() {
        //create the locations
        locations = new ArrayList<>(numLocations);
        for (int i = 0; i < numLocations; i++) {
            locations.add(new Location("City " + i));
        }

        //connect each location randomly to other locations
        //we assume that the neighborhood relation is symmetrical
        var random = new Random();
        for (int i = 0; i < numLocations - 1; i++) {
            var loc1 = locations.get(i);
            for (int j = i + 1; j < numLocations; j++) {
                var loc2 = locations.get(j);
                if (random.nextBoolean()) {
                    double length = random.nextDouble() * MAX_ROAD_LENGTH;
                    loc1.getNeighbors().put(loc2, length);
                    loc2.getNeighbors().put(loc1, length);
                }
            }
        }
    }

    //Based on the user model, a graph is created.
    //Each vertex in the graph corresponds to a location.
    //Weighted edges will correspond to connections between cities.
    private void createGraph() {
        //create an empty graph
        this.graph = GraphBuilder.empty()
                .estimatedNumVertices(numLocations)
                .buildGraph();

        //create a vertex for each location
        //the vertex number will be the index of the location in the list
        //each vertex is labeled with its location object
        for (int i = 0; i < numLocations; i++) {
            graph.addVertex(i, locations.get(i));
        }

        //create the edges of the graph
        for (var location : locations) {
            //find the vertex number of the location object
            int v = graph.findVertex(location);
            var neighborMap = location.getNeighbors();
            for (var neighbor : neighborMap.keySet()) {
                int u = graph.findVertex(neighbor);
                //the test prevents adding the same edge twice
                if (v < u) {
                    //get the length of the road between the two locations
                    double length = neighborMap.get(neighbor);
                    //add a weighted edge in the graph
                    graph.addEdge(v, u, length);
                }
            }
        }
        assert graph.numVertices() == numLocations;
    }

    //Once the graph is created, the problem can be solved.
    private void solveProblem() {
        //pick two locations
        Location fromLoc = locations.get(0);
        Location toLoc = locations.get(numLocations - 1);
        //find the shortest path between them
        findShortestPath(fromLoc, toLoc);
    }

    //This method assumes the graph is created.
    private void findShortestPath(Location fromLoc, Location toLoc) {
        //find the vertex numbers of the given locations
        int source = graph.findVertex(fromLoc);
        int target = graph.findVertex(toLoc);

        //create an algorithm for determining the shortest path
        //in this case, the algorithm is {@code BidirectionalDijkstra}.
        var alg = SinglePairShortestPath.getInstance(graph, source, target);
        //same as
        //var alg = new BidirectionalDijkstra(graph, source, target);

        //the length of the shortest path
        double length = alg.getPathWeight();
        System.out.println("The length of the shortest path: " + length);

        //the actual shortest path, containing vertex numbers.
        Path path = alg.findPath();
        System.out.println("The shortest path in the graph: " + path);

        //the shortest path created using user objects
        List<Location> route = new ArrayList<>();
        for (int v : path.vertices()) {
            route.add(locations.get(v));
        }
        System.out.println("The shortest route between locations: " + route);

    }
}
