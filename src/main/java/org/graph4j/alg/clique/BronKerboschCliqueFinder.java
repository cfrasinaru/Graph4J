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
package org.graph4j.alg.clique;

import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 * Recursive implementation.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class BronKerboschCliqueFinder extends SimpleGraphAlgorithm {

    private Clique workingClique;
    List<Clique> cliques;

    public BronKerboschCliqueFinder(Graph graph) {
        super(graph);
    }

    private void compute() {
        cliques = new ArrayList<>();

        //this is the current clique that is expaned up to a maximal clique
        workingClique = new Clique(graph);

        //subg are the vertices of the subgraph where we look for a clique
        var subg = new VertexSet(graph, graph.vertices());

        //cand are the vertices available to expand the working clique
        var cand = new VertexSet(graph, graph.vertices());

        expand(subg, cand);
    }

    public List<Clique> getCliques() {
        if (cliques == null) {
            compute();
        }
        return cliques;
    }

    private void expand(VertexSet subg, VertexSet cand) {
        /*
        System.out.println("clique: " + workingClique);
        System.out.println("subg: " + subg);
        System.out.println("cand: " + cand);
         */
        if (subg.isEmpty()) {
            //found a maximal clique
            //System.out.println(workingClique);
            cliques.add(workingClique);
            return;
        }

        //int u = subg.peek();
        //var tempCand = new VertexSet(cand);
        //tempCand.removeAll(graph.neighbors(u));
        var tempCand = cand;
        //
        while (!tempCand.isEmpty()) {
            int v = tempCand.peek();
            workingClique.add(v);
            var neighbors = graph.neighbors(v);
            var newSubg = subg.intersection(neighbors);
            var newCand = cand.intersection(neighbors);

            expand(newSubg, newCand);

            tempCand.remove(v);
            workingClique.remove(v);
        }
    }

}
