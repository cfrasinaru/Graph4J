package org.graph4j.iso.general.improved;

import org.graph4j.DirectedMultigraph;
import org.graph4j.DirectedPseudograph;
import org.graph4j.Edge;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.general.State;

public abstract class AbstractState implements State {
    protected OrderedDigraph o1, o2;
    protected int n1, n2;
    protected int core_len;         // length of current mapping
    protected int[] core_1;         // forward mapping
    protected int[] core_2;         // backward mapping

    public int getCoreLen()
    {
        return core_len;
    }

    @Override
    public IsomorphicGraphMapping getMapping() {
        int[] forwardMap = new int[n1];
        int[] backwardMap = new int[n2];

        for (int v1 : o1.getGraph().vertices()) {
            int index_1 = o1.getOrder(v1);
            int index_2 = core_1[index_1];

            forwardMap[o1.getGraph().indexOf(v1)] = o2.getVertex(index_2);
        }

        for (int v2 : o2.getGraph().vertices()) {
            int index_2 = o2.getOrder(v2);
            int index_1 = core_2[index_2];
            if (index_1 != NULL_NODE) {
                backwardMap[o2.getGraph().indexOf(v2)] = o1.getVertex(index_1);
            }
        }

        return new IsomorphicGraphMapping(forwardMap, backwardMap, o1.getGraph(), o2.getGraph());
    }

    /**
     * If the graphs allow self loops, the vertices must have the same number of self loops
     * @param v1 vertex from the first graph
     * @param v2 candidate vertex from the second graph
     */
    protected boolean compatibleVertices(int v1, int v2) {
        if (o1.getGraph() instanceof DirectedPseudograph ps1 &&
                o2.getGraph() instanceof DirectedPseudograph ps2){
            return ps1.selfLoops(o1.getVertex(v1)) == ps2.selfLoops(o2.getVertex(v2));
        }
        return true;
    }


    /**
     * If the graphs allow multiple edges, the edges must have the same multiplicity
     */
    protected boolean compatibleEdges(Edge edge_1, Edge edge_2) {
        if (o1.getGraph() instanceof DirectedMultigraph mg1 &&
                o2.getGraph() instanceof DirectedMultigraph mg2){
            return mg1.multiplicity(edge_1) == mg2.multiplicity(edge_2);
        }
        return true;
    }
}


