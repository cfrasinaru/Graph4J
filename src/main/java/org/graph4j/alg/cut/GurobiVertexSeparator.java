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
package org.graph4j.alg.cut;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graph4j.Graph;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.util.VertexSet;

/**
 * Guropi ILP model for the Vertex Separator Problem (VSP).
 *
 * @author Cristian Frăsinaru
 */
public class GurobiVertexSeparator extends VertexSeparatorBase {

    protected GRBEnv env;
    protected GRBModel model;
    protected GRBVar x[], y[];

    protected VertexSeparator solution;

    public GurobiVertexSeparator(Graph graph) {
        super(graph);
    }

    public GurobiVertexSeparator(Graph graph, int maxShoreSize) {
        super(graph, maxShoreSize);
    }

    @Override
    public VertexSeparator getSeparator() {
        if (solution == null) {
            solve();
        }
        return solution;
    }

    protected void solve() {
        try {
            env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 0);
            env.start();

            model = new GRBModel(env);
            model.set(GRB.DoubleParam.MIPGapAbs, 0);
            model.set(GRB.DoubleParam.MIPGap, 0);
            //model.set(GRB.DoubleParam.TimeLimit, 60); //sec

            createModel();

            // Optimize model
            model.optimize();

            if (model.get(GRB.IntAttr.Status) == GRB.Status.OPTIMAL) {
                // Get the solution
                createSolution();
            } else {
                if (model.get(GRB.IntAttr.Status) == GRB.Status.TIME_LIMIT) {
                    //timeExpired = true;
                }
            }
            model.dispose();
            env.dispose();

        } catch (GRBException ex) {
            System.err.println(ex);
        }
    }

    private void createModel() throws GRBException {
        System.out.println("maxShoreSize=" + maxShoreSize);
        int n = graph.numVertices();

        x = new GRBVar[n]; //for A
        y = new GRBVar[n]; //for B
        for (int i = 0; i < n; i++) {
            x[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x[" + i + "]");
            y[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y[" + i + "]");
        }

        //a vertex cannot be both in A and in B
        for (int i = 0; i < n; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, x[i]);
            expr.addTerm(1, y[i]);
            model.addConstr(expr, GRB.LESS_EQUAL, 1, "vertex_" + i);
        }

        //there are no edges between A and B
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                GRBLinExpr expr1 = new GRBLinExpr();
                expr1.addTerm(1, x[vi]);
                expr1.addTerm(1, y[ui]);
                model.addConstr(expr1, GRB.LESS_EQUAL, 1, "noedge_" + v + "," + u);
                //
                GRBLinExpr expr2 = new GRBLinExpr();
                expr2.addTerm(1, x[ui]);
                expr2.addTerm(1, y[vi]);
                model.addConstr(expr2, GRB.LESS_EQUAL, 1, "noedge_" + u + "," + v);
            }
        }

        //|A| does not exceed maxShoreSize
        GRBLinExpr sumA = new GRBLinExpr();
        for (int i = 0; i < n; i++) {
            sumA.addTerm(1, x[i]);
        }
        model.addConstr(sumA, GRB.LESS_EQUAL, maxShoreSize, "maxShoreSize_A");

        //|B| does not exceed maxShoreSize
        GRBLinExpr sumB = new GRBLinExpr();
        for (int i = 0; i < n; i++) {
            sumB.addTerm(1, y[i]);
        }
        model.addConstr(sumB, GRB.LESS_EQUAL, maxShoreSize, "maxShoreSize_B");

        //maximize |A| + |B|
        GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < n; i++) {
            obj.addTerm(1, x[i]);
            obj.addTerm(1, y[i]);
        }
        model.setObjective(obj, GRB.MAXIMIZE);
    }

    private void createSolution() {
        try {
            separator = new VertexSet(graph, graph.vertices());
            leftShore = new VertexSet(graph);
            rightShore = new VertexSet(graph);
            for (int i = 0, n = graph.numVertices(); i < n; i++) {
                int v = graph.vertexAt(i);
                if (x[i].get(GRB.DoubleAttr.X) > .00001) {
                    leftShore.add(v);
                    separator.remove(v);
                } else if (y[i].get(GRB.DoubleAttr.X) > .00001) {
                    rightShore.add(v);
                    separator.remove(v);
                }
            }
            solution = new VertexSeparator(separator, leftShore, rightShore);
            assert solution.isValid();
        } catch (GRBException ex) {
            Logger.getLogger(GurobiVertexSeparator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //simple test
    public static void main(String args[]) {
        int n = 100;
        double p = 0.1;

        var g = GraphGenerator.randomGnp(n, p);
        var alg1 = new GreedyVertexSeparator(g);
        var sep1 = alg1.getSeparator();
        System.out.println("Greedy: separator size=" + sep1.separator().size() + "\n" + sep1);

        var alg2 = new GurobiVertexSeparator(g);
        var sep2 = alg2.getSeparator();
        System.out.println("Gurobi: separator size=" + sep2.separator().size() + "\n" + sep2);
    }
}
