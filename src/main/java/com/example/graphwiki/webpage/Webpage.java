package com.example.graphwiki.webpage;

import com.example.graphwiki.clustering.Cluster;
import com.example.graphwiki.graph.GraphNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Webpage {
    /**
     * Link of this webpage.
     */
    public String link;
    /**
     * Title of this webpage.
     */
    public String title;
    /**
     * Paragraph text of this page.
     */
    public ArrayList<Entry> pText;
    /**
     * Similarity comparison score.
     */
    public double score;
    /**
     * The cluster associated with this webpage.
     */
    public Cluster associatedCluster;
    /**
     * The node in the graph that this page is associated with.
     */
    public GraphNode node;

    public Webpage(String l, String t) throws IOException {
        node = null;
        associatedCluster = null;
        score = 0;
        link = l;
        title = t;
        ArrayList<Element> list = Jsoup.connect(link).get().getElementsByTag("p");
        pText = new ArrayList<>();
        for (Element e : list) {
            String s = e.text();
            StringTokenizer tz = new StringTokenizer(s);
            while (tz.hasMoreTokens()) {
                String tok = tz.nextToken();
                boolean b = false;
                for (Entry ent : pText) if (ent.word.equalsIgnoreCase(tok)) {
                    b = true;
                    ++ent.freq;
                }
                if (!b) pText.add(new Entry(tok));
            }
        }
    }

    /**
     * Logarithmically scaled term frequency calculation.
     * @param word the word we are searching for.
     * @return Math.log10(1 + frequency of word in document) ----> TF-IDF.
     */
    public double tf(String word) {
        int freq;
        for (Entry e : pText) {
            if (e.word.equalsIgnoreCase(word)) {
                freq = e.freq;
                return Math.log(1 + freq);
            }
        }
        return Math.log10(1);
    }

    /**
     * Logarithmically scaled inverse document frequency calculation
     * @param pages the corpus.
     * @param word the word we are looking for.
     * @return Math.log10(corpus size / (1 + # of docs containing word)).
     */
    public double idf(ArrayList<Webpage> pages, String word) {
        double count = 0;
        for (Webpage page : pages) {
            for (Entry e : page.pText) {
                if (e.word.equalsIgnoreCase(word)) {
                    ++count;
                    break;
                }
            }
        }
        return Math.log10(pages.size() / (1 + count));
    }

    /**
     * Logarithmically scaled TF-IDF calculation using the last two methods has helper methods.
     * @param pages the corpus.
     * @param word the word we are looking for.
     * @return tf(word) * idf(corpus, word).
     */
    public double tfidf(ArrayList<Webpage> pages, String word) {
        return tf(word) * idf(pages, word);
    }

    /**
     * Method to generate the keywords for a given Wikipedia page.
     * @param corpus the corpus.
     * @return an ArrayList of the keywords for this page.
     */
    public ArrayList<String> getKeywords(ArrayList<Webpage> corpus) {
        ArrayList<String> keywords = new ArrayList<>();
        for (Entry e : pText) {
            double tfidf = tfidf(corpus, e.word);
            if (tfidf > .2) keywords.add(e.word);
        }
        return keywords;
    }

    /**
     * Method to clear the scores for all the pages currently in main memory.
     * @param arr the array of webpages in main memory.
     */
    public static void clearAllScores(Webpage[] arr) {
        for (Webpage w : arr) {
            w.score = 0;
        }
    }
}
