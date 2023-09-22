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
package org.graph4j.alg.connectivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.connectivity.TarjanStrongConnectivity;

/**
 *
 * @author Cristian Frăsinaru
 */
public class StrongConnectivityTest {

    @Test
    public void digraphStronglyConnected1() {
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-0").buildDigraph();
        var alg = new TarjanStrongConnectivity(g);
        assertTrue(alg.isStronglyConnected());
        assertEquals(1, alg.getStronglyConnectedSets().size());
    }

    @Test
    public void digraphStronglyConnected2() {
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5").buildDigraph();
        var alg = new TarjanStrongConnectivity(g);
        assertFalse(alg.isStronglyConnected());
        assertEquals(6, alg.getStronglyConnectedSets().size()); //each vertex
    }

    @Test
    public void digraphStronglyConnected3() {
        //https://en.wikipedia.org/wiki/Strongly_connected_component#/media/File:Scc-1.svg
        var g = GraphBuilder.numVertices(8).addEdges("0-1,1-2,2-3,3-2,3-4,4-3,4-5,2-5,5-6,6-5,1-6,1-7,7-0,7-6").buildDigraph();
        var alg = new TarjanStrongConnectivity(g);
        assertFalse(alg.isStronglyConnected());
        assertEquals(3, alg.getStronglyConnectedSets().size());
    }

    private void testSCC() {
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-0").buildDigraph();
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,3-4,4-5,5-3,2-0").buildDigraph();
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-0,0-3,3-4,4-0,0-5").buildDigraph();
        //var g = GraphBuilder.numVertices(3).addEdges("1-2").buildGraph();
        //var g = GraphBuilder.numVertices(2).addEdges("0-1").buildGraph();
        //var g = GraphBuilder.numVertices(10).addEdges("0-1,1-2,2-3,3-0,3-4,4-1,2-5,5-6,6-7,7-5,5-8,8-9,9-5").buildGraph();
        //var g = GraphBuilder.numVertices(11).addEdges("0-1,1-2,2-0,1-3,3-4,4-5,5-6,6-3,6-4,7-8,9-10").buildGraph();
        //var g = GraphBuilder.numVertices(4).addEdges("0-1,1-2,2-0,1-3").buildGraph();
        //https://en.wikipedia.org/wiki/Strongly_connected_component#/media/File:Scc-1.svg
        //var g = GraphBuilder.numVertices(8).addEdges("0-1,1-2,2-3,3-2,3-4,4-3,4-5,2-5,5-6,6-5,1-6,1-7,7-0,7-6").buildDigraph();
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-3,2-0,1-3,3-4,4-5,5-3").buildDigraph();
        //var g = GraphBuilder.labeledVertices("a", "b", "c").buildDigraph();
        System.out.println(g);
        var alg = new TarjanStrongConnectivity(g);
        //System.out.println(alg.isBiconnected());
        System.out.println(alg.getStronglyConnectedSets());
        System.out.println(alg.createCondensation());

    }

}
