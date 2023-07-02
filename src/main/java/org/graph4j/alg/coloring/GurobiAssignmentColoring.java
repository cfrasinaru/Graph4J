/*
 * Copyright (C) 2021 Faculty of Computer Science Iasi, Romania
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
package org.graph4j.alg.coloring;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;
import org.graph4j.Graph;

/**
 * ILP Assignment model. Requires a valid Gurobi installation.
 *
 * @author Cristian Frăsinaru
 */
public class GurobiAssignmentColoring extends GurobiColoringBase
        implements ColoringAlgorithm {

    public GurobiAssignmentColoring(Graph graph) {
        super(graph);
    }

    public GurobiAssignmentColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected GurobiAssignmentColoring getInstance(Graph graph, long timeLimit) {
        return new GurobiAssignmentColoring(graph, timeLimit);
    }

    @Override
    protected void createModel(int numColors) throws GRBException {
        int n = graph.numVertices();
        int k = numColors;
        x = new GRBVar[n][k];
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < k; c++) {
                x[i][c] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x[" + i + ", " + c + "]");
            }
        }

        //each node must have a color
        for (int i = 0; i < n; i++) {
            GRBLinExpr sum = new GRBLinExpr();
            for (int c = 0; c < k; c++) {
                sum.addTerm(1, x[i][c]);
            }
            model.addConstr(sum, GRB.EQUAL, 1, "color_" + i);
        }

        //two adjacent nodes cannot have the same color
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                for (int c = 0; c < k; c++) {
                    GRBLinExpr sum = new GRBLinExpr();
                    sum.addTerm(1, x[ui][c]);
                    sum.addTerm(1, x[vi][c]);
                    model.addConstr(sum, GRB.LESS_EQUAL, 1, "diffcolor_" + u + "," + v + " - " + c);
                }
            }
        }

        int color = 0;
        getMaximalClique();
        for (int u : maxClique.vertices()) {
            model.addConstr(x[graph.indexOf(u)][color++], GRB.EQUAL, 1, "maxclique_" + u);
        }
    }

}
