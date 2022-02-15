package com.example.graphwiki;
import com.example.graphwiki.clustering.Cluster;
import com.example.graphwiki.graph.Graph;
import com.example.graphwiki.graph.GraphNode;
import com.example.graphwiki.webpage.Webpage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.graphwiki.clustering.Cluster.clusterifyData;


public class Main extends Application {
    Scene s1, s2;
    Stage curr;
    public static void main(String[] args) throws IOException {
        Application.launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Graph g = new Graph(100);
        ArrayList<Webpage> pages = g.addRandomPagesToGraph();
        for (int a = 0; a < pages.size(); a++) {
            System.out.println("[" + " " + a + " ] " + pages.get(a).title);
        }
        System.out.println("DISJOINT");
        g.disjointSets.generateDisjointSets();
        int i = g.numDisjointSets;
        stage.setTitle("TF-IDF");
        curr = stage;
        Label link = new Label("Number of disjoint sets: " + i);
        TextField f = new TextField();
        Button b = new Button("Type in 2 titles of pages. By number shown in output. EX. 1 6");
        VBox layout1 = new VBox(40);
        layout1.getChildren().addAll(link, f, b);
        s1 = new Scene(layout1, 200, 200);
        b.setOnAction(e -> {
            System.out.println("BUTTON CLICKED");
            String l = f.getText();
            StringTokenizer tz = new StringTokenizer(l);
            int first = Integer.parseInt(tz.nextToken());
            int second = Integer.parseInt(tz.nextToken());
            GraphNode firstN = pages.get(first).node;
            GraphNode secondN = pages.get(second).node;
            try {
                g.setEdgeWeight(pages, firstN);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            double cost = g.shortestPath(firstN, secondN);
            boolean passedThroughCluster = false;
            boolean hasPath = false;
            StringBuilder path = new StringBuilder();
            ArrayList<Cluster> c = clusterifyData(pages);
            if (secondN.parent != null) {
                ArrayList<String> list = new ArrayList<>();
                GraphNode curr = secondN;
                while (curr != null) {
                    for (Cluster cluste : c) if (cluste.medioid.equals(curr)) passedThroughCluster = true;
                    if (curr != null) list.add(curr.page.title);
                    curr = curr.parent;
                }
                for (int k = list.size() - 1; k >= 0; k--) {
                    if (k == 0) path.append(list.get(k));
                    else path.append(list.get(k) + " --> ");
                }
            }
//            Label right = new Label("The first non-empty cluster: " + s);
            Label after = new Label("Path: " + path);
            Label clus = new Label("The path passes through cluster: " + passedThroughCluster);
//            right.setContentDisplay(ContentDisplay.TOP);
            after.setContentDisplay(ContentDisplay.RIGHT);
            clus.setContentDisplay(ContentDisplay.BOTTOM);
            VBox v2 = new VBox();
            v2.getChildren().addAll(after, clus);
            s2 = new Scene(v2, 200, 200);
            curr.setScene(s2);
        });
        stage.setScene(s1);
        stage.show();
    }
}
