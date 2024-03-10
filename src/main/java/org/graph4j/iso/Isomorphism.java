package org.graph4j.iso;

import java.util.Optional;

/**
 * Interface for isomorphism algorithms.
 *
 * @author Ignat Gabriel-Andrei
 */
public interface Isomorphism {

    /**
     * Method for getting the mappings.(optional because the trees might not be isomorphic)
     * @return an optional containing the mappings, if the trees are isomorphic, or an empty optional otherwise
     */
    Optional<GraphMapping> getMappings();

    /**
     * Method for checking if the trees are isomorphic.
     * @return true if the trees are isomorphic, false otherwise
     */
    boolean areIsomorphic();
}
