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
package org.graph4j.demo;

import java.util.Iterator;
import java.util.Set;
import org.graph4j.alg.clique.*;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class BronKerboschDemo extends PerformanceDemo {

    private final double probability = 0.7;

    public BronKerboschDemo() {
        numVertices = 100;
        //runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, probability).createGraph();
        //System.out.println(graph);
    }

    @Override
    protected void testGraph4J() {
        int count = 0;
        var alg = new PivotBronKerboschCliqueIterator(graph);
        while (alg.hasNext()) {
            var q = alg.next();
            count++;
        }
        System.out.println(count);
    }

    @Override
    protected void testJGraphT() {
        int count = 0;
        var finder = new org.jgrapht.alg.clique.BronKerboschCliqueFinder(jgrapht);
        Iterator<Set<Integer>> it = finder.iterator();
        while (it.hasNext()) {
            Set<Integer> clique = it.next();
            //System.out.println(clique);
            count++;
        }
        System.out.println(count);
    }

    @Override
    protected void testAlgs4() {
        //int count = 0;
        //var alg = new org.graph4j.alg.clique.BronKerboschCliqueFinder(graph);
        //System.out.println(alg.getCliques().size());
        int count = 0;
        var alg = new BronKerboschCliqueIterator(graph);
        while (alg.hasNext()) {
            var q = alg.next();
            count++;
        }
        System.out.println(count);        
    }

    
    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 100 * (i + 1);
        }
    }
}
