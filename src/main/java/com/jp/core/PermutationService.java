package com.jp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * n! gets out of hand really quickly, so this service just generates permutations asynchronously and
 * each permutation is dropped into a blocking queue that can be polled from as new permutations are required.
 * <p>
 * The permutation logic itself was pulled from
 * <p>
 * Created by JP on 3/10/2017.
 */
public class PermutationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermutationService.class);
    private final ExecutorService executor;
    private final BlockingQueue<Object[]> permutations = new ArrayBlockingQueue<>(20);

    private boolean permutationsCalculating = false;

    public PermutationService(ExecutorService fjp, Object[] array) throws InterruptedException {
        executor = fjp;
        executor.submit(new PermuteRunner(array));

    }

    private static void swap(Object[] arr, int i, int j) {
        Object t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public Object[] getNextPermutation() {

        try {

            if (!permutations.isEmpty()) {
                return permutations.take();
            } else {
                if (isCalculating()) {
                    LOGGER.info("trying to take...");
                    return permutations.take();
                }
            }

            // if calculating and it's empty
            // then try to take

            // if calculating and not empty
            // then try to take

            // if not calculating and not empty
            // then try to take

            // if not calculating and empty
            // return null;


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOGGER.info("no more permutations?");
        return null;
    }

    private synchronized boolean isCalculating() {
        return permutationsCalculating;
    }

    private synchronized void setPermutationsCalculating(boolean bool) {
        permutationsCalculating = bool;
    }

    private class PermuteRunner implements Runnable {

        private Object[] baseArray;

        PermuteRunner(Object[] arr) {
            baseArray = arr;
        }

        private void doPermute(Object[] arr, int n) {
            setPermutationsCalculating(true);

            LOGGER.debug("permute - {}{}{}", arr);

            if (n == 1) {
                LOGGER.debug("permute complete - {}{}{}", arr);
                try {
                    permutations.put(arr.clone());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                return;
            }

            for (int i = 0; i < n; i++) {
                swap(arr, i, n - 1);
                doPermute(arr, n - 1);
                swap(arr, i, n - 1);
            }

            setPermutationsCalculating(false);

        }

        @Override
        public void run() {
            doPermute(baseArray, baseArray.length);
        }
    }

}
