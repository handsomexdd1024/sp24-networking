package com.example.graph.graph_base;

import java.util.*;

public class Graph {
    private Map<Node, Set<Edge>> adjacencyList = new HashMap<>();

    public Graph() {
        // 无参构造函数
    }

    public void addNode(Node node) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList.put(node, new HashSet<>());
        }
    }

    public void addEdge(Node node1, Node node2, double weight) {
        adjacencyList.putIfAbsent(node1, new HashSet<>());
        adjacencyList.putIfAbsent(node2, new HashSet<>());

        if (!containsEdge(node1, node2)) {
            Edge edge = new Edge(node1, node2, weight);
            adjacencyList.get(node1).add(edge);
            adjacencyList.get(node2).add(edge);
        }
    }

    private boolean containsEdge(Node node1, Node node2) {
        Set<Edge> edges1 = adjacencyList.getOrDefault(node1, new HashSet<>());
        Set<Edge> edges2 = adjacencyList.getOrDefault(node2, new HashSet<>());

        for (Edge edge : edges1) {
            if ((edge.node1.equals(node1) && edge.node2.equals(node2)) ||
                    (edge.node1.equals(node2) && edge.node2.equals(node1))) {
                return true;
            }
        }

        return false;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public Set<Edge> getEdges(Node node) {
        return adjacencyList.get(node);
    }

    public Node findNode(String name) {
        for (Node node : adjacencyList.keySet()) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }
}
