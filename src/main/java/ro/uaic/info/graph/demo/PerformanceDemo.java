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

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class PerformanceDemo {

    protected Graph graph;
    protected org.jgrapht.Graph jgraph;
    protected com.google.common.graph.Graph guavaGraph;
    protected edu.uci.ics.jung.graph.SparseGraph jungGraph;
    //
    protected edu.princeton.cs.algs4.Graph algs4Graph;
    protected edu.princeton.cs.algs4.EdgeWeightedDigraph algs4Ewd;
    protected edu.princeton.cs.algs4.AdjMatrixEdgeWeightedDigraph adjMatrixEwd;
    //
    protected boolean runGraph4J = true;
    protected boolean runJGraphT = false;
    protected boolean runGuava = false;
    protected boolean runJung = false;
    protected boolean runAlgs4 = false;

    protected void run(Runnable snippet, String info) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;
        System.out.println(info);
        System.out.println((t1 - t0) + " ms");
        System.out.println(memoryIncrease / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }

    protected void createGraph() {
    }

    protected void prepare() {
        if (graph == null) {
            return;
        }
        if (runJGraphT) {
            jgraph = Tools.createJGraph(graph);
        }
        if (runJung) {
            jungGraph = Tools.createJungGraph(graph);
        }
        if (runGuava) {
            guavaGraph = Tools.createGuavaGraph(graph);
        }
        if (runAlgs4) {
            algs4Graph = Tools.createAlgs4Graph(graph);
        }
    }

    protected abstract void testGraph4J();

    protected void testJGraphT() {
    }

    protected void testAlgs4() {
    }

    protected void testJung() {
    }

    protected void testGuava() {
    }

    protected void demo() {
        run(this::createGraph, "Create graph");
        run(this::prepare, "Prepare other");
        if (runGraph4J) {
            run(this::testGraph4J, "Graph4J");
        }
        if (runGuava) {
            run(this::testGuava, "Guava");
        }
        if (runJung) {
            run(this::testJung, "JUNG");
        }
        if (runJGraphT) {
            run(this::testJGraphT, "JGraphT");
        }
        if (runAlgs4) {
            run(this::testAlgs4, "Algs4");
        }
    }

}
