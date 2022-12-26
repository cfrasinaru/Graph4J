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

import java.util.Arrays;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.VertexSet;
import ro.uaic.info.graph.alg.GraphMetrics;
import ro.uaic.info.graph.alg.HierholzerEulerianCircuit;
import ro.uaic.info.graph.alg.sp.BellmanFordShortestPath;
import ro.uaic.info.graph.alg.sp.DijkstraShortestPathDefault;
import ro.uaic.info.graph.alg.sp.FloydWarshallShortestPath;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {
    
    Graph graph;
    org.jgrapht.Graph jgraph;
    
    public static void main(String[] args) {
        var app = new Main();
        //var app = new DFSIteratorDemo();
        //var app = new BFSIteratorDemo();
        //var app = new EulerianCircuitDemo();
        //var app = new DijkstraDemo();
        //var app = new BellmanFordDemo();
        //var app = new FloydWarshallDemo();
        //var app = new IterateAllEdgesDemo();
        //var app = new ContainsEdgeDemo();
        //var app = new GraphCreationDemo();
        app.demo();
    }
    
    private void demo() {
        run(this::test);
        //run(this::prepare);
    }
    
    private void test() {
        //var g = new CompleteGenerator(5).createDigraph();
        //var g = GraphBuilder.numVertices(8).addCycle(0, 1, 2, 3, 4, 5).addCycle(6, 1, 5, 7, 4, 2).buildGraph();
        var g = GraphBuilder.vertexRange(0, 3).addClique(0, 1, 2, 3).buildGraph();
        //var g = GraphBuilder.numVertices(6).addEdges("0-1, 0-2, 0-3, 0-4, 0-5, 4-5,1-2, 1-3, 2-3").buildGraph();
        System.out.println(g);
        
        
        g.addVertices(8, 9);
        g.addEdge(0, 8);
        g.addEdge(0, 9);
        g.addEdge(8, 9);
        
        System.out.println(g);
        System.out.println(Arrays.deepToString(g.edges()));
        System.out.println(Arrays.toString(g.neighbors(8)));
        System.out.println(Arrays.toString(g.neighbors(9)));
        
       // g.removeVertex(0);
        //System.out.println(g);
        
        //g.addEdge(8, 9);
        //System.out.println(g);
        /*
        Circuit c1 = new Circuit(g, 0, 1, 2, 3, 4, 5);
        Circuit c2 = new Circuit(g, 6, 1, 5, 7, 4, 2);
        System.out.println(c1.join(c2));
         */
        //var g = GraphBuilder.numVertices(7).addCycle(0, 1, 2).addCycle(0, 3, 4).addCycle(0, 5, 6).buildGraph();
        //var g = GraphBuilder.numVertices(4).addEdges("0-1,0-1,0-0").buildPseudograph();
        //var g = GraphBuilder.numVertices(4).addCycle(0, 1, 2, 3).addEdges("0-1,0-1,0-0,1-1,1-1,1-1").buildPseudograph();
        //var g = GraphBuilder.numVertices(2).addEdges("0-1,0-1,0-0,0-0,1-1,1-1").buildPseudograph();
        //var h = new HierholzerEulerianCircuit(g);
        //System.out.println(h.findCircuit());

        /*
       var it = g.edgeIterator(0);
        System.out.println(g);
       while (it.hasNext()) {
           System.out.println(it.next());
           it.remove();
           System.out.println(g);
       }*/
    }
    
    protected void run(Runnable snippet) {
        long m0 = Runtime.getRuntime().freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long m1 = Runtime.getRuntime().freeMemory();
        System.out.println((t1 - t0) + " ms");
        System.out.println((m0 - m1) / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }
    
}
