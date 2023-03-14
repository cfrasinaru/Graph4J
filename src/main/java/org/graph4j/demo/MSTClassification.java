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
package org.graph4j.demo;

import org.graph4j.alg.mst.KruskalMinimumSpanningTree;
import org.graph4j.alg.mst.PrimMinimumSpanningTreeHeap;
import org.graph4j.alg.mst.PrimMinimumSpanningTreeDefault;
import org.graph4j.Graph;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnmGraphGenerator;

/**
 * PrimHeap wins almost always.
 *
 * @author Cristian Frăsinaru
 */
class MSTClassification {

    public static void main(String args[]) {
        test2();
    }

    private static void test2() {
        int n = 50_000;
        int m = 5 * n;
        long max = n * (n - 1) / 2;
        double density = (double) m / max;
        Graph g = new RandomGnmGraphGenerator(n, m).createGraph();
        EdgeWeightsGenerator.randomDoubles(g, 0, 1);
        long t0 = System.nanoTime();
        new PrimMinimumSpanningTreeDefault(g).getWeight();
        long t1 = System.nanoTime();
        new PrimMinimumSpanningTreeHeap(g).getWeight();
        long t2 = System.nanoTime();
        new KruskalMinimumSpanningTree(g).getWeight();
        long t3 = System.nanoTime();
        long prim1 = t1 - t0;
        long prim2 = t2 - t1;
        long kruskal = t3 - t2;
        if (prim1 < prim2 && prim1 < kruskal) {
            System.out.println(density + ": Prim Default");
        } else if (prim2 < prim1 && prim2 < kruskal) {
            System.out.println(density + ": Prim Heap");
        } else if (kruskal < prim1 && kruskal < prim2) {
            System.out.println(density + ": Kruskal");
        } else {
            System.out.println(density + ": Tie");
        }
    }

    private static void test1() {
        int n = 1000;
        int max = n * (n - 1) / 2;
        for (int m = n; m <= max; m += n) {
            double density = (double) m / max;
            Graph g = new RandomGnmGraphGenerator(n, m).createGraph();
            EdgeWeightsGenerator.randomDoubles(g, 0, 1);
            long t0 = System.nanoTime();
            new PrimMinimumSpanningTreeDefault(g).getWeight();
            long t1 = System.nanoTime();
            new PrimMinimumSpanningTreeHeap(g).getWeight();
            long t2 = System.nanoTime();
            new KruskalMinimumSpanningTree(g).getWeight();
            long t3 = System.nanoTime();
            long prim1 = t1 - t0;
            long prim2 = t2 - t1;
            long kruskal = t3 - t2;
            if (prim1 < prim2 && prim1 < kruskal) {
                System.out.println(density + ": Prim Default");
            } else if (prim2 < prim1 && prim2 < kruskal) {
                System.out.println(density + ": Prim Heap");
            } else if (kruskal < prim1 && kruskal < prim2) {
                System.out.println(density + ": Kruskal");
            } else {
                System.out.println(density + ": Tie");
            }
        }
    }
}
