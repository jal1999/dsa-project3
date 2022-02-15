Write a program that collects at least 500 Wikipedia pages and links from these pages to other Wikipedia pages. For each page, collect similarity data 
(for example word frequencies or word2vec). (It is OK to use other linked data sources, but ask first.) As a connectivity check, report the number of 
spanning trees (for any arbitrary node as initial starting point). Store persistently (possibly just in a Serialized file). Use caching as appropriate to 
avoid unnecessary reconstruction. Write a program (either GUI or web-based) that reads the graph from step 1, allows a user to select any two pages (by title)
and displays graphically the shortest (weighted by any similarity metric) path between them, if one exists, as well as the most similar node for each.
