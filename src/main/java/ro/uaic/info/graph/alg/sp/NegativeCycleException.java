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
package ro.uaic.info.graph.alg.sp;

import ro.uaic.info.graph.Cycle;

/**
 *
 * @author Cristian Frăsinaru
 */
public class NegativeCycleException extends RuntimeException {

    private final Cycle cycle;

    public NegativeCycleException(Cycle cycle) {
        super("A negative cost cycle was detected: "
                + cycle + " = " + cycle.computeEdgesWeight());
        this.cycle = cycle;
    }

    /**
     *
     * @return the negative cost cycle that was detected
     */
    public Cycle getCycle() {
        return cycle;
    }

}
