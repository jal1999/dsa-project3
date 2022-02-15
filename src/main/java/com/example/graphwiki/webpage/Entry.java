package com.example.graphwiki.webpage;

public class Entry {
    /**
     * The word of the entry.
     */
    public String word;
    /**
     * The frequency of this word in the document.
     */
    public int freq;

    Entry(String w) {
        word = w;
        freq = 1;
    }
}
