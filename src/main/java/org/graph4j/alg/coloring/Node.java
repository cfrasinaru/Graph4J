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
package org.graph4j.alg.coloring;

import org.graph4j.util.Domain;
import java.util.Arrays;
import org.graph4j.Graph;

/**
 * A node in the backtrack coloring search tree.
 *
 * @author Cristian Frăsinaru
 */
public class Node {

    private final Graph graph;
    private final boolean findAll;
    final int vertex; //the node was created as the result of vertex = color
    final int color;
    final Node parent;
    Coloring coloring;
    Domain[] domains;
    Domain minDomain;
    boolean removeSymmetricalColors;
    boolean propagator;
    boolean failed;

    public Node(ExactColoringBase alg, Node parent, int vertex, int color, Domain[] domains, Coloring coloring,
            boolean removeSymmetricalColors) {
        this.graph = alg.getGraph();
        this.parent = parent;
        this.vertex = vertex;
        this.color = color;
        this.domains = domains;
        this.coloring = coloring;
        this.findAll = alg.getSolutionsLimit() > 1;
        this.removeSymmetricalColors = removeSymmetricalColors;
    }

    public void prepare() {
        //find the domain with the minimum size
        int minSize = Integer.MAX_VALUE;
        minDomain = null;

        for (var dom : domains) {
            int v = dom.vertex();
            if (coloring.isColorSet(v)) {
                continue;
            }
            if (dom.size() < minSize) {
                minSize = dom.size();
                minDomain = dom;
                /*
                if (minSize == 2) {
                    break;
                }*/
            } else if (dom.size() == minSize) {
                if (graph.degree(minDomain.vertex()) < graph.degree(dom.vertex())) {
                    minDomain = dom;
                }
            }
        }
        if (minDomain != null) {
            minDomain = new Domain(minDomain);
            this.domains[graph.indexOf(minDomain.vertex())] = minDomain;
            //remove symmetrical colors from minDomain
            if (!findAll && removeSymmetricalColors) {
                removeSymmetricalColors();
            }
            //trace();
        }
    }

    private void trace() {
        var sb = new StringBuilder();
        var temp = this;
        while (temp != null) {
            sb.append(temp.minDomain.vertex()).append(" - ");
            temp = temp.parent;
        }
        System.out.println(sb.reverse());
    }

    //remove from minDomain all colors that have not been used before
    //except one (free color)
    private void removeSymmetricalColors() {
        int free = -1;
        int i = 0;
        int count = 0;
        while (i < minDomain.size()) {
            int c = minDomain.valueAt(i);
            if (!coloring.isColorUsed(c)) {
                //if (coloring.uncoloredNeighbors(c) == 0) {
                if (free >= 0) {
                    minDomain.removeAtPos(i);
                    count++;
                } else {
                    free = i;
                }
            }
            i++;
        }
        //put the free color so it will be chosen last
        if (free > 0) {
            minDomain.swapPos(0, free);
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
     * @return the coloring.
     */
    public Coloring coloring() {
        return coloring;
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
        return "Decision: " + vertex + "=" + color + "\n\t"
                + "Pivot: " + (minDomain == null ? "" : minDomain.vertex()) + "\n\t"
                + Arrays.toString(domains) + "\n\t"
                + coloring;
    }
}
