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
package ro.uaic.info.graph.alg.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Edge;

/**
 *
 * @author Cristian Frăsinaru
 */
public class NetworkFlow {

    private final int source, sink;
    private final Digraph graph;
    private final Map<Edge, Double> map;

    public NetworkFlow(Digraph graph, int source, int sink) {
        this.graph = graph;
        this.source = source;
        this.sink = sink;
        this.map = new HashMap<>(graph.numVertices()); //at least
    }

    public void put(Edge e, double value) {
        map.put(e, value);
    }

    public double get(Edge e) {
        return map.getOrDefault(e, 0.0);
    }

    public Set<Edge> edges() {
        return map.keySet();
    }

    public double value() {
        double value = 0.0;
        for (var it = graph.succesorIterator(source); it.hasNext();) {
            it.next();
            value += map.get(it.edge());
        }
        return value;
    }

    public boolean isValid() {
        throw new UnsupportedOperationException();
    }
}
