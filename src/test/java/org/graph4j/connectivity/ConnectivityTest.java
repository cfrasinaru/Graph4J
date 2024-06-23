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
package org.graph4j.connectivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.GraphUtils;
import org.graph4j.generators.CycleGenerator;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.generators.PathGenerator;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ConnectivityTest {

    @Test
    public void graphConnected1() {
        var g = GraphBuilder.numVertices(7).addEdges("0-1,1-2,3-4,3-5").buildGraph();
        var cc = new ConnectivityAlgorithm(g);
        assertFalse(cc.isConnected());
        assertEquals(3, cc.getConnectedSets().size()); //6 is isolated
    }

    @Test
    public void graphConnected2() {
        var g1 = new PathGenerator(0, 9).createGraph();
        var g2 = new CycleGenerator(10, 19).createGraph();
        var g3 = GraphUtils.disjointUnion(g1, g2);
        assertTrue(GraphTests.isConnected(g1));
        assertTrue(GraphTests.isConnected(g2));
        assertFalse(GraphTests.isConnected(GraphUtils.disjointUnion(g1, g2)));
        assertTrue(GraphTests.isConnected(GraphUtils.join(g1, g2)));
    }

    @Test
    public void graphBiconnected1() {
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-0").buildGraph();
        var alg = new TarjanBiconnectivity(g);
        assertTrue(alg.isBiconnected());
        assertEquals(1, alg.getBlocks().size());
        assertEquals(0, alg.getCutVertices().numVertices());
    }

    @Test
    public void graphBiconnected2() {
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5").buildGraph();
        var alg = new TarjanBiconnectivity(g);
        assertFalse(alg.isBiconnected());
        assertEquals(5, alg.getBlocks().size());//each edge
    }

    @Test
    public void graphBiconnected3() {
        //https://mathworld.wolfram.com/Block.html        
        var g = GraphBuilder.numVertices(10).addEdges("0-1,1-2,2-3,3-0,3-4,4-1,2-5,5-6,6-7,7-5,5-8,8-9,9-5").buildGraph();
        var alg = new TarjanBiconnectivity(g);
        assertFalse(alg.isBiconnected());
        assertEquals(4, alg.getBlocks().size());
        assertEquals(new VertexSet(g, new int[]{2, 5}), alg.getCutVertices());
    }

    @Test
    public void graphBiconnected4() {
        //https://mathworld.wolfram.com/Block.html        
        var g = GraphBuilder.numVertices(11).addEdges("0-1,1-2,2-0,1-3,3-4,4-5,5-6,6-3,6-4,7-8,9-10").buildGraph();
        var alg = new TarjanBiconnectivity(g);
        assertFalse(alg.isBiconnected());
        assertEquals(5, alg.getBlocks().size());
        assertEquals(new VertexSet(g, new int[]{1, 3}), alg.getCutVertices());
    }

    //https://mathworld.wolfram.com/Block.html        
    public void graphBiconnectedx() {
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-0").buildGraph();
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-3,2-0").buildGraph();
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-0,0-3,3-4,4-0,0-5").buildGraph();
        //var g = GraphBuilder.numVertices(3).addEdges("1-2").buildGraph();
        //var g = GraphBuilder.numVertices(2).addEdges("0-1").buildGraph();
        //var g = GraphBuilder.numVertices(4).addEdges("0-1,1-2,2-0,1-3").buildGraph();
        var g = GraphGenerator.complete(5);
        System.out.println(g);
        var alg = new TarjanBiconnectivity(g);
        System.out.println(alg.getBlocks());
    }
            
   
}
