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
import org.graph4j.util.IntPair;

/**
 * Attempts at finding the optimum coloring of a graph using a systematic
 * exploration of the search space. The backtracking is implemented in a
 * non-recursive manner.
 *
 * <p>
 * First, a maximal clique is computed that offers a lower bound <code>q</code>
 * of the chromatic number. The colors of the vertices in the maximal clique are
 * fixed before the backtracking algorithm starts.
 *
 * Secondly, an initial coloring is computed using a simple heuristic (DSatur).
 * This gives an upper bound <code>k</code>of the chromatic number.
 *
 * Next, the algorithm will attemtp to color the graph using a number of colors
 * ranging from <code>k-1</code> to <code>q</code>, determining the optimal
 * coloring.
 *
 * <p>
 * A time limit may be imposed. If the algorithm stops due to the time limit, it
 * will return the best coloring found until then.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackColoring extends SimpleGraphAlgorithm
        implements VertexColoringAlgorithm {

    private Deque<Node> stack = new ArrayDeque<>();
    private Clique maxClique;
    private long timeLimit;
    private long startTime;
    //private boolean include[][];

    public BacktrackColoring(Graph graph) {
        this(graph, 0);
    }

    public BacktrackColoring(Graph graph, int timeLimit) {
        super(graph);
        this.timeLimit = timeLimit;
        /*
        int n = graph.numVertices();
        this.include = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            int v = graph.vertexAt(i);
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                int u = graph.vertexAt(j);
                include[i][j] = (IntArrays.contains(graph.neighbors(v), graph.neighbors(u)));                
            }
        }*/
    }

    @Override
    public VertexColoring findColoring() {
        this.startTime = System.currentTimeMillis();
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getMaximalClique();
        }
        //VertexColoring coloring = new RecursiveLargestFirstColoring(graph).findColoring();
        VertexColoring coloring = new DSaturGreedyColoring(graph).findColoring();
        try {
            for (int j = coloring.numUsedColors() - 1, k = maxClique.size(); j >= k; j--) {
                //System.out.println("trying " + j + " colors");
                var c = findColoring(j);
                if (c == null) {
                    break;
                } else {
                    coloring = c;
                }
            }
        } catch (AlgorithmTimeoutException e) {
            System.out.println("Time limit expired.");
        }
        return coloring;
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        if (!init(numColors)) {
            return null;
        }
        while (!stack.isEmpty()) {
            if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) {
                throw new AlgorithmTimeoutException();
            }
            Node node = stack.peek();
            if (node.coloring.numUsedColors() > numColors) {
                stack.poll();
                continue;
            }
            if (node.coloring.isComplete()) {
                stack.poll();
                if (!node.coloring.isProper()) {
                    continue;
                }
                return node.coloring; //solution found
            }
            //select the node's domain
            Domain domain = node.minDomain;
            if (domain.isEmpty()) {
                stack.poll();
                /*
                if (node.parent != null) {
                    propagateFailure(node.vertex, node.color, node.parent.coloring, node.parent.domains);
                }*/
                continue;
            }

            //pick a color in the domain
            int color = domain.poll();

            //create the new domains
            //the domain of the selected vertex v becomes singleton
            Domain[] newDomains = Arrays.copyOf(node.domains, node.domains.length);
            for (int i = 0; i < newDomains.length; i++) {
                newDomains[i] = new Domain(node.domains[i]);
            }
            int v = domain.vertex;
            int vi = graph.indexOf(v);
            newDomains[vi].size = 1;
            newDomains[vi].colors = new int[]{color};

            //create the new coloring
            //color and propagate the decision v=c            
            var newColoring = new VertexColoring(graph, node.coloring);
            newColoring.setColor(v, color);
            if (propagateDecision(v, color, newColoring, newDomains)) {
                Node newNode = new Node(v, color, newDomains, newColoring, node);
                stack.push(newNode);
            } else {
                //propagateFailure(v, color, node.coloring, node.domains);
            }
        }
        return null;
    }

    //before findColoring
    private boolean init(int numColors) {
        if (maxClique == null) {
            maxClique = new MaximalCliqueFinder(graph).getMaximalClique();
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
                domains[i] = new Domain(v, 1);
                domains[i].colors[0] = color;
                color++;
                continue;
            }
            domains[i] = new Domain(v, numColors);
        }
        for (int v : maxClique.vertices()) {
            if (!propagateDecision(v, coloring.getColor(v), coloring, domains)) {
                return false;
            }
            color++;
        }
        stack.push(new Node(-1, -1, domains, coloring, null)); //root
        return true;
    }

    private boolean propagateDecision(int v, int color,
            VertexColoring coloring, Domain[] domains) {
        Deque<IntPair> changes = new ArrayDeque<>();
        changes.add(new IntPair(v, color));
        while (!changes.isEmpty()) {
            var change = changes.poll();
            v = change.first();
            color = change.second();
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
                    changes.offer(new IntPair(u, dom.colors[0]));
                }
            }
        }
        return true;
    }

    //after the decision v=color
    private boolean propagateDecision2(int v, int color,
            VertexColoring coloring, Domain[] domains) {
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
                if (!propagateDecision(u, dom.colors[0], coloring, domains)) {
                    return false;
                }
            }
        }
        return true;
    }

    //after a failure v=color
    @Deprecated
    private boolean propagateFailure(int v, int color,
            VertexColoring coloring, Domain[] domains) {
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            if (neighborsInclude(v, u)) {
                domains[graph.indexOf(u)].remove(color);
            }
        }
        return false;
    }

    //checks if the neighbors of v include the neighbors of u
    private boolean neighborsInclude(int v, int u) {
        //return include[graph.indexOf(v)][graph.indexOf(u)];
        return false;
    }

    //a node in the systematic search tree
    private class Node {

        int vertex;
        int color;
        VertexColoring coloring;
        Domain[] domains;
        Domain minDomain;
        Node parent;

        public Node(int vertex, int color, Domain[] domains, VertexColoring coloring, Node parent) {
            this.vertex = vertex;
            this.color = color;
            this.domains = domains;
            this.coloring = coloring;
            this.parent = parent;
            //resolve singleton domains
            //find the domain with the minimum size
            int min = Integer.MAX_VALUE;
            for (var dom : domains) {
                int v = graph.indexOf(dom.vertex);
                if (coloring.isColorSet(v)) {
                    continue;
                }
                if (dom.size == 1) {
                    coloring.setColor(v, dom.colors[0]);
                    continue;
                }
                if (dom.size < min) {
                    min = dom.size;
                    minDomain = dom;
                }
            }
            if (minDomain != null) {
                //remove symmetrical colors from minDomain
                removeSymmetricalColors();
                //if (!hasUncoloredNeighbors()) System.out.println("???");
            }
        }

        private int colorsUsed(Domain dom) {
            int count = 0;
            for (int c : dom.colors) {
                if (coloring.isColorUsed(c)) {
                    count++;
                }
            }
            return count;
        }

        private boolean hasUncoloredNeighbors() {
            int v = minDomain.vertex;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                if (!coloring.isColorSet(it.next())) {
                    return true;
                }
            }
            return false;
        }

        //remove from minDomain all colors that have not been used before
        //except one
        private void removeSymmetricalColors() {
            int count = 0;
            int i = 0;
            while (i < minDomain.size) {
                int c = minDomain.colors[i];
                if (!coloring.isColorUsed(c)) {
                    count++;
                    if (count > 1) {
                        minDomain.removeAtPos(i);
                        continue;
                    }
                }
                i++;
            }
        }

        @Override
        public String toString() {
            return (minDomain == null ? "" : minDomain.vertex)
                    + "\n\t" + Arrays.toString(domains) + "\n\t" + coloring;
        }

    }

    //colors available to a vertex
    private class Domain {

        int vertex;
        Domain parent;
        int[] colors;
        int[] positions; //position of a color in the colors array
        int size;

        public Domain(Domain parent) {
            this.parent = parent;
            this.vertex = parent.vertex;
            this.colors = parent.colors;
            this.positions = parent.positions;
            this.size = parent.size;
        }

        public Domain(int vertex, int numColors) {
            this.vertex = vertex;
            colors = new int[numColors];
            positions = new int[numColors];
            for (int i = 0; i < numColors; i++) {
                colors[i] = numColors - i - 1;
                positions[colors[i]] = i;
            }
            size = numColors;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int poll() {
            int color = colors[size - 1];
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

        public int indexOf(int color) {
            return positions[color];
            /*
            for (int i = 0; i < size; i++) {
                if (colors[i] == color) {
                    return i;
                }
            }
            return -1;
             */
        }

        public void removeAtPos(int pos) {
            if (parent != null && this.colors == parent.colors) {
                this.colors = Arrays.copyOf(parent.colors, parent.colors.length);
                this.positions = Arrays.copyOf(parent.positions, parent.positions.length);
            }
            positions[colors[pos]] = -1;
            if (pos != size - 1) {
                colors[pos] = colors[size - 1];
                positions[colors[pos]] = pos;
            }
            size--;
        }

        @Override
        public String toString() {
            return "dom(" + vertex + ")=" + Arrays.toString(Arrays.copyOf(colors, size));
        }

    }

}
