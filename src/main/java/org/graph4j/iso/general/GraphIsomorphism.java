package org.graph4j.iso.general;

import org.graph4j.iso.IsomorphicGraphMapping;

import java.util.List;
import java.util.Optional;

/**
 * Interface class for graph isomorphism
 *
 * @author Ignat Gabriel-Andrei
 */
public interface GraphIsomorphism {
    boolean areIsomorphic();
    List<IsomorphicGraphMapping> getAllMappings();
    Optional<IsomorphicGraphMapping> getMapping();
}
