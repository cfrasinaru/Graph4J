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

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.ParallelPortfolio;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.graph4j.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ChocoColoring extends ExactColoringBase {

    private static final int MAX_MODELS = 8;

    public ChocoColoring(Graph graph) {
        super(graph);
    }

    public ChocoColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected ColoringAlgorithm getInstance(Graph graph, long timeLimit) {
        return new ChocoColoring(graph, timeLimit);
    }

    @Override
    protected void solve(int numColors) {
        solutions = new HashSet<>();
        //solutions.add(normalSolve(numColors));
        solutions.add(parallelSolve(numColors));
    }

    private Model createModel(int numColors) {
        Model model = new Model("Vertex Coloring");
        int n = graph.numVertices();
        int k = numColors;

        //colors of the vertices variables
        int colorDom[] = IntStream.range(0, k).toArray();
        IntVar[] colVar = model.intVarArray("color", n, colorDom);

        //coloring constraints
        //two adjacent nodes cannot have the same color
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                colVar[vi].ne(colVar[ui]).post();
            }
        }

        //set initial colors for some max clique
        int color = 0;
        var maxClique = getMaximalClique();
        for (int v : maxClique.vertices()) {
            colVar[graph.indexOf(v)].eq(color++).post();
        }

        return model;
    }

    private Coloring parallelSolve(int numColors) {
        ParallelPortfolio portfolio = new ParallelPortfolio();
        for (int s = 0; s < MAX_MODELS; s++) {
            Model model = createModel(numColors);
            portfolio.addModel(model);
            Solver solver = model.getSolver();
            if (timeLimit > 0) {
                solver.limitTime(timeLimit);
            }
        }
        if (!portfolio.solve()) {
            return null;
        }
        return createColoring(new Solution(portfolio.getBestModel()).record());
    }

    private Coloring normalSolve(int numColors) {
        Model model = createModel(numColors);
        Solver solver = model.getSolver();
        if (timeLimit > 0) {
            solver.limitTime(timeLimit);
        }
        //solver.showStatistics();
        //solver.showDecisions();
        //solver.showDashboard();
        //solver.plugMonitor();

        //solver.setSearch(Search.minDomLBSearch(model.retrieveIntVars(false)));
        //solver.setSearch(Search.minDomLBSearch(model.retrieveIntVars(false)));
        //solver.setSearch(Search.inputOrderLBSearch(model.retrieveIntVars(false)));
        //solver.setSearch(Search.domOverWDegSearch(model.retrieveIntVars(false)));
        /*
        boolean opt = false;
        solver.setSearch(
                Search.conflictOrderingSearch(
                        Search.VarH.DOMWDEG.make(solver, model.retrieveIntVars(false), Search.ValH.MIN, opt)
                )
        );
        Search.Restarts.LUBY.declare(solver, 500, 0.d, 5000);
        solver.setSearch(lastConflict(solver.getSearch()));
         */
        if (!solver.solve()) {
            return null;
        }
        Solution sol = new Solution(model);
        sol.record();
        return createColoring(sol);
    }

    protected Coloring createColoring(Solution sol) {
        Coloring coloring = new Coloring(graph);
        List<IntVar> vars = sol.retrieveIntVars(true);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            IntVar var = findVarByName(vars, "color[" + i + "]");
            coloring.setColor(graph.vertexAt(i), var.getValue());
        }
        return coloring;
    }

    protected IntVar findVarByName(List<IntVar> vars, String name) {
        for (IntVar v : vars) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

}
