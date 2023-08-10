/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graph4j.alg;

import org.graph4j.Graph;
import org.graph4j.util.Path;

/**
 * Computes the maximum induced path.
 *
 *
 * @author Cristian Frăsinaru
 */
public class MaximumInducedPath extends GraphAlgorithm {

    private Path currentPath, maxPath;

    public MaximumInducedPath(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return the maximum length induced path.
     */
    public Path findPath() {
        if (maxPath == null) {
            this.currentPath = new Path(graph);
            currentPath.add(graph.vertexAt(0));
            findRec();
        }
        return maxPath;
    }
    
    private void findRec() {
        int k = currentPath.numVertices();
        int last = currentPath.get(k - 1);
        next:
        for (var it = graph.neighborIterator(last); it.hasNext();) {
            int u = it.next();
            if (currentPath.contains(u)) {
                continue;
            }
            for (int w : currentPath.vertices()) {
                if (w != last && graph.containsEdge(u, w)) {
                    continue next;
                }
            }
            currentPath.add(u);
            findRec();
            currentPath.removeFromPos(k);
        }
        if (maxPath == null || maxPath.length() < currentPath.length()) {
            maxPath = new Path(graph, currentPath.vertices());
            assert maxPath.isValid() && maxPath.isInduced();
        }
    }
   
}
