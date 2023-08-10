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

import java.util.ArrayList;
import org.graph4j.util.Domain;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * A node in the backtrack coloring search tree.
 *
 * @author Cristian Frăsinaru
 */
public class Node {

    private final ExactColoringBase alg;
    final int vertex; //this node was created as the result of vertex = color
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
        this.alg = alg;
        this.parent = parent;
        this.vertex = vertex;
        this.color = color;
        this.domains = domains;
        this.coloring = coloring;
        this.removeSymmetricalColors = removeSymmetricalColors;
    }

    public void prepare() {
        //find the domain with the minimum size
        var graph = alg.getGraph();
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
            if (alg.getSolutionsLimit() == 1 && removeSymmetricalColors) {
                removeSymmetricalColors();
            }
            //trace();
        }
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
            sb.append(node.vertex).append("=").append(node.color).append("->").append(node.minDomain.vertex()).append(" - ");
        }
        System.out.println(sb.toString() + ": " + minDomain);
    }

    //Remove from D=minDomain all colors that have not been used before
    //except one (free color)
    //AND
    //Two colors a and b in D are symmetrical
    //if all other domains containing a, contain also b and vice-versa
    //In this case, remove one of them from D
    //if a solution uses a from D, there is another solution 
    //where a can be replaced with b in the subgraph induced by this node
    private void removeSymmetricalColors() {
        int free = -1;
        int i = 0;
        while (i < minDomain.size()) {
            int c = minDomain.valueAt(i);
            if (!coloring.isColorUsed(c)) {
                if (free >= 0) {
                    minDomain.removeAtPos(i);
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
        //second part
        //System.out.println("=================================================");
        //System.out.println("minDomain=" + minDomain);
        //System.out.println("coloring=" + coloring);

        i = 0;
        nexta:
        while (i < minDomain.size() - 1) {
            int a = minDomain.valueAt(i);
            for (int j = i + 1, k = minDomain.size(); j < k; j++) {
                int b = minDomain.valueAt(j);
                //System.out.println("\tChecking if " + a + " is dominated by " + b);
                for (var dom : domains) {
                    if (dom == minDomain || dom.size() == 1) {
                        continue;
                    }
                    boolean ok1 = dom.contains(a);
                    boolean ok2 = dom.contains(b);
                    if ((ok1 && !ok2) || (ok2 && !ok1)) {
                        i++;
                        continue nexta;
                    }
                    //System.out.println("\t\t" + dom);
                }
                minDomain.removeAtPos(i);
                //System.out.println("\t -----> OK");
                i++;
                break;
            }
        }

        if (minDomain.size() == 1) {
            coloring.setColor(minDomain.vertex(), minDomain.valueAt(0));
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

    public FailedState getState() {
        List<Domain> list = new ArrayList<>();
        for (int i = 0; i < domains.length; i++) {
            if (domains[i].size() > 1) {
                list.add(domains[i]);
            }
        }
        return new FailedState(minDomain.vertex(), list);
    }

    @Override
    public String toString() {
        return "Decision: " + vertex + "=" + color + "\n\t"
                + "Pivot: " + (minDomain == null ? "" : minDomain.vertex()) + "\n\t"
                + Arrays.toString(domains) + "\n\t"
                + coloring;
    }
}
