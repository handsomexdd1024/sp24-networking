package Graph.Ant;

import Graph.graph_base.Edge;
import Graph.graph_base.Graph;
import Graph.graph_base.Node;

import java.util.*;

public class AntColonyOptimization {
    private Graph graph;
    private Map<Node, Map<Node, Double>> pheromones;
    private double alpha;
    private double beta;
    private double evaporationRate;
    private double Q;

    public AntColonyOptimization(Graph graph, double alpha, double beta, double evaporationRate, double Q) {
        this.graph = graph;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.Q = Q;
        initializePheromones();
    }

    private void initializePheromones() {
        pheromones = new HashMap<>();
        for (Node node : graph.getNodes()) {
            pheromones.putIfAbsent(node, new HashMap<>());
            for (Edge edge : graph.getEdges(node)) {
                pheromones.get(node).put(edge.node1, 1.0);
                pheromones.get(node).put(edge.node2, 1.0); // Ensure it's symmetric for undirected graph
            }
        }
    }

    public List<Node> findShortestPath(Node start, Node end, int numAnts, int numIterations) {
        List<Node> bestPath = null;
        double bestLength = Double.MAX_VALUE;

        for (int iteration = 0; iteration < numIterations; iteration++) {
            List<List<Node>> allPaths = new ArrayList<>();
            List<Double> allLengths = new ArrayList<>();

            for (int ant = 0; ant < numAnts; ant++) {
                List<Node> path = new ArrayList<>();
                Set<Node> visited = new HashSet<>();
                Node current = start;

                while (!current.equals(end)) {
                    path.add(current);
                    visited.add(current);
                    Node nextNode = selectNextNode(current, visited);
                    if (nextNode == null) {
                        path = null;
                        break;
                    }
                    current = nextNode;
                }

                if (path == null || !current.equals(end)) {
                    continue; // Skip incomplete paths
                }

                path.add(end);

                double length = calculatePathLength(path);
                allPaths.add(path);
                allLengths.add(length);

                if (length < bestLength) {
                    bestLength = length;
                    bestPath = path;
                }
            }

            if (allPaths.isEmpty()) {
                continue; // No paths found in this iteration
            }

            updatePheromones(allPaths, allLengths);
        }

        if (bestPath == null) {
            System.out.println("No valid path found from " + start.name + " to " + end.name);
        }

        return bestPath;
    }


    private Node selectNextNode(Node current, Set<Node> visited) {
        List<Edge> edges = new ArrayList<>(graph.getEdges(current));
        double[] probabilities = new double[edges.size()];
        double sum = 0.0;

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (!visited.contains(edge.node1)) {
                probabilities[i] = Math.pow(pheromones.get(current).get(edge.node1), alpha)
                        * Math.pow(1.0 / edge.weight, beta);
                sum += probabilities[i];
            } else if (!visited.contains(edge.node2)) {
                probabilities[i] = Math.pow(pheromones.get(current).get(edge.node2), alpha)
                        * Math.pow(1.0 / edge.weight, beta);
                sum += probabilities[i];
            }
        }

        if (sum == 0.0) return null;

        double random = Math.random() * sum;
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            if (!visited.contains(edge.node1)) {
                random -= probabilities[i];
                if (random <= 0.0) {
                    return edge.node1;
                }
            } else if (!visited.contains(edge.node2)) {
                random -= probabilities[i];
                if (random <= 0.0) {
                    return edge.node2;
                }
            }
        }

        return null;
    }

    public double calculatePathLength(List<Node> path) {
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);
            for (Edge edge : graph.getEdges(current)) {
                if ((edge.node1.equals(current) && edge.node2.equals(next)) ||
                        (edge.node1.equals(next) && edge.node2.equals(current))) {
                    length += edge.weight;
                    break;
                }
            }
        }
        return length;
    }

    private void updatePheromones(List<List<Node>> allPaths, List<Double> allLengths) {
        for (Node node : pheromones.keySet()) {
            for (Node destination : pheromones.get(node).keySet()) {
                pheromones.get(node).put(destination, pheromones.get(node).get(destination) * (1.0 - evaporationRate));
            }
        }

        for (int k = 0; k < allPaths.size(); k++) {
            List<Node> path = allPaths.get(k);
            double length = allLengths.get(k);
            double pheromoneDelta = Q / length;

            for (int i = 0; i < path.size() - 1; i++) {
                Node from = path.get(i);
                Node to = path.get(i + 1);
                pheromones.get(from).put(to, pheromones.get(from).get(to) + pheromoneDelta);
                pheromones.get(to).put(from, pheromones.get(to).get(from) + pheromoneDelta); // Assuming undirected graph
            }
        }
    }
}