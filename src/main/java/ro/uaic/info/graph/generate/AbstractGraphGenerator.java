/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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
package ro.uaic.info.graph.generate;

import java.util.stream.IntStream;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class AbstractGraphGenerator {

    protected int[] vertices;

    protected AbstractGraphGenerator() {
        vertices = null;
    }

    /**
     *
     * @param numVertices the number of vertices in the geenrated graph.
     */
    public AbstractGraphGenerator(int numVertices) {
        CheckArguments.numberOfVertices(numVertices);
        this.vertices = IntStream.range(0, numVertices).toArray();
    }

    /**
     *
     * @param firstVertex the number of the first vertex in the geenrated graph.
     * @param lastVertex the number of the last vertex in the geenrated graph.
     */
    public AbstractGraphGenerator(int firstVertex, int lastVertex) {
        CheckArguments.vertexRange(firstVertex, lastVertex);
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
    }

}
