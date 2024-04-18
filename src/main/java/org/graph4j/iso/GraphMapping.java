package org.graph4j.iso;

import org.graph4j.Edge;

/**
 * Interface class for mapping between two graphs
 *
 * @author Ignat Gabriel-Andrei
 */
public interface GraphMapping {
    int getVertexCorrespondence(int vertex, boolean forward);

    Edge<?> getEdgeCorrespondence(Edge<?> edge, boolean forward);

    @Override
    String toString();
}
