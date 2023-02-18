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
package graph;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.generate.EdgeWeightsGenerator;
import ro.uaic.info.graph.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class LineGraphTest {

    public LineGraphTest() {
    }

    @Test
    public void cycle() {
        Graph g = GraphGenerator.cycle(5);
        var lg = Graphs.lineGraph(g);
        assertEquals(5, lg.numVertices());
        assertEquals(5, lg.numEdges());
    }

    @Test
    public void complete() {
        Graph g = GraphGenerator.complete(4);
        var lg = Graphs.lineGraph(g);
        assertEquals(6, lg.numVertices());
        assertEquals(12, lg.numEdges()); //total(15)-3
    }
    
}
