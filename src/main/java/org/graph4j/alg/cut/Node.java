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
package org.graph4j.alg.cut;

import java.util.ArrayList;
import org.graph4j.util.Domain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.graph4j.util.VertexSet;

/**
 * A node in the backtrack separator search tree.
 *
 * @author Cristian Frăsinaru
 */
class Node {

    private final BacktrackVertexSeparator alg;
    final int vertex; //this node was created as the result of vertex = value
    final int value;
    final Node parent;
    VertexSeparator separator;
    Domain[] domains;
    Domain minDomain;
    boolean propagator;
    boolean failed;

    public Node(BacktrackVertexSeparator alg, Node parent, int vertex, int value, Domain[] domains, VertexSeparator separator) {
        this.alg = alg;
        this.parent = parent;
        this.vertex = vertex;
        this.value = value;
        this.domains = domains;
        this.separator = separator;
    }

    public void prepare() {
        //find the domain with the minimum size
        var graph = alg.getGraph();
        int minSize = Integer.MAX_VALUE;
        minDomain = null;

        for (var dom : domains) {
            int v = dom.vertex();
            if (separator.contains(v)) {
                continue;
            }
            int domSize = dom.size();
            if (domSize < minSize) {
                minSize = domSize;
                minDomain = dom;
            } else if (domSize == minSize) {
                //maximum degree
                if (graph.degree(minDomain.vertex()) < graph.degree(dom.vertex())) {
                    minDomain = dom;
                }
                //TODO: as few neighbors in the right/left shore
            }
        }
        if (minDomain != null) {
            minDomain = new Domain(minDomain);
            this.domains[graph.indexOf(minDomain.vertex())] = minDomain;
            //trace();
        }
    }
    
    private VertexSet neighborhood(VertexSet set) {
        var graph = alg.getGraph();
        var nb = new VertexSet(graph);
        for (int v : set.vertices()) {
            nb.addAll(graph.neighbors(v));
        }
        nb.removeAll(set.vertices());
        return nb;
    }

    private void trace() {
        List<Node> list = new ArrayList<>();
        var temp = this;
        while (temp != null) {
            list.add(temp);
            temp = temp.parent;
        }
        Collections.reverse(list);
        var sb = new StringBuilder();
        for (Node node : list) {
            sb.append(node.vertex).append("=").append(node.value).append("->").append(node.minDomain.vertex()).append(" - ");
        }
    }

    /**
     *
     * @return the domains.
     */
    public Domain[] domains() {
        return domains;
    }

    /**
     *
     * @param pos the position in the domains array.
     * @return the domain.
     */
    public Domain domain(int pos) {
        return domains[pos];
    }

    /**
     *
     * @return the separator.
     */
    public VertexSeparator separator() {
        return separator;
    }

    /**
     *
     * @return the parent.
     */
    public Node parent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Decision: " + vertex + "=" + value + "\n\t"
                + "Pivot: " + (minDomain == null ? "" : minDomain.vertex()) + "\n\t"
                + Arrays.toString(domains) + "\n"
                + separator;
    }
}
