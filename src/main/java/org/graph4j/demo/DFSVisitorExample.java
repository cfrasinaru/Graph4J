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

import org.graph4j.GraphBuilder;
import org.graph4j.traverse.DFSVisitor;
import org.graph4j.traverse.DFSTraverser;
import org.graph4j.traverse.SearchNode;

/**
 *
 * @author Cristian Frăsinaru
 */
class DFSVisitorExample {

    private void demoDFS() {
        var g = GraphBuilder.vertexRange(1, 8)
                .addPath(1, 2, 4, 6).addEdge(6, 2)
                .addPath(1, 3, 5, 7).addEdge(5, 4).addEdge(5, 8).addEdge(1, 8)
                .buildDigraph();
        System.out.println(g);
        new DFSTraverser(g).traverse(new DFSVisitor() {
            @Override
            public void startVertex(SearchNode node) {
                System.out.println((node.order() == 0 ? "Root: " : "Start vertex: ") + node);
            }

            @Override
            public void treeEdge(SearchNode from, SearchNode to) {
                System.out.println("Tree edge: " + from + "->" + to);
            }

            @Override
            public void backEdge(SearchNode from, SearchNode to) {
                System.out.println("Back edge: " + from + "->" + to);
                System.out.println("Cycle detected");
            }

            @Override
            public void forwardEdge(SearchNode from, SearchNode to) {
                System.out.println("Forward edge: " + from + "->" + to);
            }

            @Override
            public void crossEdge(SearchNode from, SearchNode to) {
                System.out.println("Cross edge: " + from + "->" + to);
            }

            @Override
            public void finishVertex(SearchNode node) {
                System.out.println("Finish vertex: " + node);
            }

            @Override
            public void upward(SearchNode from, SearchNode to) {
                System.out.println("Return to parent: " + from + "->" + to);
            }
        });
    }

    public static void main(String args[]) {
        new DFSVisitorExample().demoDFS();
    }
}
