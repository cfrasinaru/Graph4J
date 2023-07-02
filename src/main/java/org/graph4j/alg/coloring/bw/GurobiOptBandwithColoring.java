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
package org.graph4j.alg.coloring.bw;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;
import org.graph4j.Graph;

/**
 * This implementation uses the optimisation model, solving only one problem.
 *
 * Requires a valid Gurobi installation.
 *
 * @author Cristian Frăsinaru
 */
public class GurobiOptBandwithColoring extends GurobiBandwithColoring {

    private GRBVar zmax;

    public GurobiOptBandwithColoring(Graph graph) {
        super(graph);
    }

    public GurobiOptBandwithColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected GurobiOptBandwithColoring getInstance(Graph graph, long timeLimit) {
        return new GurobiOptBandwithColoring(graph, timeLimit);
    }

    @Override
    public boolean isOptimalityEnsured() {
        return true;
    }

    @Override
    protected void createModel(int numColors) throws GRBException {
        super.createModel(numColors);

        int n = graph.numVertices();
        int k = numColors;

        zmax = model.addVar(0, GRB.INFINITY, 0.0, GRB.INTEGER, "zmax");

        //zmax be greater than or equal to any color used
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < k; c++) {
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, zmax);
                expr.addTerm(-c, x[i][c]);
                model.addConstr(expr, GRB.GREATER_EQUAL, 0, "zmax_" + c);
            }
        }

        //objective
        GRBLinExpr expr = new GRBLinExpr();
        expr.addTerm(1, zmax);
        model.setObjective(expr, GRB.MINIMIZE);
    }

}
