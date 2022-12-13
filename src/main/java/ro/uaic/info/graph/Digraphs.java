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
package ro.uaic.info.graph;

import ro.uaic.info.graph.build.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Digraphs {

    private Digraphs() {
    }

    public static Digraph empty(int n) {
        return GraphBuilder.numVertices(n).buildDigraph();
    }

    /**
     *
     * @param n
     * @return
     */
    public static Digraph complete(int n) {
        return GraphBuilder.numVertices(n).complete().buildDigraph();
    }

    public static Digraph path(int n) {
        throw new UnsupportedOperationException();
    }

    public static Digraph cycle(int n) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param n
     * @param p
     * @return
     */
    public static Digraph randomGnp(int n, double p) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param n
     * @param m
     * @return
     */
    public static Digraph randomGnm(int n, int m) {
        throw new UnsupportedOperationException();
    }

}
