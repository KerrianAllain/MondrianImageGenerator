
import java.util.ArrayList;

public final class BinaryHeap<T extends Tree> {

    private final ArrayList<HeapNode<T>> heap;

    public BinaryHeap(T root) {
        this.heap = new ArrayList<>();
        this.push(root);
    }

    /**
     * Push a Tree into the heap to its right position.
     *
     * <p>
     * <b>Complexity: O(log(n))</b>
     * </p>
     *
     * @param t The Tree to push.
     */
    public void push(T t) {
        Double weight = Utils.getWeight(t);
        this.heap.add(new HeapNode<>(weight, t));
        if (this.heap.size() > 1) {
            this.siftUp(this.heap.size() - 1);
        }
    }

    /**
     * Pop a Tree from the heap, which is the leaf to divide.
     *
     * <p>
     * <b>Complexity: O(log(n))</b>
     * </p>
     *
     * @return The Tree to divide.
     */
    public T pop() {
        int n = this.heap.size();
        switch (n) {
            case 0:
                return null;
            case 1:
                return this.heap.remove(n - 1).getTree();

            default:
                T result = this.heap.get(0).getTree();
                this.heap.set(0, this.heap.remove(n - 1));
                this.siftDown(0);
                return result;
        }
    }

    /**
     * Move the tree at index k to its right position in the heap
     *
     * <p>
     * <b>Complexity: O(log(n))</b>
     * </p>
     *
     * @param t the index
     */
    private void siftUp(int k) {
        int parentIndex = parent(k);
        HeapNode<T> kth = this.heap.get(k);
        HeapNode<T> parent = this.heap.get(parentIndex);
        if (this.hasAHigherPriority(kth, parent)) {
            this.heap.set(k, parent);
            this.heap.set(parentIndex, kth);
            this.siftUp(parentIndex);
        }
    }

    /**
     * Move the tree at index k to its right position in the heap
     *
     * <p>
     * <b>Complexity: O(log(n))</b>
     * </p>
     *
     * @param t the index
     */
    private void siftDown(int k) {
        if (this.isLeaf(k)) {
            return;
        }

        int sonIndex = this.biggestSon(k);
        HeapNode<T> kth = this.heap.get(k);
        HeapNode<T> son = this.heap.get(sonIndex);
        if (this.hasAHigherPriority(son, kth)) {
            this.heap.set(k, son);
            this.heap.set(sonIndex, kth);
            this.siftDown(sonIndex);
        }
    }

    /**
     * Gives the index of the parent of the index given
     *
     * <p>
     * <bComplexity: O(1)</b> </p>
     *
     * @param k the index
     * @return the parent index
     */
    private int parent(int k) {
        return (k - 1) / 2;
    }

    /**
     * Gives the index of the left son of the index given
     *
     * <p>
     * <bComplexity: O(1)</b> </p>
     *
     * @param k the index
     * @return the left son index
     */
    private int leftSon(int k) {
        return 2 * k + 1;
    }

    /**
     * Gives the index of the right son of the index given
     *
     * <p>
     * <bComplexity: O(1)</b> </p>
     *
     * @param k the index
     * @return the right son index
     */
    private int rightSon(int k) {
        return 2 * k + 2;
    }

    /**
     * Tells if the node at index k has no son
     *
     * <p>
     * <bComplexity: O(1)</b> </p>
     *
     * @param k the index
     * @return {@code true} if the node is a leaf
     */
    private boolean isLeaf(int k) {
        return leftSon(k) >= this.heap.size();
    }

    /**
     * Return the index of the son having the greater weight
     *
     * <p>
     * <bComplexity: O(1)</b> </p>
     *
     * @param k the index
     */
    private int biggestSon(int k) {
        int leftSon = this.leftSon(k);
        int rightSon = this.rightSon(k);
        if (rightSon >= this.heap.size()) {
            return leftSon;
        }
        return this.hasAHigherPriority(this.heap.get(leftSon), this.heap.get(rightSon)) ? leftSon : rightSon;
    }

    /**
     * Determines the HeapNode element of highest priority.
     *
     * <p>
     * <bComplexity: O(1)</b>
     * </p>
     *
     * @param k the index
     * @return {@code true} if the first element has a higher priority than the second one, {@code false} otherwise.
     */
    private boolean hasAHigherPriority(HeapNode<T> elm, HeapNode<T> elmToCompare) {
        int comparison = Utils.compare(elm.getTree(), elmToCompare.getTree());
        boolean weightPriority = comparison > 0;
        boolean coordinatePriority = comparison == 0 && Utils.highestLeftCoordinate(elm.getTree().topLeftPoint(), elmToCompare.getTree().topLeftPoint()) == -1;
        return weightPriority || coordinatePriority;
    }
}
