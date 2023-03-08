/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg;

import java.util.Iterator;
import org.graph4j.Graph;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 * TODO
 * @author Cristian Frăsinaru
 */
public class CliqueIterator extends GraphAlgorithm implements Iterator<Clique> {

    private VertexSet visited;
    private VertexSet candidades;
    private Clique current;

    public CliqueIterator(Graph graph) {
        super(graph);
        candidades = new VertexSet(graph, graph.vertices());
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Clique next() {
        if (current == null) {
            findNext();
        }
        return current;
    }

    private void findNext() {
        //pick a candidate
        //create a maximal clique
    }

}
