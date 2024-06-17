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
package org.graph4j.metrics;

import org.graph4j.Graph;

/**
 * Various <i>distances</i> related to a tree.
 *
 * @author Cristian Frăsinaru
 * @author Ignat Gabriel-Andrei
 */
public class TreeMetrics extends GraphMetrics {

    public TreeMetrics(Graph graph) {
        super(graph);
        extremaCalculator = new TreeExtremaCalculator(graph);
    }

    @Override
    public int girth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double pseudoDiameter() {
        if (pseudoDiameter != null) {
            return pseudoDiameter;
        }
        pseudoDiameter = diameter();
        return pseudoDiameter;
    }

}
