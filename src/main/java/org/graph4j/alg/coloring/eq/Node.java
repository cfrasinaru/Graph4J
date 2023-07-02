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
//package org.graph4j.alg.coloring.eq;
//
//import org.graph4j.util.Domain;
//import java.util.Arrays;
//import org.graph4j.Graph;
//import org.graph4j.alg.coloring.Coloring;
//
///**
// * A node in the backtrack systematic search tree - for equitable coloring.
// *
// * @author Cristian Frăsinaru
// */
//class Node {
//
//    Graph graph;
//    boolean findAll;
//    Coloring coloring;
//    int maxClassCount; //how many coloring classes are of the largest size
//    int maxClassSize; // how big a coloring class can be
//    Domain[] domains;
//    Domain minDomain;
//    Node parent;
//
//    public Node(Graph graph, Node parent, Domain[] domains, Coloring coloring, boolean findAll) {
//        this.graph = graph;
//        this.parent = parent;
//        this.domains = domains;
//        this.coloring = coloring;
//        this.findAll = findAll;
//        if (parent != null) {
//            this.maxClassSize = parent.maxClassSize;        
//            this.maxClassCount = parent.maxClassCount;
//        }
//    }
//
//    public void prepare() {
//        //find the domain with the minimum size
//        int minSize = Integer.MAX_VALUE;
//        minDomain = null;
//        for (var dom : domains) {
//            int v = dom.vertex();
//            if (coloring.isColorSet(v)) {
//                continue;
//            }
//            if (dom.size() < minSize) {
//                minSize = dom.size();
//                minDomain = dom;
//            } else if (dom.size() == minSize) {
//                if (graph.degree(minDomain.vertex()) < graph.degree(dom.vertex())) {
//                    minDomain = dom;
//                }
//            }
//        }
//        if (minDomain != null) {
//            //System.out.println("Selected " + minDomain.vertex() + ": " + minDomain);
//            minDomain = new Domain(minDomain);
//            this.domains[graph.indexOf(minDomain.vertex())] = minDomain;
//            //remove symmetrical colors from minDomain
//            if (!findAll) {
//                removeSymmetricalColors();           
//            }
//            //trace();
//        }
//    }
//
//    private void trace() {
//        var sb = new StringBuilder();
//        var temp = this;
//        while (temp != null) {
//            sb.append(temp.minDomain.vertex()).append(" - ");
//            temp = temp.parent;
//        }
//        System.out.println(sb.reverse());
//    }
//
//    //remove from minDomain all colors that have not been used before
//    //except one (free color)
//    private void removeSymmetricalColors() {
//        int free = -1;
//        int i = 0;
//        int count = 0;
//        while (i < minDomain.size()) {
//            int c = minDomain.valueAt(i);
//            if (!coloring.isColorUsed(c)) {
//                //if (coloring.uncoloredNeighbors(c) == 0) {
//                if (free >= 0) {
//                    minDomain.removeAtPos(i);
//                    count++;
//                } else {
//                    free = i;
//                }
//            }
//            i++;
//        }
//        //put the free color so it will be chosen last
//        if (free > 0) {
//            minDomain.swapPos(0, free);
//        }
//    }
//
//    @Override
//    public String toString() {
//        return (minDomain == null ? "" : minDomain.vertex())
//                + "\n\t" + Arrays.toString(domains) + "\n\t" + coloring;
//    }
//}
