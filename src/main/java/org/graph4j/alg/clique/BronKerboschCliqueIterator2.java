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
//@Deprecated
//public class BronKerboschCliqueIterator2 extends SimpleGraphAlgorithm
//        implements MaximalCliqueIterator {
//
//    //private final Deque<VertexSet> cliqueStack;
//    private final Deque<VertexSet> subgStack;
//    private final Deque<VertexSet> candStack;
//    private Clique currentClique;
//    private Clique workingClique;
//
//    public BronKerboschCliqueIterator2(Graph graph) {
//        super(graph);
//        //
//        int n = graph.numVertices();
//        subgStack = new ArrayDeque<>(n);
//        candStack = new ArrayDeque<>(n);
//        //
//        subgStack.push(new VertexSet(graph, graph.vertices()));
//        candStack.push(new VertexSet(graph, graph.vertices()));
//        //subg = cand U fini
//        //        
//        workingClique = new Clique(graph);
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
//        while (!subgStack.isEmpty()) {
//            var subg = subgStack.peek();
//            var cand = candStack.peek();
//            /*
//            System.out.println("Popped");
//            System.out.println("\tcandidates: " + subg);
//            System.out.println("\tfinished: " + cand);
//             */
//            if (subg.isEmpty()) {
//                subgStack.pop();
//                candStack.pop();
//                currentClique = workingClique;
//                workingClique = new Clique(workingClique);
//                workingClique.pop();
//                assert currentClique.isValid();
//                return true;
//            }
//
//            int u = subg.peek();
//            var tempCand = new VertexSet(cand);
//            tempCand.removeAll(graph.neighbors(u));
//
//            if (!tempCand.isEmpty()) {
//                int v = tempCand.peek();
//                workingClique.add(v);
//                var neighbors = graph.neighbors(v);
//                subgStack.push(subg.intersection(neighbors));
//                candStack.push(cand.intersection(neighbors));
//            }
//
//        }
//        return false;
//    }
//
//}
