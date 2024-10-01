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
package org.graph4j.coloring;

import org.graph4j.util.Domain;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import org.graph4j.Graph;

/**
 * Attempts at finding the optimum coloring of a graph using a systematic
 * exploration of the search space. The backtracking is implemented in a
 * non-recursive manner, using multiple threads.
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
public abstract class BacktrackColoringBase extends ExactColoringBase {

    protected List<Worker> workers;
    protected long nodesExplored;

    public BacktrackColoringBase(Graph graph) {
        super(graph);
    }

    public BacktrackColoringBase(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    public BacktrackColoringBase(Graph graph, Coloring initialColoring) {
        super(graph, initialColoring);
    }

    public BacktrackColoringBase(Graph graph, Coloring initialColoring, long timeLimit) {
        super(graph, initialColoring, timeLimit);
    }

    @Override
    protected void solve(int numColors) {
        solutions = new HashSet<>();
        Node root = init(numColors);
        if (root == null) {
            return;
        }
        //int cores = 1;
        int cores = Runtime.getRuntime().availableProcessors();
        this.workers = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            var worker = new Worker(numColors, root);
            workers.add(worker);
            worker.start();
        }
        for (var worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException ex) {
            }
        }
    }

    //returns false if it detects infeasibility
    protected boolean prepareRootColoring(Coloring rootColoring, int numColors) {
        var maxClique = getMaximalClique();
        if (maxClique.size() > numColors) {
            return false;
        }
        int color = 0;
        for (int v : maxClique.vertices()) {
            rootColoring.setColor(v, color++);
        }
        return true;
    }

    //before the coloring starts
    protected Node init(int numColors) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        Coloring rootColoring = new Coloring(graph, initialColoring);
        if (solutionsLimit == 1 && initialColoring.isEmpty()) {
            if (!prepareRootColoring(rootColoring, numColors)) {
                return null;
            }
        }
        int[] availableColors = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            availableColors[i] = numColors - i - 1;
        }
        int n = graph.numVertices();
        Domain[] domains = new Domain[n];
        for (int i = 0; i < n; i++) {
            int v = graph.vertexAt(i);
            int color = rootColoring.getColor(v);
            if (color >= 0) {
                domains[i] = new Domain(v, color);
            } else {
                domains[i] = new Domain(v, availableColors);
            }
        }
        Node root = createNode(null, -1, -1, domains, rootColoring);
        nodesExplored = 1;
        for (int v : rootColoring.getColoredVertices()) {
            int color = rootColoring.getColor(v);
            if (!propagateAssignment(v, color, root, new int[n * numColors][2])) {
                return null;
            }
            color++;
        }
        root.prepare();
        return root;
    }

    protected Node createNode(Node parent, int vertex, int color, Domain[] domains, Coloring coloring) {
        return new Node(this, parent, vertex, color, domains, coloring, true);
    }

    //after the assignment v=color, prune the other domains
    protected abstract boolean propagateAssignment(int v, int color, Node node, int[][] assignQueue);

    //part of the propagation: removes a color from a domain
    //warning: domains references may change
    //returns -1 if the domain has become empty or infeasibility was detected
    //returns 1 it the domain has become singleton
    //returns 0 otherwise (the color was removed)
    protected int removeColor(int u, int color, Node node, int[][] assignQueue, int queuePos) {
        int ui = graph.indexOf(u);
        var dom = node.domains[ui];
        int pos = dom.indexOf(color);
        if (pos < 0) {
            return 0;
        }
        if (dom.size() == 1) {
            return -1;
        }
        if (node.parent != null && dom == node.parent.domains[ui]) {
            dom = new Domain(dom);
            node.domains[ui] = dom;
        }
        dom.removeAtPos(pos);
        node.propagator = true;
        if (dom.size() == 1) {
            int singleColor = dom.valueAt(0);
            node.coloring.setColor(u, singleColor);
            if (!propagationForcedColor(u, singleColor, node)) {
                return -1;
            }
            assignQueue[queuePos][0] = u;
            assignQueue[queuePos][1] = singleColor;
            return 1;
        }
        return 0;
    }

    //invoked when u must be colored with color as a result of propagation
    //if infeasibility is detected, it returns false
    protected boolean propagationForcedColor(int u, int color, Node node) {
        return true;
    }

    //invoke when a node was proven infeasible
    //only for necsp
    //the node was the result of an assignment x=a
    //consider the other nodes to follow x=b
    //look at the domains of uncolored vertices of the parent
    //if all the domains including b, include also a (*)
    //node x=b can be discarded
    //suppose that there is a solution with x=b
    //replace color b with a - it is possible cf (*)
    //this would be a solution with x=a, which just failed
    @Deprecated
    protected void propagateFailure(Node node) {
        Node parent = node.parent;
        if (parent == null) {
            return;
        }
        int v = node.vertex;
        int color = node.color;
        assert v == parent.minDomain.vertex();
        //v is the pivot of node.parent
        //check all the colors in the domain of v
        int i = 0;
        Domain vDomain = parent.domain(graph.indexOf(v));
        if (vDomain.size() <= 1) {
            return;
        }
        nextColor:
        while (i < vDomain.size()) {
            int other = vDomain.valueAt(i);
            if (other == color) {
                continue;
            }
            for (int u : graph.vertices()) {
                if (parent.coloring.isColorSet(u)) {
                    continue;
                }
                Domain uDomain = parent.domain(graph.indexOf(u));
                if (uDomain.contains(other) && !uDomain.contains(color)) {
                    i++;
                    continue nextColor;
                }
            }
            //remove other from v's domain
            vDomain.removeAtPos(i);
        }
    }

    //finds a node for a jobless thread
    private Node findNode() {
        List<Worker> aux = new ArrayList<>(workers);
        Collections.shuffle(aux);
        for (Worker w : aux) {
            Node node = w.offerNode();
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    /**
     *
     * @return the number of nodes explored during the search.
     */
    @Deprecated
    private long nodesExplored() {
        return nodesExplored;
    }

    //a thread exploring the search space
    protected class Worker extends Thread {

        boolean running;
        int numColors;
        final int[][] assignQueue;
        final Deque<Node> nodeStack;

        Worker(int numColors, Node root) {
            this.numColors = numColors;
            assignQueue = new int[graph.numVertices() * numColors][2];
            nodeStack = new ArrayDeque<>();
            nodeStack.push(root);
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                if (solutions.size() >= solutionsLimit) {
                    return;
                }
                if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) {
                    timeExpired = true;
                    return;
                }
                Node node;
                int v, color;
                synchronized (graph) {
                    node = nodeStack.peek();
                    if (node == null) {
                        node = findNode();
                        if (node == null) {
                            return;
                        }
                    }
                    if (node.coloring.isComplete()) {
                        nodeStack.pop();
                        if (!isValid(node.coloring)) {
                            continue;
                        }
                        //found a solution
                        solutions.add(node.coloring);
                        continue;
                    }
                    assert node.minDomain != null;

                    if (node.failed && !nodeStack.isEmpty()) {
                        nodeStack.pop();
                        continue;
                    }
                    if (node.minDomain.size() == 0) {
                        //the current node has failed, remove it from stack
                        //when popping a non propagator
                        //it's parent should pe popped too
                        if (!node.propagator && node.parent != null) {
                            node.parent.failed = true;
                        }
                        //propagateFailure(node);
                        nodeStack.pop();
                        continue;
                    }
                    //pick a color in the node's domain
                    v = node.minDomain.vertex();
                    color = node.minDomain.poll();
                }

                //create the new domains (lazy)
                Domain[] newDomains = Arrays.copyOf(node.domains, node.domains.length);
                //the domain of the selected vertex v becomes singleton
                newDomains[graph.indexOf(v)] = new Domain(v, color);

                //create the new coloring
                //color and propagate the assignment v=c
                var newColoring = new Coloring(graph, node.coloring);
                newColoring.setColor(v, color);

                Node newNode = createNode(node, v, color, newDomains, newColoring);
                nodesExplored++;
                if (propagateAssignment(v, color, newNode, assignQueue)) {
                    newNode.prepare();
                    synchronized (graph) {
                        nodeStack.push(newNode);
                    }
                }
            }
        }

        private Node offerNode() {
            synchronized (graph) {
                if (nodeStack.isEmpty()) {
                    return null;
                }
                Node selected = null;
                Node node = nodeStack.peek();
                while (node != null) {
                    if (node.minDomain != null && node.minDomain.size() > 0) {
                        selected = node;
                    }
                    node = node.parent;
                }
                return selected;
            }
        }
    }

}
