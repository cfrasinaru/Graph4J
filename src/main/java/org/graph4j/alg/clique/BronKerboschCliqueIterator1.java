///*
// * Copyright (C) 2023 Cristian Frăsinaru and contributors
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.graph4j.alg.clique;
//
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.NoSuchElementException;
//import org.graph4j.Graph;
//import org.graph4j.alg.SimpleGraphAlgorithm;
//import org.graph4j.util.Clique;
//import org.graph4j.util.VertexSet;
//
///**
// *
// *
// * @author Cristian Frăsinaru
// */
//public class BronKerboschCliqueIterator1 extends SimpleGraphAlgorithm
//        implements MaximalCliqueIterator {
//
//    //private final Deque<Clique> cliqueStack;
//    private final Deque<VertexSet> candidatesStack;
//    private final Deque<VertexSet> finishedStack;
//    private Clique workingClique;
//    private Clique currentClique;
//    private final boolean debug = false;
//
//    public BronKerboschCliqueIterator1(Graph graph) {
//        super(graph);
//        //
//        int n = graph.numVertices();
//        //cliqueStack = new ArrayDeque<>(n);
//        candidatesStack = new ArrayDeque<>(n);
//        finishedStack = new ArrayDeque<>(n);
//        //
//        //cliqueStack.push(new Clique(graph));
//        workingClique = new Clique(graph);
//        candidatesStack.push(new VertexSet(graph, graph.vertices()));
//        finishedStack.push(new VertexSet(graph));
//    }
//
//    @Override
//    public Clique next() {
//        if (currentClique != null) {
//            var temp = currentClique;
//            currentClique = null;
//            return temp;
//        }
//        if (hasNext()) {
//            return currentClique;
//        }
//        throw new NoSuchElementException();
//    }
//
//    @Override
//    public boolean hasNext() {
//        if (currentClique != null) {
//            return true;
//        }
//        while (true) {
//            //var clique = cliqueStack.peek();
//            var candidates = candidatesStack.peek();
//            var finished = finishedStack.peek();
//            if (debug) {
//                System.out.println("Current clique: " + workingClique);
//                System.out.println("Pop Candidates: " + candidates);
//                System.out.println("Pop Finished: " + finished);
//            }
//
//            if (candidates.isEmpty()) {
//                //cliqueStack.pop();
//                candidatesStack.pop();
//                finished = finishedStack.pop();
//                if (finished.isEmpty()) {
//                    currentClique = workingClique;
//                    workingClique = new Clique(workingClique);
//                    workingClique.pop();
//                    assert currentClique.isValid();
//                    return true;
//                }
//                workingClique.pop();
//                continue;
//            }
//
//            int v = candidates.peek();
//            var neighbors = graph.neighbors(v);
//
//            //var newClique = clique.union(v);
//            workingClique.add(v);
//            var newCandidates = candidates.intersection(neighbors);
//            var newFinished = finished.intersection(neighbors);
//            
//            boolean connected = false;
//            over:
//            for (int f : newFinished.vertices()) {
//                connected = true;
//                for (int c : newCandidates.vertices()) {
//                    if (!graph.containsEdge(f, c)) {
//                        connected = false;
//                        break;
//                    }
//                }
//                if (connected) break;
//            }
//            if (!connected) {
//                //cliqueStack.push(newClique);
//                candidatesStack.push(newCandidates);
//                finishedStack.push(newFinished);
//
//                if (debug) {
//                    //System.out.println("\tPush Clique: " + newClique);
//                    System.out.println("\tPush Candidates: " + newCandidates);
//                    System.out.println("\tPush Finished: " + newFinished);
//                }
//            }
//
//            candidates.pop();
//            finished.add(v);
//        }
//        return false;
//    }
//
//}
