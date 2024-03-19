package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.iso.GraphUtil;
import org.graph4j.iso.IsomorphicGraphMapping;

import java.util.*;

public abstract class AbstractGraphIsomorphism implements GraphIsomorphism {
    protected final Digraph dg1;
    protected final Digraph dg2;
    public AbstractGraphIsomorphism(Graph g1, Graph g2) {
        validateGraphs(g1, g2);
        this.dg1 = GraphUtil.convertToDigraph(g1);
        this.dg2 = GraphUtil.convertToDigraph(g2);
    }

    private void validateGraphs(Graph<?,?> g1, Graph<?,?> g2) {
        if(g1 == null || g2 == null)
            throw new NullPointerException("Graphs cannot be null");

        // they must have the same type(directed, pseudo graph, multi graph, graph)
        if(g1.isDirected() != g2.isDirected())
            throw new IllegalArgumentException("Graphs must have the same type");

        if(g1.isAllowingSelfLoops() != g2.isAllowingSelfLoops())
            throw new IllegalArgumentException("Graphs must have the same type");

        if(g1.isAllowingMultipleEdges() != g2.isAllowingMultipleEdges())
            throw new IllegalArgumentException("Graphs must have the same type");
    }

    @Override
    public boolean areIsomorphic() {
        Optional<IsomorphicGraphMapping> mappings = getMapping();
        return mappings.isPresent();
    }

    @Override
    public Optional<IsomorphicGraphMapping> getMapping() {
        List<IsomorphicGraphMapping> mappings = match(true);
        return mappings.isEmpty() ? Optional.empty() : Optional.of(mappings.get(0));
    }

    @Override
    public List<IsomorphicGraphMapping> getAllMappings() {
        return match(false);
    }


    /**
     * Check if the graphs could be isomorphic
     * {@code @return:} False if surely not isomorphic, true if not sure
     */
    protected boolean surelyNotIsomorphic(){
        if(dg1.numVertices() != dg2.numVertices())
            return true;

        return dg1.numEdges() != dg2.numEdges();
    }

    protected List<IsomorphicGraphMapping> match(boolean onlyFirstMapping){
        List<IsomorphicGraphMapping> mappings = new ArrayList<>();
        matchRecursive(getStateInstance(dg1, dg2), mappings, onlyFirstMapping);

        return mappings;
    }

    private void matchRecursive(State s, List<IsomorphicGraphMapping> mappings, boolean onlyFirstMapping) {
        if(s.isGoal()){
            mappings.add(s.getMapping());
//            System.out.println("Goal");
            return;
        }

        if (s.isDead())
            return;

        while(s.nextPair()){
            if(s.isFeasiblePair()){
//                System.out.println("Depth: " + s.getCoreLen());
                State newS = getNewStateInstance(s);
                newS.addPair();
                newS.resetPreviousVertices();

                matchRecursive(newS, mappings, onlyFirstMapping);
                newS.backTrack();
                if(onlyFirstMapping && !mappings.isEmpty())
                    return;
            }
        }
    }

    /**
     *
     */
    protected abstract State getStateInstance(Digraph g1, Digraph g2);

    /**
     *
     */
    protected abstract State getNewStateInstance(State s);
}
