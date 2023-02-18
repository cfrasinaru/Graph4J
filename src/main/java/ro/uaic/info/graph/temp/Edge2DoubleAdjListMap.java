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
package ro.uaic.info.graph.temp;

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
class Edge2DoubleAdjListMap implements Edge2DoubleMap {

    private final Graph graph;
    private final int numVertices;
    private final double[][] map;

    public Edge2DoubleAdjListMap(Graph graph) {
        this.graph = graph;
        this.numVertices = graph.numVertices();
        this.map = new double[numVertices][];
        for (int v : graph.vertices()) {
            map[graph.indexOf(v)] = new double[graph.degree(v)];
        }
    }

    private int pos(int vi, int ui) {
        int v = graph.vertexAt(vi);
        int u = graph.vertexAt(ui);
        int pos = 0;
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            if (it.next() == u) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    @Override
    public void put(int vi, int ui, double value) {
        int pos = pos(vi, ui);
        if (pos < 0) {
            throw new IllegalArgumentException();
        }
        map[vi][pos] = value;
    }

    @Override
    public void inc(int vi, int ui, double amount) {
        int pos = pos(vi, ui);
        if (pos < 0) {
            throw new IllegalArgumentException();
        }
        map[vi][pos] += amount;
    }

    @Override
    public double getOrDefault(int vi, int ui, double defaultValue) {
        int pos = pos(vi, ui);
        if (pos < 0) {
            return defaultValue;
        }
        return map[vi][pos];
    }

}
