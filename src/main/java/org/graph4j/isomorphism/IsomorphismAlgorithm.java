package org.graph4j.isomorphism;

import java.util.Arrays;
import org.graph4j.Graph;
import static org.graph4j.measures.GraphMeasures.degreeHistogram;

/**
 * Interface for graph isomorphism algorithms.
 *
 * @author Ignat Gabriel-Andrei
 */
public interface IsomorphismAlgorithm {

    /**
     * Finds the isomorphic mapping between the first and the second graph.
     *
     * @return the isomorphism or {@code null} if the graphs are not isomorphic.
     */
    Isomorphism findIsomorphism();

    /**
     * Checks if the graphs are isomorphic.
     *
     * @return {@code true} if the graphs are isomorphic, {@code false}
     * otherwise.
     */
    boolean areIsomorphic();

    /**
     * Performs some trivial checks in order to determine if two graphs may be
     * isomorphic. If the method returns {@code false}, the graphs are not
     * isomorphic.
     *
     * @param graph1 first graph.
     * @param graph2 the second graph;
     * @return {@code true} if the graphs have similar trivial properties.
     */
    default boolean checkTrivialConditions(Graph graph1, Graph graph2) {
        return (graph1.numVertices() == graph2.numVertices()
                && graph1.numEdges() == graph2.numEdges()
                && Arrays.equals(degreeHistogram(graph1), degreeHistogram(graph2)));
    }
}
