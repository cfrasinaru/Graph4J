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
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.HashSet;
import java.util.Set;
import org.graph4j.Graph;
import org.graph4j.alg.coloring.GurobiColoringBase;
import org.graph4j.alg.coloring.Coloring;

/**
 * This implementation uses an iterative approach, solving repeatedly decision
 * problems.
 *
 * Requires a valid Gurobi installation.
 *
 * @author Cristian Frăsinaru
 */
public class GurobiBandwithColoring extends GurobiColoringBase
        implements BandwithColoringAlgorithm {

    public GurobiBandwithColoring(Graph graph) {
        super(graph);
    }

    public GurobiBandwithColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected GurobiBandwithColoring getInstance(Graph graph, long timeLimit) {
        return new GurobiBandwithColoring(graph, timeLimit);
    }

    @Override
    public Set<Coloring> findAllColorings(int numColors, int solutionsLimit) {
        throw new UnsupportedOperationException(
                "This implementation does not support finding multiple colorings.");
    }

    @Override
    protected void solve(int numColors) {
        solutions = new HashSet<>();

        try {
            env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 0); //outputEnabled
            env.start();

            model = new GRBModel(env);
            model.set(GRB.DoubleParam.MIPGapAbs, 0);
            model.set(GRB.DoubleParam.MIPGap, 0);
            if (timeLimit > 0) {
                model.set(GRB.DoubleParam.TimeLimit, timeLimit / 1000);
            }

            createModel(numColors);

            // Optimize model
            model.optimize();

            if (model.get(GRB.IntAttr.Status) == GRB.Status.OPTIMAL) {
                // Get the solution
                solutions.add(createColoring(numColors));
            } else {
                if (model.get(GRB.IntAttr.Status) == GRB.Status.TIME_LIMIT) {
                    timeExpired = true;
                }
            }
            model.dispose();
            env.dispose();

        } catch (GRBException ex) {
            System.out.println(ex);
        }
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

        //bandwith constraint
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                double weight = it.getEdgeWeight();
                for (int c = 0; c < k; c++) {
                    for (int e = 0; e < k; e++) {
                        if (Math.abs(c - e) < weight) {
                            GRBLinExpr expr = new GRBLinExpr();
                            expr.addTerm(1, x[vi][c]);
                            expr.addTerm(1, x[ui][e]);
                            model.addConstr(expr, GRB.LESS_EQUAL, 1, "bandwith_" + u + "," + v + " - " + c + "," + e);
                        }
                    }
                }
            }
        }
    }

    protected Coloring createColoring(int numColors) throws GRBException {
        Coloring coloring = new Coloring(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            int v = graph.vertexAt(i);
            for (int c = 0; c < numColors; c++) {
                if (x[i][c].get(GRB.DoubleAttr.X) > .00001) {
                    coloring.setColor(v, c);
                    break;
                }
            }
        }
        return coloring;
    }

}
