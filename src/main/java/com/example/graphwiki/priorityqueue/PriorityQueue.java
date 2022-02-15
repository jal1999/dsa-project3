package com.example.graphwiki.priorityqueue;

import com.example.graphwiki.graph.GraphNode;

import java.util.Collection;

public class PriorityQueue {
    GraphNode[] array;
    int size;
    public PriorityQueue(Collection<GraphNode> nodes, GraphNode root) {
        array = new GraphNode[nodes.size()];
        root.best = 0;
        root.pqIndex = 0;
        array[0] = root;
        int k = 1;
        for (GraphNode p : nodes) {
            p.parent = null;
            if (p != root) {
                p.best = Double.MAX_VALUE;
                array[k] = p; p.pqIndex = k++;
            }
        }
        size = k;
    }
    public void resift(GraphNode x) {
        int k = x.pqIndex;
        assert (array[k] == x);
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            GraphNode p = array[parent];
            if (x.compareTo(p) >= 0)
                break;
            array[k] = p; p.pqIndex = k;
            k = parent;
        }
        array[k] = x; x.pqIndex = k;
    }
    void add(GraphNode x) { // unused; for illustration
        x.pqIndex = size++;
        resift(x);
    }

    public GraphNode poll() {
        int n = size;
        if (n == 0) return null;
        GraphNode least = array[0];
        if(least.best == Double.MAX_VALUE) return null;
        size = --n;
        if (n > 0) {
            GraphNode x = array[n]; array[n] = null;
            int k = 0, child;  // while at least a left child
            while ((child = (k << 1) + 1) < n) {
                GraphNode c = array[child];
                int right = child + 1;
                if (right < n) {
                    GraphNode r = array[right];
                    if (c.compareTo(r) > 0) {
                        c = r;
                        child = right;
                    }
                }
                if (x.compareTo(c) <= 0)
                    break;
                array[k] = c; c.pqIndex = k;
                k = child;
            }
            array[k] = x; x.pqIndex = k;
        }
        return least;
    }

}
