package Graph.graph_base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Graph {

    private Map<Node, Set<Edge>> adjacencyList = new HashMap<>();
    private String filepath;

    public Graph(String filepath){
        this.filepath = filepath;
        loadGraphFromJson(); // Load existing graph data from JSON file
    }

    public void addNode(Node node) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList.put(node, new HashSet<>());
            saveNodeToJson(node); // Save node to JSON file
        }
    }

    public void printGraph() {
        for (Node node : adjacencyList.keySet()) {
            System.out.print(node + ": ");
            for (Edge neighbor : adjacencyList.get(node)) {
                System.out.print("(" + neighbor.node1 + " <-> " + neighbor.node2 + ", " + neighbor.weight + ") ");
            }
            System.out.println();
        }
    }

    public void addEdge(Node node1, Node node2, double weight) {
        adjacencyList.putIfAbsent(node1, new HashSet<>());
        adjacencyList.putIfAbsent(node2, new HashSet<>());

        if (!containsEdge(node1, node2)) {
            Edge edge = new Edge(node1, node2, weight);
            adjacencyList.get(node1).add(edge);
            adjacencyList.get(node2).add(edge);
            saveEdgeToJson(node1, node2, weight); // Save edge to JSON file
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

    public void loadGraphFromJson() {
        try {
            Gson gson = new Gson();
            Type graphType = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> graphData = gson.fromJson(new FileReader(filepath), graphType);

            List<Map<String, Object>> nodes = graphData.getOrDefault("nodes", new ArrayList<>());
            List<Map<String, Object>> edges = graphData.getOrDefault("edges", new ArrayList<>());

            Map<String, Node> nodeMap = new HashMap<>();
            for (Map<String, Object> nodeData : nodes) {
                String name = (String) nodeData.get("name");
                double latitude = ((Number) nodeData.get("latitude")).doubleValue();
                double longitude = ((Number) nodeData.get("longitude")).doubleValue();
                Node node = new Node(name, latitude, longitude);
                nodeMap.put(name, node);
                adjacencyList.putIfAbsent(node, new HashSet<>());
            }

            for (Map<String, Object> edgeData : edges) {
                Node node1 = nodeMap.get(edgeData.get("node1"));
                Node node2 = nodeMap.get(edgeData.get("node2"));
                double weight = ((Number) edgeData.get("weight")).doubleValue();
                addEdge(node1, node2, weight);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Node> breadthFirstSearch(Node start) {
        List<Node> visited = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Edge edge : adjacencyList.get(current)) {
                Node neighbor = edge.node1.equals(current) ? edge.node2 : edge.node1;
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited;
    }

    public void saveGraphToJson(String filePath) {
        try {
            Gson gson = new Gson();
            Map<String, List<Map<String, Object>>> graphData = new HashMap<>();
            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> edges = new ArrayList<>();

            for (Node node : getNodes()) {
                Map<String, Object> nodeData = new HashMap<>();
                nodeData.put("name", node.name);
                nodeData.put("latitude", node.latitude);
                nodeData.put("longitude", node.longitude);
                nodes.add(nodeData);
            }

            Set<Edge> edgeSet = new HashSet<>();
            for (Node node : adjacencyList.keySet()) {
                for (Edge edge : adjacencyList.get(node)) {
                    // Ensure each edge is only added once
                    if (!edgeSet.contains(edge)) {
                        Map<String, Object> edgeData = new HashMap<>();
                        edgeData.put("node1", edge.node1.name);
                        edgeData.put("node2", edge.node2.name);
                        edgeData.put("weight", edge.weight);
                        edges.add(edgeData);
                        edgeSet.add(edge);
                    }
                }
            }

            graphData.put("nodes", nodes);
            graphData.put("edges", edges);

            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(graphData, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNodeToJson(Node node) {
        try {
            Gson gson = new Gson();
            Type graphType = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> graphData;

            try (FileReader reader = new FileReader(filepath)) {
                graphData = gson.fromJson(reader, graphType);
            } catch (FileNotFoundException e) {
                graphData = new HashMap<>();
            }

            List<Map<String, Object>> nodes = graphData.getOrDefault("nodes", new ArrayList<>());

            Map<String, Object> nodeData = new HashMap<>();
            nodeData.put("name", node.name);
            nodeData.put("latitude", node.latitude);
            nodeData.put("longitude", node.longitude);
            nodes.add(nodeData);

            graphData.put("nodes", nodes);

            try (FileWriter writer = new FileWriter(filepath)) {
                gson.toJson(graphData, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEdgeToJson(Node node1, Node node2, double weight) {
        try {
            Gson gson = new Gson();
            Type graphType = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> graphData;

            try (FileReader reader = new FileReader(filepath)) {
                graphData = gson.fromJson(reader, graphType);
            } catch (FileNotFoundException e) {
                graphData = new HashMap<>();
            }

            List<Map<String, Object>> edges = graphData.getOrDefault("edges", new ArrayList<>());

            // Check if edge already exists
            boolean edgeExists = edges.stream()
                    .anyMatch(edge -> (edge.get("node1").equals(node1.name) && edge.get("node2").equals(node2.name)) ||
                            (edge.get("node1").equals(node2.name) && edge.get("node2").equals(node1.name)));

            if (!edgeExists) {
                Map<String, Object> edgeData = new HashMap<>();
                edgeData.put("node1", node1.name);
                edgeData.put("node2", node2.name);
                edgeData.put("weight", weight);
                edges.add(edgeData);

                graphData.put("edges", edges);

                try (FileWriter writer = new FileWriter(filepath)) {
                    gson.toJson(graphData, writer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}