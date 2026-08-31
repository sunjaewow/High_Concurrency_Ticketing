package com.highconcurrency.ticketing.infrastructure.database.java;

public class WaitingSeqTree {

    private final int[] tree;

    public WaitingSeqTree(int maxSize) {
        this.tree = new int[maxSize + 1];
    }

    public synchronized void add(int seq, int value) {
        while (seq < tree.length) {
            tree[seq] += value;
            seq += seq & -seq;
        }
    }

    public int getRank(int seq) {
        if (seq <= 0) return 0;
        if (seq >= tree.length) seq = tree.length - 1;

        return sum(seq);
    }

    private synchronized int sum(int seq) {
        int result = 0;
        while (seq > 0) {
            result += tree[seq];
            seq -= seq & -seq;
        }
        return result;
    }
}
