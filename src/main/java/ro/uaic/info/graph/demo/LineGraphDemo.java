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
package ro.uaic.info.graph.demo;

import org.jgrapht.alg.transform.LineGraphConverter;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.generate.GnpGraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class LineGraphDemo extends PerformanceDemo {

    public LineGraphDemo() {
        runJGraphT = true;
    }

    
    @Override
    protected void createGraph() {
        graph = new GnpGraphGenerator(1_000, 0.1).createGraph();
        //graph = GraphGenerator.complete(300);
    }

    @Override
    protected void testGraph4J() {
        var lg = Graphs.lineGraph(graph);
        System.out.println(lg.numEdges());

    }

    @Override
    protected void testJGraphT() {
        var target = Converter.createJGraphT(null);
        new LineGraphConverter(jgrapht).convertToLineGraph(target);
        System.out.println(target.edgeSet().size());
    }


    public static void main(String args[]) {
        var app = new LineGraphDemo();
        app.demo();
    }

}
