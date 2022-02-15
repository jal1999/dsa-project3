package com.example.graphwiki.graph;

import com.example.graphwiki.disjointsets.DisjointSets;
import com.example.graphwiki.persistentstores.EdgesStore;
import com.example.graphwiki.priorityqueue.PriorityQueue;
import com.example.graphwiki.webpage.Webpage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
    /**
     * The set of nodes in the graph.
     */
    public HashSet<GraphNode> nodes;
    /**
     * The disjoint sets of this graph.
     */
    public DisjointSets disjointSets;
    /**
     * Number of disjoint sets that this graph contains.
     */
    public int numDisjointSets;
    /**
     * Size of the graph in terms of edges.
     */
    public int numEdges;
    /**
     * Size of graph in terms of nodes.
     */
    public int numNodes;
    /**
     * Persistent store for storing data on the edges of the graph.
     */
    EdgesStore es;

    public Graph(long mark) throws FileNotFoundException {
        nodes = new HashSet<>();
        disjointSets = new DisjointSets(this);
        numEdges = 0;
        numNodes = 0;
        es = new EdgesStore(String.valueOf(mark));
    }

    /**
     * Method to determine if the graph is empty or not.
     * @return true if the graph is empty and false otherwise.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Method to determine the size of the graph (in terms of nodes).
     * @return the size of the graph's hashset.
     */
    public int size() {
        return nodes.size();
    }

    /**
     * Method to add a node to the graph.
     * @param w the page we are adding to the graph.
     */
    public void addNode(Webpage w) {
        nodes.add(new GraphNode(w, numNodes++));
    }

    /**
     * Method to add a node to the graph.
     * @param n the node we are adding to the graph.
     */
    public void addNode(GraphNode n) {
        nodes.add(n);
        numNodes++;
    }

    /**
     * Method to add an edge to the graph.
     * @param src the source node.
     * @param dst the destination node.
     */
    public void addEdge(GraphNode src, GraphNode dst, double weight, int id) throws IOException {
        Edge e = new Edge(src, dst, weight, numEdges++);
        src.addEdge(e);
        es.diskWrite(src, e);
    }

    /**
     * Method to add an edge to the graph.
     * @param e the edge we're adding to the graph.
     */
    public void addEdge(Edge e) throws IOException {
        e.src.addEdge(e);
        numEdges++;
        es.diskWrite(e.src, e);
    }

    /**
     * Method for generating the disjoint sets.
     */
    public void generateDisjointSets() {
        disjointSets.generateDisjointSets();
    }

    /**
     * Method to add a predetermined number of Wikipedia pages to the tree.
     * @throws IOException if Jsoup throws an IO exception.
     */
     public ArrayList<Webpage> addRandomPagesToGraph() throws IOException {
        ArrayList<Webpage> pages = new ArrayList<>();
        int z = 0;
        for (int i = 0; i < 5; i++) { // changed to 5 for speed
            Document d = Jsoup.connect("https://en.wikipedia.org/wiki/Special:Random").get();
            Webpage w = new Webpage(d.location(), d.title());
            pages.add(w);
            GraphNode n = new GraphNode(w, numNodes++);
            w.node = n;
            addNode(n);
            ArrayList<Element> list = d.select("p a");
            if (list.size() < 5) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).absUrl("href").contains("#")) continue;
                    if (list.get(j).absUrl("href").contains("en.wikipedia.org") && !list.get(j).absUrl("href").equalsIgnoreCase(n.page.link)) {
                        Document doc;
                        try {
                            doc = Jsoup.connect(list.get(j).absUrl("href")).get();
                        } catch (Exception exc) {
                            doc = Jsoup.connect("https://en.wikipedia.org/wiki").get();
                            continue;
                        }
                        Webpage page = new Webpage(doc.location(), doc.title());
                        GraphNode gn = new GraphNode(page, numNodes++);
                        page.node = gn;
                        addNode(gn);
                        Edge e = new Edge(n, gn, 0, numEdges++);
                        addEdge(e);
                    }
                }
            } else {
                for (int j = 0; j < 5; j++) {
                    if (list.get(j).absUrl("href").contains("#")) continue;
                    if (list.get(j).absUrl("href").contains("en.wikipedia.org")) {
                        Document doc;
                        try {
                            doc = Jsoup.connect(list.get(j).absUrl("href")).get();
                        } catch (Exception exc) {
                            continue;
                        }
                        ArrayList<String> theseWords = new ArrayList<>();
                        Webpage page = new Webpage(doc.location(), doc.title());
                        pages.add(page);
                        GraphNode gn = new GraphNode(page, numNodes++);
                        page.node = gn;
                        addNode(gn);
                        Edge newE = new Edge(n, gn, 0, numEdges++);
                        addEdge(newE);
                    }
                }
            }
        }
        return pages;
    }

    /**
     * Method to reset all edge weights in the graph.
     * @throws IOException if disk write throws an IO exception.
     */
    public void resetEdgeWeights() throws IOException {
        for (GraphNode n : nodes) {
            for (Edge e : n.neighbors) {
                if (e.weight == 0) continue;
                else {
                    e.weight = 0;
                    es.diskWrite(e.src, e);
                }
            }
        }
    }

    /**
     * Method that computes the weights of all the edges in the graph based
     * on their inverse similarity to a starting node. We will accomplish this
     * BFS style.
     * @param baseNode the node we are basing all edge's weights on
     */
    public void setEdgeWeight(ArrayList<Webpage> corpus, GraphNode baseNode) throws IOException {
        ArrayList<String> keywords = baseNode.page.getKeywords(corpus);
        for (GraphNode n : nodes) {
            for (Edge e : n.neighbors) {
                int count = 0;
                for (String word : keywords) {
                    if (e.dst.page.pText.contains(word)) ++count;
                }
                e.dst.page.score = count;
                e.weight = count * -1;
                es.diskWrite(e.src, e);
            }
        }
    }

    /**
     * Method to find shorted path from a source node to a destination node.
     * @param src source node.
     * @param dst destination node.
     * @return infinity if there is no path to the destination node, or the cost of the path to that
     * node if there is indeed a path.
     */
    public double shortestPath(GraphNode src, GraphNode dst) {
        PriorityQueue pq = new PriorityQueue(nodes, src);
        GraphNode p;
        int g;
        while ((p = pq.poll()) != null) {
            for (Edge e : p.neighbors) {
                GraphNode s = e.src, d = e.dst;
                double w = s.best + e.weight;
                if (w < d.best) {
                    d.parent = s;
                    d.best = w;
                    pq.resift(d);
                }
            }
        }
        return dst.best;
    }
}
