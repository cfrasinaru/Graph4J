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
package ro.uaic.info.graph.demo;

import com.google.common.graph.MutableValueGraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class LabeledGraphDemo extends PerformanceDemo {

    private City[] cities;
    private Road[][] roads;

    public LabeledGraphDemo() {
        numVertices = 1000;
        runJGraphT = true;
        runGuava = true;
        runJung = true;
    }

    @Override
    protected void prepareGraphs() {
        createObjects(numVertices);
    }

    @Override
    protected void beforeRun(int step) {
        super.beforeRun(step);
        int n = args[step];
        createObjects(n);
    }

    private void createObjects(int n) {
        cities = new City[n];
        for (int i = 0; i < n; i++) {
            cities[i] = new City("City " + i);
        }
        roads = new Road[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                roads[i][j] = roads[j][i] = new Road("Road " + i + "-" + j);
            }
        }
    }

    @Override
    protected void testGraph4J() {
        Graph<City, Road> g = GraphBuilder.empty()
                .estimatedNumVertices(numVertices)
                .estimatedAvgDegree(numVertices - 1)
                .buildGraph();
        for (int i = 0; i < numVertices; i++) {
            g.addVertex(i, cities[i]);
        }
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                g.addEdge(cities[i], cities[j], roads[i][j]);
                //g.addLabeledEdge(i, j, roads[i][j]);
            }
        }
    }

    @Override
    protected void testJGraphT() {
        var g = new org.jgrapht.graph.SimpleGraph<City, Road>(Road.class);
        for (int i = 0; i < numVertices; i++) {
            g.addVertex(cities[i]);
        }
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                g.addEdge(cities[i], cities[j], roads[i][j]);
            }
        }
    }

    @Override
    protected void testGuava() {
        MutableValueGraph<City, Road> g = com.google.common.graph.ValueGraphBuilder
                .undirected()
                .expectedNodeCount(numVertices).build();
        for (int i = 0; i < numVertices; i++) {
            g.addNode(cities[i]);
        }
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                g.putEdgeValue(cities[i], cities[j], roads[i][j]);
            }
        }
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.UndirectedSparseGraph<City, Road>();
        for (int i = 0; i < numVertices; i++) {
            g.addVertex(cities[i]);
        }
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                g.addEdge(roads[i][j], cities[i], cities[j]);
            }
        }
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 500 * (i + 1);
        }
    }

}
