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
package org.graph4j.alg.coloring.exact;

import org.graph4j.alg.coloring.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.graph4j.Graph;
import org.graph4j.alg.clique.MaximalCliqueFinder;

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
public class ParallelBacktrackColoring extends ExactColoringBase {

    private Set<VertexColoring> solutions;
    private boolean findAll;
    private List<Worker> workers;

    public ParallelBacktrackColoring(Graph graph) {
        super(graph);
    }

    public ParallelBacktrackColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    public ParallelBacktrackColoring(Graph graph, VertexColoring initialColoring) {
        super(graph, initialColoring);
    }

    public ParallelBacktrackColoring(Graph graph, VertexColoring initialColoring, long timeLimit) {
        super(graph, initialColoring, timeLimit);
    }

    @Override
    protected ParallelBacktrackColoring getInstance(Graph graph, long timeLimit) {
        return new ParallelBacktrackColoring(graph, initialColoring, timeLimit);
    }

    /**
     *
     * @param numColors the maximum number of colors to be used.
     * @return all the vertex colorings of the graph.
     */
    public Set<VertexColoring> findAllColorings(int numColors) {
        findAll = true;
        compute(numColors);
        return solutions;
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        findAll = false;
        compute(numColors);
        return solutions.isEmpty() ? null : solutions.iterator().next();
    }

    private void compute(int numColors) {
        //TODO
        //for small numColors and 
        //if there is a small vertex separator (A,C,B)
        //findAllColorings(C)
        //color A U C and B U C with the initial coloring given by C    
        /*
        if (numColors == 3 && graph.numVertices() >= 100) {
            var sep = new GreedyVertexSeparator(graph).getSeparator();
            if (sep.separator().size() <= 10) {
                var a = sep.leftShore();
                var b = sep.rightShore();
                var c = sep.separator();
                var g0 = graph.subgraph(c);
                var g1 = graph.subgraph(a.union(c.vertices()));
                var g2 = graph.subgraph(b.union(c.vertices()));                
                var all = new ParallelBacktrackColoring(g0).findAllColorings(numColors);
                int x = 0;
                for(var col0 : all) {
                    System.out.println(x++);
                    var col1 = new ParallelBacktrackColoring(g1, col0).findColoring(numColors);
                    if (col1 != null) {
                        var col2 = new ParallelBacktrackColoring(g2, col0).findColoring(numColors);
                        if (col2 != null) {
                            System.out.println("Bingo!");
                            return;
                        }
                    }
                }
                System.out.println("NOPE");
            }
        }*/
        
        solutions = new HashSet<>();
        Node root = init(numColors);
        if (root == null) {
            return;
        }
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

    //before findColoring
    private Node init(int numColors) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        if (!findAll && initialColoring.isEmpty()) {
            if (maxClique == null) {
                maxClique = new MaximalCliqueFinder(graph).getMaximalClique();
            }
            if (maxClique.size() > numColors) {
                return null;
            }            
            int color = 0;
            for (int v : maxClique.vertices()) {
                initialColoring.setColor(v, color++);
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
            int color = initialColoring.getColor(v);
            if (color >= 0) {
                domains[i] = new Domain(v, color);
            } else {
                domains[i] = new Domain(v, availableColors);
            }
        }
        Node root = new Node(graph, null, -1, -1, domains, initialColoring, findAll);
        for (int v : initialColoring.getColoredVertices()) {
            int color = initialColoring.getColor(v);
            if (!propagateAssignment(v, color, root, new int[n * numColors][2])) {
                return null;
            }
            color++;
        }
        root.prepare();
        return root;
    }

    //after the assignment v=color, prunte the other domains
    private boolean propagateAssignment(int v, int color, Node node, int[][] assignQueue) {
        var coloring = node.coloring;
        var domains = node.domains;
        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = color;
        while (i < j) {
            v = assignQueue[i][0];
            color = assignQueue[i][1];
            i++;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (coloring.isColorSet(u)) {
                    continue;
                }
                int ui = graph.indexOf(u);
                var dom = domains[ui];
                int pos = dom.indexOf(color);
                if (pos < 0) {
                    continue;
                }
                if (dom.size == 1) {
                    return false;
                }
                if (node.parent != null && dom == node.parent.domains[ui]) {
                    dom = new Domain(dom);
                    domains[ui] = dom;
                }
                dom.removeAtPos(pos);
                if (dom.size == 1) {
                    assignQueue[j][0] = u;
                    assignQueue[j][1] = dom.values[0];
                    j++;
                }
            }
        }

        return true;
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

    private class Worker extends Thread {

        boolean running;
        int numColors;
        final int[][] assignQueue;
        final Deque<Node> nodeStack;

        public Worker(int numColors, Node root) {
            this.numColors = numColors;
            assignQueue = new int[graph.numVertices() * numColors][2];
            nodeStack = new ArrayDeque<>();
            nodeStack.push(root);
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                if (!findAll && !solutions.isEmpty()) {
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
                        if (!node.coloring.isProper()) {
                            continue;
                        }
                        //found a solution
                        solutions.add(node.coloring);
                        if (findAll) {
                            continue;
                        }
                        return;
                    }
                    assert node.minDomain != null;
                    if (node.minDomain.size == 0) {
                        nodeStack.pop();
                        continue;
                    }
                    //pick a color in the node's domain
                    v = node.minDomain.vertex;
                    color = node.minDomain.poll();
                }

                //create the new domains (lazy)
                Domain[] newDomains = Arrays.copyOf(node.domains, node.domains.length);
                //the domain of the selected vertex v becomes singleton
                newDomains[graph.indexOf(v)] = new Domain(v, color);

                //create the new coloring
                //color and propagate the assignment v=c
                var newColoring = new VertexColoring(graph, node.coloring);
                newColoring.setColor(v, color);

                Node newNode = new Node(graph, node, v, color, newDomains, newColoring, findAll);
                if (propagateAssignment(v, color, newNode, assignQueue)) {
                    newNode.prepare();
                    synchronized (graph) {
                        //System.out.println(newNode);
                        nodeStack.push(newNode);
                    }
                } else {
                    //System.out.println("Failure; colored vertices=" + node.coloring.numColoredVertices());
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
                    if (node.minDomain != null && node.minDomain.size > 0) {
                        selected = node;
                    }
                    node = node.parent;
                }
                return selected;
            }
        }
    }

}
