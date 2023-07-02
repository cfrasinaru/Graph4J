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
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.HashSet;
import org.graph4j.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class GurobiColoringBase extends ExactColoringBase {

    protected GRBEnv env;
    protected GRBModel model;
    protected GRBVar x[][];

    public GurobiColoringBase(Graph graph) {
        super(graph);
    }

    public GurobiColoringBase(Graph graph, long timeLimit) {
        super(graph, timeLimit);
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

    protected abstract void createModel(int numColors) throws GRBException;

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
