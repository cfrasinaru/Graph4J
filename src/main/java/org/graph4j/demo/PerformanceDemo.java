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
package org.graph4j.demo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
abstract class PerformanceDemo {

    protected static final String GRAPH4J = "Graph4J";
    protected static final String JGRAPHT = "JGraphT";
    protected static final String JGRAPHT_F = "JGraphT-F";
    protected static final String GUAVA = "Guava";
    protected static final String JUNG = "JUNG";
    protected static final String ALGS4 = "ALGS4";
    protected static final String OTHER = "Other";

    protected Graph graph;

    protected org.jgrapht.Graph jgrapht;
    protected org.jgrapht.Graph jgraphf; //FastUtil

    protected com.google.common.graph.MutableGraph guavaGraph;
    protected com.google.common.graph.MutableValueGraph guavaValueGraph;
    protected com.google.common.graph.MutableNetwork guavaNetwork;
    
    protected edu.uci.ics.jung.graph.Graph jungGraph;
    //
    protected edu.princeton.cs.algs4.Graph algs4Graph;
    protected edu.princeton.cs.algs4.Digraph algs4Digraph;
    protected edu.princeton.cs.algs4.EdgeWeightedDigraph algs4Ewd;
    protected edu.princeton.cs.algs4.EdgeWeightedGraph algs4Ewg;
    protected edu.princeton.cs.algs4.AdjMatrixEdgeWeightedDigraph adjMatrixEwd;
    protected edu.princeton.cs.algs4.FlowNetwork algs4Net;
    //
    protected boolean runGraph4J = true;
    protected boolean runJGraphT = false;
    protected boolean runJGraphF = false;
    protected boolean runGuava = false;
    protected boolean runJung = false;
    protected boolean runAlgs4 = false;
    protected boolean runOther = false;

    protected static final String EOL = System.lineSeparator();
    protected static final String TAB = "\t";

    protected int numVertices; //number of vertices, for single run only
    protected int[] args; //arguments: number of vertices, degrees
    protected Map<String, List<Long>> timeMap = new HashMap<>();
    protected Map<String, List<Long>> memoryMap = new HashMap<>();
    protected Random random = new Random();

    public void singleRun(Runnable snippet, String key) {
        run(snippet, true, key);
    }

    public void run(Runnable snippet, String key) {
        run(snippet, false, key);
    }

    protected void run(Runnable snippet, boolean singleRun, String key) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long runningTime = System.currentTimeMillis() - t0;
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024);
        if (singleRun) {
            printResult(key, runningTime, memoryIncrease);
            return;
        }
        var timeList = timeMap.get(key);
        if (timeList == null) {
            timeList = new ArrayList<>(100);
            timeMap.put(key, timeList);
        }
        var memoryList = memoryMap.get(key);
        if (memoryList == null) {
            memoryList = new ArrayList<>(100);
            memoryMap.put(key, memoryList);
        }
        timeList.add(runningTime);
        memoryList.add(memoryIncrease);
    }

    public void clear() {
        timeMap.clear();
        memoryMap.clear();
    }

    private void printResult(String key, long time, long memory) {
        var sb = new StringBuilder();
        if (key != null) {
            sb.append(Tools.padRight(key, 20));
        }
        sb.append(Tools.padLeft(time + " ms", 10));
        sb.append(Tools.padLeft(memory + " MB", 10)).append(EOL);
        String result = sb.toString();
        System.out.print(result);
        /*
        try {
            Files.writeString(Paths.get("results.txt"), result, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e);
        }*/
    }

    protected void createGraph() {
    }

    protected void prepareGraphs() {
        if (graph == null) {
            return;
        }
        if (runJGraphT) {
            jgrapht = Converter.createJGraphT(graph);
        }
        if (runJGraphF) {
            jgraphf = Converter.createJGraphF(graph);
        }
        if (runJung) {
            jungGraph = Converter.createJungGraph(graph);
        }
        if (runGuava) {
            if (graph.isEdgeWeighted()) {
                guavaValueGraph = Converter.createGuavaValueGraph(graph);
            } else {
                guavaGraph = Converter.createGuavaGraph(graph);
                //guavaNetwork = Converter.createGuavaNetwork(graph);
            }
        }
        if (runAlgs4) {
            if (graph.isEdgeWeighted()) {
                if (graph.isDirected()) {
                    algs4Ewd = Converter.createAlgs4EdgeWeightedDigraph(graph);
                    algs4Net = Converter.createAlgs4Network(graph);
                } else {
                    algs4Ewg = Converter.createAlgs4EdgeWeightedGraph(graph);
                }
                adjMatrixEwd = Converter.createAlgs4AdjMatrixEwd(graph);
            } else {
                if (graph.isDirected()) {
                    algs4Digraph = Converter.createAlgs4Digraph(graph);
                } else {
                    algs4Graph = Converter.createAlgs4Graph(graph);
                }
            }
        }
    }

    protected void testGraph4J() {
    }

    protected void testJGraphT() {
    }

    protected void testJGraphF() {
    }

    protected void testAlgs4() {
    }

    protected void testJung() {
    }

    protected void testGuava() {
    }

    protected void testOther() {
    }
    
    protected void demo() {
        System.out.println(this.getClass().getSimpleName() + " " + Tools.formatTimestamp(new Date()));
        //without warmup, this is not benchmarked
        runAll(true);
    }

    protected void prepareArgs() {
        throw new UnsupportedOperationException();
    }

    protected void beforeRun(int step) {
        numVertices = args[step];
    }

    public void benchmark() {
        prepareArgs();
        System.out.println("Warming up");
        for (int k = 1; k < 10; k++) {
            beforeRun(0);
            runAll(false);
        }
        System.out.println();
        clear();
        for (int i = 0; i < args.length; i++) {
            System.out.println(i);
            beforeRun(i);
            runAll(false);
        }
        writeToFile(timeMap, "time");
        writeToFile(memoryMap, "memory");
    }

    protected void runAll(boolean singleRun) {
        singleRun(this::createGraph, "Create");
        singleRun(this::prepareGraphs, "Prepare");
        if (runGraph4J) {
            run(this::testGraph4J, singleRun, GRAPH4J);
        }
        if (runGuava) {
            run(this::testGuava, singleRun, GUAVA);
        }
        if (runJung) {
            run(this::testJung, singleRun, JUNG);
        }
        if (runJGraphT) {
            run(this::testJGraphT, singleRun, JGRAPHT);
        }
        if (runJGraphF) {
            run(this::testJGraphF, singleRun, JGRAPHT_F);
        }
        if (runAlgs4) {
            run(this::testAlgs4, singleRun, ALGS4);
        }
        if (runOther) {
            run(this::testOther, singleRun, OTHER);
        }
    }

    protected void writeToFile(Map<String, List<Long>> map, String type) {
        try (java.io.PrintWriter br = new PrintWriter("results/" + this.getClass().getSimpleName() + "-" + type + ".csv")) {
            var header = new StringBuilder();
            header.append("Args").append(", ").append(GRAPH4J);
            if (runJGraphT) {
                header.append(", ").append(JGRAPHT);
            }
            if (runGuava) {
                header.append(", ").append(GUAVA);
            }
            if (runJung) {
                header.append(", ").append(JUNG);
            }
            br.println(header.toString());
            for (int i = 0; i < args.length; i++) {
                br.print(args[i]);
                br.print(", ");
                br.print(map.get(GRAPH4J).get(i));
                if (runJGraphT) {
                    br.print(", ");
                    br.print(map.get(JGRAPHT).get(i));
                }
                if (runGuava) {
                    br.print(", ");
                    br.print(map.get(GUAVA).get(i));
                }
                if (runJung) {
                    br.print(", ");
                    br.print(map.get(JUNG).get(i));
                }
                br.print(EOL);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

}
