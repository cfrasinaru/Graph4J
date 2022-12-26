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

import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.EmptyGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphCreationDemo extends PerformanceDemo {

    private int n = 5_000;
    private double p = 0.05;
    private int m = (int) (p * (n * (n - 1) / 2));
    private int avgDegree = 20;

    @Override
    protected void prepare() {
    }

    @Override
    protected void test1() {
        //empty1();
        //sparse1();
        complete1();
        //random1();
    }

    @Override
    protected void test2() {
        //empty2();
        //sparse2();
        complete2();
        //random2();
    }

    protected void empty1() {
        GraphGenerator.empty(n);
    }

    protected void empty2() {
        new EmptyGraphGenerator(n).generateGraph(Tools.createJGraph(null));
    }
    
    protected void complete1() {
        new CompleteGenerator(n).createGraph();
    }

    protected void complete2() {
        new CompleteGraphGenerator(n).generateGraph(Tools.createJGraph(null));
    }

    protected void sparse1() {
        var g = GraphBuilder.numVertices(n).avgDegree(avgDegree).buildGraph();
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
    }

    private void sparse2() {
        var g = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        for (int v = 0; v < n; v++) {
            g.addVertex(v);
        }
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
    }

    protected void random1() {
        var g = new GnpRandomGenerator(n, p).createGraph();
        //var g = new GnmRandomGenerator(n, m).createGraph();
    }

    protected void random2() {
        var gnp = new GnpRandomGraphGenerator<Integer, DefaultEdge>(n, p);
        gnp.generateGraph(Tools.createJGraph(null));
        //var gnp = new GnmRandomGraphGenerator<Integer, DefaultEdge>(n, m);
    }

    public static void main(String args[]) {
        var app = new GraphCreationDemo();
        app.demo();
    }

}
