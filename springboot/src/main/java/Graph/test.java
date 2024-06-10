package Graph;

import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;

import java.util.Arrays;
import java.util.List;

import Graph.services.BestNode;

public class test {
    public static void main(String[] args) {
        Graph graph = new Graph("D:\\Project Files\\futureNetwork_tsim\\tsim\\springboot\\src\\main\\java\\Graph\\graph.json");

        graph.loadGraphFromJson();

        // 打印图
        System.out.println("Graph:");
        graph.printGraph();

        AntColonyOptimization aco = new AntColonyOptimization(graph, 0.1, 5.0, 0.5, 12000);

        // 示例字符串列表
        List<String> stringList = Arrays.asList("天津", "北京","石家庄");
        BestNode bestNode = new BestNode(stringList);

        // 调用 traverseAndPrintList 方法
        System.out.println("Traversing String List:");
        BestNode.traverseAndPrintList(stringList);

        // 找到距离目标节点最近的节点
        Node targetNode = graph.findNode("呼和浩特");
        if (targetNode != null) {
            Node closestNode = bestNode.findClosestNode(aco, graph, targetNode);
            if (closestNode != null) {
                System.out.println("Closest Node to " + targetNode.name + " is " + closestNode.name);
            } else {
                System.out.println("No closest node found.");
            }
        } else {
            System.out.println("Target node not found in the graph.");
        }
    }
}
