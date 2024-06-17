package org.graph4j.isomorphism;

import org.graph4j.isomorphism.Isomorphism;

import java.util.List;
import java.util.Optional;

/**
 * Interface class for graph isomorphism
 *
 * @author Ignat Gabriel-Andrei
 */
public interface GraphIsomorphism {

    boolean areIsomorphic();

    List<Isomorphism> getAllMappings();

    Optional<Isomorphism> getMapping();
}
