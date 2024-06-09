package Graph.services;
import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;

import java.util.List;

public class BestNode {
    private  List<String> nodeNames;

    public BestNode(List<String> nodeNames){
        this.nodeNames = nodeNames;
    }
    // 方法：遍历并打印字符串列表
    public static void traverseAndPrintList(List<String> list) {
        for (String str : list) {
            System.out.println(str);
        }
    }

    // 方法：找到距离目标节点最近的节点
    public  Node findClosestNode(AntColonyOptimization aco, Graph graph, Node targetNode) {
        Node closestNode = null;
        double shortestDistance = Double.MAX_VALUE;

        for (String nodeName : nodeNames) {
            Node currentNode = graph.findNode(nodeName);
            if (currentNode != null) {
                List<Node> path = aco.findShortestPath(currentNode, targetNode, 10, 100); // 假设使用10个蚂蚁和100次迭代
                double distance = aco.calculatePathTime(path);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestNode = currentNode;
                }
            }
        }

        return closestNode;
    }

}
