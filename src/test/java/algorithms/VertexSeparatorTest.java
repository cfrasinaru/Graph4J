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
package algorithms;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.Graphs;
import org.graph4j.alg.cut.GreedyVertexSeparator;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class VertexSeparatorTest {

    @Test
    public void greedyRandom() {
        var g = GraphGenerator.randomGnp(20, Math.random());
        var alg = new GreedyVertexSeparator(g);
        int maxShoreSize = 1 + new Random().nextInt(g.numVertices() - 3);
        var sep = alg.getSeparator(maxShoreSize);
        var rest = new VertexSet(g, g.vertices());
        rest.removeAll(sep.separator().vertices());
        if (!sep.rightShore().isEmpty()) {
            assertFalse(Graphs.isConnected(g.subgraph(rest.vertices())));
        }
        assertTrue(sep.leftShore().size() <= maxShoreSize);
        //assertTrue(alg.getRightShore().size() <= maxShoreSize);
    }

}
