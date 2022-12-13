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
package core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.alg.GraphConnectivity;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ConnectivityTest {

    @Test
    public void graphConnected1() {
        var g = GraphBuilder.numVertices(7).addEdges("0-1,1-2,3-4,3-5").buildGraph();
        var cc = new GraphConnectivity(g);
        assertFalse(cc.isConnected());
        assertEquals(3, cc.components().size()); //6 is isolated
    }
    
    @Test
    public void graphConnected2() {
        var g1 = GraphBuilder.vertexRange(0,9).path().buildGraph();
        var g2 = GraphBuilder.vertexRange(10,19).cycle().buildGraph();
        var g3 = Graphs.disjointUnion(g1,g2);
        assertTrue(Graphs.isConnected(g1));
        assertTrue(Graphs.isConnected(g2));
        assertFalse(Graphs.isConnected(Graphs.disjointUnion(g1,g2)));        
        assertTrue(Graphs.isConnected(Graphs.join(g1,g2)));        
    }
    

}
