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
package org.graph4j.alg.coloring.bb;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.graph4j.Graph;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.ExactColoringBase;
import org.graph4j.util.Domain;
import org.graph4j.util.IntArrays;

/**
 * Useless.
 *
 * @author Cristian Frăsinaru
 */
class ZykovColoring extends ExactColoringBase {

    private Deque<Node> stack;
    private int[][] assignQueue;
    private final boolean debug = false;

    public ZykovColoring(Graph graph) {
        super(graph);
    }

    public ZykovColoring(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected ZykovColoring getInstance(Graph graph, long timeLimit) {
        return new ZykovColoring(graph, timeLimit);
    }   
    
    @Override
    public void solve(int numColors) {
        solutions = new HashSet<>();
        if (numColors > 3) {
            //return new ParallelBacktrackColoring(graph).findColoring(numColors);
        }
        compute(numColors);
    }

    private Coloring compute(int numColors) {
        if (!init(numColors)) {
            return null;
        }
        var root = stack.peek();
        int ret = check(root, numColors);
        if (ret < 0) {
            return null;
        }
        if (ret > 0) {
            return root.coloring;
        }

        Node best = null;
        while (!stack.isEmpty()) {
            var node = stack.peek();
            var g = node.graph;
            int n = g.numVertices();

            assert !node.coloring.isComplete();

            if (debug) {
                System.out.println(" --- PEEK --- stack size=" + stack.size());
                System.out.println("Graph vertices= " + node.graph.numVertices() + " edges=" + node.graph.numEdges());
                System.out.println("\tColored vertices=" + node.coloring.numColoredVertices());
                //System.out.println("\t" + Arrays.toString(node.domains));
            }

            /*
            if (n < 87) {
                //var col = new RecursiveLargestFirstColoring(g).findColoring(numColors);
                var bt = new ParallelBacktrackColoring(g, 1000);
                var col = bt.findColoring(numColors);
                if (!bt.isTimeExpired()) {
                    if (col != null) {
                        best = node;
                        best.coloring = col;
                        break;
                    } else {
                        stack.pop();
                        continue;
                    }
                }
            }*/
            //choose a pair of vertices that are not adjacent
            int bestv = -1, bestu = -1;
            long bestScore = Long.MAX_VALUE;
            for (int i = 0; i < n - 1; i++) {
                int v = g.vertexAt(i);
                //System.out.println("\t" + v + ": " + node.domains[i]);
                if (g.degree(v) == n - 1) {
                    continue;
                }
                var dom1 = node.domains[i];
                //if (dom1.size() == 3) continue;
                //System.out.println("looking at v=" + v + ": " + node.domains[i]);
                for (int j = i + 1; j < n; j++) {
                    int u = g.vertexAt(j);
                    if (g.containsEdge(v, u)) {
                        continue;
                    }
                    var dom2 = node.domains[j];
                    //if (dom2.size() == 3) continue;
                    if (dom1.size() == 1 && dom2.size() == 1) {
                        continue;
                    }
                    //System.out.println("\tlooking at u=" + u + ": " + node.domains[j]);
                    boolean reverse = false;
                    if (dom1.size() > dom2.size()) {
                        var aux = dom1;
                        dom1 = dom2;
                        dom2 = aux;
                        reverse = true;
                    }
                    long score = 1L * n * n * (dom1.size() - 1)
                            + n * (dom2.size() - 2)
                            + (dom2.contains(dom1.valueAt(0)) ? 0 : 1);
                    if (score < bestScore) {
                        bestScore = score;
                        bestv = reverse ? u : v;
                        bestu = reverse ? v : u;
                        if (score == 0) {
                            break;
                        }
                    }
                }
            }
            assert bestv != -1;
            Domain domv = node.domains[g.indexOf(bestv)];
            Domain domu = node.domains[g.indexOf(bestu)];
            if (domv.size() > domu.size()) {
                var aux = domv;
                domv = domu;
                domu = aux;
            }
            assert domv.size() <= domu.size();
            var inter = IntArrays.intersection(domv.values(), domu.values());

            if (debug) {
                System.out.println("Selected " + domv + ", " + domu);
            }
            //contract vertices
            Node newNode = null;
            if (inter.length > 0) {
                var g2 = g.copy();
                int w = g2.contractVertices(bestv, bestu); //minus one vertex
                var newColoring2 = new Coloring(g2, node.coloring);
                var newDomains2 = new Domain[g2.numVertices()];
                for (int v : g2.vertices()) {
                    if (v != w) {
                        newDomains2[g2.indexOf(v)] = new Domain(v, node.domains[g.indexOf(v)].values());
                    }
                }
                newNode = new Node(node, g2, newDomains2, newColoring2, bestv, bestu, w);
                if (inter.length == 1) {
                    int color = inter[0];
                    newColoring2.setColor(w, color);
                    newDomains2[g2.indexOf(w)] = new Domain(w, color);
                    if (!propagateAssignment(w, color, newNode)) {
                        newNode = null;
                    }
                } else {
                    newDomains2[g2.indexOf(w)] = new Domain(w, inter);
                    //TODO propagate2                
                }
            }

            //add edge
            boolean nodeFailed = false;
            g.addEdge(bestv, bestu); //the same number of vertices
            if (domv.size() == 1) {
                nodeFailed = !propagateAssignment(bestv, domv.valueAt(0), node);
            }

            ret = nodeFailed ? -1 : check(node, numColors);
            if (ret < 0) {
                stack.pop();
            } else if (ret > 0) {
                best = node;
                assert best.coloring.isProper();
                break;
            }

            if (newNode != null) {
                ret = check(newNode, numColors);
                if (ret > 0) {
                    best = newNode;
                    assert best.coloring.isProper();
                    break;
                } else if (ret == 0) {
                    stack.push(newNode);
                }
            }
        }
        if (best == null) {
            return null;
        }
        //restore the coloring of the graph
        Map<Integer, Integer> map = new HashMap<>();
        for (int v : best.graph.vertices()) {
            map.put(v, best.coloring.getColor(v));
        }
        while (best != null) {
            if (best.w != -1) {
                assert best != null;
                assert best.coloring != null;
                int color = map.get(best.w);
                map.put(best.v, color);
                map.put(best.u, color);
            }
            best = best.parent;
        }
        var coloring = new Coloring(graph);
        for (int v : graph.vertices()) {
            coloring.setColor(v, map.get(v));
        }
        assert coloring.isProper();
        return coloring;
    }

    private int check(Node node, int numColors) {
        if (node.coloring.isComplete()) {
            return node.coloring.isProper() ? 1 : -1;
        }
        var g = node.graph;
        if (g.isComplete()) {
            if (g.numVertices() > numColors) {
                return -1;
            }
            int color = 0;
            for (int v : g.vertices()) {
                if (node.coloring.isColorSet(v)) {
                    continue;
                }
                while (node.coloring.isColorUsed(color)) {
                    color++;
                }
                node.coloring.setColor(v, color);
            }
            return 1;
        }
        var clique = new MaximalCliqueFinder(g).getMaximalClique();
        if (clique.numVertices() > numColors) {
            return -1;
        }
        return 0;
    }

    //before findColoring
    private boolean init(int numColors) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        var maxClique = getMaximalClique();
        if (maxClique.size() > numColors) {
            return false;
        }
        int[] initialColors = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            initialColors[i] = numColors - i - 1;
        }
        var g = graph.copy();
        int n = g.numVertices();
        this.stack = new ArrayDeque<>();
        this.assignQueue = new int[n * numColors][2];
        Domain[] domains = new Domain[n];
        Coloring coloring = new Coloring(g);
        int color = 0;
        for (int i = 0; i < n; i++) {
            int v = g.vertexAt(i);
            if (maxClique.contains(v)) {
                coloring.setColor(v, color);
                domains[i] = new Domain(v, color);
                color++;
                continue;
            }
            domains[i] = new Domain(v, initialColors);
        }
        Node root = new Node(null, g, domains, coloring, -1, -1, -1);
        for (int v : maxClique.vertices()) {
            if (!propagateAssignment(v, coloring.getColor(v), root)) {
                return false;
            }
            color++;
        }
        //root.prepare();
        stack.push(root);
        return true;
    }

    //after the assignment v=color, prunte the other domains
    private boolean propagateAssignment(int v, int color, Node node) {
        var coloring = node.coloring;
        var domains = node.domains;
        int i = 0, j = 1;
        assignQueue[i][0] = v;
        assignQueue[i][1] = color;
        while (i < j) {
            v = assignQueue[i][0];
            color = assignQueue[i][1];
            i++;
            for (var it = node.graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (coloring.isColorSet(u)) {
                    continue;
                }
                int ui = node.graph.indexOf(u);
                var dom = domains[ui];
                int pos = dom.indexOf(color);
                if (pos < 0) {
                    continue;
                }
                if (dom.size() == 1) {
                    return false;
                }
                dom.removeAtPos(pos);
                if (dom.size() == 1) {
                    assignQueue[j][0] = u;
                    assignQueue[j][1] = dom.valueAt(0);
                    j++;
                }
            }
        }
        //resolve singleton domains
        for (var dom : domains) {
            int u = dom.vertex();
            if (coloring.isColorSet(u)) {
                continue;
            }
            if (dom.size() == 1) {
                coloring.setColor(u, dom.valueAt(0));
            }
        }
        return true;
    }

    //vu is already an edge
    private boolean contains4Clique(Graph g, int v, int u) {
        for (var it1 = g.neighborIterator(v); it1.hasNext();) {
            int w1 = it1.next();
            if (w1 == u) {
                continue;
            }
            if (!g.containsEdge(w1, u)) {
                return false;
            }
            for (var it2 = g.neighborIterator(w1); it2.hasNext();) {
                int w2 = it2.next();
                if (w2 == v || w2 == u) {
                    continue;
                }
                if (!g.containsEdge(w1, v) || !g.containsEdge(w1, u)) {
                    return false;
                }
            }
        }
        return false;
    }

}
