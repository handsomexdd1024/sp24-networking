package Graph;

import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;

import Graph.services.BestNode;

public class test {
    public static void main(String[] args) {
        // 初始化Spring上下文
        ApplicationContext context = new AnnotationConfigApplicationContext("com.example");

        // 获取Graph和ACO bean
        Graph graph = context.getBean(Graph.class);
        AntColonyOptimization aco = context.getBean(AntColonyOptimization.class);

        // 打印图
        System.out.println("Graph:");
        graph.loadGraphFromDatabase();  // 从数据库加载图数据
        graph.printGraph();

        // 示例字符串列表
        List<String> stringList = Arrays.asList("A", "B", "C", "D", "F");
        BestNode bestNode = new BestNode(stringList);

        // 找到距离目标节点最近的节点
        Node targetNode = graph.findNode("E");
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
