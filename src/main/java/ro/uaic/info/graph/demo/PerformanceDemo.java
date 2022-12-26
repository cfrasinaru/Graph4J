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
package ro.uaic.info.graph.demo;

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class PerformanceDemo {

    protected Graph graph;
    protected org.jgrapht.Graph jgraph;

    protected void run(Runnable snippet) {
        long m0 = Runtime.getRuntime().freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long m1 = Runtime.getRuntime().freeMemory();
        System.out.println((t1 - t0) + " ms");
        System.out.println((m0 - m1) / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }

    protected void printObjectSize(Object object) {
        //System.out.println("Object type: " + object.getClass() + ", size: " + InstrumentationAgent.getObjectSize(object) + " bytes");
    }

    protected abstract void prepare();

    protected abstract void test1();

    protected abstract void test2();

    protected void demo() {
        run(this::prepare);
        run(this::test1);
        run(this::test2);
    }

}
