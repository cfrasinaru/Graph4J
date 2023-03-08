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
package org.graph4j.generate;

import java.util.Random;
import org.graph4j.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EdgeWeightsGenerator {

    /**
     *
     * @param graph
     * @param min inclusive
     * @param max inclusive
     */
    public static void randomIntegers(Graph graph, int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Incorrect range of values: ["
                    + min + "," + max + "]");
        }
        Random rnd = new Random();
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                double weight = min + rnd.nextInt(max - min + 1);
                it.setEdgeWeight(weight);
            }
        }
    }

    /**
     *
     * @param graph
     */
    public static void consecutiveIntegers(Graph graph) {
        int weight = 0;
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                it.setEdgeWeight(++weight);
            }
        }
    }

    /**
     *
     * @param graph
     * @param min inclusive
     * @param max inclusive
     */
    public static void randomDoubles(Graph graph, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Incorrect range of values: ["
                    + min + "," + max + "]");
        }
        Random rnd = new Random();
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                double weight = min + (max - min + Double.MIN_NORMAL) * rnd.nextDouble();
                it.setEdgeWeight(weight);                 
            }
        }
    }

    /**
     *
     * @param graph
     * @param value
     */
    public static void fill(Graph graph, double value) {
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                it.setEdgeWeight(value);
            }
        }
    }

}
