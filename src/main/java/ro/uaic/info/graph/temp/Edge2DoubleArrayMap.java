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

import java.util.Arrays;
import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
class Edge2DoubleArrayMap implements Edge2DoubleMap {

    private final Graph graph;
    private final int numVertices;
    private final double[] map;

    public Edge2DoubleArrayMap(Graph graph) {
        this.graph = graph;
        this.numVertices = graph.numVertices();
        if (numVertices > 10_000) {
            throw new IllegalArgumentException();
        }
        this.map = new double[numVertices * numVertices];
        Arrays.fill(map, Double.POSITIVE_INFINITY);
    }

    @Override
    public void put(int vi, int ui, double value) {
        map[vi * numVertices + ui] = value;
    }

    @Override
    public void inc(int vi, int ui, double amount) {
        map[vi * numVertices + ui] = getOrDefault(vi, ui, 0.0) + amount;
    }

    @Override
    public double getOrDefault(int vi, int ui, double defaultValue) {
        double value = map[vi * numVertices + ui];
        return value == Double.POSITIVE_INFINITY ? defaultValue : value;
    }

}
