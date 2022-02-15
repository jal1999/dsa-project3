package com.example.graphwiki.graph;

public class Edge {
    /**
     * Source node for this edge.
     */
    public GraphNode src;
    /**
     * Destination node for this edge.
     */
    public GraphNode dst;
    /**
     * Weight of this edge.
     */
    public double weight;
    /**
     * ID of this edge (for persistent storage purposes).
     */
    public long id;
    /**
     * Source node ID (for persistent storage purposes).
     * I am not writing the data for all the nodes to disk,
     * am only writing the ID of the src and dst nodes to disk
     * so I can use that ID number to find the nodes
     * in the graph's hashset for further processing after
     * reporting # of disjoint sets.
     */
    public long fakeSrc;
    /**
     * Destination node ID (see above comment for explanation).
     */
    public long fakeDst;

    public Edge(GraphNode s, GraphNode d, double w, long l) {
        dst = d;
        weight = w; // weight will initially be 0, to be updated later after similarity comparisons
        id = l;
        src = s;
    }

    public Edge() {} // for disk read of the edge
}
