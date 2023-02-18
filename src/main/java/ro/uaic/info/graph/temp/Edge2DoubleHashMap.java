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

import java.util.HashMap;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.IntPair;

/**
 *
 * @author Cristian Frăsinaru
 */
class Edge2DoubleHashMap implements Edge2DoubleMap {

    private final Graph graph;
    private final HashMap<IntPair, Double> map;

    public Edge2DoubleHashMap(Graph graph) {
        this.graph = graph;
        map = new HashMap<>(graph.numVertices());
    }

    @Override
    public void put(int vi, int ui, double value) {
        map.put(new IntPair(vi, ui), value);
    }

    @Override
    public void inc(int vi, int ui, double amount) {
        var pair = new IntPair(vi, ui);
        double value = map.getOrDefault(pair, 0.0);
        map.put(pair, value + amount);
    }

    @Override
    public double getOrDefault(int vi, int ui, double defaultValue) {
        return map.getOrDefault(new IntPair(vi, ui), defaultValue);
    }

}
