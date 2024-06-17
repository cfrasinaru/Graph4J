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
package org.graph4j;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 *
 * @author Cristian Frăsinaru
 */
class Main {

    public static void main(String[] args) throws FileNotFoundException {
        var app = new Main();
        app.demo();
    }

    private void demo() {
        run(this::test);
    }

    private void test() {

        Graph g = GraphBuilder.empty().estimatedNumVertices(5).buildGraph();

        g.addVertex(); // v = 0
        g.addVertex(); // v = 1
        g.addVertex(); // v = 2
        g.addVertex(); // v = 3
        g.addVertex(); // v = 4

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);

        System.out.println("edges: " + Arrays.toString(g.edges()));

        g.removeVertex(2);

        System.out.println("edges: " + Arrays.toString(g.edges()));

        g.addVertex(); // v = 5

        System.out.println("edges: " + Arrays.toString(g.edges()));
        //var g = GraphGenerator.mycielski(5);
        //System.out.println(g);
        //[0-1, 0-4, 0-6, 0-9, 1-2, 1-5, 1-7, 2-3, 2-6, 2-8, 3-4, 3-7, 3-9, 4-5, 4-8, 5-10, 6-10, 7-10, 8-10, 9-10]}
        //var g = NetworkBuilder.edges("0-1, 0-4, 0-6, 0-9, 1-2, 1-5, 1-7, 2-3, 2-6, 2-8, 3-4, 3-7, 3-9, 4-5, 4-8, 5-10, 6-10, 7-10, 8-10, 9-10").buildNetwork();
        //new EdgeDataGenerator(g, CAPACITY).fill(Double.POSITIVE_INFINITY);

        /*
        int n = 2000;
        Network g = new RandomGnpGraphGenerator(n, 0.5).createNetwork();
        var alg1 = new DinicMaximumFlow(g);
        new EdgeDataGenerator(g, CAPACITY).randomDoubles(0, 1);
        //new EdgeDataGenerator(g, CAPACITY).randomIntegers(0, n);
        alg1.computeMaximumFlow();
         */
 /*
        int n = 1000;
        for (int i = 0; i < 100; i++) {
            Network g = new RandomGnpGraphGenerator(n, Math.random()).createNetwork();
            new EdgeDataGenerator(g, CAPACITY).randomIntegers(0, n);
            var alg1 = new EdmondsKarpMaximumFlow(g);
            var alg2 = new DinicMaximumFlow(g);
            //var alg2 = new EdmondsKarpMaximumFlow1(g);
            //var alg2 = new PushRelabelMaximumFlow(g);
            //var alg1 = new EdmondsKarpMaximumFlow1(g, 0, n-1);
            //var alg2 = new PushRelabelMaximumFlow1(g, 0, n-1);
            double x = alg1.getMaximumFlowValue();
            double y = alg2.getMaximumFlowValue();
            if (x != y) {
                System.out.println("OOPS: " + g);
                System.out.println(x);
                System.out.println(y);
                break;
            }
        }*/
    }

    protected void run(Runnable snippet) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;
        System.out.println((t1 - t0) + " ms");
        System.out.println(memoryIncrease / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }

}
