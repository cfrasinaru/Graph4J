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
package org.graph4j.vsp;

import org.graph4j.util.Domain;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.GraphUtils;
import org.graph4j.measures.GraphMeasures;

/**
 * WORK IN PROGRESS.
 *
 * Attempts at finding an optimum vertex separator using a systematic
 * exploration of the search space. The backtracking is implemented in a
 * non-recursive manner, using multiple threads.
 *
 * <p>
 * A time limit may be imposed. If the algorithm stops due to the time limit, it
 * will return the best separator found until then.
 *
 * @author Cristian Frăsinaru
 */
public class BacktrackVertexSeparator extends VertexSeparatorBase {

    private long timeLimit;
    private long startTime;
    private boolean timeExpired;
    private VertexSeparator solution;

    private List<Worker> workers;
    private long nodesExplored;
    private int minSepSize = Integer.MAX_VALUE; //the size of the best separator found so far
    private int vertexConnectivity;
    private int greedySepSize;
    private final int LEFT = 0, RIGHT = 1, SEP = 2;
    private final int UNKNOWN = 0, FAILURE = -1, POTENTIAL_SOLUTION = -2;

    public BacktrackVertexSeparator(Graph graph) {
        super(graph);
    }

    public BacktrackVertexSeparator(Graph graph, int maxShoreSize) {
        super(graph, maxShoreSize);
    }

    @Override
    public VertexSeparator getSeparator() {
        solve();
        return solution;
    }

    private void solve() {
        Node root = init();
        if (root == null) {
            return;
        }
        //int cores = 1;
        int cores = Runtime.getRuntime().availableProcessors();
        this.workers = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            var worker = new Worker(root);
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

    //before the algorithm starts
    private Node init() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        vertexConnectivity = GraphUtils.computeVertexConnectivity(graph);
        greedySepSize = new GreedyVertexSeparator(graph, maxShoreSize).getSeparator().separator().size();

        int[] values = {SEP, RIGHT, LEFT}; //reverse order for poll
        VertexSeparator sep = new VertexSeparator(graph, maxShoreSize);
        int n = graph.numVertices();
        Domain[] domains = new Domain[n];
        for (int i = 0; i < n; i++) {
            int v = graph.vertexAt(i);
            domains[i] = new Domain(v, values);
        }
        //
        int v = GraphMeasures.minDegreeVertex(graph);
        //int v = GraphMeasures.maxDegreeVertex(graph);
        domains[graph.indexOf(v)].remove(RIGHT); //avoid a simple symmetry
        //
        Node root = new Node(this, null, -1, -1, domains, sep);
        nodesExplored = 1;
        root.prepare();
        return root;
    }

    //adding a new value in the separator
    private int setValue(int v, int value, VertexSeparator sep) {
        switch (value) {
            case LEFT:
                if (sep.leftShore().size() >= maxShoreSize) {
                    return FAILURE;
                }
                sep.leftShore().add(v);
                break;
            case RIGHT:
                if (sep.rightShore().size() >= maxShoreSize) {
                    return FAILURE;
                }
                sep.rightShore().add(v);
                break;
            default:
                //if (sep.separator().size() >= minSepSize - 1 || sep.separator().size() >= greedySepSize) {
                if (sep.separator().size() >= minSepSize - 1) {
                    return FAILURE;
                }
                sep.separator().add(v);
                break;
        }
        //return checkCapacity(v, value, node);
        return UNKNOWN;
    }

    //after the assignment v=value, prune the other domains
    private int propagateAssignment(int v, int value, Node node, int[][] assignQueue) {
        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = value;
        //propagate
        while (i < j) {
            v = assignQueue[i][0];
            value = assignQueue[i][1];
            i++;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ret = 0;
                if (value == LEFT) {
                    //if we put v at left, it's neighbors cannot be at right
                    ret = removeValue(u, RIGHT, node, assignQueue, j);
                } else if (value == RIGHT) {
                    //if we put v at right, it's neighbors cannot be at left
                    ret = removeValue(u, LEFT, node, assignQueue, j);
                }
                if (ret != UNKNOWN) {
                    return ret;
                }
                j += ret;
            }
        }
        int ret = checkCapacity(v, value, node);
        if (ret != UNKNOWN) {
            return ret;
        }
        return checkRemaining(node);
        /*
        for (int u : graph.vertices()) {
            int ui = graph.indexOf(u);
            if (node.domains[ui].size() == 1) {
                int a = node.domains[ui].valueAt(0);
                setValue(node.separator, u, a);
            }
        }*/
    }

    //part of the propagation: removes a value from a domain
    //warning: domains references may change
    //returns -1 if the domain has becomes empty or infeasibility was detected
    //returns 1 it the domain has become singleton
    //returns 0 otherwise (the value was removed)
    private int removeValue(int u, int value, Node node, int[][] assignQueue, int queuePos) {
        int ui = graph.indexOf(u);
        var dom = node.domains[ui];
        int pos = dom.indexOf(value);
        if (pos < 0) {
            return 0;
        }
        if (dom.size() == 1) {
            return FAILURE;
        }
        if (node.parent != null && dom == node.parent.domains[ui]) {
            dom = new Domain(dom);
            node.domains[ui] = dom;
        }
        dom.removeAtPos(pos);
        node.propagator = true;
        if (dom.size() == 1) {
            int singleValue = dom.valueAt(0);
            int ret = setValue(u, singleValue, node.separator);
            if (ret == FAILURE) {
                return ret;
            }
            ret = checkCapacity(u, singleValue, node);
            if (ret != UNKNOWN) {
                return ret;
            }
            if (assignQueue != null) {
                assignQueue[queuePos][0] = u;
                assignQueue[queuePos][1] = singleValue;
            }
            return 1;
        }
        return 0;
    }

    //part of the propagation
    //if the size of left shore is maximum, remove LEFT from all domains
    //if the size of right shore is maximum, remove RIGHT from all domains
    //if the size of separator is maximum, remove SEP from all domains
    private int checkCapacity(int v, int value, Node node) {
        if (value == LEFT && node.separator.leftShore().size() == maxShoreSize) {
            for (int u : graph.vertices()) {
                if (u == v || node.separator.contains(u)) {
                    continue;
                }
                if (node.domains[graph.indexOf(u)].contains(RIGHT)
                        && node.separator.rightShore().size() < maxShoreSize) {
                    node.separator.rightShore().add(u);
                } else {
                    if (node.separator.separator().size() >= minSepSize) {
                        return FAILURE;
                    }
                    node.separator.separator().add(u);
                }
            }
            return POTENTIAL_SOLUTION;
        }
        if (value == RIGHT && node.separator.rightShore().size() == maxShoreSize) {
            for (int u : graph.vertices()) {
                if (u == v || node.separator.contains(u)) {
                    continue;
                }
                if (node.domains[graph.indexOf(u)].contains(LEFT)
                        && node.separator.leftShore().size() < maxShoreSize) {
                    node.separator.leftShore().add(u);
                } else {
                    if (node.separator.separator().size() >= minSepSize) {
                        return FAILURE;
                    }
                    node.separator.separator().add(u);
                }
            }
            return POTENTIAL_SOLUTION;
        }
        if (value == SEP && node.separator.separator().size() == minSepSize - 1) {
            //process the left shore
            for (int u : node.separator.leftShore().vertices()) {
                for (var it = graph.neighborIterator(u); it.hasNext();) {
                    int w = it.next();
                    if (node.separator.contains(w)) {
                        continue;
                    }
                    if (node.domains[graph.indexOf(w)].contains(LEFT)
                            && node.separator.leftShore().size() < maxShoreSize) {
                        node.separator.leftShore().add(w);
                    } else {
                        return FAILURE;
                    }
                }
            }
            //process the right shore
            for (int u : node.separator.rightShore().vertices()) {
                for (var it = graph.neighborIterator(u); it.hasNext();) {
                    int w = it.next();
                    if (node.separator.contains(w)) {
                        continue;
                    }
                    if (node.domains[graph.indexOf(w)].contains(RIGHT)
                            && node.separator.rightShore().size() < maxShoreSize) {
                        node.separator.rightShore().add(w);
                    } else {
                        return FAILURE;
                    }
                }
            }

            //System.out.println("POTENTIAL SOLUTION: \n" + node);
        }
        return POTENTIAL_SOLUTION;
    }

    //count how many can we put in the left and right shore    
    private int checkRemaining(Node node) {
        int n = graph.numVertices();
        int countLeft = 0, countRight = 0;
        for (int i = 0; i < n; i++) {
            int u = graph.vertexAt(i);
            if (node.separator.contains(u)) {
                continue;
            }
            if (node.domains[i].contains(LEFT)) {
                countLeft++;
            }
            if (node.domains[i].contains(RIGHT)) {
                countRight++;
            }
        }
        //no vertex left for the left shore
        if (node.separator.leftShore().size() + countLeft == 0) {
            return FAILURE;
        }
        //no vertex left for the right shore
        if (node.separator.rightShore().size() + countRight == 0) {
            return FAILURE;
        }
        return UNKNOWN;
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
    public long nodesExplored() {
        return nodesExplored;
    }

    //a thread exploring the search space
    private class Worker extends Thread {

        boolean running;
        final int[][] assignQueue;
        final Deque<Node> nodeStack;

        Worker(Node root) {
            assignQueue = new int[3 * graph.numVertices()][2];
            nodeStack = new ArrayDeque<>();
            nodeStack.push(root);
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                if (timeLimit > 0 && System.currentTimeMillis() - startTime > timeLimit) {
                    timeExpired = true;
                    return;
                }
                Node node;
                int v, value;
                synchronized (graph) {
                    node = nodeStack.peek();
                    if (node == null) {
                        node = findNode();
                        if (node == null) {
                            return;
                        }
                    }
                    if (node.separator.isComplete()) {
                        nodeStack.pop();
                        if (!node.separator.isValid()) {
                            continue;
                        }
                        //found a solution
                        int sepSize = node.separator.separator().size();
                        if (sepSize < minSepSize) {
                            minSepSize = sepSize;
                            solution = node.separator;
                            if (minSepSize == vertexConnectivity) {
                                break;
                            }
                        }
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
                        nodeStack.pop();
                        continue;
                    }
                    //pick a value in the node's domain
                    v = node.minDomain.vertex();
                    value = node.minDomain.poll();
                }

                //create the new domains (lazy)
                Domain[] newDomains = Arrays.copyOf(node.domains, node.domains.length);
                //the domain of the selected vertex v becomes singleton
                newDomains[graph.indexOf(v)] = new Domain(v, value);

                //create the new separator
                //assign value and propagate the assignment v=c
                var newSep = new VertexSeparator(node.separator);
                int ret = setValue(v, value, newSep);
                if (ret == FAILURE) {
                    continue;
                }
                Node newNode = new Node(BacktrackVertexSeparator.this, node, v, value, newDomains, newSep);
                nodesExplored++;
                ret = propagateAssignment(v, value, newNode, assignQueue);
                if (ret == FAILURE) {
                    continue;
                }
                newNode.prepare();
                synchronized (graph) {
                    nodeStack.push(newNode);
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
