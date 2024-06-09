package Graph;

import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;
import Graph.services.DispatchService;

import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args) {
        Graph graph = new Graph("D:\\Project Files\\futureNetwork_tsim\\tsim\\springboot\\src\\main\\java\\Graph\\graph.json");
        graph.loadGraphFromJson();

        // 打印图
        System.out.println("Graph:");
        graph.printGraph();

        AntColonyOptimization aco = new AntColonyOptimization(graph, 0.1, 5.0, 0.5, 12000);

        // 创建 DispatchService 实例
        DispatchService dispatchService = new DispatchService(graph, aco);

        // 示例目标节点和货物需求
        String targetNodeName = "E";
        String goodsNameOrCategory = "example_goods"; // 替换为实际货物名字或类别
        int quantity = 100;

        // 查找最快路径
        List<Node> fastestPath = dispatchService.findFastestPath(targetNodeName, goodsNameOrCategory, quantity);
        if (fastestPath != null) {
            System.out.println("Fastest path from available nodes to " + targetNodeName + ":");
            for (Node node : fastestPath) {
                System.out.print(node.name + " ");
            }
            System.out.println();
        } else {
            System.out.println("No path found.");
        }
    }
}
