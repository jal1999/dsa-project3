package com.example.graphwiki.clustering;

import com.example.graphwiki.webpage.Webpage;

import java.util.ArrayList;
import java.util.Random;

public class Cluster {
    /**
     * The webpages that are currently in this cluster.
     */
    public ArrayList<Webpage> pages;
    /**
     * The current medioid of this cluster.
     */
    public Webpage medioid;
    /**
     * Current dissimilarity of this cluster.
     */
    double currDissimilarity;

    Cluster(ArrayList<Webpage> corpus) {
        pages = new ArrayList<>();
        Random r = new Random();
        int i = r.nextInt(corpus.size());
        medioid = corpus.get(i);
        currDissimilarity = 0;
    }

    Cluster() {
        pages = new ArrayList<>();
        medioid = null;
        currDissimilarity = 0;
    }
    /**
     * Contains method for array of clusters.
     *
     * @param cs array of clusters
     * @param c  cluster we're checking for in the array.
     * @return true if array already contains the cluster, false otherwise.
     */
    static boolean arrContains(ArrayList<Cluster> cs, Cluster c) {
        for (Cluster clust : cs) {
            if (clust == null) continue;
            if (clust.medioid.equals(c.medioid)) return true;
        }
        return false;
    }

    /**
     * Determine if the cluster is already present.
     * @param c the array of clusters we're checking.
     * @param w the medioid of the cluster we're looking for.
     * @return true if cluster is already present, false otherwise.
     */
    static boolean clusterAlreadyPresent(ArrayList<Cluster> c, Webpage w) {
        for (Cluster cluster : c) {
            if (cluster == null) continue;
            if ((cluster.medioid.score == w.score) && cluster.medioid.link.equalsIgnoreCase(w.link)) return true;
        }
        return false;
    }

    /**
     * Method to create the initial clusters.
     * @param arr the corpus.
     */
    static ArrayList<Cluster> createIntialClusters(ArrayList<Webpage> arr) {
        ArrayList<Cluster> initialClusters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Cluster c = new Cluster(arr);
            while (clusterAlreadyPresent(initialClusters, c.medioid)) {
                c = new Cluster(arr); // assuring unique medioid
            }
            initialClusters.add(c);
        }
        for (Webpage key : arr) {
            Cluster best = null;
            double disSim = Double.MAX_VALUE;
            for (int j = 0; j < 5; j++) {
                double dissimToCluster = key.score - initialClusters.get(j).medioid.score;
                if (dissimToCluster < 0) dissimToCluster *= -1;
                if (best == null) {
                    disSim = dissimToCluster;
                    best = initialClusters.get(j);
                } else if (dissimToCluster < disSim) {
                    best = initialClusters.get(j);
                    disSim = dissimToCluster;
                }
            }
            key.associatedCluster = best;
            best.pages.add(key);
            best.currDissimilarity += disSim;
        }
        return initialClusters;
    }

    /**
     * Helper method to swap out a random cluster, and replace it with a new one (iterative algorithmic technique).
     * @param w the corpus.
     * @param clusts our current clusters.
     * @return the new cluster array.
     */
    static ArrayList<Cluster> swapOneOut(ArrayList<Webpage> w, ArrayList<Cluster> clusts) {
        Cluster c = new Cluster(w);
        Random r = new Random();
        int rand = r.nextInt(clusts.size());
        clusts.set(rand, c);
        return clusts;
    }

    /**
     * Method to optimize the clusters.
     * @param pages
     */
    public static ArrayList<Cluster> clusterifyData(ArrayList<Webpage> pages) {
        Random r = new Random();
        ArrayList<Cluster> clusters = createIntialClusters(pages);
        double totalCost = 0;
        for (Cluster clutering : clusters) totalCost += clutering.currDissimilarity;
        for (int i = 0; i < 1000; i++) {
            ArrayList<Cluster> tmpClusters = swapOneOut(pages, clusters);
            ArrayList<Cluster> onesWereUsing = new ArrayList<>();
            for (int clusta = 0; clusta < 5; clusta++) {
                onesWereUsing.add(new Cluster());
                onesWereUsing.get(clusta).medioid = tmpClusters.get(clusta).medioid;
            }

            for (Webpage w : pages) {
                Cluster best = null;
                double currDist = Double.MAX_VALUE;

                for (int j = 0; j < tmpClusters.size(); j++) {
                    double dist = w.score - onesWereUsing.get(j).medioid.score;
                    if (dist < 0) dist *= -1;
                    if (best == null) {
                        best = onesWereUsing.get(j);
                        currDist = dist;
                    } else if (dist < currDist) {
                        best = onesWereUsing.get(j);
                        currDist = dist;
                    }
                }
                best.currDissimilarity += currDist;
                best.pages.add(w);
            }
            double costofit = 0;
            for (Cluster cluster : onesWereUsing) costofit += cluster.currDissimilarity;

            if (costofit < totalCost) {
                clusters = onesWereUsing;
                for (Cluster c : clusters) {
                    for (Webpage w : c.pages) {
                        w.associatedCluster = c;
                    }
                }
            }
        }
        return clusters;
    }
}
