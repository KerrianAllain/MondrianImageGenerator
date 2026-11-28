
public class HeapNode<T> {

    private final Double weight;
    private final T tree;

    public HeapNode(Double weight, T tree) {
        this.weight = weight;
        this.tree = tree;
    }

    public Double getWeight() {
        return this.weight;
    }

    public T getTree() {
        return this.tree;
    }
}
