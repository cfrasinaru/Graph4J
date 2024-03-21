package org.graph4j.iso.general.improved;

import org.graph4j.Digraph;

import java.util.List;

public class UllmanSubState extends AbstractUllmanState {
    public UllmanSubState(Digraph g1, Digraph g2) {
        super(g1, g2);
    }

    public UllmanSubState(UllmanSubState s) {
        super(s);
    }
    @Override
    public boolean exactOrSubgraphIsomorphismCompatibilityCheck(int v1, int v2) {
        return o1.indegree(v1) <= o2.indegree(v2) &&
                o1.outdegree(v1) <= o2.outdegree(v2);
    }

    @Override
    public boolean isGoal() {
        return core_len == n1;
    }

    @Override
    public boolean isDead() {
        if (n1 > n2)
            return true;

        for (int i = 0; i < n1; i++){
            List<Integer> candidates = getCandidates(i);
            if (candidates.isEmpty())
                return true;
        }
        return false;
    }
}

