package org.graph4j.route;

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
//package org.graph4j.alg;
//
//import org.graph4j.Graph;
//import org.graph4j.util.Cycle;
//import org.graph4j.util.Path;
//
///**
// * Computes the maximum induced cycle.
// *
// *
// * @author Cristian Frăsinaru
// */
//public class MaximumInducedCycle extends GraphAlgorithm {
//
//    private Path currentPath;
//    private Cycle maxCycle;
//
//    public MaximumInducedCycle(Graph graph) {
//        super(graph);
//    }
//
//    /**
//     *
//     * @return the maximum length induced cycle, or {@code null} if none exists.
//     */
//    public Cycle findCycle() {
//        if (maxCycle == null) {
//            this.currentPath = new Path(graph);
//            currentPath.add(0);
//            findRec();
//        }
//        return maxCycle;
//    }
//
//    private void findRec() {
//        int k = currentPath.numVertices();
//        int last = currentPath.get(k - 1);
//        next:
//        for (var it = graph.neighborIterator(last); it.hasNext();) {
//            int u = it.next();
//            if (currentPath.contains(u)) {
//                if (k >= 2 && u != currentPath.get(k - 2)) {
//                    checkCycle(u);
//                }
//                continue;
//            }
//            //TODO
//            /*
//            for (int w : currentPath.vertices()) {
//                if (w != last && graph.containsEdge(u, w)) {
//                    continue next;
//                }
//            }
//            */
//            currentPath.add(u);
//            findRec();
//            currentPath.removeFromPos(k);
//        }
//    }
//
//    //u is a neighbor of v=the last vertex in path 
//    //u belongs to the path
//    //the cycle is between u and v, but it may not be induced
//    private void checkCycle(int u) {
//        int pos = currentPath.indexOf(u);
//        int k = currentPath.numVertices();
//        int v = currentPath.get(k - 1);
//        if (maxCycle == null || maxCycle.length() < k - pos) {
//            Cycle temp = new Cycle(graph);
//            for (int i = pos; i < k; i++) {
//                int w = currentPath.get(i);
//                if (i > pos && i < k - 2 && graph.containsEdge(v, w)) {
//                    return;
//                }
//                temp.add(w);
//            }
//            maxCycle = temp;
//            assert maxCycle.isValid() && maxCycle.isInduced();
//        }
//    }
//
//}
