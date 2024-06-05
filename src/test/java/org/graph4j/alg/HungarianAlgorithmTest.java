package org.graph4j.alg;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.assignment.HungarianAlgorithm;
import org.graph4j.util.Matching;
import org.junit.jupiter.api.Test;

public class HungarianAlgorithmTest {

    @Test
    public void wikipediaTest() {
        final int ALICE = 0;
        final int BOB   = 1;
        final int DORA  = 2;
        final int CLEAN_BATHROOM = 3;
        final int SWEEP_FLOORS = 4;
        final int WASH_WINDOWS = 5;
        Graph g = GraphBuilder.numVertices(6).buildGraph();
        g.addWeightedEdge(ALICE, CLEAN_BATHROOM, 8);
        g.addWeightedEdge(BOB, CLEAN_BATHROOM, 5);
        g.addWeightedEdge(DORA, CLEAN_BATHROOM, 9);
        g.addWeightedEdge(ALICE, SWEEP_FLOORS, 4);
        g.addWeightedEdge(BOB, SWEEP_FLOORS, 2);
        g.addWeightedEdge(DORA, SWEEP_FLOORS, 4);
        g.addWeightedEdge(ALICE, WASH_WINDOWS, 7);
        g.addWeightedEdge(BOB, WASH_WINDOWS, 3);
        g.addWeightedEdge(DORA, WASH_WINDOWS, 8);

        HungarianAlgorithm h = new HungarianAlgorithm(g);
        Matching m = h.getMatching();
        int cost = 0;
        for (int[] edge : m.edges()) {
            cost += (int) g.getEdgeWeight(edge[0], edge[1]);
        }
        assert(cost == 15);
    }

}
