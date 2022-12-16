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
package ro.uaic.info.graph.gen;

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.DirectedMultigraph;
import ro.uaic.info.graph.DirectedPseudograph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.Pseudograph;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class AbstractGenerator {

    public Graph createGraph() {
        throw new UnsupportedOperationException();
    }

    public Digraph createDigraph() {
        throw new UnsupportedOperationException();
    }

    public Multigraph createMultiGraph() {
        throw new UnsupportedOperationException();
    }

    public Pseudograph createPseudograph() {
        throw new UnsupportedOperationException();
    }

    public DirectedMultigraph createDirectedMultigraph() {
        throw new UnsupportedOperationException();
    }

    public DirectedPseudograph createDirectedPseudograph() {
        throw new UnsupportedOperationException();
    }

}
