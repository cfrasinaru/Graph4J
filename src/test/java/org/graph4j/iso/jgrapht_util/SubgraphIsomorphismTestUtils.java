package org.graph4j.iso.jgrapht_util;

/*
 * (C) Copyright 2015-2023, by Fabian Späh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */

import org.jgrapht.*;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.*;

import java.util.*;

public class SubgraphIsomorphismTestUtils
{
    private static final boolean DEBUG = false;

    public static boolean allMatchingsCorrect(
            VF2SubgraphIsomorphismInspector<Integer, DefaultEdge> vf2, Graph<Integer, DefaultEdge> g1,
            Graph<Integer, DefaultEdge> g2)
    {
        boolean isCorrect = true;

        for (Iterator<GraphMapping<Integer, DefaultEdge>> mappings = vf2.getMappings();
             mappings.hasNext();)
        {
            isCorrect = isCorrect && isCorrectMatching(mappings.next(), g1, g2);
        }

        return isCorrect;
    }

    public static boolean isCorrectMatching(
            GraphMapping<Integer, DefaultEdge> rel, Graph<Integer, DefaultEdge> g1,
            Graph<Integer, DefaultEdge> g2)
    {
        Set<Integer> vertexSet = g2.vertexSet();

        for (Integer u1 : vertexSet) {
            Integer v1 = rel.getVertexCorrespondence(u1, false);

            for (Integer u2 : vertexSet) {
                if (u1 == u2)
                    continue;

                Integer v2 = rel.getVertexCorrespondence(u2, false);

                if (v1 == v2) {
                    return false;
                }

                if (g1.containsEdge(v1, v2) != g2.containsEdge(u1, u2)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Graph<Integer, DefaultEdge> randomSubgraph(
            Graph<Integer, DefaultEdge> g1, int vertexCount, long seed)
    {
        Map<Integer, Integer> map = new HashMap<>();
        Graph<Integer, DefaultEdge> g2 = new DefaultDirectedGraph<>(DefaultEdge.class);
        Set<Integer> vertexSet = g1.vertexSet();
        int n = vertexSet.size();

        Random rnd = new Random();
        rnd.setSeed(seed);

        for (int i = 0; i < vertexCount;) {
            for (Integer v : vertexSet) {
                if (rnd.nextInt(n) == 0 && !map.containsKey(v)) {
                    Integer u = i++;
                    g2.addVertex(u);
                    map.put(v, u);
                }
            }
        }

        for (DefaultEdge e : g1.edgeSet()) {
            Integer v1 = g1.getEdgeSource(e), v2 = g1.getEdgeTarget(e);
            if (map.containsKey(v1) && map.containsKey(v2)) {
                Integer u1 = map.get(v1), u2 = map.get(v2);
                g2.addEdge(u1, u2);
            }
        }

        return g2;
    }

    public static Graph<Integer, DefaultEdge> randomGraph(int vertexCount, int edgeCount, long seed)
    {
        Integer[] vertexes = new Integer[vertexCount];
        Graph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int i = 0; i < vertexCount; i++)
            g.addVertex(vertexes[i] = i);

        Random rnd = new Random();
        rnd.setSeed(seed);

        for (int i = 0; i < edgeCount;) {
            Integer source = vertexes[rnd.nextInt(vertexCount)],
                    target = vertexes[rnd.nextInt(vertexCount)];

            if (source != target && !g.containsEdge(source, target)) {
                g.addEdge(source, target);
                i++;
            }
        }

        return g;
    }

    private static ArrayList<ArrayList<Integer>> getPermutations(boolean[] vertexSet, int len)
    {
        ArrayList<ArrayList<Integer>> perms = new ArrayList<ArrayList<Integer>>();

        if (len <= 0) {
            perms.add(new ArrayList<Integer>());
            return perms;
        }

        for (int i = 0; i < vertexSet.length; i++) {
            if (!vertexSet[i]) {
                vertexSet[i] = true;
                ArrayList<ArrayList<Integer>> newPerms = getPermutations(vertexSet, len - 1);
                vertexSet[i] = false;

                for (ArrayList<Integer> perm : newPerms)
                    perm.add(i);

                perms.addAll(newPerms);
            }
        }

        return perms;
    }
}

