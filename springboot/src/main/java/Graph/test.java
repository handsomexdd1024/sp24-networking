package Graph;

import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;

import java.util.List;

public class test {
    public static void main(String[] args) {

        Graph graph = new Graph("D:\\Project Files\\futureNetwork_tsim\\tsim\\springboot\\src\\main\\java\\Graph\\graph.json");
//        springboot/src/main/java/Graph/graph.json
//        D:\Project Files\futureNetwork_tsim\tsim\springboot\src\main\java\Graph\graph.json
        graph.loadGraphFromJson();

        // 打印图
        System.out.println("Graph:");
        graph.printGraph();



        AntColonyOptimization aco = new AntColonyOptimization(graph, 0.1, 5.0, 0.5, 12000);
        Node startNode = graph.findNode("A");
        Node endNode = graph.findNode("E");

        if (startNode != null && endNode != null) {
            List<Node> shortestPath = aco.findShortestPath(startNode, endNode, 10, 100);
            double pathLength = aco.calculatePathLength(shortestPath);
            System.out.println("Shortest Path: " + shortestPath+"\n");
            System.out.println("Path length:"+pathLength+"\n");
        } else {
            System.out.println("Start or end node not found in the graph.");
        }

    }
}
