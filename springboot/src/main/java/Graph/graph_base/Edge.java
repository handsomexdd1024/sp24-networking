package Graph.graph_base;

import java.util.Objects;

public class Edge {
    public Node node1;
    public Node node2;
    public double weight;

    public Edge(Node node1, Node node2, double weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "(" + node1 + " <-> " + node2 + ") with weight " + weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        // Check both (node1, node2) and (node2, node1) pairs
        return (Objects.equals(node1, edge.node1) && Objects.equals(node2, edge.node2)) ||
                (Objects.equals(node1, edge.node2) && Objects.equals(node2, edge.node1));
    }

    @Override
    public int hashCode() {
        // Ensure consistent hash code for both (node1, node2) and (node2, node1) pairs
        return Objects.hash(node1, node2) + Objects.hash(node2, node1);
    }
}
