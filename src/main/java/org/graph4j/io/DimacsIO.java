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
package org.graph4j.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

/**
 * c comments
 *
 * p edge number-of-nodes number-of-edges
 *
 * e edge-source1 edge-target1
 *
 * e edge-source2 edge-target2
 *
 * @author Cristian Frăsinaru
 */
public class DimacsIO {

    public Graph read(String file) {
        Graph graph = null;
        try {
            for (String line : Files.readAllLines(Paths.get(file))) {
                if (line.trim().equals("")) {
                    continue;
                }
                char start = line.charAt(0);
                String[] tokens = line.split("\\s+");
                if (start == 'p') {
                    int n = Integer.parseInt(tokens[2]);
                    int m = Integer.parseInt(tokens[3]);
                    graph = GraphBuilder.vertexRange(1, n).estimatedNumEdges(m).buildGraph();
                } else if (start == 'e') {
                    if (graph == null) {
                        throw new RuntimeException("Invalid graph format - p line is missing");
                    }
                    int v = Integer.parseInt(tokens[1]);
                    int u = Integer.parseInt(tokens[2]);
                    if (v != u) {
                        graph.addEdge(v, u);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return graph;
    }
}
