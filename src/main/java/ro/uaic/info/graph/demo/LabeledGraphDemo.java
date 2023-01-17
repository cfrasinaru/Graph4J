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

import ro.uaic.info.graph.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class LabeledGraphDemo extends PerformanceDemo {

    int n = 2000;
    private City[] cities;
    private Road[][] roads;

    @Override
    protected void prepare() {
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
        var g = new GraphBuilder<City, Road>().labeledVertices(cities).buildGraph();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addLabeledEdge(cities[i], cities[j], roads[i][j]);
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                //g.edge(cities[v],cities[u]);
                g.findSingleEdge(roads[i][j]);
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
                g.getEdgeSource(roads[i][j]);
                g.getEdgeTarget(roads[i][j]);
            }
        }
    }

    public static void main(String args[]) {
        var app = new LabeledGraphDemo();
        app.demo();
    }

}
