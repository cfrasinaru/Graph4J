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
package ro.uaic.info.graph.alg.coloring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Stream;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.VertexSet;

/**
 * A coloring of the vertices of a graph.
 *
 * @author Cristian Frăsinaru
 * @param <T> the type of vertex colors.
 */
public class VertexColoring<T> {

    private final Graph graph;
    private final Object[] vertexColor;
    private Map<T, VertexSet> colorMap;

    /**
     *
     * @param graph the graph for which this coloring is created.
     */
    public VertexColoring(Graph graph) {
        this.graph = graph;
        vertexColor = new Object[graph.numVertices()];
    }

    /**
     *
     * @param graph
     * @param colorClasses
     */
    public VertexColoring(Graph graph, List<VertexSet> colorClasses) {
        this(graph);
        for (int color = 0, k = colorClasses.size(); color < k; color++) {
            for (int v : colorClasses.get(color).vertices()) {
                vertexColor[graph.indexOf(v)] = color;
            }
        }
    }

    /**
     *
     * @param v a vertex number.
     * @return {@code true} if a color was set for the vertex.
     */
    public boolean isColorSet(int v) {
        return vertexColor[graph.indexOf(v)] != null;
    }

    /**
     *
     * @param v a vertex number.
     * @param color an object representing the color, or {@code null} to uncolor
     * the specified vertex.
     */
    public void setColor(int v, T color) {
        vertexColor[graph.indexOf(v)] = color;
        colorMap = null;
    }

    /**
     *
     * @param v a vertex number;
     * @return the color assigned to v, or {@code null} if no color was set.
     */
    public T getColor(int v) {
        return (T) vertexColor[graph.indexOf(v)];
    }

    /**
     *
     * @return the color classes.
     */
    public Map<T, VertexSet> getColorClasses() {
        if (colorMap == null) {
            createColorClasses();
        }
        return colorMap;
    }

    private void createColorClasses() {
        colorMap = new HashMap<>();
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            T color = (T) vertexColor[i];
            if (color == null) {
                continue;
            }
            var set = colorMap.get(color);
            if (set == null) {
                set = new VertexSet(graph);
                colorMap.put(color, set);
            }
            set.add(graph.vertexAt(i));
        }
    }

    /**
     *
     * @return the number of used colors.
     */
    public int numUsedColors() {
        return getColorClasses().size();
    }

    /**
     *
     * @return the number of colored vertices.
     */
    public int numColoredVertices() {
        return (int) Stream.of(vertexColor).filter(c -> c != null).count();
    }

    /**
     *
     * @return {@code true} if all the vertices have been colored.
     */
    public boolean isComplete() {
        for (var color : vertexColor) {
            if (color == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * A proper coloring is an assignment of colors to the vertices of a graph
     * so that no two adjacent vertices have the same color.
     *
     * @return @{code true} if the coloring is proper.
     */
    public boolean isProper() {
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            int vi = graph.indexOf(e.source());
            int ui = graph.indexOf(e.target());
            T vc = (T) vertexColor[vi];
            T uc = (T) vertexColor[ui];
            if (vc != null && uc != null && vc.equals(uc)) {
                System.err.println("Vertices " + e.source() + " and " + e.target()
                        + " have the same color: " + vc);
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(",");
        for (int v : graph.vertices()) {
            T vc = (T) vertexColor[graph.indexOf(v)];
            if (vc != null) {
                sb.add(v + ":" + vc);
            }
        }
        return sb.toString();
    }

}
