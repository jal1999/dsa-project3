package com.example.graphwiki.disjointsets;



import com.example.graphwiki.graph.GraphNode;
import com.example.graphwiki.webpage.Webpage;

import java.util.ArrayList;

public class DisjointEntry {
    /**
     * Representative node of this disjoint set.
     */
    public GraphNode rep;
    /**
     * The container for the disjoint set.
     */
    public ArrayList<GraphNode> set;
    /**
     * The overall set of disjoint sets that this belongs to.
     */
    public DisjointSets parent;

   public  DisjointEntry(GraphNode w, DisjointSets p) {
        rep = w;
        set = new ArrayList<>();
        set.add(rep);
        parent = p;
    }

    /**
     * Find method for a webpage in this disjoint set.
     * @param w the webpage we are querying for.
     * @return true if target webpage is present, false otherwise.
     */
    public boolean find(Webpage w) {
        if (set.contains(w)) return true;
        else return false;
    }

    /**
     * Method to determine if a disjoint entry is equal to another.
     * @param otherOne the disjoint entry we are comparing to.
     * @return true if their representative's link is the same.
     */
    public boolean equals(DisjointEntry otherOne) {
        if (otherOne.rep.page.link.equalsIgnoreCase(rep.page.link)) return true;
        else return false;
    }
}
