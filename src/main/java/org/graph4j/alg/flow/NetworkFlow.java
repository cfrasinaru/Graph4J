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
package org.graph4j.alg.flow;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.graph4j.Digraph;
import org.graph4j.Edge;

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
        this.map = new TreeMap<>(); //at least
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

    public boolean isValid() {        
        int n = graph.numVertices();
        double[] in = new double[n];
        double[] out = new double[n];
        for (Edge e : map.keySet()) {
            double w = map.get(e);
            int vi = graph.indexOf(e.source());
            int ui = graph.indexOf(e.target());
            //v->u
            out[vi] += w;
            in[vi] += w;
        }
        int si = graph.indexOf(source);
        int ti = graph.indexOf(sink);
        for (int i = 0; i < n; i++) {
            if (i == si || i == ti) {
                continue;
            }
            if (in[i] != out[i]) {
                System.err.println("Conservation flow not respected in " + graph.vertexAt(i));
                return false;
            }
        }
        return true;
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
