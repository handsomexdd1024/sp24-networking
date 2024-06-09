// DispatchService.java
package Graph.services;

import Graph.Ant.AntColonyOptimization;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;
import com.example.entity.Station;
import com.example.mapper.StationGoodsMapper;
import com.example.mapper.StationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DispatchService {
    private Graph graph;
    private AntColonyOptimization aco;

    @Resource
    private StationGoodsMapper stationGoodsMapper;

    @Resource
    private StationMapper stationMapper;

    public DispatchService(Graph graph, AntColonyOptimization aco) {
        this.graph = graph;
        this.aco = aco;
    }

    public List<Node> findFastestPath(String targetNodeName, String goodsNameOrCategory, int quantity) {
        Node targetNode = graph.findNode(targetNodeName);
        if (targetNode == null) {
            throw new IllegalArgumentException("Target node not found");
        }

        List<Node> availableNodes = getAvailableNodesWithGoods(goodsNameOrCategory, quantity);
        Node closestNode = null;
        double bestTime = Double.MAX_VALUE;

        for (Node node : availableNodes) {
            List<Node> path = aco.findShortestPath(node, targetNode, 10, 100); // 假设使用10个蚂蚁和100次迭代
            double time = aco.calculatePathTime(path);
            if (time < bestTime) {
                bestTime = time;
                closestNode = node;
            }
        }

        return aco.findShortestPath(closestNode, targetNode, 10, 100); // 返回最快路径
    }

    private List<Node> getAvailableNodesWithGoods(String goodsNameOrCategory, int quantity) {
        List<Integer> stationIds = stationGoodsMapper.selectStationsWithGoods(goodsNameOrCategory, quantity);
        List<Node> availableNodes = new ArrayList<>();
        for (Integer stationId : stationIds) {
            Station station = stationMapper.selectById(stationId);
            if (station != null && "0".equals(station.getDisableFlag())) {
                availableNodes.add(new Node(station.getName(), station.getLatitude(), station.getLongitude()));
            }
        }
        return availableNodes;
    }
}
