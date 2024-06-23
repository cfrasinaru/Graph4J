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
import org.graph4j.clique.MaximalCliqueIterator;
import org.graph4j.util.Clique;

/**
 * Consider a social network formed by persons (instances of the class
 * {@code Person}), each person being friends with other persons (the
 * relationship is symmetrical).
 *
 * The problem is to find maximal cliques, that is maximal groups of persons
 * that all know each other. Since there may be an exponential number of maximal
 * cliques, a certain limit or timeout should be imposed.
 *
 * @author Cristian Frăsinaru
 */
public class MaximalCliqueExample {

    private final int numPersons = 30;
    private List<Person> persons;
    private static final double FRIENDSHIP_PROBABILITY = 0.5;
    private static final int TIMEOUT = 5; //seconds
    //
    private Graph graph;

    public static void main(String args[]) {
        var app = new MaximalCliqueExample();
        app.createUserModel();
        app.createGraph();
        app.solveProblem();
    }

    //Create randomly persons and their friends.
    //This is the user model, it has nothing to do with Graph4J.
    //Based on this model, a graph will be created in the method createGraph.
    private void createUserModel() {
        //create the persons
        persons = new ArrayList<>(numPersons);
        for (int i = 0; i < numPersons; i++) {
            persons.add(new Person("Person " + i));
        }

        //connect each person randomly to other persons
        //we assume that the friendship relation is symmetrical
        var random = new Random();
        for (int i = 0; i < numPersons - 1; i++) {
            var pers1 = persons.get(i);
            for (int j = i + 1; j < numPersons; j++) {
                var pers2 = persons.get(j);
                if (random.nextDouble() < FRIENDSHIP_PROBABILITY) {
                    pers1.getFriends().add(pers2);
                    pers2.getFriends().add(pers1);
                }
            }
        }
    }

    //Based on the user model, a simple undirected graph is created.
    //Each vertex in the graph corresponds to a person.
    //If two persons are friends, we add an edge in the graph.
    private void createGraph() {
        //create a graph based on the user objects
        //each person will receive a vertex number equal to its index in the list
        //each vertex is labeled with its person object
        this.graph = GraphBuilder
                .labeledVertices(persons)
                .estimatedDensity(FRIENDSHIP_PROBABILITY)
                .buildGraph();

        //create the edges of the graph
        for (var person : persons) {
            for (var friend : person.getFriends()) {
                //add an edge in the graph for the two friends                
                graph.addEdge(person, friend);
            }
        }
        assert graph.numVertices() == numPersons;
    }

    //Once the graph is created, the problem can be solved.
    private void solveProblem() {
        System.out.println("Solving the problem");
        //create an algorithm for iterating over the maximal cliques
        var alg = MaximalCliqueIterator.getInstance(graph);
        //same as
        //var alg = new BronKerboschCliqueIterator(graph);
        long t0 = System.currentTimeMillis();
        while (alg.hasNext()) {
            Clique clique = alg.next();
            System.out.println(clique);
            long t1 = System.currentTimeMillis();
            if (t1 - t0 > TIMEOUT * 1000) {
                break;
            }
        }
    }

}
