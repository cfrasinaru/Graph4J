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
package org.graph4j.alg.coloring.eq;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;
import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.alg.clique.DFSCliqueIterator;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.GurobiColoringBase;
import org.graph4j.util.VertexSet;

/**
 * ILP Stable set model. Requires a valid Gurobi installation.
 *
 * @author Cristian Frăsinaru
 */
public class GurobiStableModelEquitableColoring extends GurobiColoringBase
        implements EquitableColoringAlgorithm {

    private GRBVar x[];
    private List<VertexSet> stables;

    public GurobiStableModelEquitableColoring(Graph graph) {
        this(graph, 0);
    }

    public GurobiStableModelEquitableColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected GurobiStableModelEquitableColoring getInstance(Graph graph, long timeLimit) {
        return new GurobiStableModelEquitableColoring(graph, timeLimit);
    }

    //stable sets with size n/k or between n/k and 1+n/k    
    private void createStableSets(int lb, int ub) {
        stables = new ArrayList<>();
        var it = new DFSCliqueIterator(graph.complement(), lb, ub);
        while (it.hasNext()) {
            stables.add(it.next());
            if (!checkTime()) {
                return;
            }
        }
        if (outputEnabled) {
            System.out.println("Stable sets to consider: " + stables.size() + ": " + lb + " <= size <= " + ub);
        }
    }

    @Override
    protected void createModel(int numColors) throws GRBException {
        int n = graph.numVertices();
        int k = numColors;
        
        int lb = n / k;
        int ub = n % k == 0 ? n / k : 1 + n / k;
        createStableSets(lb, ub);

        int s = stables.size();
        x = new GRBVar[s];
        for (int i = 0; i < s; i++) {
            x[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x[" + i + "]");
        }

        //stable sets must cover all nodes
        for (int u : graph.vertices()) {
            GRBLinExpr sum = new GRBLinExpr();
            for (int i = 0; i < s; i++) {
                if (stables.get(i).contains(u)) {
                    sum.addTerm(1, x[i]);
                }
            }
            model.addConstr(sum, GRB.EQUAL, 1, "cover_" + u);
        }

        //n nodes    
        /*
        GRBLinExpr sumNodes = new GRBLinExpr();
        for (int i = 0; i < s; i++) {
            sumNodes.addTerm(stables.get(i).size(), x[i]);
        }
        model.addConstr(sumNodes, GRB.EQUAL, n, "nodes_" + n);
        nbConstraints++;
         */
        int r = n % k;
        if (r == 0) {
            //k color classes
            GRBLinExpr sum = new GRBLinExpr();
            for (int i = 0; i < s; i++) {
                sum.addTerm(1, x[i]);
            }
            model.addConstr(sum, GRB.EQUAL, k, "number_" + k);

        } else {
            //k-r lb classes, r ub classes
            GRBLinExpr lbSum = new GRBLinExpr();
            GRBLinExpr ubSum = new GRBLinExpr();
            for (int i = 0; i < s; i++) {
                if (stables.get(i).size() == lb) {
                    lbSum.addTerm(1, x[i]);
                } else {
                    ubSum.addTerm(1, x[i]);
                }
            }
            model.addConstr(lbSum, GRB.EQUAL, k - r, "lb_classes_" + lb);
            model.addConstr(ubSum, GRB.EQUAL, r, "ub_classes_" + ub);
        }

    }

    @Override
    protected Coloring createColoring(int numColors) throws GRBException {
        Coloring coloring = new Coloring(graph);
        int col = 0;
        for (int j = 0; j < stables.size(); j++) {
            if (x[j].get(GRB.DoubleAttr.X) > .00001) {
                for (int v : stables.get(j)) {
                    coloring.setColor(v, col);
                }
                col++;
            }
        }
        return coloring;
    }
}
