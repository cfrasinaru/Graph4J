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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.util.AlgorithmTimeoutException;
import org.graph4j.util.Clique;

/**
 * Attempts at finding the optimum coloring of a graph using a systematic
 * exploration of the search space. The backtracking is implemented in a
 * non-recursive manner.
 *
 * <p>
 * A maximal clique is computed that offers a lower bound <code>q</code> of the
 * chromatic number. The colors of the vertices in the maximal clique are fixed
 * before the backtracking algorithm starts.
 *
 * An initial coloring is computed using a simple heuristic (DSatur). This gives
 * an upper bound <code>k</code>of the chromatic number.
 *
 * The algorithm will attemtp to color the graph using a number of colors
 * ranging from <code>k-1</code> to <code>q</code>, determining the optimal
 * coloring.
 *
 * <p>
 * A timeout may be imposed. If the algorithm stops due to the timeout, it
 * returns the best coloring found.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackColoring extends SimpleGraphAlgorithm
        implements VertexColoringAlgorithm {

    private Deque<Node> stack = new ArrayDeque<>();
    private Clique maxClique;
    private long timeout;
    private long startTime;

    public BacktrackColoring(Graph graph) {
        super(graph);
    }

    public BacktrackColoring(Graph graph, int timeout) {
        super(graph);
        this.timeout = timeout;
    }

    @Override
    public VertexColoring findColoring() {
        this.startTime = System.currentTimeMillis();
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getClique();
        }
        //VertexColoring coloring = new RecursiveLargestFirstColoring(graph).findColoring();
        VertexColoring coloring = new DSaturGreedyColoring(graph).findColoring();
        try {
            for (int j = coloring.numUsedColors() - 1, k = maxClique.size(); j >= k; j--) {
                var c = findColoring(j);
                if (c == null) {
                    break;
                } else {
                    coloring = c;
                }
            }
        } catch (AlgorithmTimeoutException e) {
            System.err.println("Timeout.");
        }
        return coloring;
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        if (!init(numColors)) {
            return null;
        }
        while (!stack.isEmpty()) {
            if (timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
                throw new AlgorithmTimeoutException();
            }
            Node node = stack.peek();
            if (node.coloring.numUsedColors() > numColors) {
                stack.poll();
                continue;
            }
            if (node.coloring.isComplete()) {
                stack.poll();
                return node.coloring; //solution found
            }
            //select the node's domain
            Domain domain = node.minDomain;
            int v = graph.vertexAt(domain.index);

            //pick a color in the domain
            int color = domain.poll();
            if (domain.isEmpty()) {
                stack.poll();
            }

            //create the new domains
            //the domain of the selected vertex v becomes singleton
            Domain[] newDomains = Arrays.copyOf(node.domains, node.domains.length);
            for (int i = 0; i < newDomains.length; i++) {
                newDomains[i] = new Domain(node.domains[i]);
            }
            newDomains[domain.index].size = 1;
            newDomains[domain.index].values = new int[]{color};

            //create the new coloring
            //color and propagate the decision v=c            
            var newColoring = new VertexColoring(graph, node.coloring);
            newColoring.setColor(v, color);
            if (propagate(v, color, newColoring, newDomains)) {
                Node newNode = new Node(newDomains, newColoring);
                stack.push(newNode);
            }
        }
        return null;
    }

    //before findColoring
    private boolean init(int numColors) {
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getClique();
        }
        if (maxClique.size() > numColors) {
            return false;
        }
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        int n = graph.numVertices();
        Domain[] domains = new Domain[n];
        VertexColoring coloring = new VertexColoring(graph);
        int color = 0;
        for (int i = 0; i < n; i++) {
            int v = graph.vertexAt(i);
            if (maxClique.contains(v)) {
                coloring.setColor(v, color);
                domains[i] = new Domain(i, 1);
                domains[i].values[0] = color;
                color++;
                continue;
            }
            domains[i] = new Domain(i, numColors);
        }
        for (int v : maxClique.vertices()) {
            if (!propagate(v, coloring.getColor(v), coloring, domains)) {
                return false;
            }
            color++;
        }
        stack.push(new Node(domains, coloring));
        return true;
    }

    //after the decision v=color
    private boolean propagate(int v, int color, VertexColoring coloring, Domain[] domains) {
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            if (coloring.isColorSet(u)) {
                continue;
            }
            var dom = domains[graph.indexOf(u)];
            if (!dom.remove(color)) {
                continue;
            }
            if (dom.isEmpty()) {
                return false;
            }
            if (dom.size == 1) {
                if (!propagate(u, dom.values[0], coloring, domains)) {
                    return false;
                }
            }
        }
        return true;
    }

    //a node in the systematic search tree
    private class Node {

        VertexColoring coloring;
        Domain[] domains;
        Domain minDomain;

        public Node(Domain[] domains, VertexColoring coloring) {
            this.domains = domains;
            int min = Integer.MAX_VALUE;
            for (var dom : domains) {
                int v = graph.indexOf(dom.index);
                if (coloring.isColorSet(v)) {
                    continue;
                }
                if (dom.size == 1) {
                    coloring.setColor(v, dom.values[0]);
                    continue;
                }
                if (dom.size < min) {
                    min = dom.size;
                    minDomain = dom;
                }
            }
            this.coloring = coloring;
        }

        @Override
        public String toString() {
            return (minDomain == null ? "" : minDomain.index)
                    + "\n\t" + Arrays.toString(domains) + "\n\t" + coloring;
        }

    }

    //colors available to a vertex
    private class Domain {

        int index;
        Domain parent;
        int[] values;
        int size;

        public Domain(Domain parent) {
            this.parent = parent;
            this.index = parent.index;
            this.values = parent.values;
            this.size = parent.size;
        }

        public Domain(int index, int numColors) {
            this.index = index;
            values = new int[numColors];
            for (int i = 0; i < numColors; i++) {
                values[i] = numColors - i - 1;
            }
            size = numColors;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int poll() {
            int color = values[size - 1];
            removeAtPos(size - 1);
            return color;
        }

        public boolean remove(int color) {
            int pos = indexOf(color);
            if (pos < 0) {
                return false;
            }
            removeAtPos(pos);
            return true;
        }

        private int indexOf(int color) {
            for (int i = 0; i < size; i++) {
                if (values[i] == color) {
                    return i;
                }
            }
            return -1;
        }

        private void removeAtPos(int pos) {
            if (parent != null && this.values == parent.values) {
                this.values = Arrays.copyOf(parent.values, size);
            }
            if (pos != size - 1) {
                values[pos] = values[size - 1];
            }
            size--;
        }

        @Override
        public String toString() {
            return "dom(" + index + ")=" + Arrays.toString(Arrays.copyOf(values, size));
        }

    }

}
