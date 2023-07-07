package org.graph4j.alg.mst;

import org.graph4j.Edge;
import org.graph4j.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Parallel implementation of the Boruvka algorithm for finding a minimum
 * spanning tree.
 *
 * You can see more :
 * https://web.archive.org/web/20110410100229id_/http://www.globalstf.org:80/docs/proceedings/adpc/ADPC_22.pdf
 * SECTION 5 - EXPERIMENTS
 *
 * @author Sorodoc Cosmin
 */
public class BoruvkaMinimumSpanningTreeParallel extends BoruvkaMinimumSpanningTreeBase {

    private final Edge[] allEdges;
    private final int nrThreads;

    /**
     * we'll use a threadPool in order to parallelize the execution
     */
    private final ThreadPoolExecutor executor;

    public BoruvkaMinimumSpanningTreeParallel(Graph graph) {
        this(graph, Runtime.getRuntime().availableProcessors());
    }

    /**
     *
     * @param graph the input graph.
     * @param nrThreads the number of threads.
     */
    public BoruvkaMinimumSpanningTreeParallel(Graph graph, int nrThreads) {
        super(graph);
        this.allEdges = this.graph.edges();
        this.nrThreads = nrThreads;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrThreads);

    }

    @Override
    protected void compute() {
        super.compute();
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean updateCheapestEdges() {

        //the numbers of edges that each thread will process
        int chunkSize = this.allEdges.length / nrThreads;

        //this variable will be changed during the parallel execution
        AtomicBoolean hasOutgoingEdges = new AtomicBoolean(false);

        //we need this list such that we can wait for all tasks to finish
        List<Future<?>> tasks = new ArrayList<>();

        for (int i = 0; i < nrThreads; ++i) {

            int start = i * chunkSize;
            int end;

            if (i == nrThreads - 1) {
                end = this.allEdges.length;
            } else {
                end = (i + 1) * chunkSize;
            }

            tasks.add(this.executor.submit(() -> {//this is the task that will be executed by the threads
                for (int j = start; j < end; ++j) {

                    Edge e = allEdges[j];

                    int componentNode1, componentNode2;

                    //synchronized (uf) ??
                    componentNode1 = uf.find(e.source());
                    componentNode2 = uf.find(e.target());

                    if (componentNode1 == componentNode2) {
                        continue;
                    }

                    synchronized (cheapest) {
                        if (cheapest[componentNode1] == null || cheapest[componentNode1].weight() > e.weight()) {
                            cheapest[componentNode1] = e;
                            hasOutgoingEdges.set(true);
                        }

                        if (cheapest[componentNode2] == null || cheapest[componentNode2].weight() > e.weight()) {
                            cheapest[componentNode2] = e;
                            hasOutgoingEdges.set(true);
                        }
                    }

                }
            }));
        }

        //wait for all tasks to finish updating the cheapest edges        
        for (Future<?> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return hasOutgoingEdges.get();
    }

}
