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
import org.graph4j.connectivity.StoerWagnerMinimumCut1;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.RegularGraphGenerator;

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
        
        /*
        //var g = GraphGenerator.randomGnp(1000, 0.1); 
        var g = new RegularGraphGenerator(100, 3).createGraph();
        EdgeWeightsGenerator.randomDoubles(g, 0, 1);
        g = GraphUtils.shuffle(g);
        var alg = new StoerWagnerMinimumCut1(g);
        System.out.println(alg.getMinimumCutWeight());
        */
        

        //int n = 10;
        //var g = new RandomGnpGraphGenerator(IntArrays.createMockVertices(n, 100), 0.01).createGraph();
        //var g = new RandomUnitDiskGenerator(n, 0.1).createGraph();
        //var g = new RandomLayeredGenerator(100, 10, 50, 0.5, 0.5, x -> Math.exp(x/2) - 1).createGraph();
        //var g = new RandomLayeredGenerator(300, 10, 20, 0.5, 0.5, x -> x / 2).createGraph();
        //System.out.println(g);
        //System.out.println(GraphTests.isConnected(g));
        //var g = new RandomKNNGenerator(n, 5).createGraph();
        //var g = new GridGenerator(1000, 10).createGraph();
        //var g = GraphGenerator.cycle(n);        
        //var g = GraphGenerator.path(n);
        // var g = GraphGenerator.regular(10_000, 5);
        //var g = new WattsStrogatzGenerator(n, 6, 0.1, 0).createGraph();
        //EdgeWeightsGenerator.fill(g, 1);        
        //EdgeWeightsGenerator.randomDoubles(g, 0, 1);
        //var g = new CompleteTreeGenerator(5, 3).create();
        //System.out.println(g);
        //var alg = new StoerWagnerMinimumCut1(g);
        //System.out.println(alg.getMinimumCutWeight());
        //System.out.println(alg.getMinimumCut());
        /*
        int n = 100;
        for(int i=0; i<100; i++) {
            var g = GraphGenerator.randomGnp(n, Math.random());
            EdgeWeightsGenerator.randomIntegers(g, 1, n);
            var alg0 = new StoerWagnerMinimumCut(g);
            var alg1 = new StoerWagnerMinimumCut1(g);
            double x = alg0.getMinimumCutWeight();
            double y = alg1.getMinimumCutWeight();
            if (x != y) {
                System.out.println("OOPS: " + x + " != " + y);
                System.out.println(g);
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
