/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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

import java.util.function.Supplier;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.gen.WheelGenerator;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) {
        var app = new Main();
        app.test();
        //app.demoContains();
    }

    public static void printObjectSize(Object object) {
        //System.out.println("Object type: " + object.getClass() + ", size: " + InstrumentationAgent.getObjectSize(object) + " bytes");
    }

    private void test() {
        var g = new WheelGenerator(5,9).createGraph();
        //g.setName("K4");
        System.out.println(g);
    }

    private void demoMem() {
        int n = 1_000;
        long mem0 = Runtime.getRuntime().freeMemory();
        var g = GraphBuilder.vertices(1, 2, 100_000_000).buildGraph();
        //var g = RandomGenerator.createGraphGnp(n, 0.1);
        System.out.println(g);
        long mem1 = Runtime.getRuntime().freeMemory();
        System.out.println((mem0 - mem1) / (1024 * 1024) + " MB");
        //printObjectSize(g);
    }

    private void demoContains() {
        int n = 10_000;
        var g = GraphBuilder.numVertices(n).complete().buildGraph();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.containsEdge(g.vertexAt(i), g.vertexAt(j));
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + " ms");
        System.out.println(g);
        System.out.println((int) g.numEdges());
    }

    private void demoRandom() {
        int n = 500;
        double p = 0.3;
        int m = (int) (p * (n * (n - 1) / 2));
        long t0 = System.currentTimeMillis();
        //var g = Graphs.randomGnp(n, p);
        //var g = Graphs.randomGnm(n, m);
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");

        t0 = System.currentTimeMillis();
        Supplier<Integer> vSupplier = new Supplier<Integer>() {
            private int id = 0;

            @Override
            public Integer get() {
                return id++;
            }
        };
        var jg = new org.jgrapht.graph.SimpleGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        //var gnp = new GnpRandomGraphGenerator<Integer, DefaultEdge>(n, p);
        var gnp = new GnmRandomGraphGenerator<Integer, DefaultEdge>(n, m);
        gnp.generateGraph(jg);
        //System.out.println(jg);
        t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");
    }

}
