package org.graph4j.iso.general;

import org.graph4j.iso.GraphMapping;
import org.graph4j.iso.IsomorphicGraphMapping;

interface State {
    int NULL_NODE = -1;
    boolean DEBUG = false;
    boolean nextPair();
    boolean isFeasiblePair();
    boolean isDead();
    boolean isGoal();
    void addPair();
    void backTrack();
    IsomorphicGraphMapping getMapping();
    void resetPreviousVertices();

    int getCoreLen();
}
