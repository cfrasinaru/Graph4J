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
import org.graph4j.generate.GraphGenerator;

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
        int n = 20;
        double p = 0.2;
        var g = GraphGenerator.randomGnp(n, p);
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/" + "queen8_8.col");
        //var g = GraphBuilder.numVertices(5).addEdges("0-1,1-2,2-3,3-4").buildGraph();
        //var g = GraphBuilder.numVertices(5).addEdges("0-1,0-2,1-2,2-3,2-4,3-4").buildGraph();
        //var g = GraphBuilder.numVertices(10).addClique(0,1,2,3,4,5).addClique(0,6,7,8,9).buildGraph();
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
