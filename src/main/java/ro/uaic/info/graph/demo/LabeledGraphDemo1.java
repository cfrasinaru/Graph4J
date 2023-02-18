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

import com.google.common.graph.MutableNetwork;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class LabeledGraphDemo1 extends PerformanceDemo {

    int n = 3000;
    private City[] cities;
    private Road[][] roads;

    public LabeledGraphDemo1() {
        numVertices = 3000;
        runJGraphT = true;
        runGuava = true;
        runJung = true;
    }

    @Override
    protected void prepareGraphs() {
        cities = new City[n];
        for (int i = 0; i < n; i++) {
            cities[i] = new City("City" + i);
        }
        roads = new Road[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                roads[i][j] = roads[j][i] = new Road("Road" + i + "-" + j);
            }
        }
    }

    @Override
    protected void testGraph4J() {
        Graph<City, Road> g = GraphBuilder.labeledVertices(cities).buildGraph();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addEdge(cities[i], cities[j], roads[i][j]);
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                //g.edge(cities[v],cities[u]);
                g.findEdge(roads[i][j]);
            }
        }
    }

    @Override
    protected void testJGraphT() {
        var g = new org.jgrapht.graph.SimpleGraph<City, Road>(Road.class);
        for (int v = 0; v < n; v++) {
            g.addVertex(cities[v]);
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addEdge(cities[i], cities[j], roads[i][j]);
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                //g.getEdge(cities[v],cities[u]);
                var v = g.getEdgeSource(roads[i][j]);
                var u = g.getEdgeTarget(roads[i][j]);
                g.getEdge(v, u);
            }
        }
    }

    @Override
    protected void testGuava() {
        MutableNetwork<City, Road> g = com.google.common.graph.NetworkBuilder
                .undirected()
                .expectedNodeCount(n).build();
        for (int i = 0; i < n; i++) {
            g.addNode(cities[i]);
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addEdge(cities[i], cities[j], roads[i][j]);
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                var pair = g.incidentNodes(roads[i][j]);
                g.edgeConnecting(pair).orElse(null);
            }
        }
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.SparseGraph<City, Road>();
        for (int i = 0; i < n; i++) {
            g.addVertex(cities[i]);
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addEdge(roads[i][j], cities[i], cities[j]);
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                var pair = g.getEndpoints(roads[i][j]);
                g.findEdge(pair.getFirst(), pair.getSecond());
            }
        }
    }

    public static void main(String args[]) {
        var app = new LabeledGraphDemo1();
        app.demo();
    }

}
