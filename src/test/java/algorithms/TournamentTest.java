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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.Tournament;
import org.graph4j.generate.TournamentGenerator;
import org.graph4j.util.Path;

/**
 *
 * @author Cristian Frăsinaru
 */
public class TournamentTest {

    @Test
    public void simple() {
        var g = GraphBuilder.numVertices(3).addEdges("0-2,1-0,1-2").buildDigraph();
        var alg = new Tournament(g);
        assertTrue(alg.isTournament());
        Path path = alg.getHamiltonianPath();
        assertTrue(path.isValid() && path.isHamiltonian());
    }

    @Test
    public void random() {
        var g = new TournamentGenerator(10).createRandom();
        var alg = new Tournament(g);
        assertTrue(alg.isTournament());
        Path path = alg.getHamiltonianPath();
        assertTrue(path.isValid() && path.isHamiltonian());
    }

}
