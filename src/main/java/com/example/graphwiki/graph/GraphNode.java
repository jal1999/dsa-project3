package com.example.graphwiki.graph;

import com.example.graphwiki.disjointsets.DisjointEntry;
import com.example.graphwiki.webpage.Webpage;

import java.util.HashSet;

public class GraphNode implements Comparable<GraphNode> {
    /**
     * The webpage that this node is representing.
     */
    public Webpage page;
    /**
     * The collection of nodes that this node is connected to (can reach).
     */
    public HashSet<Edge> neighbors;
    /**
     * The disjoint set that this node belongs to.
     */
    public DisjointEntry set;
    /**
     * Number of outgoing edges this node has.
     */
    public int numEdges;
    /**
     * ID for this node (for persistent storage purposes.
     */
    public long id;
    /**
     * Using for DFS.
     */
    public GraphNode parent;
    /**
     * For Dijkstra's
     */
    public double best;
    /**
     * For Dijkstra's
     */
    public int pqIndex;

    public GraphNode(Webpage w, long i) {
        page = w;
        neighbors = new HashSet<>();
        numEdges = 0;
        id = i;
        best = Double.POSITIVE_INFINITY;
        pqIndex = -1;
    }

    /**
     * Method to add an edge to this node.
     *
     * @param dst the node we are connecting this node with.
     */
    public void addEdge(GraphNode dst, double weight, long l) {
        neighbors.add(new Edge(this, dst, weight, l));
        ++numEdges;
    }

    /**
     * Method to add an outgoing edge for this node.
     *
     * @param e the outgoing edge we are adding.
     */
    public void addEdge(Edge e) {
        neighbors.add(e);
    }

    /**
     * Method to perform a recursive DFS-style connectivity query.
     * @param n the target node.
     * @return true if the current node can reach the target node.
     */
    public boolean canReach(GraphNode n) {
        if (n == this) return true;
        for (Edge e : neighbors) {
            e.dst.parent = this;
            if (e.dst.canReach(n)) return true;
        }
        return false;
    }

    /**
     * Method to set the edge weight of a given edge of this node.
     * @param e the target edge whose weight is to be changed.
     * @param weight the weight we are changing this edge's weight to.
     */
    public void setEdge(Edge e, double weight) {
        for (Edge edge : neighbors) {
            if (edge == e) {
                e.weight = weight;
                return;
            }
        }
    }

    /**
     * CompareTo method for use in the priority queue.
     * @param x the node we are comparing to.
     * @return 1 if current node is smaller, -1 if current node is larger, 0 otherwise.
     */
    public int compareTo(GraphNode x) {
        if (best < x.best) return -1;
        else if (best > x.best) return 1;
        else return 0;
    }
}
