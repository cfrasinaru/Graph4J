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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.util.AlgorithmTimeoutException;
import org.graph4j.util.Clique;

/**
 * List
 * @author Cristian Frăsinaru
 */
@Deprecated
class BacktrackColoring2 extends SimpleGraphAlgorithm
        implements VertexColoringAlgorithm {

    private Deque<Node> stack = new ArrayDeque<>();
    private Clique maxClique;
    private long timeout;
    private long startTime;
    private final boolean debug = false;

    public BacktrackColoring2(Graph graph) {
        super(graph);
    }

    public BacktrackColoring2(Graph graph, int timeout) {
        super(graph);
        this.timeout = timeout;
    }

    @Override
    public VertexColoring findColoring() {
        this.startTime = System.currentTimeMillis();
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getClique();
            if (debug) {
                System.out.println("Maximal clique size: " + maxClique.size());
            }
        }
        //VertexColoring coloring = new RecursiveLargestFirstColoring(graph).findColoring();
        VertexColoring coloring = new DSaturGreedyColoring(graph).findColoring();
        try {
            for (int j = coloring.numUsedColors() - 1, k = maxClique.size(); j >= k; j--) {
                //System.out.println("Trying " + j + " colors");
                var c = findColoring(j);
                if (c == null) {
                    //System.out.println("\t...Nope");
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
        VertexColoring bestColoring = null;
        while (!stack.isEmpty()) {
            if (timeout > 0 && System.currentTimeMillis() - startTime > timeout) {
                throw new AlgorithmTimeoutException();
            }
            Node node = stack.peek();
            if (debug) {
                System.out.println("Popped node " + node);
            }
            assert node.coloring.numColoredVertices() + node.candidates.size() == graph.numVertices();
            if (node.numColors() > numColors) {
                //cut this branch
                stack.poll();
                continue;
            }
            if (node.coloring.isComplete()) {
                //solution found
                stack.poll();
                if (debug) {
                    System.out.println("Solution: " + node.numColors() + " colors: " + node.coloring);
                }
                bestColoring = node.coloring;
                break;
            }
            Domain domain = node.minDomain;
            int v = graph.vertexAt(domain.index);

            //pick a color
            int color = domain.poll();
            if (domain.isEmpty()) {
                stack.poll();
            }
            //prepare the new domains
            List<Domain> newDomains = new ArrayList(node.candidates.size());
            for (Domain cand : node.candidates) {
                if (cand.index == domain.index) {
                    continue;
                }
                newDomains.add(new Domain(cand));
            }
            //create the new coloring
            var newColoring = new VertexColoring(graph, node.coloring);

            //color and propagate the decision v=c            
            //System.out.println("Decision " + v + "=" + color);
            newColoring.setColor(v, color);
            if (propagate(v, color, newColoring, newDomains)) {
                Node newNode = new Node(newDomains, newColoring);
                if (debug) {
                    System.out.println("New node " + newNode);
                }
                stack.push(newNode);
            }
        }
        return bestColoring;
    }

    //
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
        int k = maxClique.size();
        List<Domain> domains = new ArrayList<>(n - k);
        for (int i = 0; i < n; i++) {
            if (maxClique.contains(graph.vertexAt(i))) {
                continue;
            }
            domains.add(new Domain(i, numColors));
        }
        //
        VertexColoring coloring = new VertexColoring(graph);
        int color = 0;
        for (int v : maxClique.vertices()) {
            coloring.setColor(v, color);
            if (!propagate(v, color, coloring, domains)) {
                return false;
            }
            color++;
        }
        stack.push(new Node(domains, coloring));
        return true;
    }

    //
    private boolean propagate(int v, int color, VertexColoring coloring, List<Domain> domains) {
        //System.out.println("Propagate decision for " + v);
        for (Domain dom : domains) {
            int u = graph.vertexAt(dom.index);
            if (u == v || coloring.isColorSet(u)) {
                continue;
            }
            if (graph.containsEdge(v, u)) {
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
        }
        return true;
    }

    private class Node {

        VertexColoring coloring;
        List<Domain> candidates;
        Domain minDomain;

        public Node(List<Domain> domains, VertexColoring coloring) {
            this.candidates = domains;
            int min = Integer.MAX_VALUE;
            for (var it = candidates.listIterator(); it.hasNext();) {
                var dom = it.next();
                int v = graph.indexOf(dom.index);
                if (dom.size == 1) {
                    coloring.setColor(v, dom.values[0]);
                    it.remove();
                    continue;
                }
                if (dom.size < min) {
                    min = dom.size;
                    minDomain = dom;
                }
            }
            this.coloring = coloring;
        }

        public int numColors() {
            return coloring.numUsedColors();
        }

        @Override
        public String toString() {
            return (minDomain == null ? "" : minDomain.index) + "\n\t" + candidates + "\n\t" + coloring;
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

        public int value(int i) {
            return values[i];
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

