package org.graph4j.alg.mst;

import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.UnionFind;

/**
 * Base class for Boruvka's minimum spanning tree implementations.
 *
 * @author Sorodoc Cosmin
 */
public abstract class BoruvkaMinimumSpanningTreeBase extends MinimumSpanningTreeBase {

    protected final UnionFind uf;
    protected final Edge[] cheapest;//an array in which we store the cheapest(smallest weight) edge for each component
    //when a component c is 'union' with another, then cheapest[c] = null

    public BoruvkaMinimumSpanningTreeBase(Graph graph) {
        super(graph);
        this.uf = new UnionFind(this.graph.numVertices());
        this.cheapest = new Edge[this.graph.numVertices()];
    }

    @Override
    protected void compute() {

        treeEdges = new EdgeSet(graph, graph.numVertices() - 1);
        minWeight = 0.0;
        int n = this.graph.numVertices();

        //while the components are not combined into a single MST
        //and there are outgoing edges from the components
        while (uf.numSets() > 1 && updateCheapestEdges()) {

            //go through the cheapest edges and add them to the MST
            for (int i = 0; i < n; ++i) {
                Edge e = cheapest[i];
                if (e != null) {
                    int componentNode1 = uf.find(e.source());
                    int componentNode2 = uf.find(e.target());
                    this.cheapest[componentNode1] = null;
                    this.cheapest[componentNode2] = null;

                    if (componentNode1 != componentNode2) {
                        this.treeEdges.add(e);
                        this.minWeight += e.weight();
                        uf.union(componentNode1, componentNode2);
                    }
                }
            }
        }

    }

    /**
     * For each component, find the smallest weighted edge and update the
     * cheapest array
     *
     * @return true if there is at least one outgoing edge from the components,
     * false otherwise
     */
    protected abstract boolean updateCheapestEdges();

}
