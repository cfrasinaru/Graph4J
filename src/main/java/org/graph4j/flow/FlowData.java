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
package org.graph4j.flow;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.graph4j.Network;
import org.graph4j.Edge;

/**
 * Utility class for representing the flow of a network.
 *
 * @author Cristian Frăsinaru
 */
public class FlowData {

    private final int source;
    private final int sink;
    private final Network graph;
    private final Map<Edge, Double> map;

    public FlowData(Network graph) {
        this.graph = graph;
        this.source = graph.getSource();
        this.sink = graph.getSink();
        this.map = new TreeMap<>();
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
        for (var it = graph.successorIterator(source); it.hasNext();) {
            it.next();
            value += map.get(it.edge());
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Edge e : map.keySet()) {
            double w = map.get(e);
            if (w > 0) {
                sb.append(e).append(" :").append(w).append("\n");
            }
        }
        return sb.toString();
    }

}
