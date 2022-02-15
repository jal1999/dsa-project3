package com.example.graphwiki.disjointsets;

import com.example.graphwiki.graph.Edge;
import com.example.graphwiki.graph.Graph;
import com.example.graphwiki.graph.GraphNode;
import com.example.graphwiki.webpage.Webpage;

import java.util.HashSet;

public class DisjointSets {
    /**
     * Container for all  the disjoint sets in this collection.
     */
    public HashSet<DisjointEntry> disjointSets;
    /**
     * The graph for which this disjoint set is for;
     */
    Graph graph;

    public DisjointSets(Graph g) {
        graph = g;
        disjointSets = new HashSet<>();
    }

    /**
     * Union method for joining two sets together.
     * @param entry the set who is being joined
     * @param joinee the set that is joining the other.
     */
    public void union(DisjointEntry entry, DisjointEntry joinee) {
        entry.set.addAll(joinee.set);
        disjointSets.remove(joinee);
        joinee.set = entry.set;
    }

    /**
     * Method to find the disjoint set that a webpage belongs to.
     * @param w the webpage whose disjoint set we are looking for.
     * @return the disjoint set that the webpage belongs to.
     */
    public DisjointEntry find(Webpage w) {
        for (DisjointEntry e : disjointSets) {
            if (e.find(w)) return e;
        }
        return null; // fall through
    }

    /**
     * Method to generate initial disjoint sets (each node being its own disjoint set).
     */
    public void createInitialDisjointSets() {
        for (GraphNode n : graph.nodes) { // giving each node its own disjoint set
            DisjointEntry de = new DisjointEntry(n, this);
            disjointSets.add(de);
            de.parent = this;
            n.set = de;
        }
    }

    /**
     * Method to generate the disjoint sets (performing all unions possible on the initial sets).
     */
   public void generateDisjointSets() {
       createInitialDisjointSets();
       System.out.println("Initial sets = graph size: " + (disjointSets.size() == graph.nodes.size()));
        for (GraphNode n : graph.nodes) {
            for (Edge e : n.neighbors) {
                if (!e.dst.set.equals(n.set) && e.dst.canReach(n)) { // extra constraint for directed vs being undirected
                    union(n.set, e.dst.set);
                }
            }
        }
       System.out.println("Graph size: " + graph.nodes.size());
        graph.numDisjointSets = disjointSets.size();
    }
}
